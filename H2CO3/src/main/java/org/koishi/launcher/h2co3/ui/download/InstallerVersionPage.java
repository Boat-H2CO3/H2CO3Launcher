package org.koishi.launcher.h2co3.ui.download;

import static org.koishi.launcher.h2co3core.util.Logging.LOG;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.DownloadProviders;
import org.koishi.launcher.h2co3core.download.RemoteVersion;
import org.koishi.launcher.h2co3core.download.VersionList;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherTempPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherCheckBox;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class InstallerVersionPage extends H2CO3LauncherTempPage implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private final String gameVersion;
    private final String libraryId;
    private final Callback callback;
    private RemoteVersionListAdapter.OnRemoteVersionSelectListener listener;

    private H2CO3LauncherCheckBox checkRelease;
    private H2CO3LauncherCheckBox checkSnapShot;
    private H2CO3LauncherCheckBox checkOld;
    private H2CO3LauncherImageButton refresh;
    private H2CO3LauncherImageButton failedRefresh;
    private H2CO3LauncherProgressBar progressBar;
    private ListView listView;

    public InstallerVersionPage(Context context, int id, H2CO3LauncherUILayout parent, int resId, String gameVersion, String libraryId, Callback callback) {
        super(context, id, parent, resId);
        this.gameVersion = gameVersion;
        this.libraryId = libraryId;
        this.callback = callback;
        create();
    }

    public void create() {
        H2CO3LauncherLinearLayout checkBar = findViewById(R.id.bar);
        checkBar.setVisibility(DownloadProviders.getDownloadProvider().getVersionListById(libraryId).hasType() ? View.VISIBLE : View.GONE);

        checkRelease = findViewById(R.id.release);
        checkSnapShot = findViewById(R.id.snapshot);
        checkOld = findViewById(R.id.old);
        refresh = findViewById(R.id.refresh);
        failedRefresh = findViewById(R.id.failed_refresh);
        progressBar = findViewById(R.id.progress);
        listView = findViewById(R.id.list);

        checkRelease.setChecked(true);

        checkRelease.setOnCheckedChangeListener(this);
        checkSnapShot.setOnCheckedChangeListener(this);
        checkOld.setOnCheckedChangeListener(this);
        refresh.setOnClickListener(this);
        failedRefresh.setOnClickListener(this);

        listener = callback::onSelect;

        refreshList();
    }

    private List<RemoteVersion> loadVersions() {
        return DownloadProviders.getDownloadProvider().getVersionListById(libraryId).getVersions(gameVersion).stream()
                .filter(it -> {
                    switch (it.getVersionType()) {
                        case RELEASE:
                            return checkRelease.isChecked();
                        case SNAPSHOT:
                            return checkSnapShot.isChecked();
                        case OLD:
                            return checkOld.isChecked();
                        default:
                            return true;
                    }
                })
                .sorted().collect(Collectors.toList());
    }

    public void refreshDisplayVersions() {
        List<RemoteVersion> items = loadVersions();
        RemoteVersionListAdapter adapter = new RemoteVersionListAdapter(getContext(), (ArrayList<RemoteVersion>) items, listener);
        listView.setAdapter(adapter);
    }

    public void refreshList() {
        listView.setVisibility(View.GONE);
        failedRefresh.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        refresh.setEnabled(false);
        VersionList<?> currentVersionList = DownloadProviders.getDownloadProvider().getVersionListById(libraryId);
        currentVersionList.refreshAsync(gameVersion).whenComplete((result, exception) -> {
            if (isShowing()) {
                if (exception == null) {
                    List<RemoteVersion> items = loadVersions();

                    Schedulers.androidUIThread().execute(() -> {
                        if (currentVersionList.getVersions(gameVersion).isEmpty()) {
                            Toast.makeText(getContext(), getContext().getString(R.string.download_failed_empty), Toast.LENGTH_SHORT).show();
                            listView.setVisibility(View.GONE);
                            failedRefresh.setVisibility(View.VISIBLE);
                        } else {
                            if (items.isEmpty()) {
                                checkRelease.setChecked(true);
                                checkSnapShot.setChecked(true);
                                checkOld.setChecked(true);
                            } else {
                                RemoteVersionListAdapter adapter = new RemoteVersionListAdapter(getContext(), (ArrayList<RemoteVersion>) items, listener);
                                listView.setAdapter(adapter);
                            }
                            listView.setVisibility(View.VISIBLE);
                            failedRefresh.setVisibility(View.GONE);
                        }
                        progressBar.setVisibility(View.GONE);
                        refresh.setEnabled(true);
                    });
                } else {
                    LOG.log(Level.WARNING, "Failed to fetch versions list", exception);
                    Schedulers.androidUIThread().execute(() -> {
                        listView.setVisibility(View.GONE);
                        failedRefresh.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        refresh.setEnabled(true);
                    });
                }
            }

            System.gc();
        });
    }

    @Override
    public Task<?> refresh(Object... param) {
        return Task.runAsync(() -> {

        });
    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onClick(View view) {
        if (view == refresh || view == failedRefresh) {
            refreshList();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == checkRelease || compoundButton == checkSnapShot || compoundButton == checkOld) {
            refreshDisplayVersions();
        }
    }

    public interface Callback {
        void onSelect(RemoteVersion remoteVersion);
    }
}
