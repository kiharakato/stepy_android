package me.stepy.app.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties class Note {

    var name: String? = null

    constructor()

    constructor(name: String) {
        this.name = name
    }
}
