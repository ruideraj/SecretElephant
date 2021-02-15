package com.ruideraj.secretelephant.contacts

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ruideraj.secretelephant.R
import com.ruideraj.secretelephant.ViewModelFactory

class ContactsPermissionRationaleDialog : DialogFragment() {

    private val viewModel by activityViewModels<ContactsViewModel> { ViewModelFactory(requireActivity()) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().let {
            AlertDialog.Builder(it)
                    .setTitle(R.string.contact_permission_dialog_title)
                    .setMessage(R.string.contact_permission_message)
                    .setPositiveButton(R.string.action_continue) { _, _ ->
                        viewModel.onPermissionRationaleResult(true)
                    }.setNegativeButton(R.string.action_cancel) { _, _ ->
                        viewModel.onPermissionRationaleResult(false)
                    }.create()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        viewModel.onPermissionRationaleResult(false)
    }

}