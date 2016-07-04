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
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.main_flagment.*
import me.stepy.app.R
import me.stepy.app.StepyApplication
import me.stepy.app.activity.MainActivity
import me.stepy.app.db.realmObj.Item
import me.stepy.app.recyclerView.DividerItemDecoration
import me.stepy.app.recyclerView.ItemOnTouchCallback
import me.stepy.app.recyclerView.RecyclerAdapter
import me.stepy.app.util.tracking.GATracker
import me.stepy.app.util.tracking.GATracker.Companion.ACTION
import me.stepy.app.util.tracking.GATracker.Companion.CATEGORY
import me.stepy.app.util.tracking.GATracker.Companion.LABEL
import me.stepy.app.util.tracking.GATracker.Companion.SCREEN
import kotlin.properties.Delegates

class MainFragment : Fragment() {

    private var mAdapter: RecyclerAdapter by Delegates.notNull()
    private val mRecyclerView: RecyclerView by bindView(R.id.recycler_view)
    private val RecyclerViewRefresh: SwipeRefreshLayout by bindView(R.id.recycler_view_refresh)
    private var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val realmConfiguration = RealmConfiguration.Builder(activity).build()
        val _realm = Realm.getInstance(realmConfiguration)
        mAdapter = RecyclerAdapter(_realm)
        realm = _realm
        GATracker.screen(SCREEN.HOME.to)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.main_flagment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setOnKeyEvent()

        tool_bar.apply {
            title = "Stepy"
            setTitleTextColor(ContextCompat.getColor(activity, R.color.white))
        }

        // なぜこの書き方でないとダメなのかが謎？
        // 以下のように書きたい。
        // mAdapter.onDataChangeListener = RecyclerAdapter.DataChangeListener { dataCount -> // doing}
        mAdapter.onDataChangeListener = object : RecyclerAdapter.DataChangeListener {
            override fun onChange(dataCount: Int) {
                RecyclerViewRefresh.isEnabled = 0 < dataCount
            }
        }
        mAdapter.onCreateGroupClickListener = View.OnClickListener {
            (activity as? MainActivity)?.applyCreateGroupFragment()
        }
        mAdapter.onGroupClickListener = View.OnClickListener { view ->
            val tag = view.tag
            if (tag is String) {
                (activity as? MainActivity)?.applyGroupEditFragment(tag)
            }
        }
        fab_create_group.setOnClickListener {
            (activity as? MainActivity)?.applyCreateGroupFragment()
            fab_menu.toggle()
        }
        //fab_search.setOnClickListener { (activity as? MainActivity)?.applyCreateGroupFragment() }

        mAdapter.onItemClickListener = View.OnClickListener { view ->
            val tag = view.tag
            if (tag !is String) return@OnClickListener
            val _realm = realm ?: return@OnClickListener
            _realm.beginTransaction()
            val item = _realm.where(Item::class.java).equalTo("id", tag).findFirst()
            _realm.commitTransaction()

            //            EventTracker.getDefaultTracker().send(HitBuilders.EventBuilder().setAction("onClick").setCategory("itemEdit").setLabel(item.action).build())
            val modal = LayoutInflater.from(activity).inflate(R.layout.module_create_group_dialog, null)
            val inputGroupName = modal.findViewById(R.id.inputGroupName) as EditText
            inputGroupName.setText(item.action)
            inputGroupName.setSelection(item.action.length)
            MaterialDialog.Builder(activity).run {
                title("Edit Item")
                customView(modal, false)
                autoDismiss(true)
                negativeText("cancel")
                onNegative { materialDialog, dialogAction ->
                    GATracker.event(CATEGORY.EDIT_ITEM.to, ACTION.MODAL_CANCEL.to, LABEL.TITLE.to, 1)
                }
                positiveText("ok")
                onPositive { materialDialog, dialogAction ->
                    _realm.beginTransaction()
                    val item = _realm.where(Item::class.java).equalTo("id", tag).findFirst()
                    item.action = inputGroupName.text.toString()
                    _realm.commitTransaction()
                    mAdapter.refresh()
                    GATracker.event(CATEGORY.EDIT_ITEM.to, ACTION.MODAL_OK.to, LABEL.TITLE.to, 1)
                }
                showListener {
                    StepyApplication.showKeyboard(inputGroupName, activity)
                    GATracker.event(CATEGORY.EDIT_ITEM.to, ACTION.MODAL_SHOW.to, LABEL.TITLE.to, 1)
                }
                show()
            }
        }

        mRecyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(activity))
        }

        RecyclerViewRefresh.setOnRefreshListener {
            mAdapter.refresh()
            RecyclerViewRefresh.isRefreshing = false
        }

        val refreshParams = RecyclerViewRefresh.layoutParams as CoordinatorLayout.LayoutParams
        refreshParams.behavior = CustomScrollingViewBehavior()
        RecyclerViewRefresh.layoutParams = refreshParams

        val callback = ItemOnTouchCallback(activity)
        callback.onSwipeListener = object : ItemOnTouchCallback.OnSwipeListener {
            override fun onSwiped(view: RecyclerView.ViewHolder, direction: Int) {
                mAdapter.refresh()
                if (view.itemViewType == RecyclerAdapter.VIEW_TYPE_CHILD) {
                    GATracker.event(CATEGORY.LIST.to, ACTION.SWIPED.to, LABEL.ITEM.to, 1)
                } else if (view.itemViewType == RecyclerAdapter.VIEW_TYPE_LIST) {
                    GATracker.event(CATEGORY.LIST.to, ACTION.SWIPED.to, LABEL.GROUP.to, 1)
                }
            }
        }
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(mRecyclerView)
        mRecyclerView.addItemDecoration(helper)

        app_bar.addOnOffsetChangedListener { appBarLayout, mode ->
            RecyclerViewRefresh.isEnabled = if (mode == 0) true else false
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

    fun onFragmentResume() {
        mAdapter.refresh()
    }

    override fun onResume() {
        super.onResume()
        mAdapter.refresh()
    }

    private fun setOnKeyEvent() {
        view?.setOnKeyListener({ view, keyCode, keyEvent ->
            if (keyEvent.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
            return@setOnKeyListener when (keyCode) {
                KeyEvent.KEYCODE_BACK -> if (mAdapter.isCreateItem) true else false
                else -> false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRecyclerView.adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (realm?.isClosed ?: false) realm?.close()
    }
}
