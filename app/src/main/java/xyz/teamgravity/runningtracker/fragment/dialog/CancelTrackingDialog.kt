package xyz.teamgravity.runningtracker.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.teamgravity.runningtracker.R

class CancelTrackingDialog : DialogFragment() {

    var listener: OnCancelTrackingListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(R.string.cancel_run)
            .setMessage(R.string.wanna_cancel_run)
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(R.string.yes) { _, _ ->
                listener?.onCancelTrackingPositiveClick()
            }.setNegativeButton(R.string.no) { _, _ ->

            }.create()
    }

    interface OnCancelTrackingListener {
        fun onCancelTrackingPositiveClick()
    }
}