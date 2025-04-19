package org.koishi.launcher.h2co3library.browser.adapter;

public interface FileBrowserListener {
    void onEnterDir(String path);
    void onSelect(FileBrowserAdapter adapter, String path);
}
