package me.stepy.app.recyclerView

import android.app.Activity
import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.afollestad.materialdialogs.MaterialDialog
import me.stepy.app.db.realmObj.Group
import me.stepy.app.db.realmObj.Item
import me.stepy.app.db.repository.GroupRepo
import me.stepy.app.db.repository.ItemRepo
import me.stepy.app.util.tracking.GATracker

import me.stepy.app.util.tracking.GATracker.Companion.SCREEN
import me.stepy.app.util.tracking.GATracker.Companion.CATEGORY
import me.stepy.app.util.tracking.GATracker.Companion.ACTION
import me.stepy.app.util.tracking.GATracker.Companion.LABEL

class ItemOnTouchCallback(val activtiy: Activity) : ItemTouchHelper.Callback() {

    var onSwipeListener: OnSwipeListener? = null

    interface OnSwipeListener {
        fun onSwiped(view: RecyclerView.ViewHolder, direction: Int)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val type = viewHolder.itemViewType
        if (type == GroupAdapter.VIEW_CREATE_GROUP) {
            return makeMovementFlags(0, 0)
        }

        return makeMovementFlags(0, ItemTouchHelper.END)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val type = viewHolder.itemViewType

        if (direction == ItemTouchHelper.END) {
            viewHolder.itemView?.tag ?: return

            var id = viewHolder.itemView.tag
            if (id !is String) return

            if (type == GroupAdapter.VIEW_TYPE_CHILD) {
                // update database
                ItemRepo.updateStatus(id, Item.STATUS_DONE)
                onSwipeListener?.onSwiped(viewHolder, direction)
            } else if (type == GroupAdapter.VIEW_TYPE_LIST) {
                MaterialDialog.Builder(activtiy).run {
                    title("グループを削除してよいですか？")
                    autoDismiss(true)
                    negativeText("cancel")
                    onNegative { materialDialog, dialogAction ->
                        onSwipeListener?.onSwiped(viewHolder, direction)
                        GATracker.event(CATEGORY.EDIT_GROUP_NAME.to, ACTION.MODAL_CANCEL.to, LABEL.HOME.to, 1)
                    }
                    positiveText("ok")
                    onPositive { materialDialog, dialogAction ->
                        GroupRepo.updateStatus(id, Group.STATUS_DONE)
                        onSwipeListener?.onSwiped(viewHolder, direction)
                        GATracker.event(CATEGORY.EDIT_GROUP_NAME.to, ACTION.MODAL_OK.to, LABEL.HOME.to, 1)
                    }
                    showListener { GATracker.event(CATEGORY.EDIT_GROUP_NAME.to, ACTION.MODAL_SHOW.to, LABEL.HOME.to, 1) }
                    show()
                }
            }
        }

    }

    override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (viewHolder != null) {
            val type = viewHolder.itemViewType
            if (type == GroupAdapter.VIEW_TYPE_CHILD || type == GroupAdapter.VIEW_TYPE_LIST) {
                if (viewHolder is GroupAdapter.RecyclerViewHolder) {
                    viewHolder.content!!.translationY = dY
                    viewHolder.content!!.translationX = dX
                    return
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}