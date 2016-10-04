package me.stepy.app.activity

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.KeyEvent
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.mikepenz.materialdrawer.DrawerBuilder
import io.fabric.sdk.android.Fabric
import me.stepy.app.App
import me.stepy.app.R

class MainActivity : FragmentActivity() {

    companion object {
        const val FRAGMENT_BACK_STACK_MAIN = "main"
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fabric = Fabric.Builder(this).kits(Crashlytics()).debuggable(true).build()
        Fabric.with(fabric)

        if(App.auth.currentUser == null) {
            App.auth.signInAnonymously()
        }

        DrawerBuilder().withActivity(this).build()
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
