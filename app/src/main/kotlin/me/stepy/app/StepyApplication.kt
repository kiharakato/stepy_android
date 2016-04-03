package me.stepy.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Point
import android.os.Build
import android.view.Display
import android.view.View
import android.view.inputmethod.InputMethodManager
import io.realm.Realm
import io.realm.RealmConfiguration
import me.stepy.app.db.StepySQLiteHelper
import java.util.*
import kotlin.properties.Delegates

class StepyApplication : Application() {

    val DB: SQLiteDatabase by lazy { StepySQLiteHelper(this).writableDatabase }

    override fun onCreate() {
        super.onCreate()
        StepyApplication.context = applicationContext
    }

    companion object {

        private var context: Context by Delegates.notNull()

        fun getInstance(): Context = context

        fun getDateTime(): Long = Calendar.getInstance().timeInMillis

        fun hideKeyboard(context: Context) {
            val activity = context as? Activity ?: return
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        fun showKeyboard(focusView: View, context: Context) {
            val activity = context as? Activity ?: return
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(focusView, 0)
        }

        fun getDisplayRealSize(activity: Activity): Point {
            val display = activity.windowManager.defaultDisplay
            val point = Point(0, 0)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealSize(point);
                return point;

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                try {
                    val getRawWidth = Display::class.java.getMethod("getRawWidth");
                    val getRawHeight = Display::class.java.getMethod("getRawHeight");
                    val width = getRawWidth.invoke(display) as Int
                    val height = getRawHeight.invoke(display) as Int
                    point.set(width, height);

                    return point;
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }

            return point;
        }

        fun getRealm(): Realm {
            val realmConfiguration = RealmConfiguration.Builder(context).build()
            return Realm.getInstance(realmConfiguration)

        }

        var activity: Activity? = null
        fun setMainActivity(activity: Activity) {
            this.activity = activity
        }


    }

}