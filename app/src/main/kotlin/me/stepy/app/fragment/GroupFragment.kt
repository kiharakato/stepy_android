package me.stepy.app.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import butterknife.bindView
import com.afollestad.materialdialogs.MaterialDialog
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.fragment_group.*
import kotlinx.android.synthetic.main.parts_create_item_box.*
import me.stepy.app.R
import me.stepy.app.StepyApplication
import me.stepy.app.activity.MainActivity
import me.stepy.app.db.realmObj.Group
import me.stepy.app.db.repository.ItemRepo
import me.stepy.app.recyclerView.DividerItemDecoration
import me.stepy.app.recyclerView.GroupAdapter
import me.stepy.app.recyclerView.ItemOnTouchCallback
import me.stepy.app.util.tracking.GATracker
import me.stepy.app.util.tracking.GATracker.Companion.ACTION
import me.stepy.app.util.tracking.GATracker.Companion.CATEGORY
import me.stepy.app.util.tracking.GATracker.Companion.LABEL
import me.stepy.app.util.tracking.GATracker.Companion.SCREEN
import kotlin.properties.Delegates

class GroupFragment : Fragment() {

    private var title = ""
    private val createTodoContainer: LinearLayout by bindView(R.id.createTodoContainer)
    private val createTodo: EditText by bindView(R.id.createTodo)
    private var groupId: String? = null
    private var realm: Realm by Delegates.notNull()
    private var mAdapter: GroupAdapter? = null
    private val baseAc: MainActivity
        get() = activity as MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val realmConfiguration = RealmConfiguration.Builder(activity).build()
        realm = Realm.getInstance(realmConfiguration)

