package org.koishi.launcher.h2co3.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GuideUtil {
    public static final String TAG_GUIDE_THEME_2 = "theme2";
    private static final List<String> tagList = new ArrayList<>();
    private static final File file = new File(H2CO3LauncherTools.FILES_DIR + "/guide_tag.txt");

    static {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            tagList.addAll(Files.readAllLines(Paths.get(file.getPath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void show(Activity activity, View view, String title) {
        TapTargetView.showFor(
                activity, TapTarget.forView(view, title)
                        .transparentTarget(true)
        );
    }

    public static void show(Dialog dialog, View view, String title) {
        TapTargetView.showFor(
                dialog, TapTarget.forView(view, title)
                        .transparentTarget(true)
        );
    }

    private static void addTag(String tag) {
        tagList.add(tag);
        try {
            Files.write(file.toPath(), tagList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void show(Activity activity, View view, String title, String tag) {
        if (!tagList.contains(tag)) {
            addTag(tag);
            show(activity, view, title);
        }
    }

    public static void show(Dialog dialog, View view, String title, String tag) {
        if (!tagList.contains(tag)) {
            addTag(tag);
            show(dialog, view, title);
        }
    }
}