package org.koishi.launcher.h2co3.ui.version;

import android.content.Context;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.ui.PageManager;
import org.koishi.launcher.h2co3.ui.UIListener;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.util.ArrayList;

public class VersionPageManager extends PageManager {

    public static final int PAGE_ID_VERSION_LIST = 15020;

    private static VersionPageManager instance;

    private VersionListPage versionListPage;

    public VersionPageManager(Context context, H2CO3LauncherUILayout parent, int defaultPageId, UIListener listener) {
        super(context, parent, defaultPageId, listener);
        instance = this;
    }

    public static VersionPageManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("VersionPageManager not initialized!");
        }
        return instance;
    }

    @Override
    public void init(UIListener listener) {
        versionListPage = new VersionListPage(getContext(), PAGE_ID_VERSION_LIST, getParent(), R.layout.page_version_list);

        if (listener != null) {
            listener.onLoad();
        }
    }

    @Override
    public ArrayList<H2CO3LauncherCommonPage> getAllPages() {
        ArrayList<H2CO3LauncherCommonPage> pages = new ArrayList<>();
        pages.add(versionListPage);
        return pages;
    }
}
