package me.stepy.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import me.stepy.app.R

class TodoGroupListAdapter(context: Context, resource: Int) : ArrayAdapter<String>(context, resource) {

    val layoutInflate: LayoutInflater by lazy { LayoutInflater.from(context) }
    public var checkPosition: Int = -1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var _view: View
        if (convertView == null) {
            _view = layoutInflate.inflate(R.layout.parts_list_check_text, parent, false)
        } else {
            _view = convertView
        }

        (_view.findViewById(R.id.text) as TextView).text = getItem(position)
        val checkBox = (_view.findViewById(R.id.checkbox) as CheckBox).apply {
            setOnCheckedChangeListener { button, check ->
                if (check) checkPosition = position
            }
        }
        if (checkPosition == position) {
            checkBox.isChecked = true
        }

        return _view
    }

}