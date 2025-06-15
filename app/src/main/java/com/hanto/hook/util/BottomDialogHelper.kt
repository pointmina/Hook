package com.hanto.hook.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.hanto.hook.R
import com.hanto.hook.data.model.Hook
import com.hanto.hook.ui.view.HookDetailActivity
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
            dialog.setCancelable(true)

            // 고정하기 버튼 클릭 시
            val btnPinHook: MaterialButton = view.findViewById(R.id.btn_set_pin)

            // 버튼 텍스트 설정
            btnPinHook.text = if (selectedItem.isPinned) {
                context.getString(R.string.remove_pin)
            } else {
                context.getString(R.string.set_pin)
            }

            btnPinHook.setOnClickListener {
                Log.d(
                    TAG,
                    "Pin button clicked for hook: ${selectedItem.hookId}, current isPinned: ${selectedItem.isPinned}"
                )

                val newPinnedState = !selectedItem.isPinned
                Log.d(TAG, "Setting new pinned state: $newPinnedState")

                try {
                    hookViewModel.setPinned(selectedItem.hookId, newPinnedState)
                    Log.d(TAG, "setPinned called successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting pinned status", e)
                }

                dialog.dismiss()
            }

            // 수정하기 버튼 클릭 시
            val btnModifyHook: MaterialButton = view.findViewById(R.id.btn_modify_hook)
            btnModifyHook.setOnClickListener {
                val intent = Intent(context, HookDetailActivity::class.java)
                intent.putExtra("HOOK", selectedItem)
                context.startActivity(intent)
                dialog.dismiss()
            }

            // 삭제하기 버튼 클릭 시
            val btnDeleteHook: MaterialButton = view.findViewById(R.id.bt_delete_hook)
            btnDeleteHook.setOnClickListener {
                Log.d(TAG, "Delete button clicked for hook: ${selectedItem.hookId}")

                try {
                    hookViewModel.deleteHookAndTags(selectedItem.hookId)
                    Log.d(TAG, "deleteHookAndTags called successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting hook", e)
                }

                dialog.dismiss()
            }

            // 공유하기 버튼 클릭 시
            val btnShare: MaterialButton = view.findViewById(R.id.btn_share_hook)
            btnShare.setOnClickListener {
                Log.d(TAG, "Share button clicked for hook: ${selectedItem.hookId}")

                try {
                    shareHook(context, selectedItem)
                    Log.d(TAG, "shareHook called successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error sharing hook", e)
                }
                dialog.dismiss()
            }

            dialog.show()
        }

        @SuppressLint("QueryPermissionsNeeded")
        private fun shareHook(context: Context, hook: Hook) {
            // 공유할 텍스트 구성
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