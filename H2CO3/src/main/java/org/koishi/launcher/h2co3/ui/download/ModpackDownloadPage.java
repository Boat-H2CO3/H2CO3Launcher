package org.koishi.launcher.h2co3.ui.download;

import android.content.Context;
import android.view.View;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.game.LocalizedRemoteModRepository;
import org.koishi.launcher.h2co3.ui.version.Versions;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3core.mod.RemoteModRepository;
import org.koishi.launcher.h2co3core.mod.curse.CurseForgeRemoteModRepository;
import org.koishi.launcher.h2co3core.mod.modrinth.ModrinthRemoteModRepository;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

public class ModpackDownloadPage extends DownloadPage {

    private H2CO3LauncherButton installModpack;

    public ModpackDownloadPage(Context context, int id, H2CO3LauncherUILayout parent, int resId) {
        super(context, id, parent, resId, null);

        repository = new Repository();

        supportChinese.set(true);
        downloadSources.get().setAll(context.getString(R.string.mods_curseforge), context.getString(R.string.mods_modrinth));
        if (CurseForgeRemoteModRepository.isAvailable())
            downloadSource.set(context.getString(R.string.mods_curseforge));
        else
            downloadSource.set(context.getString(R.string.mods_modrinth));

        create();
    }

    @Override
    protected String getLocalizedCategory(String category) {
        if (getContext().getString(R.string.mods_modrinth).equals(downloadSource.get())) {
            return AndroidUtils.getLocalizedText(getContext(), "modrinth_category_" + category.replaceAll("-", "_"));
        } else {
            return AndroidUtils.getLocalizedText(getContext(), "curse_category_" + category);
        }
    }

    @Override
    protected String getLocalizedOfficialPage() {
        return downloadSource.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        installModpack = findViewById(R.id.install_modpack);
        installModpack.setVisibility(View.VISIBLE);
        installModpack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == installModpack) {
            Versions.importModpack(getContext(), getParent());
        }
    }

    private class Repository extends LocalizedRemoteModRepository {

        @Override
        protected RemoteModRepository getBackedRemoteModRepository() {
            if (getContext().getString(R.string.mods_modrinth).equals(downloadSource.get())) {
                return ModrinthRemoteModRepository.MODPACKS;
            } else {
                return CurseForgeRemoteModRepository.MODPACKS;
            }
        }

        @Override
        protected SortType getBackedRemoteModRepositorySortOrder() {
            if (getContext().getString(R.string.mods_modrinth).equals(downloadSource.get())) {
                return SortType.NAME;
            } else {
                return SortType.POPULARITY;
            }
        }

        @Override
        public Type getType() {
            return Type.MODPACK;
        }
    }
}
