package me.stepy.app.fragment

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import me.stepy.app.R

class BaseFragmentManager(val manager: FragmentManager, val layoutId: Int) {

    enum class ActionType {
        ADD, REPLACE, REMOVE
    }

    interface NavigationListener {
        fun onBackStackChanged()
    }

    private var fragmentManager: FragmentManager? = null

    var navigationListener: NavigationListener? = null

    init {
        manager.addOnBackStackChangedListener {
            navigationListener?.onBackStackChanged()
            (fragmentManager?.findFragmentById(layoutId) as? BaseFragment)?.onResumeFromBackStack()
        }
        fragmentManager = manager
    }

    private fun open(fragment: BaseFragment, type: ActionType, tag: String? = null) {
        val setDefaultAnimation = fun(t: FragmentTransaction): FragmentTransaction {
            return t.setCustomAnimations(
                    R.anim.slide_in_left, R.anim.slide_out_right,
                    R.anim.slide_in_right, R.anim.slide_out_left)
                    .addToBackStack(fragment.toString())
        }

        fragmentManager?.let {
            val t = it.beginTransaction()
            when (type) {
                ActionType.ADD -> {
                    t.add(layoutId, fragment, tag)
                    setDefaultAnimation(t)
                }
                ActionType.REPLACE -> {
                    t.replace(layoutId, fragment, tag)
                    setDefaultAnimation(t)
                }
                ActionType.REMOVE -> {
                    t.remove(fragment)
                }
            }
            t.commit()
        }
    }

    fun openAsAddWithRemoveOfTags(fragmentClass: Class<*>, removeFragmentTags: Array<String>, arg: Bundle? = null) {
        open(createFragment(fragmentClass, arg), ActionType.ADD, fragmentClass.name)
        removeFragmentTags.map { tag ->
            popSpecifiedTagFragment(tag)
        }
    }

    fun openAsAddWithRemoveOfTag(fragmentClass: Class<*>, removeFragmentTag: String, arg: Bundle? = null) {
        open(createFragment(fragmentClass, arg), ActionType.ADD, fragmentClass.name)
        popSpecifiedTagFragment(removeFragmentTag)
    }

    fun openAsAdd(fragmentClass: Class<*>, arg: Bundle? = null): BaseFragment {
        val fragment = createFragment(fragmentClass, arg)
        open(fragment, ActionType.ADD, fragmentClass.name)
        return fragment
    }

    fun openAsRoot(fragmentClass: Class<*>, arg: Bundle? = null) {
        popEveryFragment()
        open(createFragment(fragmentClass, arg), ActionType.REPLACE, fragmentClass.name)
    }

    fun removeByFragment(fragment: BaseFragment) {
        open(fragment, ActionType.REMOVE)
    }

    private fun createFragment(fragmentClass: Class<*>, arg: Bundle?): BaseFragment {
        return (fragmentClass.getDeclaredConstructor().newInstance() as BaseFragment).apply {
            if (arg != null) arguments = arg
        }
    }

    private fun popSpecifiedTagFragment(tag: String) {
        val fragment = (fragmentManager?.findFragmentByTag(tag) as? BaseFragment) ?: return
        open(fragment, ActionType.REMOVE)
    }

    private fun popEveryFragment() {
        val _manager = fragmentManager ?: return
        val backStackCount = _manager.backStackEntryCount
        for (i in 0..backStackCount - 1) {
            val backStackId = _manager.getBackStackEntryAt(i).id
            _manager.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    fun navigateBack(activity: Activity) {
        if (fragmentManager?.backStackEntryCount == 1) {
            activity.finish()
        } else {
            fragmentManager?.popBackStackImmediate()
        }
    }

    val isRootFragmentVisible: Boolean
        get() = (fragmentManager?.backStackEntryCount ?: 0) <= 1

    fun popBackStackImmediate() {
        fragmentManager?.popBackStackImmediate()
    }
}