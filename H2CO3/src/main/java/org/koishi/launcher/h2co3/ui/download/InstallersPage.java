package org.koishi.launcher.h2co3.ui.download;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.game.H2CO3LauncherGameRepository;
import org.koishi.launcher.h2co3.setting.Profiles;
import org.koishi.launcher.h2co3.ui.InstallerItem;
import org.koishi.launcher.h2co3.ui.PageManager;
import org.koishi.launcher.h2co3.ui.TaskDialog;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.TaskCancellationAction;
import org.koishi.launcher.h2co3core.download.ArtifactMalformedException;
import org.koishi.launcher.h2co3core.download.DefaultDependencyManager;
import org.koishi.launcher.h2co3core.download.GameBuilder;
import org.koishi.launcher.h2co3core.download.LibraryAnalyzer;
import org.koishi.launcher.h2co3core.download.RemoteVersion;
import org.koishi.launcher.h2co3core.download.UnsupportedInstallationException;
import org.koishi.launcher.h2co3core.download.VersionMismatchException;
import org.koishi.launcher.h2co3core.download.game.GameAssetIndexDownloadTask;
import org.koishi.launcher.h2co3core.download.game.LibraryDownloadException;
import org.koishi.launcher.h2co3core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3core.task.DownloadException;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.task.TaskExecutor;
import org.koishi.launcher.h2co3core.task.TaskListener;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3core.util.io.ResponseCodeException;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3MaterialDialog;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherTempPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.zip.ZipException;

public class InstallersPage extends H2CO3LauncherTempPage implements View.OnClickListener {

    private final String gameVersion;
    private final Map<String, RemoteVersion> map = new HashMap<>();
    private InstallerItem.InstallerItemGroup group;
    private LinearLayoutCompat nameBar;

    private H2CO3LauncherEditText editText;
    private H2CO3LauncherImageButton install;
    private AlertDialog taskListPaneAlert;

    public InstallersPage(Context context, int id, H2CO3LauncherUILayout parent, int resId, final String gameVersion) {
        super(context, id, parent, resId);
        this.gameVersion = gameVersion;
        onCreate(gameVersion);
    }

