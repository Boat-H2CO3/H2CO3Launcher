package org.koishi.launcher.h2co3.ui.download;

import android.content.Context;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.game.LocalizedRemoteModRepository;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.setting.Profiles;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3core.mod.ModManager;
import org.koishi.launcher.h2co3core.mod.RemoteModRepository;
import org.koishi.launcher.h2co3core.mod.curse.CurseForgeRemoteModRepository;
import org.koishi.launcher.h2co3core.mod.modrinth.ModrinthRemoteModRepository;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

public class ModDownloadPage extends DownloadPage {
    private ModManager modManager;

    public ModDownloadPage(Context context, int id, H2CO3LauncherUILayout parent, int resId) {
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
    public void loadVersion(Profile profile, String version) {
        super.loadVersion(profile, version);
        modManager = Profiles.getSelectedProfile().getRepository().getModManager(Profiles.getSelectedVersion());
    }

    public ModManager getModManager() {
        return modManager;
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

    private class Repository extends LocalizedRemoteModRepository {

        @Override
        protected RemoteModRepository getBackedRemoteModRepository() {
            if (getContext().getString(R.string.mods_modrinth).equals(downloadSource.get())) {
                return ModrinthRemoteModRepository.MODS;
            } else {
                return CurseForgeRemoteModRepository.MODS;
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
            return Type.MOD;
        }
    }

}
