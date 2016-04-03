package me.stepy.app.db.repository

import android.content.ContentValues
import android.text.TextUtils
import me.stepy.app.db.StepySQLiteHelper
import me.stepy.app.db.model.TodoGroup
import java.util.*

class TodoGroupRepository : BaseRepository() {

    companion object {

        const val TABLE_NAME = StepySQLiteHelper.TODO_GROUP_TABLE_NAME
        const val NAME_KEY = "name"

        fun create(name: String): TodoGroup {
            val group = TodoGroup.create(null, name)

            val content = ContentValues().apply {
                put(NAME_KEY, group.name)
                put(CREATED_KEY, group.created_at)
                put(UPDATED_KEY, group.updated_at)
            }
            group.id = getDB().insert(TABLE_NAME, null, content)

            return group
        }

        fun selectUpdateOneByName(name: String): TodoGroup? {
            val cursor = getDB().rawQuery("SELECT * FROM $TABLE_NAME WHERE name = $1", arrayOf(name))

            try {
                if (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndex(ID_KEY))
                    val updatedAt = cursor.getLong(cursor.getColumnIndex(UPDATED_KEY))
                    val createdAt = cursor.getLong(cursor.getColumnIndex(CREATED_KEY))
                    return TodoGroup(id, name, updatedAt, createdAt)
                } else {
                    return create(name)
                }
            } finally {
                cursor.close()
            }
        }

        fun selectAll(): ArrayList<TodoGroup> {
            val groups: ArrayList<TodoGroup> = arrayListOf()
            val cursor = getDB().rawQuery("SELECT $ID_KEY, $NAME_KEY FROM $TABLE_NAME ORDER BY CREATED_AT DESC", null)

            try {
                for (i in 0..cursor.count) {
                    if (!cursor.moveToNext()) break
                    val id = cursor.getLong(cursor.getColumnIndex(ID_KEY))
                    val name = cursor.getString(cursor.getColumnIndex(NAME_KEY))
                    groups.add(TodoGroup(id, name))
                }
            } finally {
                cursor.close()
            }
            return groups
        }

        fun selectAllNames(): ArrayList<String> {
            val names = ArrayList<String>()
            val cursor = getDB().rawQuery("SELECT $NAME_KEY FROM $TABLE_NAME", null)

            try {
                for (i in 0..cursor.count) {
                    if (!cursor.moveToNext()) break
                    val name = cursor.getString(cursor.getColumnIndex(NAME_KEY)) ?: ""
                    if (TextUtils.isEmpty(name)) break
                    names.add(name)
                }
            } finally {
                cursor.close()
            }

            return names
        }
    }

}