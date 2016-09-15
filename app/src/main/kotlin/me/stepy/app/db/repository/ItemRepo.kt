package me.stepy.app.db.repository

import io.realm.Realm
import me.stepy.app.App
import me.stepy.app.db.realmObj.Item
import java.util.*

class ItemRepo {
    companion object {

        fun create(action: String, parent: String? = null): Item {

            val item = Item()
            item.action = action
            item.parent = parent
            item.updated_at = Date()

            val realm = App.getRealm()
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(item)
            realm.commitTransaction()

            return item
        }

        fun updateStatus(id: String, status: Int) {
            val realm = App.getRealm()
            realm.beginTransaction()
            val item = realm.where(Item::class.java)
                    .equalTo("id", id)
                    .findFirst()
            item.status = status
            realm.commitTransaction()
        }

    }
}
