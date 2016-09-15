package me.stepy.app.db.repository

import me.stepy.app.App

open class BaseRepository {

    companion object {

        const val ID_KEY = "id"
        const val CREATED_KEY = "created_at"
        const val UPDATED_KEY = "updated_at"

        fun getDB() = (App.getInstance() as App).DB

    }
}