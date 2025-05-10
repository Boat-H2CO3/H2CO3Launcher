package org.koishi.launcher.h2co3.dialog

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koishi.launcher.h2co3.H2CO3LauncherApplication
import org.koishi.launcher.h2co3.R
import org.koishi.launcher.h2co3.adapter.ManageJavaItemAdapter
import org.koishi.launcher.h2co3.util.AndroidUtils
import org.koishi.launcher.h2co3.util.RequestCodes
import org.koishi.launcher.h2co3.util.RuntimeUtils
import org.koishi.launcher.h2co3core.game.JavaVersion
import org.koishi.launcher.h2co3core.task.Schedulers
import org.koishi.launcher.h2co3core.util.io.FileUtils
import org.koishi.launcher.h2co3core.util.java.JavaManager.addToJavaVersion
import org.koishi.launcher.h2co3core.util.java.JavaManager.javaList
import org.koishi.launcher.h2co3core.util.java.JavaManager.remove
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools
import org.koishi.launcher.h2co3library.browser.FileBrowser
import org.koishi.launcher.h2co3library.browser.options.LibMode
import org.koishi.launcher.h2co3library.browser.options.SelectionMode
import org.koishi.launcher.h2co3library.component.dialog.H2CO3CustomViewDialog
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog
import org.koishi.launcher.h2co3library.component.dialog.H2CO3MaterialDialog
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors

class JavaManageDialog(context: Context, onSelected: OnSelectedListener) :
    H2CO3CustomViewDialog(context) {
    private val versionList: MutableList<JavaVersion> = ArrayList()
    private var isLoading = false

    init {
        setCancelable(false)

        setCustomView(R.layout.dialog_manage_java)
        val recyclerView = customView.findViewById<RecyclerView>(R.id.recycler_view)
        val progressBar = customView.findViewById<ProgressBar>(R.id.progress)
        val cancelButton = customView.findViewById<Button>(R.id.cancel)
        val autoSelectButton = customView.findViewById<Button>(R.id.auto_select)
        val importJavaButton = customView.findViewById<Button>(R.id.import_java)
        alertDialog = create()

        refresh()
        recyclerView.adapter = ManageJavaItemAdapter(
            context, versionList
        ) { java: JavaVersion, isDelete: Boolean ->
            if (isDelete) {
                H2CO3MaterialDialog(context)
                    .setMessage(context.getString(R.string.button_remove_confirm))
                    .setPositiveButton(
                        org.koishi.launcher.h2co3library.R.string.dialog_positive
                    ) { dialog: DialogInterface?, which: Int ->
                        remove(java.name)
                        refresh()
                        recyclerView.adapter!!.notifyDataSetChanged()
                    }
                    .setNegativeButton(
                        org.koishi.launcher.h2co3library.R.string.dialog_negative
                    ) { dialog: DialogInterface?, which: Int ->
                        alertDialog.dismiss()
                    }
                    .create()
                    .show()
            } else {
                onSelected.invoke(java.name)
                alertDialog.dismiss()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        cancelButton.setOnClickListener { v: View? ->
            if (!isLoading) alertDialog.dismiss()
        }
        autoSelectButton.setOnClickListener { v: View? ->
            if (isLoading) return@setOnClickListener
            onSelected.invoke("Auto")
            alertDialog.dismiss()
        }
        importJavaButton.setOnClickListener { v: View? ->
            if (isLoading) return@setOnClickListener
            val builder = FileBrowser.Builder(getContext())
            builder.setLibMode(LibMode.FILE_CHOOSER)
            builder.setSelectionMode(SelectionMode.SINGLE_SELECTION)
            builder.create().browse(
                H2CO3LauncherApplication.getCurrentActivity(),
                RequestCodes.SELECT_JAVA_CODE
            ) { requestCode: Int, resultCode: Int, data: Intent? ->
                if (requestCode == RequestCodes.SELECT_JAVA_CODE && resultCode == Activity.RESULT_OK && data != null) {
                    val path = FileBrowser.getSelectedFiles(data)[0]
                    val uri = Uri.parse(path)
                    val fileName = if (AndroidUtils.isDocUri(uri)) AndroidUtils.getFileName(
                        context,
                        uri
                    ) else File(path).name
                    if (!fileName.endsWith(".tar.xz")) {
                        H2CO3LauncherAlertDialog.Builder(context)
                            .setMessage(context.getString(R.string.import_java_wrong_file))
                            .setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT)
                            .setNegativeButton(null)
                            .create()
                            .show()
                        return@browse
                    }
                    try {
                        if (AndroidUtils.isDocUri(uri)) context.contentResolver.openInputStream(uri) else Files.newInputStream(
                            Paths.get(path)
                        ).use { inputStream ->
                            progressBar.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                            isLoading = true
                            CompletableFuture.supplyAsync<Boolean> {
                                try {
                                    val dest = File(H2CO3LauncherTools.JAVA_PATH, fileName)
                                    if (dest.exists()) {
                                        FileUtils.deleteDirectory(
                                            dest
                                        )
                                    }
                                    RuntimeUtils.uncompressTarXZ(inputStream, dest)
                                    RuntimeUtils.patchJava(context, dest.absolutePath)
                                } catch (e: Throwable) {
                                    return@supplyAsync false
                                }
                                true
                            }.thenApplyAsync<Boolean> { success: Boolean ->
                                if (success) {
                                    return@thenApplyAsync AndroidUtils.checkElfIsAndroid(
                                        File(
                                            H2CO3LauncherTools.JAVA_PATH,
                                            fileName
                                        ).resolve("bin/java")
                                    )
                                }
                                false
                            }.thenAcceptAsync { isAndroid: Boolean ->
                                Schedulers.androidUIThread().execute {
                                    isLoading = false
                                    progressBar.visibility = View.GONE
                                    recyclerView.visibility = View.VISIBLE
                                    if (isAndroid) {
                                        addToJavaVersion(
                                            File(
                                                H2CO3LauncherTools.JAVA_PATH,
                                                fileName
                                            )
                                        )
                                        refresh()
                                        recyclerView.adapter!!.notifyDataSetChanged()
                                    } else {
                                        H2CO3MaterialDialog(context)
                                            .setMessage(context.getString(R.string.import_java_error))
                                            .setPositiveButton(
                                                context.getString(R.string.mod_check_continue)
                                            ) { dialog: DialogInterface?, which: Int ->
                                                addToJavaVersion(
                                                    File(
                                                        H2CO3LauncherTools.JAVA_PATH,
                                                        fileName
                                                    )
                                                )
                                                refresh()
                                                recyclerView.adapter!!.notifyDataSetChanged()
                                            }
                                            .setNegativeButton(
                                                context.getString(R.string.button_cancel)
                                            ) { dialog: DialogInterface?, which: Int ->
                                                try {
                                                    FileUtils.deleteDirectory(
                                                        File(
                                                            H2CO3LauncherTools.JAVA_PATH,
                                                            fileName
                                                        )
                                                    )
                                                } catch (e: IOException) {
                                                    throw RuntimeException(e)
                                                }
                                            }
                                            .create()
                                            .show()
                                    }
                                }
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun refresh() {
        versionList.clear()
        versionList.addAll(
            javaList.stream()
                .filter { javaVersion: JavaVersion -> !javaVersion.isAuto }
                .collect(Collectors.toList()))
    }

    interface OnSelectedListener {
        fun invoke(name: String?)
    }
}