package me.stepy.app.activity

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.KeyEvent
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mikepenz.materialdrawer.DrawerBuilder
import io.fabric.sdk.android.Fabric
import me.stepy.app.R
import me.stepy.app.fragment.GroupFragment
import me.stepy.app.fragment.LoginFragment
import me.stepy.app.fragment.MainFragment

class MainActivity : FragmentActivity() {

    companion object {
        const val FRAGMENT_BACK_STACK_MAIN = "main"
        const val TAG = "MainActivity"
    }

    lateinit var auth: FirebaseAuth
    val user: FirebaseUser?
        get() = auth.currentUser
    val isLogin: Boolean
        get() = auth.currentUser?.isAnonymous == false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_main)

        val fabric = Fabric.Builder(this).kits(Crashlytics()).debuggable(true).build()
        Fabric.with(fabric)

        supportFragmentManager.addOnBackStackChangedListener {
            supportFragmentManager ?: return@addOnBackStackChangedListener
            val fragment = supportFragmentManager.findFragmentById(R.id.mainFragment) as? MainFragment ?: return@addOnBackStackChangedListener
            fragment.onFragmentResume()
        }

        DrawerBuilder().withActivity(this).build()

        applyMainFragment()
    }

    override fun onStart() {
        super.onStart()
        auth.currentUser ?: let {
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d(TAG, "Authentication failed", task.exception)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val fragmentManager = supportFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    fun clearAllFragments() {
        val fragments = supportFragmentManager.fragments
        fragments.forEach { fragment ->
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }
    }

    fun applyTopMainFragment() {
        clearAllFragments()
        applyMainFragment()
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

    fun applyLoginFragment() {
        val fragment = LoginFragment()
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
