package me.stepy.app.model

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties class Item {

    var action: String? = null
    var status: Int? = null
    var parent: String? = null
    var created_at: Date? = null

    constructor()

    constructor(
            action: String,
            status: Int,
            parent: String?,
            created_at: Date? = null) {

        this.action = action
        this.status = status
        this.parent = parent
        this.created_at = created_at
    }

}