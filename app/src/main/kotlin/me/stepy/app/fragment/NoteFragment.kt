package me.stepy.app.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.bindView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_group.*
import kotlinx.android.synthetic.main.parts_create_item_box.*
import me.stepy.app.App
import me.stepy.app.R
import me.stepy.app.model.Item
import me.stepy.app.recyclerView.DividerItemDecoration
import me.stepy.app.recyclerView.ItemOnTouchCallback
import kotlin.properties.Delegates

class NoteFragment : BaseFragment() {

    private val createTodoContainer: LinearLayout by bindView(R.id.createTodoContainer)
    private val createTodo: EditText by bindView(R.id.createTodo)
    private var noteKey: String? = null
    private var itemAdapter: FirebaseRecyclerAdapter<Item, ItemViewHolder> by Delegates.notNull()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_group, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()

        createTodoBtn.apply {
            setOnClickListener { v ->
                val text = createTodo.text ?: ""
                if (text.isEmpty()) return@setOnClickListener
                App.hideKeyboard(activity)
                createTodo.setText("")
            }
        }

        FirebaseAuth.getInstance().currentUser?.uid.let {
            val itemRef = FirebaseDatabase.getInstance().reference.child("items").child(it)
            itemAdapter = object : FirebaseRecyclerAdapter<Item, ItemViewHolder>(
                    Item::class.java, R.layout.row_todo, ItemViewHolder::class.java, itemRef) {
                override fun populateViewHolder(viewHolder: ItemViewHolder?, model: Item?, position: Int) {
                    viewHolder?.bind(model, position)
                }
            }

            appearAppBar()
            appearItemList()
            appearCreateItem()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        activity.menuInflater.inflate(R.menu.menu_main, menu)
    }

    private fun appearCreateItem() {
        val toX = createTodoContainer.x
        val transAnim = ObjectAnimator.ofFloat(createTodoContainer, "translationX", toX, 0F)
        transAnim.duration = 400
        transAnim.start()
        createTodo.isFocusable = true
    }

    private fun appearItemList() {
        recyclerView.apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(activity))
        }
        val callback = ItemOnTouchCallback(activity)
        callback.onSwipeListener = object : ItemOnTouchCallback.OnSwipeListener {
            override fun onSwiped(view: RecyclerView.ViewHolder, direction: Int) {
                itemAdapter.cleanup()
            }
        }
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(recyclerView)
        recyclerView.addItemDecoration(helper)
    }

    private fun appearAppBar() {
        val transAnim = ObjectAnimator.ofFloat(appBar, "translationY", -200F, 0F)
        transAnim.duration = 400

        val fadeAnim = ObjectAnimator.ofFloat(appBar, "alpha", 0F, 1F)
        fadeAnim.duration = 400

        AnimatorSet().run {
            playTogether(listOf(transAnim, fadeAnim))
            start()
        }
    }

    companion object {
        fun buildArgument(key: String): Bundle? {
            return Bundle().apply {
                putString("key", key)
            }
        }
    }

    private fun setToolbar() {
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolBar.setNavigationOnClickListener { activity.supportFragmentManager.popBackStack() }
        toolBar.inflateMenu(R.menu.menu_main)
        // val menuTitle = if (baseAc.isLogin) "公開する" else "公開をやめる"
        // toolBar.menu.add(menuTitle)
        toolBar.setOnMenuItemClickListener { item ->
            if (item.title == "公開する") {
                // todo update database
            } else if (item.title == "公開をやめる") {
                // todo remove database
            }
            return@setOnMenuItemClickListener true
        }
    }

    class ItemViewHolder(val box: View) : RecyclerView.ViewHolder(box) {
        var actionView: TextView? = null
        var createdAtView: TextView? = null

        init {
            actionView = box.findViewById(R.id.action) as TextView
            createdAtView = box.findViewById(R.id.createdAt) as TextView
        }

        fun bind(item: Item?, position: Int) {
            item ?: return
            actionView?.text = item.action
            createdAtView?.setText(0)

        }

    }

}
