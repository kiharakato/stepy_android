package me.stepy.app.fragment

import android.accounts.AuthenticatorDescription
import android.os.Bundle
import android.support.v4.app.Fragment

class GroupEditFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.getLong(ARGUMENT_GROUP_ID) ?: ""
    }

    companion object {
        const val ARGUMENT_GROUP_ID = "groupId"

        fun create(groupId: Long): GroupEditFragment {
            val bundle = Bundle().apply {
                putLong(ARGUMENT_GROUP_ID, groupId)
            }

            return GroupEditFragment().apply { arguments = bundle }
        }
    }
}
