package me.stepy.app.model

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties class Item {

    var action: String? = null
    var status: Int? = null
    var note: String? = null
    var created_at: Date? = null

    constructor()

    constructor(
            action: String,
            status: Int,
            note: String?,
            createdAt: Date? = null) {

        this.action = action
        this.status = status
        this.note = note
        this.created_at = createdAt ?: Date()
    }

}