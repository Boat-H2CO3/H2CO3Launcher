//Created by Mio
package org.koishi.launcher.h2co3.util

import android.content.Context
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import org.koishi.launcher.h2co3.R
import org.koishi.launcher.h2co3.setting.Profiles
import org.koishi.launcher.h2co3launcher.H2CO3LauncherConfig
import org.koishi.launcher.h2co3launcher.plugins.DriverPlugin
import org.koishi.launcher.h2co3launcher.plugins.RendererPlugin
import org.koishi.launcher.h2co3library.util.ConvertUtils
import java.util.function.Consumer

class RendererUtil {
    companion object {
        @JvmStatic
        fun openRendererMenu(
            context: Context,
            view: View,
            x: Int,
            y: Int,
            width: Int,
            height: Int,
            isGlobal: Boolean,
            callback: Consumer<String>
        ) {
            val listView = ListView(context)
            var popupWindow: PopupWindow? = null
            listView.adapter =
                ArrayAdapter(context, R.layout.item_renderer, mutableListOf<String>().apply {
                    add(context.getString(R.string.settings_h2co3Launcher_renderer_gl4es))
                    add(context.getString(R.string.settings_h2co3Launcher_renderer_virgl))
                    add(context.getString(R.string.settings_h2co3Launcher_renderer_vgpu))
                    add(context.getString(R.string.settings_h2co3Launcher_renderer_zink))
                    add(context.getString(R.string.settings_h2co3Launcher_renderer_freedreno))
                    add(context.getString(R.string.settings_h2co3Launcher_renderer_gl4esp))
                    RendererPlugin.rendererList.forEach {
                        add(it.des)
                    }
                })
            listView.setOnItemClickListener { _, _, position, _ ->
                val versionSetting = if (isGlobal) Profiles.getSelectedProfile().global else Profiles.getSelectedProfile().versionSetting
                val rendererList = mutableListOf<H2CO3LauncherConfig.Renderer>().apply {
                    add(H2CO3LauncherConfig.Renderer.RENDERER_GL4ES)
                    add(H2CO3LauncherConfig.Renderer.RENDERER_VIRGL)
                    add(H2CO3LauncherConfig.Renderer.RENDERER_VGPU)
                    add(H2CO3LauncherConfig.Renderer.RENDERER_ZINK)
                    add(H2CO3LauncherConfig.Renderer.RENDERER_FREEDRENO)
                    add(H2CO3LauncherConfig.Renderer.RENDERER_GL4ESPLUS)
                }
                if (position > rendererList.size - 1) {
                    versionSetting.renderer = H2CO3LauncherConfig.Renderer.RENDERER_CUSTOM
                    RendererPlugin.selected =
                        RendererPlugin.rendererList[position - rendererList.size]
                    versionSetting.customRenderer = RendererPlugin.selected!!.des
                } else {
                    versionSetting.renderer = rendererList[position]
                }
                popupWindow?.dismiss()
                callback.accept(listView.adapter.getItem(position).toString())
            }
            popupWindow = PopupWindow(
                listView,
                width,
                height
            ).apply {
                isClippingEnabled = false
                isOutsideTouchable = true
                enterTransition = Slide(Gravity.BOTTOM)
                exitTransition = Slide(Gravity.BOTTOM)
                showAtLocation(view, Gravity.START or Gravity.TOP, x, y)
            }
        }

        @JvmStatic
        fun openDriverMenu(
            context: Context,
            view: View,
            isGlobal: Boolean,
            callback: Consumer<String>
        ) {
            val listView = ListView(context)
            var popupWindow: PopupWindow? = null
            listView.adapter =
                ArrayAdapter(context, R.layout.item_renderer, mutableListOf<String>().apply {
                    DriverPlugin.driverList.forEach {
                        add(it.driver)
                    }
                })
            listView.setOnItemClickListener { _, _, position, _ ->
                val versionSetting = if (isGlobal) Profiles.getSelectedProfile().global else Profiles.getSelectedProfile().versionSetting
                versionSetting.driver = DriverPlugin.driverList[position].driver
                DriverPlugin.selected = DriverPlugin.driverList[position]
                popupWindow?.dismiss()
                callback.accept(listView.adapter.getItem(position).toString())
            }
            popupWindow = PopupWindow(
                listView,
                ConvertUtils.dip2px(context, 200F),
                ConvertUtils.dip2px(context, 300F)
            ).apply {
                isClippingEnabled = false
                isOutsideTouchable = true
                enterTransition = Slide(Gravity.TOP)
                exitTransition = Slide(Gravity.TOP)
                val pos = intArrayOf(-1, -1)
                view.getLocationInWindow(pos)
                showAtLocation(view, Gravity.START, pos[0], pos[1] - 50)
            }
        }
    }
}