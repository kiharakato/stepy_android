package me.stepy.app.recyclerView

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm
import io.realm.Sort
import me.stepy.app.R
import me.stepy.app.db.realmObj.Item
import java.lang.ref.WeakReference
import java.util.*

class GroupAdapter(val groupId: String, realm: Realm) : RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>() {

    private var realmWeakReference: WeakReference<Realm?> = WeakReference(null)
    private var items: ArrayList<Item> = arrayListOf()

    init {
        realmWeakReference = WeakReference(realm)
        initDate()
    }

    private fun initDate() {
        items.clear()

        val realm = realmWeakReference.get() ?: return
        realm.beginTransaction()
        realm.where(Item::class.java)
                .equalTo("parent", groupId)
                .equalTo("status", Item.STATUS_ACTIVE)
                .findAllSorted("updated_at", Sort.DESCENDING)
                .forEach { item ->
                    items.add(item)
                }
        realm.commitTransaction()
    }

    override fun onBindViewHolder(viewHolder: RecyclerAdapter.RecyclerViewHolder, position: Int) {
        val item = items[position]

        viewHolder.content?.apply {
            visibility = View.VISIBLE
            translationX = 0f
            translationY = 0f
        }

        viewHolder.itemView.tag = item.id
        viewHolder.createdAt?.visibility = View.GONE
        viewHolder.action?.text = item.action
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): RecyclerAdapter.RecyclerViewHolder? {
        val inflater = LayoutInflater.from(viewGroup.context)
        return RecyclerAdapter.RecyclerViewHolder(inflater.inflate(R.layout.row_todo, viewGroup, false), RecyclerAdapter.VIEW_TYPE_CHILD)
    }

    override fun getItemViewType(position: Int): Int {
        return RecyclerAdapter.VIEW_TYPE_CHILD
    }

    fun addItem(item: Item) {
        items.add(item)
        refresh()
    }

    fun refresh() {
        initDate()
        notifyDataSetChanged()
    }

}