package org.koishi.launcher.h2co3.ui.setting

import android.content.Context
import org.koishi.launcher.h2co3.R
import org.koishi.launcher.h2co3.setting.Profiles
import org.koishi.launcher.h2co3.ui.PageManager
import org.koishi.launcher.h2co3.ui.UIListener
import org.koishi.launcher.h2co3.ui.manage.VersionSettingPage
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonPage
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout

class SettingPageManager(
    context: Context,
    parent: H2CO3LauncherUILayout,
    defaultPageId: Int,
    val listener: UIListener?
) : PageManager(context, parent, defaultPageId, listener) {
    companion object {
        @JvmStatic
        var instance: SettingPageManager? = null
        const val PAGE_ID_SETTING_GAME: Int = 15030
        const val PAGE_ID_SETTING_LAUNCHER: Int = 15031
        const val PAGE_ID_SETTING_HELP: Int = 15032
        const val PAGE_ID_SETTING_COMMUNITY: Int = 15033
        const val PAGE_ID_SETTING_ABOUT: Int = 15034
    }

    init {
        instance = this
    }

    private lateinit var versionSettingPage: VersionSettingPage
    private val launcherSettingPage: LauncherSettingPage by lazy {
        LauncherSettingPage(
            context,
            PAGE_ID_SETTING_LAUNCHER,
            parent,
            R.layout.page_launcher_setting
        )
    }
    private val helpPage: HelpPage by lazy {
        HelpPage(context, PAGE_ID_SETTING_HELP, parent, R.layout.page_help)
    }
    private val communityPage: CommunityPage by lazy {
        CommunityPage(context, PAGE_ID_SETTING_COMMUNITY, parent, R.layout.page_community)
    }
    private val aboutPage: AboutPage by lazy {
        AboutPage(context, PAGE_ID_SETTING_ABOUT, parent, R.layout.page_about)
    }


    override fun init(listener: UIListener?) {
        versionSettingPage = VersionSettingPage(
            context,
            PAGE_ID_SETTING_GAME,
            parent,
            R.layout.page_version_setting,
            true
        )
        versionSettingPage.loadVersion(Profiles.getSelectedProfile(), null)
        listener?.onLoad()
    }

    override fun getAllPages(): ArrayList<H2CO3LauncherCommonPage> {
        return ArrayList<H2CO3LauncherCommonPage>().apply {
            add(versionSettingPage)
        }
    }

    override fun createPageById(id: Int): H2CO3LauncherCommonPage? {
        val page: H2CO3LauncherCommonPage? = when (id) {
            PAGE_ID_SETTING_LAUNCHER -> launcherSettingPage
            PAGE_ID_SETTING_HELP -> helpPage
            PAGE_ID_SETTING_COMMUNITY -> communityPage
            PAGE_ID_SETTING_ABOUT -> aboutPage
            else -> null
        }
        return page
    }
}