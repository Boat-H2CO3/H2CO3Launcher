package org.koishi.launcher.h2co3.control

import android.content.Context
import androidx.databinding.DataBindingUtil
import org.koishi.launcher.h2co3.R
import org.koishi.launcher.h2co3.databinding.ViewGamepadBinding
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog

class GamepadButtonBindingDialog(
    context: Context,
    val map: MutableMap<Int, Int>
) : H2CO3LauncherDialog(context) {

    init {
        setCancelable(false)
        DataBindingUtil.inflate<ViewGamepadBinding>(
            layoutInflater,
            R.layout.view_gamepad,
            null,
            false
        ).apply {
            dialog = this@GamepadButtonBindingDialog
            setContentView(root)
        }
    }

    fun bindingAction(gamepadKey: Int) {
        val list = FXCollections.observableList(mutableListOf(map.getOrDefault(gamepadKey, -1)))
        SelectKeycodeDialog(
            context,
            list,
            true, true
        ) {
            map[gamepadKey] = it.selectionProperty().get()
        }.show()
    }
}
