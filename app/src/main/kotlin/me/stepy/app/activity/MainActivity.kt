package me.stepy.app.activity

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import me.stepy.app.BuildConfig
import me.stepy.app.R
import me.stepy.app.fragment.GroupFragment
import me.stepy.app.fragment.MainFragment

class MainActivity : FragmentActivity() {

    companion object {
        const val FRAGMENT_BACK_STACK_MAIN = "main"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fabric = Fabric.Builder(this).kits(Crashlytics()).debuggable(true).build()
        Fabric.with(fabric)

        supportFragmentManager.addOnBackStackChangedListener {
            supportFragmentManager ?: return@addOnBackStackChangedListener
            val fragment = supportFragmentManager.findFragmentById(R.id.mainFragment) as? MainFragment ?: return@addOnBackStackChangedListener
            fragment.onFragmentResume()
        }

        applyMainFragment()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val fragmentManager = supportFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
                return true;
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    fun applyMainFragment() {
        val mainFragment = MainFragment()
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        android.support.v7.appcompat.R.anim.abc_slide_in_top,
                        android.support.v7.appcompat.R.anim.abc_slide_out_bottom)
                .add(R.id.mainFragment, mainFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
    }

    fun applyCreateGroupFragment() {
        val groupFragment = GroupFragment.createNewGroupFragment()
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        android.support.v7.appcompat.R.anim.abc_fade_in,
                        android.support.v7.appcompat.R.anim.abc_fade_out,
                        android.support.v7.appcompat.R.anim.abc_fade_in,
                        android.support.v7.appcompat.R.anim.abc_fade_out)
                .add(R.id.mainFragment, groupFragment)
                .addToBackStack(FRAGMENT_BACK_STACK_MAIN)
                .commit()
    }

    fun applyGroupEditFragment(groupId: String) {
        val fragment = GroupFragment.createOpenGroupFragment(groupId)
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out)
                .add(R.id.mainFragment, fragment)
                .addToBackStack(FRAGMENT_BACK_STACK_MAIN)
                .commit()
    }

}
