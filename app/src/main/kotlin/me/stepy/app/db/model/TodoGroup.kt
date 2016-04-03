package me.stepy.app.db.model

import me.stepy.app.StepyApplication

data class TodoGroup(
        var id: Long?,
        val name: String,
        val updated_at: Long = 0,
        val created_at: Long = 0) {

    companion object {
        public fun create(id: Long? = null, name: String): TodoGroup {
            val now = StepyApplication.getDateTime()
            return TodoGroup(id, name, now, now)
        }
    }
}