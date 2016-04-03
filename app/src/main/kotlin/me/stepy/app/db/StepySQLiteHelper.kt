package me.stepy.app.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StepySQLiteHelper : SQLiteOpenHelper {

    companion object {
        const val DB: String = "stepy.db"
        const val TODO_TABLE_NAME = "Todos"
        const val TODO_GROUP_TABLE_NAME = "TodoGroups"
        const val DB_VERSION: Int = 1
    }

    constructor(context: Context) : super(context, DB, null, DB_VERSION)

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
        CREATE TABLE $TODO_TABLE_NAME (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        action TEXT,
        status INTEGER NOT NULL,
        parent INTEGER,
        updated_at INTEGER NOT NULL,
        created_at INTEGER NOT NULL
        );
        """)

        db.execSQL("""
        CREATE TABLE $TODO_GROUP_TABLE_NAME (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT,
        updated_at INTEGER NOT NULL,
        created_at INTEGER NOT NULL
        );
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("""
        DROP TABLE $TODO_TABLE_NAME;
        """)

        db.execSQL("""
        DROP TABLE $TODO_GROUP_TABLE_NAME;
        """)

        onCreate(db)
    }

}
