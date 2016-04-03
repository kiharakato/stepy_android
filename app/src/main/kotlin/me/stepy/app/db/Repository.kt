package me.stepy.app.db

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.Sort
import me.stepy.app.StepyApplication
import me.stepy.app.db.realmObj.Item
import java.util.*


class Repository {

    //    companion object {
    //        private val realm: Realm
    //            get() = StepyApplication.realm
    //
    //        fun findHomeItemAndList(): Map<RealmResults<Item>, RealmResults<List>> {
    //            realm.beginTransaction()
    //            val items = realm.where(Item::class.java)
    //                    .equalTo("status", Item.STATUS_ACTIVE)
    //                    .findAllSorted("updated_at", Sort.DESCENDING)
    //            val lists = realm.where(Item::class.java)
    //                    .equalTo("status", List.STATUS_ACTIVE)
    //                    .findAllSorted("updated_at", Sort.DESCENDING)
    //            realm.commitTransaction()
    //
    //            val pair = Pair<RealmResults<Item>, RealmResults<List>>(items, lists)
    //            return pair
    //        }
    //    }

}