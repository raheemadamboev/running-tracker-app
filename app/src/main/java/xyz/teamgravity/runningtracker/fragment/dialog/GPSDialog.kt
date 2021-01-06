package xyz.teamgravity.runningtracker.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.teamgravity.runningtracker.R

class GPSDialog: DialogFragment() {

    var listener: OnGPSListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(resources.getString(R.string.gps_title))
            .setMessage(resources.getString(R.string.gps_message))
            .setIcon(R.drawable.ic_gps)
            .setPositiveButton(resources.getString(R.string.enable)) { _, _ ->
                listener?.onGPSPositiveClick()
            }.setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

            }.create()
    }

    interface OnGPSListener {
        fun onGPSPositiveClick()
    }
}