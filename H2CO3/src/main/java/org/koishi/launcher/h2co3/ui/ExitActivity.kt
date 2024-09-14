package org.koishi.launcher.h2co3.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.annotation.Keep
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog

@Keep
class ExitActivity : H2CO3Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showMessageListView()

        val code = intent.getIntExtra(EXTRA_CODE, -1)
        if (code == -1) {
            // 错误处理：没有接收到有效的代码
            finish()
            return
        }

        val exitDialog = H2CO3MessageDialog(this)
            .setMessage("Minecraft exited with code: $code")
            .setPositiveButton("Exit") { _: DialogInterface, _: Int -> finish() }
            .setOnDismissListener { _: DialogInterface ->
                // 确保启动新活动前当前活动已经结束
                startActivity(Intent(this, H2CO3MainActivity::class.java))
            }

        exitDialog.show()
    }

    companion object {
        private const val EXTRA_CODE = "code"

        @JvmStatic
        fun showExitMessage(ctx: Context, code: Int) {
            val targetActivity = if (code == 0) H2CO3MainActivity::class.java else ExitActivity::class.java
            val intent = Intent(ctx, targetActivity).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(EXTRA_CODE, code)
            }
            ctx.startActivity(intent)
        }
    }
}
