package me.stepy.app.fragment

import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.AttributeSet
import android.view.*
import android.widget.EditText
import android.widget.TextView
import butterknife.bindView
import com.afollestad.materialdialogs.MaterialDialog
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.main_flagment.*
import me.stepy.app.App
import me.stepy.app.R
import me.stepy.app.model.Note
import me.stepy.app.recyclerView.DividerItemDecoration
import me.stepy.app.recyclerView.ItemOnTouchCallback
import me.stepy.app.util.SLog
import kotlin.properties.Delegates

class MainFragment : BaseFragment() {

    private var noteAdapter: FirebaseRecyclerAdapter<Note, NoteViewHolder> by Delegates.notNull()
    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    private val refreshView: SwipeRefreshLayout by bindView(R.id.recycler_view_refresh)
    private var userRef: DatabaseReference by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userKey = FirebaseAuth.getInstance().currentUser?.uid ?: return
        userRef = FirebaseDatabase.getInstance().reference.child("notes").child(userKey)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.main_flagment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setOnKeyEvent()

        toolBar.apply {
            title = "Stepy"
            setTitleTextColor(App.getColor(R.color.white))
        }

        noteAdapter = object : FirebaseRecyclerAdapter<Note, NoteViewHolder>(
                Note::class.java, R.layout.row_group_title, NoteViewHolder::class.java, userRef) {
            override fun populateViewHolder(viewHolder: NoteViewHolder?, model: Note?, position: Int) {
                viewHolder?.bind(model, position)
            }
        }

        fabMenu.let {
            it.setOnClickListener { appearCreateNoteDialog() }
            it.setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN ->
                        view.setBackgroundResource(R.color.cyan_500)
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP ->
                        view.setBackgroundResource(R.color.cyan_400)
                }
                return@setOnTouchListener false
            }
        }

        recyclerView.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(activity))
        }

        refreshView.let {
            it.setOnRefreshListener {
                noteAdapter.cleanup()
                it.isRefreshing = false
            }
            (it.layoutParams as CoordinatorLayout.LayoutParams).let { param ->
                param.behavior = CustomScrollingViewBehavior()
                it.layoutParams = param
            }
        }

        ItemOnTouchCallback(activity).let {
            it.onSwipeListener = object : ItemOnTouchCallback.OnSwipeListener {
                override fun onSwiped(view: RecyclerView.ViewHolder, direction: Int) {
                    noteAdapter.cleanup()
                }
            }
            ItemTouchHelper(it).let {
                it.attachToRecyclerView(recyclerView)
                recyclerView.addItemDecoration(it)
            }
        }

        app_bar.addOnOffsetChangedListener { appBarLayout, mode ->
            refreshView.isEnabled = if (mode == 0) true else false
        }

    }

    class CustomScrollingViewBehavior : AppBarLayout.ScrollingViewBehavior {
        constructor() : super()

        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

        override fun layoutDependsOn(parent: CoordinatorLayout?, child: View?, dependency: View?): Boolean {
            val isAppBarLayout = super.layoutDependsOn(parent, child, dependency)
            return isAppBarLayout
        }

        override fun onDependentViewChanged(parent: CoordinatorLayout?, child: View?, dependency: View?): Boolean {
            if (child == null || dependency == null) return super.onDependentViewChanged(parent, child, dependency)
            return super.onDependentViewChanged(parent, child, dependency)
        }
    }

    private fun setOnKeyEvent() {
        view?.setOnKeyListener({ view, keyCode, keyEvent ->
            if (keyEvent.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
            return@setOnKeyListener false
        })
    }

    private fun appearCreateNoteDialog() {
        val modal = LayoutInflater.from(activity).inflate(R.layout.module_create_group_dialog, null)
        val inputGroupName = modal.findViewById(R.id.inputGroupName) as EditText
        MaterialDialog.Builder(activity).run {
            title("Create New Note")
            customView(modal, false)
            autoDismiss(true)
            dismissListener { _activity?.let { App.hideKeyboard(it) } }
            negativeText("cancel")
            positiveText("ok")
            onPositive { dialog, action -> createNote(inputGroupName.text.toString()) }
            show()
        }
    }

    private fun createNote(name: String) {
        userRef.push().apply {
            setValue(Note(name))
            _activity?.openAsRootForFragment(NoteFragment::class.java, NoteFragment.buildArgument(key))
        }
    }

    class NoteViewHolder(val box: View) : RecyclerView.ViewHolder(box) {
        var listName: TextView? = null
        var listChildCount: TextView? = null

        init {
            listName = box.findViewById(R.id.bar_title) as TextView
            listChildCount = box.findViewById(R.id.number_of_todo) as TextView
        }

        fun bind(note: Note?, position: Int) {
            note ?: return
            listName?.text = note.name
            listChildCount?.text = "0"

        }

    }
}
