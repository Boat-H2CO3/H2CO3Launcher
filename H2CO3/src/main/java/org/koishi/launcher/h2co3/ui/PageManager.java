package org.koishi.launcher.h2co3.ui;

import android.content.Context;

import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonPage;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherTempPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.util.ArrayList;
import java.util.logging.Level;

public abstract class PageManager {

    public static final int PAGE_ID_TEMP = -10000;
    public final ArrayList<H2CO3LauncherCommonPage> allPages;
    private final Context context;
    private final H2CO3LauncherUILayout parent;
    private final int defaultPageId;
    private H2CO3LauncherCommonPage currentPage;

    public PageManager(Context context, H2CO3LauncherUILayout parent, int defaultPageId, UIListener listener) {
        this.context = context;
        this.parent = parent;
        this.defaultPageId = defaultPageId;

        init(listener);
        allPages = getAllPages();
        if (!allPages.isEmpty()) {
            switchPage(defaultPageId);
        }
    }

    public Context getContext() {
        return context;
    }

    public H2CO3LauncherUILayout getParent() {
        return parent;
    }

    public int getDefaultPageId() {
        return defaultPageId;
    }

    public H2CO3LauncherCommonPage getCurrentPage() {
        return currentPage;
    }

    public abstract void init(UIListener listener);

    public abstract ArrayList<H2CO3LauncherCommonPage> getAllPages();

    public H2CO3LauncherCommonPage createPageById(int id) {
        return null;
    }

    public H2CO3LauncherCommonPage getPageById(int id) {
        for (H2CO3LauncherCommonPage page : allPages) {
            if (page.getId() == id) {
                return page;
            }
        }
        return null;
    }

    public void switchPage(int id) {
        if (allPages.isEmpty()) {
            Logging.LOG.log(Level.WARNING, "No page!");
            return;
        }

        H2CO3LauncherCommonPage targetPage = getPageById(id);
        if (targetPage == null) {
            targetPage = createPageById(id);
            if (targetPage == null) {
                throw new IllegalStateException("Wrong page id, this should not happen!");
            }
        }

        handleCurrentPageStop();

        if (targetPage.getCurrentTempPage() != null) {
            targetPage.getCurrentTempPage().restart();
            targetPage.getCurrentTempPage().onRestart();
        } else {
            targetPage.onStart();
        }
        currentPage = targetPage;
    }

    private void handleCurrentPageStop() {
        if (currentPage != null) {
            if (currentPage.isShowing()) {
                currentPage.onStop();
            }
            H2CO3LauncherTempPage currentTempPage = currentPage.getCurrentTempPage();
            if (currentTempPage != null && currentTempPage.isShowing()) {
                currentTempPage.onStop();
            }
        }
    }

    public void showTempPage(H2CO3LauncherTempPage h2co3LauncherTempPage) {
        if (currentPage != null) {
            handleCurrentPageStop();
            currentPage.getAllTempPages().add(h2co3LauncherTempPage);
            currentPage.setCurrentTempPage(h2co3LauncherTempPage);
            h2co3LauncherTempPage.onStart();
        }
    }

    public boolean canReturn() {
        return currentPage != null && currentPage.getCurrentTempPage() != null;
    }

    public void dismissCurrentTempPage() {
        if (currentPage != null) {
            H2CO3LauncherTempPage currentTempPage = currentPage.getCurrentTempPage();
            if (currentTempPage != null) {
                currentTempPage.dismiss();
                currentPage.getAllTempPages().remove(currentTempPage);
                handleTempPageRestart();
            }
        }
    }

    private void handleTempPageRestart() {
        if (!currentPage.getAllTempPages().isEmpty()) {
            H2CO3LauncherTempPage lastTempPage = currentPage.getAllTempPages().get(currentPage.getAllTempPages().size() - 1);
            lastTempPage.restart();
            lastTempPage.onRestart();
            currentPage.setCurrentTempPage(lastTempPage);
        } else {
            currentPage.onStart();
            currentPage.setCurrentTempPage(null);
        }
    }

    public void dismissAllTempPagesCreatedByPage(int id) {
        H2CO3LauncherCommonPage commonPage = getPageById(id);
        if (commonPage != null) {
            H2CO3LauncherTempPage currentTempPage = commonPage.getCurrentTempPage();
            if (currentTempPage != null) {
                currentTempPage.dismiss();
            }
            commonPage.getAllTempPages().clear();
            commonPage.setCurrentTempPage(null);
            if (currentPage == commonPage) {
                commonPage.onStart();
            }
        }
    }

    public void dismissAllTempPages() {
        for (H2CO3LauncherCommonPage page : allPages) {
            dismissAllTempPagesCreatedByPage(page.getId());
        }
    }

    public void onPause() {
        for (H2CO3LauncherCommonPage page : allPages) {
            page.onPause();
            for (H2CO3LauncherTempPage tempPage : page.getAllTempPages()) {
                tempPage.onPause();
            }
        }
    }

    public void onResume() {
        for (H2CO3LauncherCommonPage page : allPages) {
            page.onResume();
            for (H2CO3LauncherTempPage tempPage : page.getAllTempPages()) {
                tempPage.onResume();
            }
        }
    }
}