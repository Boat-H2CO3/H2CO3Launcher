package org.koishi.launcher.h2co3.ui.manage

import android.content.Context
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import org.koishi.launcher.h2co3.util.AnimUtil
import org.koishi.launcher.h2co3.util.AnimUtil.Companion.interpolator
import org.koishi.launcher.h2co3.R
import org.koishi.launcher.h2co3.databinding.PageManageBinding
import org.koishi.launcher.h2co3.setting.Profile
import org.koishi.launcher.h2co3.ui.ProgressDialog
import org.koishi.launcher.h2co3.ui.UIManager.Companion.instance
import org.koishi.launcher.h2co3.ui.manage.ManageUI.VersionLoadable
import org.koishi.launcher.h2co3.ui.manage.adapter.ManageItemAdapter
import org.koishi.launcher.h2co3.ui.manage.item.ManageItem
import org.koishi.launcher.h2co3.ui.version.Versions
import org.koishi.launcher.h2co3.util.RequestCodes
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleBooleanProperty
import org.koishi.launcher.h2co3core.task.Schedulers
import org.koishi.launcher.h2co3core.task.Task
import org.koishi.launcher.h2co3core.util.io.FileUtils
import org.koishi.launcher.h2co3library.browser.FileBrowser
import org.koishi.launcher.h2co3library.browser.options.LibMode
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonPage
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout
import java.io.File

class ManagePage(context: Context, id: Int, parent: H2CO3LauncherUILayout, resId: Int) :
    H2CO3LauncherCommonPage(context, id, parent, resId), VersionLoadable {
    private val currentVersionUpgradable: BooleanProperty = SimpleBooleanProperty()

    private lateinit var binding: PageManageBinding

    init {
        create()
    }

    override fun refresh(vararg param: Any): Task<*>? {
        return null
    }

    override fun loadVersion(profile: Profile, version: String) {
        currentVersionUpgradable.set(profile.repository.isModpack(version))
    }

    private fun create() {
        binding = PageManageBinding.bind(contentView).apply {
            left.layoutManager = LinearLayoutManager(context)
            left.adapter = ManageItemAdapter(context, mutableListOf<ManageItem>().apply {
                add(ManageItem(R.drawable.ic_baseline_script_24, R.string.folder_h2co3Launcher_log) {
                    onBrowse(
                        H2CO3LauncherTools.LOG_DIR
                    )
                })
                add(ManageItem(R.drawable.ic_baseline_videogame_asset_24, R.string.folder_game) {
                    onBrowse("")
                })
                add(ManageItem(R.drawable.ic_outline_extension_24, R.string.folder_mod) {
                    onBrowse("mods")
                })
                add(ManageItem(R.drawable.ic_baseline_settings_24, R.string.folder_config) {
                    onBrowse("config")
                })
                add(ManageItem(R.drawable.ic_baseline_texture_24, R.string.folder_resourcepacks) {
                    onBrowse("resourcepacks")
                })
                add(ManageItem(R.drawable.ic_baseline_application_24, R.string.folder_shaderpacks) {
                    onBrowse("shaderpacks")
                })
                add(ManageItem(R.drawable.ic_baseline_screenshot_24, R.string.folder_screenshots) {
                    onBrowse("screenshots")
                })
                add(ManageItem(R.drawable.ic_baseline_earth_24, R.string.folder_saves) {
                    onBrowse("saves")
                })
                add(ManageItem(R.drawable.ic_baseline_script_24, R.string.folder_log) {
                    onBrowse("logs")
                })
            })
            right.layoutManager = LinearLayoutManager(context)
            right.adapter = ManageItemAdapter(context, mutableListOf<ManageItem>().apply {
                add(ManageItem(R.drawable.ic_baseline_update_24, R.string.version_update) {
                    if (!currentVersionUpgradable.get()) {
                        AnimUtil.playTranslationX(it, 500, 0f, 50f, -50f, 0f)
                            .interpolator(OvershootInterpolator()).start()
                    } else {
                        updateGame()
                    }
                })
                add(ManageItem(R.drawable.ic_baseline_edit_24, R.string.version_manage_rename) {
                    rename()
                })
                add(
                    ManageItem(
                        R.drawable.ic_baseline_content_copy_24,
                        R.string.version_manage_duplicate
                    ) {
                        duplicate()
                    })
                add(ManageItem(R.drawable.ic_baseline_output_24, R.string.modpack_export) {
                    export()
                })
                add(
                    ManageItem(
                        R.drawable.ic_baseline_list_24,
                        R.string.version_manage_redownload_assets_index
                    ) {
                        redownloadAssetIndex()
                    })
                add(
                    ManageItem(
                        R.drawable.ic_baseline_delete_24,
                        R.string.version_manage_remove_libraries
                    ) {
                        clearLibraries()
                    })
                add(ManageItem(R.drawable.ic_baseline_delete_24, R.string.version_manage_clean) {
                    clearJunkFiles()
                })
            })
        }
    }

    private fun onBrowse(dir: String) {
        val builder = FileBrowser.Builder(context)
        builder.setLibMode(LibMode.FILE_BROWSER)
        builder.setInitDir(
            if (dir.startsWith("/")) dir else File(
                profile.repository.getRunDirectory(
                    version
                ), dir
            ).absolutePath
        )
        builder.create().browse(activity, RequestCodes.BROWSE_DIR_CODE, null)
    }

    private fun redownloadAssetIndex() {
        Versions.updateGameAssets(context, profile, version)
    }

    private fun clearLibraries() {
        val builder = H2CO3LauncherAlertDialog.Builder(context)
        builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT)
        builder.setMessage(
            String.format(
                context.getString(R.string.version_manage_remove_confirm),
                "libraries"
            )
        )
        builder.setPositiveButton {
            val progress = ProgressDialog(context)
            Task.runAsync {
                FileUtils.deleteDirectoryQuietly(
                    File(
                        profile.repository.baseDirectory, "libraries"
                    )
                )
            }.whenComplete(Schedulers.androidUIThread()) { _: Exception? ->
                progress.dismiss()
            }.start()
        }
        builder.setNegativeButton(null)
        builder.create().show()
    }

    private fun clearJunkFiles() {
        val builder = H2CO3LauncherAlertDialog.Builder(context)
        builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT)
        builder.setMessage(
            String.format(
                context.getString(R.string.version_manage_remove_confirm),
                "logs"
            )
        )
        builder.setPositiveButton {
            val progress = ProgressDialog(context)
            Task.runAsync {
                Versions.cleanVersion(
                    profile, version
                )
            }.whenComplete(Schedulers.androidUIThread()) { _: Exception? ->
                progress.dismiss()
            }.start()
        }
        builder.setNegativeButton(null)
        builder.create().show()
    }

    private fun updateGame() {
        Versions.updateVersion(context, parent, profile, version)
    }

    private fun export() {
        Versions.exportVersion(context, parent, profile, version)
    }

    private fun rename() {
        Versions.renameVersion(context, profile, version)
            .thenApply {
                instance.manageUI.preferredVersionName = it
            }
    }

    private fun duplicate() {
        Versions.duplicateVersion(context, profile, version)
    }

    val profile: Profile
        get() = instance.manageUI.profile

    val version: String
        get() = instance.manageUI.version

}
