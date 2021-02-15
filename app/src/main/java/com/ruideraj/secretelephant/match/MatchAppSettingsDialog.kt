package com.ruideraj.secretelephant.match

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ruideraj.secretelephant.R

class MatchAppSettingsDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().let {
            AlertDialog.Builder(it)
                    .setMessage(R.string.match_settings_text)
                    .setPositiveButton(R.string.action_settings) { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", it.packageName, null)).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(intent)
                    }.setNegativeButton(R.string.action_cancel) { _, _ ->
                        // Do nothing other than close the dialog.
                    }.create()
        }
    }

}