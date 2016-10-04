package me.stepy.app.recyclerView

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import io.realm.Realm
import me.stepy.app.R
import me.stepy.app.model.Item
import me.stepy.app.model.Note
import java.lang.ref.WeakReference

class GroupAdapter(realm: Realm) : RecyclerView.Adapter<GroupAdapter.RecyclerViewHolder>() {
    data class ListHome(val note: Note, val childCount: Long)

    private val items = arrayListOf<Item>()
    private val lists = arrayListOf<ListHome>()
    private var realWeakReference: WeakReference<Realm?> = WeakReference(null)

    var onDataChangeListener: DataChangeListener? = null
    var onCreateGroupClickListener: View.OnClickListener? = null
    var onGroupClickListener: View.OnClickListener? = null
    var onItemClickListener: View.OnClickListener? = null
    var isCreateItem = false

    init {
        realWeakReference = WeakReference(realm)
        refresh()
    }

    private fun initData() {
        items.clear()
        lists.clear()
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            (position <= items.size - 1) -> VIEW_TYPE_CHILD
            else -> VIEW_TYPE_LIST
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): RecyclerViewHolder {
        val createViewHolder: (layout: Int) -> RecyclerViewHolder = { layout ->
            val inflater = LayoutInflater.from(viewGroup.context)
            RecyclerViewHolder(inflater.inflate(layout, viewGroup, false), type)
        }

        return when (type) {
            VIEW_CREATE_GROUP ->
                createViewHolder(R.layout.create_group)
            VIEW_TYPE_LIST ->
                createViewHolder(R.layout.row_list)
            else ->
                createViewHolder(R.layout.row_todo)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerViewHolder, position: Int) {
        val type = viewHolder.itemViewType

        if (type == VIEW_TYPE_CHILD) {
            val item = items[getItemPosition(position)]

            viewHolder.content?.apply {
                visibility = View.VISIBLE
                translationX = 0f
                translationY = 0f
            }

            // viewHolder.box.tag = item.id
            // viewHolder.createdAt?.text = item.getElapsedTime()
            viewHolder.createdAt?.visibility = View.GONE
            viewHolder.action?.text = item.action
            viewHolder.action?.setOnClickListener { view ->
                // view.tag = item.id
                onItemClickListener?.onClick(view)
            }
        } else if (type == VIEW_TYPE_LIST) {
            viewHolder.content?.apply {
                visibility = View.VISIBLE
                translationX = 0f
                translationY = 0f
            }

            val list = lists[getListPosition(position)]
            viewHolder.listName?.text = list.note.name
            viewHolder.listChildCount?.text = list.childCount.toString()
            // viewHolder.box.tag = list.note.id
            viewHolder.box.setOnClickListener { view ->
                onGroupClickListener?.onClick(view)
            }
        } else if (type == VIEW_CREATE_GROUP) {
            viewHolder.createGroupBtn?.apply {
                setOnClickListener { view ->
                    onCreateGroupClickListener?.onClick(view)
                }
            }
        }
    }

    private fun getListPosition(position: Int): Int {
        return position - (items.size - 1) - 1
    }

    private fun getItemPosition(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        // create new group の分 +1 している。
        // return items.size + lists.size + 1
        return items.size + lists.size
    }

    class RecyclerViewHolder(val box: View, val type: Int) : RecyclerView.ViewHolder(box) {
        var createdAt: TextView? = null
        var action: TextView? = null
        var content: View? = null
        var title: TextView? = null
        var numberOfItem: TextView? = null
        var createEditText: EditText? = null
        var listName: TextView? = null
        var listChildCount: TextView? = null
        var createGroupBtn: TextView? = null

        init {
            if (type == VIEW_TYPE_TOP) {
                title = box.findViewById(R.id.bar_title) as TextView
                numberOfItem = box.findViewById(R.id.number_of_todo) as TextView

            } else if (type == VIEW_TYPE_CHILD) {
                createdAt = box.findViewById(R.id.created_at) as TextView
                action = box.findViewById(R.id.action) as TextView
                content = box.findViewById(R.id.module)

            } else if (type == VIEW_CREATE_GROUP) {
                createGroupBtn = box.findViewById(R.id.create_group) as TextView

            } else if (type == VIEW_TYPE_LIST) {
                content = box.findViewById(R.id.module)
                listName = box.findViewById(R.id.listName) as TextView
                listChildCount = box.findViewById(R.id.childCount) as TextView
            }
        }

    }

    companion object {
        const val VIEW_TYPE_TOP = 0
        const val VIEW_TYPE_CHILD = 1
        const val VIEW_TYPE_CREATE = 2
        const val VIEW_TYPE_EMPTY = -1
        const val VIEW_CREATE_GROUP = 3
        const val VIEW_TYPE_LIST = 4
    }

    fun refresh() {
        initData()
        isCreateItem = false
        notifyDataSetChanged()
        onDataChangeListener?.onChange(items.size)
    }

    interface DataChangeListener {
        fun onChange(dataCount: Int)
    }

    fun addItem(item: Item) {
        items.add(item)
        refresh()
    }

}