        GATracker.screen(SCREEN.GROUP.to)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_group, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        appBarCollapsing.setOnClickListener { view ->
            val modal = LayoutInflater.from(activity).inflate(R.layout.module_create_group_dialog, null)
            val inputGroupName = modal.findViewById(R.id.inputGroupName) as EditText
            inputGroupName.setText(appBarCollapsing.title)
            inputGroupName.setSelection(appBarCollapsing.title.toString().length)
            MaterialDialog.Builder(activity).run {
                title("Edit Group Name.")
                customView(modal, false)
                autoDismiss(true)
                negativeText("cancel")
                onNegative { materialDialog, dialogAction ->
                    GATracker.event(CATEGORY.EDIT_GROUP_NAME.to, ACTION.MODAL_CANCEL.to, LABEL.TITLE.to, 1)
                }
                positiveText("ok")
                onPositive { materialDialog, dialogAction ->
                    realm.beginTransaction()
                    val group = realm.where(Group::class.java).equalTo("id", groupId).findFirst()
                    group.name = inputGroupName.text.toString()
                    realm.commitTransaction()
                    appBarCollapsing.title = group.name
                    GATracker.event(CATEGORY.EDIT_GROUP_NAME.to, ACTION.MODAL_OK.to, LABEL.TITLE.to, 1)
                }
                showListener {
                    StepyApplication.showKeyboard(inputGroupName, activity)
                    GATracker.event(CATEGORY.EDIT_GROUP_NAME.to, ACTION.MODAL_SHOW.to, LABEL.TITLE.to, 1)
                }
                show()
            }
        }
        createTodoBtn.apply {
            setOnClickListener { v ->
                mAdapter ?: return@setOnClickListener
                val text = createTodo.text ?: ""
                if (text.isEmpty()) return@setOnClickListener
                val item = ItemRepo.create(text.toString(), groupId)
                mAdapter?.addItem(item)
                StepyApplication.hideKeyboard(activity)
                createTodo.setText("")
                GATracker.event(CATEGORY.CREATE_ITEM.to, ACTION.CLICK.to, LABEL.GROUP.to, 1)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        activity.menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onResume() {
        super.onResume()
        disappearContent()

        val mode = arguments.getInt(ARGUMENT_MODE)

        if (mode == MODE_NEW) {
            appearCreateDialog()
        } else if (mode == MODE_OPEN) {
            groupId = arguments.getString(ARGUMENT_GROUP_ID)
            realm.beginTransaction()
            val group = realm.where(Group::class.java).equalTo("id", groupId).findFirst()
            realm.commitTransaction()
            title = group.name
            appBarCollapsing.title = title
            appBarCollapsing.setCollapsedTitleTextColor(Color.WHITE)
            appBarCollapsing.setExpandedTitleColor(Color.WHITE)
            appearContent()
        }
    }

    private fun disappearContent() {
        appBar.apply {
            clearAnimation()
            alpha = 0F
        }

        createTodoContainer.apply {
            clearAnimation()
            translationX = StepyApplication.getDisplayRealSize(activity).x.toFloat()
        }
    }

    private fun appearCreateDialog() {
        val modal = LayoutInflater.from(activity).inflate(R.layout.module_create_group_dialog, null)
        val inputGroupName = modal.findViewById(R.id.inputGroupName) as EditText
        MaterialDialog.Builder(activity).run {
            title("Create New Group")
            customView(modal, false)
            negativeText("cancel")
            onNegative { dialog, action ->
                StepyApplication.hideKeyboard(activity)
                activity.supportFragmentManager.popBackStack()
            }
            positiveText("ok")
            autoDismiss(true)
            showListener {
                StepyApplication.showKeyboard(inputGroupName, activity)
            }
            onPositive { dialog, action ->
                createNewGroup(inputGroupName.text.toString())
                StepyApplication.hideKeyboard(activity)
            }
            show()
        }
    }

    private fun createNewGroup(title: String) {
        this.title = title
        appBarCollapsing.title = title
        appBarCollapsing.setCollapsedTitleTextColor(Color.WHITE)
        appBarCollapsing.setExpandedTitleColor(Color.WHITE)

        val group = Group().apply { name = title }
        groupId = group.id
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(group)
        realm.commitTransaction()

        appearContent()
    }

    private fun appearContent() {
        appearAppBar()
        appearItemList()
        appearCreateItem()
    }

    private fun appearCreateItem() {
        val toX = createTodoContainer.x
        val transAnim = ObjectAnimator.ofFloat(createTodoContainer, "translationX", toX, 0F)
        transAnim.duration = 400
        transAnim.start()
        createTodo.isFocusable = true
    }

    private fun appearItemList() {
        mAdapter = GroupAdapter(groupId!!, realm)
        recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(activity))
        }
        val callback = ItemOnTouchCallback(activity)
        callback.onSwipeListener = object : ItemOnTouchCallback.OnSwipeListener {
            override fun onSwiped(view: RecyclerView.ViewHolder, direction: Int) {
                mAdapter?.refresh()
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

        const val TAG = "GropuFragment"

        const val ARGUMENT_MODE = "startup"
        const val MODE_NEW = 0
        const val MODE_OPEN = 1

        const val ARGUMENT_GROUP_ID = "title"

        fun createNewGroupFragment(): GroupFragment {
            val bundle = Bundle().apply {
                putInt(ARGUMENT_MODE, MODE_NEW)
            }
            val fragment = GroupFragment().apply {
                arguments = bundle
            }
            return fragment
        }

        fun createOpenGroupFragment(groupId: String): GroupFragment {
            val bundle = Bundle().apply {
                putInt(ARGUMENT_MODE, MODE_OPEN)
                putString(ARGUMENT_GROUP_ID, groupId)
            }
            return GroupFragment().apply { arguments = bundle }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (realm.isClosed) realm.close()
    }

    private fun setToolbar() {
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolBar.setNavigationOnClickListener { activity.supportFragmentManager.popBackStack() }
        toolBar.inflateMenu(R.menu.menu_main)
        val menuTitle = if (baseAc.isLogin) "公開する" else "公開をやめる"
        toolBar.menu.add(menuTitle)
        toolBar.setOnMenuItemClickListener { item ->
            if (item.title == "公開する") {
                // todo update database
            } else if (item.title == "公開をやめる") {
                // todo remove database
            }
            return@setOnMenuItemClickListener true
        }
    }
}
