package com.hanto.hook.util

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.hanto.hook.R
import com.hanto.hook.data.model.Hook
import com.hanto.hook.ui.view.HookDetailActivity
import com.hanto.hook.viewmodel.HookViewModel
import kotlinx.coroutines.launch

class BottomDialogHelper {

    companion object {
        fun showHookOptionsDialog(
            context: Context,
            selectedItem: Hook,
            hookViewModel: HookViewModel
        ) {
            val dialog = BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme)
            val view = LayoutInflater.from(context).inflate(R.layout.bottom_dialog_home, null)
            dialog.setContentView(view)
            dialog.setCancelable(true)

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
                selectedItem.let {
                    (context as? AppCompatActivity)?.lifecycleScope?.launch {
                        hookViewModel.deleteHookAndTags(selectedItem.hookId)
                    }
                }
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}
