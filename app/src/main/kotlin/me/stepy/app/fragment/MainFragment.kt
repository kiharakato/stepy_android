package me.stepy.app.fragment

import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.bindView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.realm.Realm
import kotlinx.android.synthetic.main.main_flagment.*
import me.stepy.app.App
import me.stepy.app.R
import me.stepy.app.activity.MainActivity
import me.stepy.app.recyclerView.DividerItemDecoration
import me.stepy.app.recyclerView.ItemOnTouchCallback
import me.stepy.app.recyclerView.ListAdapter
import me.stepy.app.util.tracking.GATracker
import me.stepy.app.util.tracking.GATracker.Companion.ACTION
import me.stepy.app.util.tracking.GATracker.Companion.CATEGORY
import me.stepy.app.util.tracking.GATracker.Companion.LABEL
import kotlin.properties.Delegates

class MainFragment : Fragment() {

    private var listAdapter: ListAdapter by Delegates.notNull()
    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    private val refreshView: SwipeRefreshLayout by bindView(R.id.recycler_view_refresh)
    private var realm: Realm by Delegates.notNull<Realm>()
    private var userRef: DatabaseReference by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // realm = Realm.getDefaultInstance()
        listAdapter = ListAdapter(realm)

        val userKey = App.auth.currentUser.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        userRef = rootRef.child("notes").child(userKey)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.main_flagment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setOnKeyEvent()

        toolBar.apply {
            title = "Stepy"
            setTitleTextColor(ContextCompat.getColor(activity, R.color.white))
        }

        listAdapter.let {
            it.onDataChangeListener = { dataCount ->
                refreshView.isEnabled = 0 < dataCount
            }
            it.onCreateGroupClickListener = View.OnClickListener {
                // (activity as? MainActivity)?.applyCreateGroupFragment()
            }
            it.onGroupClickListener = View.OnClickListener { view ->
                if (view.tag is String) {
                    // (activity as? MainActivity)?.applyGroupEditFragment(tag)
                }
            }
            /***
             * 今回は ListとItemを混合にしないのでコメントアウト。
             * TODO: コミット前に削除
            it.onItemClickListener = View.OnClickListener { view ->
                if (view.tag !is String) return@OnClickListener

                // val _realm = realm
                // _realm.beginTransaction()
                // val item = _realm.where(Item::class.java).equalTo("id", tag).findFirst()
                // _realm.commitTransaction()

                val modal = LayoutInflater.from(activity).inflate(R.layout.module_create_group_dialog, null)
                val inputGroupName = modal.findViewById(R.id.inputGroupName) as EditText

                inputGroupName.setText(item.action)
                inputGroupName.setSelection(item.action.length)

                MaterialDialog.Builder(activity).run {
                    title("Edit Item")
                    customView(modal, false)
                    autoDismiss(true)
                    negativeText("cancel")
                    positiveText("ok")
                    onPositive { materialDialog, dialogAction ->
                        // _realm.beginTransaction()
                        // val item = _realm.where(Item::class.java).equalTo("id", tag).findFirst()
                        // item.action = inputGroupName.text.toString()
                        // _realm.commitTransaction()

                        listAdapter.refresh()
                    }
                    showListener {
                        App.showKeyboard(inputGroupName, activity)
                    }
                    show()
                }
            }
            ***/
        }

        fabCreateGroup.setOnClickListener {
            fab_menu.toggle()
            // TODO: Create Note
        }

        recyclerView.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(activity))
        }

        refreshView.setOnRefreshListener {
            listAdapter.refresh()
            refreshView.isRefreshing = false
        }

        val refreshParams = refreshView.layoutParams as CoordinatorLayout.LayoutParams
        refreshParams.behavior = CustomScrollingViewBehavior()
        refreshView.layoutParams = refreshParams

        val callback = ItemOnTouchCallback(activity)
        callback.onSwipeListener = object : ItemOnTouchCallback.OnSwipeListener {
            override fun onSwiped(view: RecyclerView.ViewHolder, direction: Int) {
                listAdapter.refresh()
                if (view.itemViewType == ListAdapter.VIEW_TYPE_CHILD) {
                    GATracker.event(CATEGORY.LIST.to, ACTION.SWIPED.to, LABEL.ITEM.to, 1)
                } else if (view.itemViewType == ListAdapter.VIEW_TYPE_LIST) {
                    GATracker.event(CATEGORY.LIST.to, ACTION.SWIPED.to, LABEL.GROUP.to, 1)
                }
            }
        }
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(recyclerView)
        recyclerView.addItemDecoration(helper)

        app_bar.addOnOffsetChangedListener { appBarLayout, mode ->
            refreshView.isEnabled = if (mode == 0) true else false
            GATracker.event(CATEGORY.LIST.to, ACTION.REFRESH.to, LABEL.HOME.to, 1)
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

    override fun onResume() {
        super.onResume()
        listAdapter.refresh()
    }

    private fun setOnKeyEvent() {
        view?.setOnKeyListener({ view, keyCode, keyEvent ->
            if (keyEvent.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
            return@setOnKeyListener when (keyCode) {
                KeyEvent.KEYCODE_BACK -> if (listAdapter.isCreateItem) true else false
                else -> false
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (realm.isClosed) realm.close()
    }
}
