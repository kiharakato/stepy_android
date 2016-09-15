package me.stepy.app.db.repository

import android.content.ContentValues
import android.database.Cursor
import me.stepy.app.App
import me.stepy.app.db.StepySQLiteHelper
import me.stepy.app.db.model.Todo
import java.util.*

class TodoRepository : BaseRepository() {

    companion object {

        const val TABLE_NAME = StepySQLiteHelper.TODO_TABLE_NAME
        const val ACTION_KEY = "action"
        const val STATUS_KEY = "status"
        const val PARENT_KEY = "parent"

        /**
         * Create new Todo data.
         */
        fun create(action: String, parent: Long? = null): Todo {
            val todo = Todo.create(action, parent)
            val values = ContentValues().apply {
                put("action", todo.action)
                put("status", todo.status)
                put("parent", todo.parent)
                put("created_at", todo.created_at)
                put("updated_at", todo.updated_at)
            }

            todo.id = getDB().insert(TABLE_NAME, null, values)

            return todo
        }

        fun selectAll(): ArrayList<Todo> {
            val data: ArrayList<Todo> = arrayListOf()
            val cursor = getDB().rawQuery("SELECT * FROM $TABLE_NAME", null)

            try {
                for (i in 0..cursor.count) {
                    if (!cursor.moveToNext()) break
                    val id = cursor.getLong(cursor.getColumnIndex(ID_KEY))
                    val action = cursor.getString(cursor.getColumnIndex(ACTION_KEY))
                    val status = cursor.getInt(cursor.getColumnIndex(STATUS_KEY))
                    val parent = cursor.getLong(cursor.getColumnIndex(PARENT_KEY))
                    val createAt = cursor.getLong(cursor.getColumnIndex(CREATED_KEY))
                    val todo = Todo(id, action, status, createAt, 0, parent)
                    data.add(todo)
                }
            } finally {
                cursor.close()
            }
            return data
        }

        /***
         * Get status is action of the All field.
         */
        fun SelectActionStatusAll(): Cursor? {
            return getDB().query(
                    TABLE_NAME,
                    Array(1, { "*" }),
                    "status = ${Todo.ACTION_STATUS}",
                    null, null, null, "updated_at desc")
        }

        /***
         * Get status is done of the All field.
         */
        fun SelectDoneStatusAll(): Cursor? {
            return getDB().query(TABLE_NAME, Array(1, { "*" }), "status = ${Todo.DONE_STATUS}",
                    null, null, null, "updated_at desc")
        }

        fun selecActiontWithName(groupId: Long? = null): ArrayList<Todo> {
            val data: ArrayList<Todo> = arrayListOf()
            val cursor = getDB().rawQuery(
                    """
                    SELECT * FROM $TABLE_NAME WHERE status = ${Todo.ACTION_STATUS} AND parent = ${groupId.toString()}
                    """, null)

            try {
                for (i in 0..cursor.count) {
                    if (!cursor.moveToNext()) break
                    val action = cursor.getString(cursor.getColumnIndex(ACTION_KEY))
                    val status = cursor.getInt(cursor.getColumnIndex(STATUS_KEY))
                    val parent = cursor.getLong(cursor.getColumnIndex(PARENT_KEY))
                    val updatedAt = cursor.getLong(cursor.getColumnIndex(UPDATED_KEY))
                    val createdAt = cursor.getLong(cursor.getColumnIndex(CREATED_KEY))
                    val todo = Todo(null, action, status, updatedAt, createdAt, parent)
                    data.add(todo)
                }
            } finally {
                cursor.close()
            }
            return data
        }

        /**
         *
         */
        fun UpdateStatus(id: Long, status: Int): Int {
            val contentValues = ContentValues().apply {
                put("status", status)
                put("updated_at", App.getDateTime())
            }
            return getDB().update(TABLE_NAME, contentValues, "id = $id", null)
        }

        /**
         * parentの設定がないTodoを取得
         */
        fun selectByNotParentAndActionStatus(): ArrayList<Todo> {
            val data: ArrayList<Todo> = arrayListOf()
            val cursor = getDB().rawQuery(
                    """
                     SELECT * FROM $TABLE_NAME WHERE parent IS NULL AND status = ${Todo.ACTION_STATUS} ORDER BY created_at DESC
                    """, null)

            try {
                for (i in 0..cursor.count) {
                    if (!cursor.moveToNext()) break
                    val id = cursor.getLong(cursor.getColumnIndex(ID_KEY))
                    val action = cursor.getString(cursor.getColumnIndex(ACTION_KEY))
                    val status = cursor.getInt(cursor.getColumnIndex(STATUS_KEY))
                    val parent = cursor.getLong(cursor.getColumnIndex(PARENT_KEY))
                    val updatedAt = cursor.getLong(cursor.getColumnIndex(UPDATED_KEY))
                    val createdAt = cursor.getLong(cursor.getColumnIndex(CREATED_KEY))
                    val todo = Todo(id, action, status, updatedAt, createdAt, parent)
                    data.add(todo)
                }
            } finally {
                cursor.close()
            }

            return data
        }

        fun selectByParent(id: Long): ArrayList<Todo> {
            val data: ArrayList<Todo> = arrayListOf()
            val cursor = getDB().rawQuery(
                    """
                     SELECT * FROM $TABLE_NAME WHERE parent = $id
                    """, null)

            try {
                for (i in 0..cursor.count) {
                    if (!cursor.moveToNext()) break
                    val id = cursor.getLong(cursor.getColumnIndex(ID_KEY))
                    val action = cursor.getString(cursor.getColumnIndex(ACTION_KEY))
                    val status = cursor.getInt(cursor.getColumnIndex(STATUS_KEY))
                    val parent = cursor.getLong(cursor.getColumnIndex(PARENT_KEY))
                    val updatedAt = cursor.getLong(cursor.getColumnIndex(UPDATED_KEY))
                    val createdAt = cursor.getLong(cursor.getColumnIndex(CREATED_KEY))
                    val todo = Todo(id, action, status, updatedAt, createdAt, parent)
                    data.add(todo)
                }
            } finally {
                cursor.close()
            }

            return data
        }
    }
}