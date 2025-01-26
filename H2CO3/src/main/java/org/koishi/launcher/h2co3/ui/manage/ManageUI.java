package org.koishi.launcher.h2co3.ui.manage;

import static org.koishi.launcher.h2co3core.util.Lang.tryCast;

import android.content.Context;

import com.google.android.material.tabs.TabLayout;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.H2CO3MainActivity;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.util.WeakListenerHolder;
import org.koishi.launcher.h2co3core.event.EventBus;
import org.koishi.launcher.h2co3core.event.EventPriority;
import org.koishi.launcher.h2co3core.event.RefreshedVersionsEvent;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.game.GameRepository;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherBasePage;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherMultiPageUI;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTabLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class ManageUI extends H2CO3LauncherMultiPageUI implements TabLayout.OnTabSelectedListener {

    private final ObjectProperty<Profile.ProfileVersion> version = new SimpleObjectProperty<>();
    private final WeakListenerHolder listenerHolder = new WeakListenerHolder();
    public String preferredVersionName = null;
    private ManagePageManager pageManager;
    private H2CO3LauncherUILayout container;

    public ManageUI(Context context, H2CO3LauncherUILayout parent, int id) {
        super(context, parent, id);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        H2CO3LauncherTabLayout tabLayout = findViewById(R.id.tab_layout);
        container = findViewById(R.id.container);

        tabLayout.addOnTabSelectedListener(this);
        initPages();
        listenerHolder.add(EventBus.EVENT_BUS.channel(RefreshedVersionsEvent.class).registerWeak(event -> checkSelectedVersion(), EventPriority.HIGHEST));
    }

    @Override
    public void onStart() {
        super.onStart();
        addLoadingCallback(()->{
            // If we jumped to game list page and deleted this version
            // and back to this page, we should return to main page.
            if (!getProfile().getRepository().isLoaded() ||
                    !getProfile().getRepository().hasVersion(getVersion())) {
                Schedulers.androidUIThread().execute(() -> {
                    if (isShowing()) {
                        H2CO3MainActivity.getInstance().cleanItemChecked();
                        H2CO3MainActivity.getInstance().setNavigationItemChecked(R.id.navigation_main);
                    }
                });
                return;
            }
            loadVersion(getVersion(), getProfile());
        });
    }

    @Override
    public void onBackPressed() {
        if (pageManager != null && pageManager.canReturn()) {
            pageManager.dismissCurrentTempPage();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (pageManager != null) {
            pageManager.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pageManager != null) {
            pageManager.onResume();
        }
    }

    @Override
    public void initPages() {
        pageManager = new ManagePageManager(getContext(), container, ManagePageManager.PAGE_ID_MANAGE_MANAGE, null);
    }

    @Override
    public ArrayList<H2CO3LauncherBasePage> getAllPages() {
        return pageManager == null ? null : (ArrayList<H2CO3LauncherBasePage>) pageManager.getAllPages().stream().map(it -> tryCast(it, H2CO3LauncherBasePage.class)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    @Override
    public H2CO3LauncherBasePage getPage(int id) {
        return pageManager == null ? null : pageManager.getPageById(id);
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (pageManager != null) {
            switch (tab.getPosition()) {
                case 1:
                    pageManager.switchPage(ManagePageManager.PAGE_ID_MANAGE_SETTING);
                    break;
                case 2:
                    pageManager.switchPage(ManagePageManager.PAGE_ID_MANAGE_INSTALL);
                    break;
                case 3:
                    pageManager.switchPage(ManagePageManager.PAGE_ID_MANAGE_MOD);
                    break;
                case 4:
                    pageManager.switchPage(ManagePageManager.PAGE_ID_MANAGE_WORLD);
                    break;
                default:
                    pageManager.switchPage(ManagePageManager.PAGE_ID_MANAGE_MANAGE);
                    break;
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void checkSelectedVersion() {
        Schedulers.androidUIThread().execute(() -> {
            if (this.version.get() == null) return;
            GameRepository repository = this.version.get().getProfile().getRepository();
            if (!repository.hasVersion(this.version.get().getVersion())) {
                if (preferredVersionName != null) {
                    loadVersion(preferredVersionName, this.version.get().getProfile());
                } else if (isShowing()) {
                    H2CO3MainActivity.getInstance().cleanItemChecked();
                    H2CO3MainActivity.getInstance().setNavigationItemChecked(R.id.navigation_main);
                }
            }
        });
    }

    public void setVersion(String version, Profile profile) {
        this.version.set(new Profile.ProfileVersion(profile, version));
    }

    public void loadVersion(String version, Profile profile) {
        // If we jumped to game list page and deleted this version
        // and back to this page, we should return to main page.
        if (this.version.get() != null && (!getProfile().getRepository().isLoaded() ||
                !getProfile().getRepository().hasVersion(version))) {
            Schedulers.androidUIThread().execute(() -> {
                if (isShowing()) {
                    H2CO3MainActivity.getInstance().cleanItemChecked();
                    H2CO3MainActivity.getInstance().setNavigationItemChecked(R.id.navigation_main);
                }
            });
            return;
        }

        setVersion(version, profile);
        preferredVersionName = version;

        pageManager.dismissAllTempPages();
        pageManager.loadVersion(profile, version);
    }

    public Profile getProfile() {
        return Optional.ofNullable(version.get()).map(Profile.ProfileVersion::getProfile).orElse(null);
    }

    public String getVersion() {
        return Optional.ofNullable(version.get()).map(Profile.ProfileVersion::getVersion).orElse(null);
    }

    public interface VersionLoadable {
        void loadVersion(Profile profile, String version);
    }
}