    public static void alertFailureMessage(Context context, Exception exception, Runnable next) {
        H2CO3MaterialDialog builder = new H2CO3MaterialDialog(context);
        builder.setCancelable(false);
        builder.setNegativeButton(context.getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), (dialogInterface, i) -> next.run());
        String title;
        String msg;
        if (exception instanceof LibraryDownloadException) {
            String message = AndroidUtils.getLocalizedText(context, "launch_failed_download_library", ((LibraryDownloadException) exception).getLibrary().getName()) + "\n";
            if (exception.getCause() instanceof ResponseCodeException) {
                ResponseCodeException rce = (ResponseCodeException) exception.getCause();
                int responseCode = rce.getResponseCode();
                URL url = rce.getUrl();
                if (responseCode == 404)
                    message += AndroidUtils.getLocalizedText(context, "download_code_404", url);
                else
                    message += AndroidUtils.getLocalizedText(context, "download_failed", url, responseCode);
            } else {
                message += StringUtils.getStackTrace(exception.getCause());
            }
            title = context.getString(R.string.install_failed_downloading);
            msg = message;
        } else if (exception instanceof DownloadException) {
            URL url = ((DownloadException) exception).getUrl();
            if (exception.getCause() instanceof SocketTimeoutException) {
                title = context.getString(R.string.install_failed_downloading);
                msg = AndroidUtils.getLocalizedText(context, "install_failed_downloading_timeout", url);
            } else if (exception.getCause() instanceof ResponseCodeException) {
                ResponseCodeException responseCodeException = (ResponseCodeException) exception.getCause();
                if (AndroidUtils.hasStringId(context, "download_code_" + responseCodeException.getResponseCode())) {
                    title = context.getString(R.string.install_failed_downloading);
                    msg = AndroidUtils.getLocalizedText(context, "download_code_" + responseCodeException.getResponseCode(), url);
                } else {
                    title = context.getString(R.string.install_failed_downloading);
                    msg = AndroidUtils.getLocalizedText(context, "install_failed_downloading_detail", url);
                }
            } else {
                title = context.getString(R.string.install_failed_downloading);
                msg = AndroidUtils.getLocalizedText(context, "install_failed_downloading_detail", url) + "\n" + StringUtils.getStackTrace(exception.getCause());
            }
        } else if (exception instanceof UnsupportedInstallationException) {
            if (((UnsupportedInstallationException) exception).getReason() == UnsupportedInstallationException.FORGE_1_17_OPTIFINE_H1_PRE2) {
                title = context.getString(R.string.install_failed);
                msg = context.getString(R.string.install_failed_optifine_forge_1_17);
            } else {
                title = context.getString(R.string.install_failed);
                msg = context.getString(R.string.install_failed_optifine_conflict);
            }
        } else if (exception instanceof DefaultDependencyManager.UnsupportedLibraryInstallerException) {
            title = context.getString(R.string.install_failed);
            msg = context.getString(R.string.install_failed_install_online);
        } else if (exception instanceof ArtifactMalformedException || exception instanceof ZipException) {
            title = context.getString(R.string.install_failed);
            msg = context.getString(R.string.install_failed_malformed);
        } else if (exception instanceof GameAssetIndexDownloadTask.GameAssetIndexMalformedException) {
            title = context.getString(R.string.install_failed);
            msg = context.getString(R.string.assets_index_malformed);
        } else if (exception instanceof VersionMismatchException) {
            VersionMismatchException e = ((VersionMismatchException) exception);
            title = context.getString(R.string.install_failed);
            msg = AndroidUtils.getLocalizedText(context, "install_failed_version_mismatch", e.getExpect(), e.getActual());
        } else if (exception instanceof CancellationException) {
            // Ignore cancel
            title = "";
            msg = "";
        } else {
            title = context.getString(R.string.install_failed);
            msg = StringUtils.getStackTrace(exception);
        }
        builder.setTitle(title);
        builder.setMessage(msg);
        if (!(exception instanceof CancellationException)) {
            builder.create().show();
        }
    }

    public void onCreate(String gameVersion) {
        group = new InstallerItem.InstallerItemGroup(getContext(), gameVersion);
        nameBar = findViewById(R.id.name_bar);

        editText = findViewById(R.id.edit);
        install = findViewById(R.id.install);
        editText.setText(gameVersion);
        install.setOnClickListener(this);

        ScrollView scrollView = findViewById(R.id.scroll);
        scrollView.addView(group.getView());
        ViewGroup.LayoutParams layoutParams = scrollView.getChildAt(0).getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        scrollView.getChildAt(0).setLayoutParams(layoutParams);

        for (InstallerItem library : group.getLibraries()) {
            String libraryId = library.getLibraryId();
            if (libraryId.equals("game")) continue;
            library.action.set(() -> {
                if (LibraryAnalyzer.LibraryType.FABRIC_API.getPatchId().equals(libraryId)) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                    builder.setCancelable(false);
                    builder.setMessage(getContext().getString(R.string.install_installer_fabric_api_warning));
                    builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                    builder.show();
                }

                if (library.incompatibleLibraryName.get() == null) {
                    InstallerVersionPage page = new InstallerVersionPage(getContext(), PageManager.PAGE_ID_TEMP, getParent(), R.layout.page_install_version, gameVersion, libraryId, remoteVersion -> {
                        map.put(libraryId, remoteVersion);
                        DownloadPageManager.getInstance().dismissCurrentTempPage();
                    });
                    DownloadPageManager.getInstance().showTempPage(page);
                }
            });
            library.removeAction.set(() -> {
                map.remove(libraryId);
                reload();
            });
        }
    }

    @Override
    public Task<?> refresh(Object... param) {
        return Task.runAsync(() -> {

        });
    }

    @Override
    public void onRestart() {
        reload();
    }

    @Override
    public void onClick(View view) {
        if (view == install) {
            if (StringUtils.isBlank(Objects.requireNonNull(editText.getText()).toString())) {
                Toast.makeText(getContext(), getContext().getString(R.string.input_not_empty), Toast.LENGTH_SHORT).show();
            } else if (Profiles.getSelectedProfile().getRepository().versionIdConflicts(editText.getText().toString())) {
                Toast.makeText(getContext(), getContext().getString(R.string.install_new_game_already_exists), Toast.LENGTH_SHORT).show();
            } else if (!H2CO3LauncherGameRepository.isValidVersionId(editText.getText().toString())) {
                Toast.makeText(getContext(), getContext().getString(R.string.install_new_game_malformed), Toast.LENGTH_SHORT).show();
            } else {
                GameBuilder builder = Profiles.getSelectedProfile().getDependency().gameBuilder();

                String name = editText.getText().toString();
                builder.name(name);
                builder.gameVersion(gameVersion);

                for (Map.Entry<String, RemoteVersion> entry : map.entrySet()) {
                    if (!LibraryAnalyzer.LibraryType.MINECRAFT.getPatchId().equals(entry.getKey())) {
                        builder.version(entry.getValue());
                    }
                }

                Task<Void> task = builder.buildAsync().whenComplete(any -> Profiles.getSelectedProfile().getRepository().refreshVersions())
                        .thenRunAsync(Schedulers.androidUIThread(), () -> Profiles.getSelectedProfile().setSelectedVersion(name));

                TaskDialog taskListPane = new TaskDialog(getContext(), new TaskCancellationAction(TaskDialog::dismissDialog));
                taskListPaneAlert = taskListPane.create();
                taskListPane.setCancel(new TaskCancellationAction(taskListPaneAlert::dismiss));
                taskListPaneAlert.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                taskListPaneAlert.show();

                Schedulers.androidUIThread().execute(() -> {
                    TaskExecutor executor = task.executor(new TaskListener() {
                        @Override
                        public void onStop(boolean success, TaskExecutor executor) {
                            Schedulers.androidUIThread().execute(() -> {
                                taskListPaneAlert.dismiss();
                                if (success) {
                                    H2CO3LauncherTools.showMessage(H2CO3MessageManager.NotificationItem.Type.INFO, getContext().getString(R.string.install_success));
                                } else if (executor.getException() != null) {
                                    H2CO3LauncherTools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, String.valueOf(executor.getException()));
                                }
                            });
                        }
                    });
                    taskListPane.setExecutor(executor, true);
                    taskListPaneAlert.show();
                    executor.start();
                });
            }
        }
    }

    private String getVersion(String id) {
        return Objects.requireNonNull(map.get(id)).getSelfVersion();
    }

    protected void reload() {
        for (InstallerItem library : group.getLibraries()) {
            String libraryId = library.getLibraryId();
            if (map.containsKey(libraryId)) {
                library.libraryVersion.set(getVersion(libraryId));
                library.removable.set(true);
            } else {
                library.libraryVersion.set(null);
                library.removable.set(false);
            }
        }
    }
}
