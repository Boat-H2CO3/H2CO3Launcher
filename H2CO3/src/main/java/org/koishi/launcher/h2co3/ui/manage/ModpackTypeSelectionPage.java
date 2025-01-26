package org.koishi.launcher.h2co3.ui.manage;

import android.content.Context;
import android.view.View;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.ui.PageManager;
import org.koishi.launcher.h2co3core.mod.ModpackExportInfo;
import org.koishi.launcher.h2co3core.mod.mcbbs.McbbsModpackExportTask;
import org.koishi.launcher.h2co3core.mod.multimc.MultiMCModpackExportTask;
import org.koishi.launcher.h2co3core.mod.server.ServerModpackExportTask;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherTempPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

public class ModpackTypeSelectionPage extends H2CO3LauncherTempPage implements View.OnClickListener {

    public static final String MODPACK_TYPE_MCBBS = "mcbbs";
    public static final String MODPACK_TYPE_MULTIMC = "multimc";
    public static final String MODPACK_TYPE_SERVER = "server";
    private final Profile profile;
    private final String version;
    private H2CO3LauncherLinearLayout mcbbs;
    private H2CO3LauncherLinearLayout multimc;
    private H2CO3LauncherLinearLayout server;

    public ModpackTypeSelectionPage(Context context, int id, H2CO3LauncherUILayout parent, int resId, Profile profile, String version) {
        super(context, id, parent, resId);
        this.profile = profile;
        this.version = version;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mcbbs = findViewById(R.id.mcbbs);
        multimc = findViewById(R.id.multimc);
        server = findViewById(R.id.server);
        mcbbs.setOnClickListener(this);
        multimc.setOnClickListener(this);
        server.setOnClickListener(this);
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onClick(View v) {
        String type = null;
        ModpackExportInfo.Options options = null;
        if (v == mcbbs) {
            type = MODPACK_TYPE_MCBBS;
            options = McbbsModpackExportTask.OPTION;
        }
        if (v == multimc) {
            type = MODPACK_TYPE_MULTIMC;
            options = MultiMCModpackExportTask.OPTION;
        }
        if (v == server) {
            type = MODPACK_TYPE_SERVER;
            options = ServerModpackExportTask.OPTION;
        }
        ModpackInfoPage page = new ModpackInfoPage(getContext(), PageManager.PAGE_ID_TEMP, getParent(), R.layout.page_modpack_info, profile, version, type, options);
        ManagePageManager.getInstance().showTempPage(page);
    }
}
