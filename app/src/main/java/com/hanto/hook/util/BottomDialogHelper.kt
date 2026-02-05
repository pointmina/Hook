package com.hanto.hook.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.hanto.hook.R
import com.hanto.hook.data.model.Hook
import com.hanto.hook.ui.view.activity.HookDetailActivity
import com.hanto.hook.viewmodel.HookViewModel

class BottomDialogHelper {

    companion object {
        private const val TAG = "BottomDialogHelper"

        fun showHookOptionsDialog(
            context: Context,
            selectedItem: Hook,
            hookViewModel: HookViewModel
        ) {
            val dialog = BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme)
            val view = LayoutInflater.from(context).inflate(R.layout.bottom_dialog_home, null)
            dialog.setContentView(view)

            dialog.setOnShowListener { dialogInterface ->
                val bottomSheetDialog = dialogInterface as BottomSheetDialog
                val bottomSheet =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout

                bottomSheet?.let { sheet ->
                    val behavior = BottomSheetBehavior.from(sheet)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.skipCollapsed = true
                }
            }

            dialog.setCancelable(true)

            val btnPinHook: MaterialButton = view.findViewById(R.id.btn_set_pin)

            btnPinHook.text = if (selectedItem.isPinned) {
                context.getString(R.string.remove_pin)
            } else {
                context.getString(R.string.set_pin)
            }

            btnPinHook.setOnClickListener {
                val newPinnedState = !selectedItem.isPinned
                try {
                    hookViewModel.setPinned(selectedItem.hookId, newPinnedState)
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting pinned status", e)
                }

                dialog.dismiss()
            }

            // 수정하기
            val btnModifyHook: MaterialButton = view.findViewById(R.id.btn_modify_hook)
            btnModifyHook.setOnClickListener {
                val intent = Intent(context, HookDetailActivity::class.java)
                intent.putExtra("HOOK", selectedItem)
                context.startActivity(intent)
                dialog.dismiss()
            }

            // 삭제하기
            val btnDeleteHook: MaterialButton = view.findViewById(R.id.bt_delete_hook)
            btnDeleteHook.setOnClickListener {
                try {
                    hookViewModel.deleteHookAndTags(selectedItem.hookId)
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting hook", e)
                }

                dialog.dismiss()
            }

            // 공유하기
            val btnShare: MaterialButton = view.findViewById(R.id.btn_share_hook)
            btnShare.setOnClickListener {
                try {
                    shareHook(context, selectedItem)
                } catch (e: Exception) {
                    Log.e(TAG, "Error sharing hook", e)
                }
                dialog.dismiss()
            }

            dialog.show()
        }

        @SuppressLint("QueryPermissionsNeeded")
        private fun shareHook(context: Context, hook: Hook) {
            val shareText = buildString {
                append("${hook.title}\n")

                hook.description?.let { content ->
                    append("${content}\n")
                }

                hook.url?.let { content ->
                    append(content)
                }
            }

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)

                putExtra(Intent.EXTRA_SUBJECT, hook.title)
            }

            val chooser = Intent.createChooser(shareIntent, context.getString(R.string.share_hook))

            if (shareIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(chooser)
            } else {
                Log.e(TAG, "No app available to handle share intent")
            }
        }
    }
}