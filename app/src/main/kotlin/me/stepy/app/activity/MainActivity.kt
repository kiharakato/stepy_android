package me.stepy.app.activity

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.mikepenz.materialdrawer.DrawerBuilder
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import me.stepy.app.R
import me.stepy.app.fragment.BaseFragment
import me.stepy.app.fragment.BaseFragmentManager
import me.stepy.app.fragment.MainFragment
import kotlin.properties.Delegates

class MainActivity : FragmentActivity() {

    private val TAG = "MainActivity"
    private var auth by Delegates.notNull<FirebaseAuth>()
    val fm = BaseFragmentManager(supportFragmentManager, R.id.mainFragment)

    private var authListener = FirebaseAuth.AuthStateListener { auth ->
        auth.currentUser ?: let {
            auth.signInAnonymously()
            return@AuthStateListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Fabric.Builder(this).kits(Crashlytics()).debuggable(true).build().let {
            Fabric.with(it)
        }

        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        DrawerBuilder().withActivity(this).build()
        fm.openAsRoot(MainFragment::class.java)
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.removeAuthStateListener(authListener)
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
}
