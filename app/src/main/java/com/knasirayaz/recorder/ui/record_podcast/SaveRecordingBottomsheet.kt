package com.knasirayaz.recorder.ui.record_podcast

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.knasirayaz.recorder.R
import com.knasirayaz.recorder.utils.snackbar
import java.io.File


class SaveRecordingBottomsheet(var callback: (fileName : String) -> Unit) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, R.layout.bottom_sheet_save_recording, null)
        dialog.setContentView(view)
        val fileNameEt = dialog.findViewById<EditText>(R.id.et_filename)
        val saveBtn = dialog.findViewById<Button>(R.id.btn_save)

        saveBtn?.setOnClickListener {
            if(!fileNameEt?.text.isNullOrEmpty()) {
                callback.invoke(fileNameEt?.text.toString())
                dialog.dismiss()
            }else{
                snackbar(dialog.window?.decorView!!, "Please enter valid file name")
            }
        }


        return dialog
    }
    fun show(fragmentActivity: FragmentActivity) {
        if(!this.isAdded){
            show(fragmentActivity.supportFragmentManager, TAG)
        }
    }

    companion object {
        private const val TAG = "SaveRecordingBottomSheet"
    }

}
