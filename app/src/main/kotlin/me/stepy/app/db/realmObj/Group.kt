package me.stepy.app.db.realmObj

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class Group(
        @PrimaryKey open var id: String = UUID.randomUUID().toString(),
        open var name: String = "",
        open var status: Int = STATUS_ACTIVE,
        open var updated_at: Date? = Date(),
        open var created_at: Date? = Date()
) : RealmObject() {
    companion object {
        const val STATUS_ACTIVE = 0
        const val STATUS_DONE = 1
    }
}