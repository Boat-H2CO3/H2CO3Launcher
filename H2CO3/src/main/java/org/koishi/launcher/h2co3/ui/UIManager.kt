package org.koishi.launcher.h2co3.ui

import android.content.Context
import org.koishi.launcher.h2co3.R
import org.koishi.launcher.h2co3.ui.account.AccountUI
import org.koishi.launcher.h2co3.ui.controller.ControllerUI
import org.koishi.launcher.h2co3.ui.download.DownloadUI
import org.koishi.launcher.h2co3.ui.main.MainUI
import org.koishi.launcher.h2co3.ui.manage.ManageUI
import org.koishi.launcher.h2co3.ui.multiplayer.MultiplayerUI
import org.koishi.launcher.h2co3.ui.setting.SettingUI
import org.koishi.launcher.h2co3.ui.version.VersionUI
import org.koishi.launcher.h2co3core.util.Logging
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherBaseUI
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonUI
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout
import java.util.logging.Level

class UIManager(val context: Context, val parent: H2CO3LauncherUILayout) {
    companion object {
        @JvmStatic
        lateinit var instance: UIManager
    }

    private var initialized = false
    lateinit var mainUI: MainUI
    val manageUI: ManageUI by lazy { ManageUI(context, parent, R.layout.ui_manage) }
    val downloadUI: DownloadUI by lazy { DownloadUI(context, parent, R.layout.ui_download) }
    val controllerUI: ControllerUI by lazy { ControllerUI(context, parent, R.layout.ui_controller) }
    val multiplayerUI: MultiplayerUI by lazy {
        MultiplayerUI(
            context,
            parent,
            R.layout.ui_multiplayer
        )
    }
    val settingUI: SettingUI by lazy { SettingUI(context, parent, R.layout.ui_setting) }

    private val allUIList = mutableListOf<H2CO3LauncherBaseUI>()
    var currentUI: H2CO3LauncherBaseUI? = null

    fun init(listener: UIListener) {
        if (initialized) {
            Logging.LOG.log(Level.WARNING, "UIManager already initialized!")
            return
        }
        instance = this
        mainUI = MainUI(context, parent, R.layout.ui_main)
        allUIList.add(mainUI)
        mainUI.addLoadingCallback {
            listener.onLoad()
        }
    }

    fun switchUI(ui: H2CO3LauncherCommonUI) {
        var isFirstAdd = false
        if (!allUIList.contains(ui)) {
            isFirstAdd = true
            allUIList.add(ui)
        }
        for (baseUI in allUIList) {
            if (ui === baseUI) {
                currentUI?.onStop()
                if (isFirstAdd) {
                    ui.addLoadingCallback {
                        ui.onStart()
                    }
                } else {
                    ui.onStart()
                }
                currentUI = ui
                break
            }
        }
    }

    fun registerDefaultBackEvent(runnable: Runnable?) {
        H2CO3LauncherBaseUI.setDefaultBackEvent(runnable)
    }

    fun onBackPressed() {
        currentUI?.onBackPressed()
    }

    fun onPause() {
        for (baseUI in allUIList) {
            baseUI.onPause()
        }
    }

    fun onResume() {
        for (baseUI in allUIList) {
            baseUI.onResume()
        }
    }
}