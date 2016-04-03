package me.stepy.app.fragment

import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.EditText
import android.widget.ImageView
import butterknife.bindView
import me.stepy.app.R
import me.stepy.app.db.repository.TodoGroupRepository
import me.stepy.app.db.repository.TodoRepository
import me.stepy.app.view.TodoGroupLinearLayout

class CreateTodoFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private val parentContainer: TodoGroupLinearLayout by bindView(R.id.parent_container)
    private val createParentBtn: View by bindView(R.id.create_parent_btn)
    private val createBtn: View by bindView(R.id.create_btn)
    private val createTodoTextView: EditText by bindView(R.id.create_todo)
    private val dismissBtn: ImageView by bindView(R.id.dismiss_btn)
    private val errorMessage: ViewStub by bindView(R.id.error_message)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_create_todo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        createParentBtn.setOnClickListener {
            parentContainer.addNewGroup()
        }

        createBtn.setOnClickListener {
            if (TextUtils.isEmpty(createTodoTextView.text)) {
                errorMessage.inflate()
            } else {
                val todoGroup = TodoGroupRepository.selectUpdateOneByName(parentContainer.getName())
                val todo = TodoRepository.create(createTodoTextView.text.toString(), todoGroup?.id)
                //(activity as MainActivity).addTodo(todo)

            }
            fragmentManager.beginTransaction().remove(this).commit()
        }

        dismissBtn.setOnClickListener {
            fragmentManager.beginTransaction().remove(this).commit()
        }
    }

    companion object {
        fun newInstance(): CreateTodoFragment {
            val fragment = CreateTodoFragment()
            return fragment
        }
    }

}
