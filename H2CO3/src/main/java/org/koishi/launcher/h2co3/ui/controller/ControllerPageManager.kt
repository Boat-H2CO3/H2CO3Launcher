package org.koishi.launcher.h2co3.ui.controller

import android.content.Context
import org.koishi.launcher.h2co3.R
import org.koishi.launcher.h2co3.ui.PageManager
import org.koishi.launcher.h2co3.ui.UIListener
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonPage
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout

class ControllerPageManager(
    context: Context?,
    parent: H2CO3LauncherUILayout?,
    defaultPageId: Int,
    listener: UIListener?
) : PageManager(context, parent, defaultPageId, listener) {
    companion object {
        const val PAGE_ID_CONTROLLER_MANAGER: Int = 15040
        const val PAGE_ID_CONTROLLER_REPO: Int = 15041

        @JvmStatic
        var instance: ControllerPageManager? = null
    }

    private lateinit var controllerManagePage: ControllerManagePage
    private val controllerRepoPage: ControllerRepoPage by lazy {
        ControllerRepoPage(
            context,
            PAGE_ID_CONTROLLER_REPO,
            parent,
            R.layout.page_controller_repo
        )
    }

    init {
        instance = this
    }

    override fun init(listener: UIListener?) {
        controllerManagePage = ControllerManagePage(
            context,
            PAGE_ID_CONTROLLER_MANAGER,
            parent,
            R.layout.page_controller_manager
        )
        listener?.onLoad()
    }

    override fun getAllPages(): ArrayList<H2CO3LauncherCommonPage> {
        return ArrayList<H2CO3LauncherCommonPage>().apply {
            add(controllerManagePage)
        }
    }

    override fun createPageById(id: Int): H2CO3LauncherCommonPage? {
        val page: H2CO3LauncherCommonPage? = when (id) {
            PAGE_ID_CONTROLLER_REPO -> controllerRepoPage
            else -> null
        }
        if (page != null) {
            allPages.add(page)
        }
        return page
    }
}
