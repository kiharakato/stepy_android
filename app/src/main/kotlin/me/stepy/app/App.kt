package me.stepy.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlin.properties.Delegates

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        App.context = applicationContext
    }

    companion object {
        var context: Context by Delegates.notNull()
        fun getColor(id: Int) = ContextCompat.getColor(context, id)

        fun hideKeyboard(activity: Activity) {
            val activity = context as? Activity ?: return
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        fun showKeyboard(focusView: View, activity: Activity) {
            val activity = context as? Activity ?: return
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(focusView, 0)
        }

    }

}