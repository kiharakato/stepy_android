package me.stepy.app.db.model

import me.stepy.app.App
import java.util.*

data class Todo(
        var id: Long?,
        val action: String,
        val status: Int,
        val updated_at: Long,
        val created_at: Long,
        val parent: Long? = null) {

    val children: Array<Todo>? = null

    companion object {
        const val ACTION_STATUS = 0
        const val DONE_STATUS = 1

        fun create(action: String, parent: Long?): Todo {
            val now = App.getDateTime()
            return Todo(null, action, ACTION_STATUS, now, now, parent)
        }
    }

    fun getElapsedTime(): String {
        val now = Calendar.getInstance().timeInMillis
        val diff = now.minus(created_at)

        var _diff = diff / (60 * 1000)
        if (_diff == 0L) {
            return "今"
        } else if (_diff < 60L) {
            return "$_diff 分前"
        }

        _diff = diff / (60 * 60 * 1000)
        if (_diff < 24L) {
            return "$_diff 時間前"
        }

        _diff = diff / (24 * 60 * 60 * 1000)
        return "$_diff 日前"
    }

}
