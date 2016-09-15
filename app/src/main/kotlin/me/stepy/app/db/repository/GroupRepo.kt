package me.stepy.app.db.repository

import me.stepy.app.App
import me.stepy.app.db.realmObj.Group

class GroupRepo {
    companion object {

        fun updateStatus(id: String, status: Int) {
            val realm = App.getRealm()
            realm.beginTransaction()
            val group = realm.where(Group::class.java)
                    .equalTo("id", id)
                    .findFirst()
            group.status = status
            realm.commitTransaction()
        }

    }
}
