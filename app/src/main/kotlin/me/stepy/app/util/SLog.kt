package me.stepy.app.util

import android.util.Log
import me.stepy.app.Environment

class SLog {
    companion object {
        private fun isDebug() = !Environment.IS_PRODUCTION

        fun e(tag: String, body: String) {
            if (isDebug()) return
            Log.e(tag, body)
        }
        fun d(tag: String, body: String) {
            if (isDebug()) return
            Log.d(tag, body)
        }
        fun i(tag: String, body: String) {
            if (isDebug()) return
            Log.i(tag, body)
        }
        fun w(tag: String, body: String) {
            if (isDebug()) return
            Log.w(tag, body)
        }
        fun v(tag: String, body: String) {
            if (isDebug()) return
            Log.v(tag, body)
        }
    }
}
