package me.stepy.app.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import me.stepy.app.App
import me.stepy.app.activity.MainActivity
import java.lang.ref.WeakReference

open class BaseFragment : Fragment() {

    private var attachActivity: WeakReference<MainActivity?> = WeakReference(null)
    val _activity: MainActivity?
        get() = attachActivity.get()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is MainActivity) {
            attachActivity = WeakReference(context)
        }
    }

    interface Create<out T : BaseFragment> {
        fun create(arg: Bundle? = null): T
        val TAG: String
    }

    open fun onResumeFromBackStack() {
    }

}
