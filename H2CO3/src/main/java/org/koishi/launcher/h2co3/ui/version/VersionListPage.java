package org.koishi.launcher.h2co3.ui.version;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Profiles;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3RecyclerView;

public class VersionListPage extends H2CO3LauncherCommonPage implements View.OnClickListener {

    private H2CO3LauncherButton refresh;
    private H2CO3LauncherButton newProfile;
    private H2CO3RecyclerView profileListView;

    private VersionList versionList;

    public VersionListPage(Context context, int id, H2CO3LauncherUILayout parent, int resId) {
        super(context, id, parent, resId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        refresh = findViewById(R.id.refresh);
        newProfile = findViewById(R.id.new_profile);
        profileListView = findViewById(R.id.profile_list);
        H2CO3LauncherProgressBar progressBar = findViewById(R.id.progress);
        H2CO3RecyclerView versionListView = findViewById(R.id.version_list);

        refresh.setOnClickListener(this);
        newProfile.setOnClickListener(this);

        refreshProfile();
        versionList = new VersionList(getContext(), versionListView, refresh, progressBar);
    }

    @Override
    public Task<?> refresh(Object... param) {
        return Task.runAsync(() -> {

        });
    }

    public void refreshProfile() {
        ProfileListAdapter adapter = new ProfileListAdapter(getContext(), Profiles.getProfiles());
        profileListView.setLayoutManager(new LinearLayoutManager(getContext()));
        profileListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view == refresh) {
            versionList.refreshList();
        }
        if (view == newProfile) {
            AddProfileDialog dialog = new AddProfileDialog(getContext());
            dialog.createDialog();
        }
    }
}
