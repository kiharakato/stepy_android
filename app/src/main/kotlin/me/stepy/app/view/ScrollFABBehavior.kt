package me.stepy.app.view

import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.util.Log
import android.view.View

@CoordinatorLayout.DefaultBehavior(ScrollFABBehavior::class)
public class ScrollFABBehavior : FloatingActionButton.Behavior {

    constructor() : super()

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout?, child: FloatingActionButton?, directTargetChild: View?, target: View?, nestedScrollAxes: Int): Boolean {
        Log.d("logcat", "onStartNestedScroll")
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes)
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout?, child: FloatingActionButton?, target: View?, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        child ?: return

        if (dyConsumed < 0) {
            child.show()
        } else if (dyConsumed > 0) {
            child.hide()
        }
    }
}
