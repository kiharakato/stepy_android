package me.stepy.app.util.tracking

import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import me.stepy.app.R
import me.stepy.app.App

class GATracker {

    companion object {
        private var tracker: Tracker? = null

        fun getDefaultTracker(): Tracker {
            return tracker ?: run {
                val analytics = GoogleAnalytics.getInstance(App.getInstance())
                tracker = analytics.newTracker(R.xml.global_tracker)
                return tracker as Tracker
            }
        }

        fun event(category: String, action: String = "", label: String = "", value: Long = -1) {
            val builder = HitBuilders.EventBuilder().setCategory(category)
            if (!action.isEmpty()) builder.setAction(action)
            if (!label.isEmpty()) builder.setLabel(label)
            if (value.compareTo(-1) != 0) builder.setValue(value)

            getDefaultTracker().send(builder.build())
        }

        fun screen(screen: String) {
            getDefaultTracker().setScreenName(screen)
            getDefaultTracker().send(HitBuilders.ScreenViewBuilder().build())
        }

        enum class SCREEN(val to: String) {
            HOME("Home"),
            GROUP("Group"),
        }

        enum class CATEGORY(val to: String) {
            EDIT_GROUP_NAME("EditGroupName"),
            EDIT_ITEM("EditItem"),
            DELETE_GROUP("DeleteGroup"),
            LIST("List"),
            CREATE_ITEM("CreateList"),
        }

        enum class ACTION(val to: String) {
            MODAL_SHOW("ModalShow"),
            MODAL_OK("ModalOk"),
            MODAL_CANCEL("ModalCancel"),
            REFRESH("Refresh"),
            CLICK("Click"),
            SWIPED("Swiped"),
        }

        enum class LABEL(val to: String) {
            TITLE("Title"),
            HOME("Home"),
            GROUP("Group"),
            ITEM("Item"),
        }

    }

}
