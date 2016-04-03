package me.stepy.app.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import me.stepy.app.R
import me.stepy.app.db.repository.TodoGroupRepository
import java.util.*

class TodoGroupLinearLayout : LinearLayout {

    private var groupNames: ArrayList<String> = TodoGroupRepository.selectAllNames()
    //private var groupNames: ArrayList<String> = arrayListOf()
    private val inflater: LayoutInflater
    private val groupSpinner: GroupSpinner

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        inflater = LayoutInflater.from(context)

        val groupAdapter = ArrayAdapter<String>(context, R.layout.parts_groups_spinner, groupNames)
        groupSpinner = GroupSpinner(context).apply {
            adapter = groupAdapter
        }
        addView(groupSpinner, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

    }

    fun addNewGroup() {
        val newGroup = inflater.inflate(R.layout.parts_new_parent_text, this)
    }

    public fun getName(): String {
        if (childCount == 2) return (findViewById(R.id.new_group_name) as EditText).text.toString()

        //        for (i in 0..viewGroupContainer.childCount) {
        //            val childView = viewGroupContainer.getChildAt(i)
        //            if ((childView.findViewById(R.id.checkbox) as CheckBox).isChecked) {
        //                return (childView.findViewById(R.id.text) as TextView).text.toString()
        //            }
        //        }

        return groupSpinner.selectedItem?.toString() ?: ""
    }

    inner class GroupSpinner : Spinner {

        constructor(context: Context) : super(context)

        constructor(context: Context, mode: Int) : super(context, mode)

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

        constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


        init {
            val newGroups = EditText(context)
            newGroups.setText(R.string.create_group)
            //            this.addView(newGroups)
        }
    }

}
