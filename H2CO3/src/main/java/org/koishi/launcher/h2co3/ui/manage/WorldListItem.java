package org.koishi.launcher.h2co3.ui.manage;

import static org.koishi.launcher.h2co3core.util.StringUtils.parseColorEscapes;
import static org.koishi.launcher.h2co3core.util.LocaleUtils.formatDateTime;

import android.app.Activity;
import android.content.Context;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.ui.PageManager;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.RequestCodes;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleStringProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.StringProperty;
import org.koishi.launcher.h2co3core.game.World;
import org.koishi.launcher.h2co3core.util.versioning.VersionNumber;
import org.koishi.launcher.h2co3library.browser.FileBrowser;
import org.koishi.launcher.h2co3library.browser.options.LibMode;
import org.koishi.launcher.h2co3library.browser.options.SelectionMode;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.time.Instant;

public class WorldListItem {
    private final Context context;
    private final Activity activity;
    private final H2CO3LauncherUILayout parent;
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty subtitle = new SimpleStringProperty();
    private final World world;

    public WorldListItem(Context context, Activity activity, H2CO3LauncherUILayout parent, World world) {
        this.context = context;

        this.activity = activity;

        this.parent = parent;

        this.world = world;

        title.set(parseColorEscapes(world.getWorldName()));

        subtitle.set(AndroidUtils.getLocalizedText(context, "world_description", world.getFileName(), formatDateTime(context, Instant.ofEpochMilli(world.getLastPlayed())), world.getGameVersion() == null ? context.getString(R.string.message_unknown) : world.getGameVersion()));
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty subtitleProperty() {
        return subtitle;
    }

    public void export() {
        FileBrowser.Builder builder = new FileBrowser.Builder(context);
        builder.setLibMode(LibMode.FOLDER_CHOOSER);
        builder.setSelectionMode(SelectionMode.SINGLE_SELECTION);
        builder.create().browse(activity, RequestCodes.SELECT_WORLD_EXPORT_CODE, ((requestCode, resultCode, data) -> {
            if (requestCode == RequestCodes.SELECT_WORLD_EXPORT_CODE && resultCode == Activity.RESULT_OK && data != null) {
                String path = FileBrowser.getSelectedFiles(data).get(0);
                WorldExportDialog dialog = new WorldExportDialog(context, world, path);
                dialog.show();
            }
        }));
    }

    public void manageDatapacks() {
        if (world.getGameVersion() == null || // old game will not write game version to level.dat
                (VersionNumber.isIntVersionNumber(world.getGameVersion()) // we don't parse snapshot version
                        && VersionNumber.asVersion(world.getGameVersion()).compareTo(VersionNumber.asVersion("1.13")) < 0)) {
            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
            builder.setMessage(context.getString(R.string.world_datapack_1_13));
            builder.setNegativeButton(context.getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
            builder.create().show();
            return;
        }
        DatapackListPage page = new DatapackListPage(context, PageManager.PAGE_ID_TEMP, parent, R.layout.page_datapack_list, world.getWorldName(), world.getFile());
        ManagePageManager.getInstance().showTempPage(page);
    }

    public void showInfo() {
        try {
            WorldInfoPage page = new WorldInfoPage(context, PageManager.PAGE_ID_TEMP, parent, R.layout.page_world_info, world);
            ManagePageManager.getInstance().showTempPage(page);
        } catch (Exception e) {
            // TODO
        }
    }
}