package org.koishi.launcher.h2co3.ui.controller;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialog;

import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.download.ControllerIndex;
import org.koishi.launcher.h2co3.control.download.ControllerVersion;
import org.koishi.launcher.h2co3.setting.Controller;
import org.koishi.launcher.h2co3.setting.Controllers;
import org.koishi.launcher.h2co3.setting.DownloadProviders;
import org.koishi.launcher.h2co3.ui.TaskDialog;
import org.koishi.launcher.h2co3.util.TaskCancellationAction;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.task.FileDownloadTask;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.task.TaskExecutor;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3core.util.function.ExceptionalConsumer;
import org.koishi.launcher.h2co3core.util.gson.JsonUtils;
import org.koishi.launcher.h2co3core.util.gson.fakefx.factories.JavaFxPropertyTypeAdapterFactory;
import org.koishi.launcher.h2co3core.util.io.FileUtils;
import org.koishi.launcher.h2co3core.util.io.NetworkUtils;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherTempPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

public class ControllerDownloadPage extends H2CO3LauncherTempPage implements View.OnClickListener {

    private final ArrayList<String> categories;
    private final ControllerIndex index;
    private final String url;

    private ControllerVersion controllerVersion;

    private H2CO3LauncherLinearLayout layout;
    private H2CO3LauncherProgressBar progressBar;
    private H2CO3LauncherImageButton retry;
    private H2CO3LauncherImageView icon;
    private H2CO3LauncherTextView name;
    private H2CO3LauncherTextView tag;
    private H2CO3LauncherTextView intro;

    private ListView listView;
    private H2CO3LauncherTextView author;
    private H2CO3LauncherTextView version;
    private H2CO3LauncherTextView device;
    private H2CO3LauncherTextView description;

    private H2CO3LauncherButton history;
    private H2CO3LauncherButton latest;

    public ControllerDownloadPage(Context context, int id, H2CO3LauncherUILayout parent, int resId, int source, ArrayList<String> categories, ControllerIndex index) {
        super(context, id, parent, resId);
        this.categories = categories;
        this.index = index;
        this.url = (source == 0 ? ControllerRepoPage.CONTROLLER_GITHUB : ControllerRepoPage.CONTROLLER_GIT_CN) + "repo_json/" + index.getId() + "/";
        create();
    }

    public void setLoading(boolean loading) {
        Schedulers.androidUIThread().execute(() -> {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            layout.setVisibility(loading ? View.GONE : View.VISIBLE);
            if (loading) {
                retry.setVisibility(View.GONE);
            }
        });
    }

    public void setFailed() {
        Schedulers.androidUIThread().execute(() -> {
            retry.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
        });
    }

    private void refresh() {
        setLoading(true);
        String versionUrl = url + "version.json";
        String screenshotUrl = url + "screenshots/";
        Task.supplyAsync(() -> {
            String versionStr = NetworkUtils.doGet(NetworkUtils.toURL(versionUrl));
            return JsonUtils.GSON.fromJson(versionStr, ControllerVersion.class);
        }).thenAcceptAsync((ExceptionalConsumer<ControllerVersion, Exception>) controllerVersion -> {
            this.controllerVersion = controllerVersion;
            ArrayList<String> screenshotUrls = new ArrayList<>();
            for (int i = 1; i <= controllerVersion.getScreenshot(); i++) {
                String num = i + "";
                if (num.length() == 1)
                    num = "0" + num;
                screenshotUrls.add(screenshotUrl + num + ".png");
            }
            String[] allDevices = new String[] {
                    getContext().getString(R.string.control_download_device_phone),
                    getContext().getString(R.string.control_download_device_pad),
                    getContext().getString(R.string.control_download_device_other)
            };
            ArrayList<String> devices = new ArrayList<>();
            for (int d : index.getDevice()) {
                if (0 <= d && 2 >= d)
                    devices.add(allDevices[d]);
            }
            StringBuilder stringBuilder = new StringBuilder();
            devices.forEach(it -> stringBuilder.append(it).append("  "));
            String deviceText = StringUtils.removeSuffix(stringBuilder.toString(), "  ");
            listView.post(() -> {
                ControllerScreenshotAdapter adapter = new ControllerScreenshotAdapter(getContext(), screenshotUrls);
                listView.setAdapter(adapter);
            });
            author.setText(controllerVersion.getAuthor());
            version.setText(controllerVersion.getLatest().getVersionName());
            device.setText(deviceText);
            description.setText(controllerVersion.getDescription());
        }).whenComplete(Schedulers.androidUIThread(), exception -> {
            setLoading(false);
            if (exception != null) {
                setFailed();
            }
        }).start();
    }

    private void create() {
        layout = findViewById(R.id.layout);
        progressBar = findViewById(R.id.progress);
        retry = findViewById(R.id.retry);
        icon = findViewById(R.id.icon);
        name = findViewById(R.id.name);
        tag = findViewById(R.id.tag);
        intro = findViewById(R.id.intro);

        listView = findViewById(R.id.list);
        author = findViewById(R.id.author);
        version = findViewById(R.id.version);
        device = findViewById(R.id.device);
        description = findViewById(R.id.description);

        history = findViewById(R.id.history);
        latest = findViewById(R.id.latest);

        Glide.with(getContext()).load(url + "icon.png").into(icon);
        name.setText(index.getName());
        StringBuilder stringBuilder = new StringBuilder();
        categories.forEach(it -> stringBuilder.append(it).append("   "));
        String tagText = StringUtils.removeSuffix(stringBuilder.toString(), "   ");
        tag.setText(tagText);
        intro.setText(index.getIntroduction());

        retry.setOnClickListener(this);
        history.setOnClickListener(this);
        latest.setOnClickListener(this);

        refresh();
    }

    private void download(int versionCode) {
        if (Controllers.findControllerById(index.getId()).getId().equals(index.getId())) {
            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
            builder.setCancelable(false);
            builder.setMessage(getContext().getString(R.string.control_download_exist));
            builder.setPositiveButton(() -> downloadFile(versionCode));
            builder.setNegativeButton(null);
            builder.create().show();
        } else {
            downloadFile(versionCode);
        }
    }

    private void downloadFile(int versionCode) {
        FileUtils.deleteDirectoryQuietly(new File(H2CO3LauncherTools.CACHE_DIR + "/control"));
        String downloadUrl = url + "versions/" + versionCode + ".json";
        String destPath = H2CO3LauncherTools.CONTROLLER_DIR + "/" + index.getId() + ".json";
        String cache = H2CO3LauncherTools.CACHE_DIR + "/control/" + index.getId() + ".json";
        boolean exist = new File(destPath).exists();
        Controller old = exist ? Controllers.findControllerById(index.getId()) : null;
        TaskDialog taskDialog = new TaskDialog(getContext(), new TaskCancellationAction(TaskDialog::dismissDialog));
        taskDialog.setTitle(getContext().getString(R.string.message_downloading));
        TaskExecutor executor = Task.composeAsync(() -> {
            if (exist && old != null) {
                FileUtils.copyFile(new File(destPath), new File(cache));
                ((ControllerManagePage) ControllerPageManager.getInstance().getPageById(ControllerPageManager.PAGE_ID_CONTROLLER_MANAGER)).removeController(old);
            }
            FileDownloadTask task = new FileDownloadTask(NetworkUtils.toURL(downloadUrl), new File(destPath));
            task.setName(index.getName());
            return task;
        }).whenComplete(Schedulers.defaultScheduler(), exception -> {
            if (exception != null) {
                if (new File(cache).exists()) {
                    FileUtils.copyFile(new File(cache), new File(destPath));
                    ((ControllerManagePage) ControllerPageManager.getInstance().getPageById(ControllerPageManager.PAGE_ID_CONTROLLER_MANAGER)).addController(old);
                }
                Schedulers.androidUIThread().execute(() -> {
                    if (exception instanceof CancellationException) {
                        Toast.makeText(getContext(), getContext().getString(R.string.message_cancelled), Toast.LENGTH_SHORT).show();
                    } else {
                        H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                        builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                        builder.setCancelable(false);
                        builder.setTitle(getContext().getString(R.string.install_failed_downloading));
                        builder.setMessage(DownloadProviders.localizeErrorMessage(getContext(), exception));
                        builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                        builder.create().show();
                    }
                });
            } else {
                FileUtils.deleteDirectoryQuietly(new File(H2CO3LauncherTools.CACHE_DIR + "/control"));
                Controller controller = new GsonBuilder()
                        .registerTypeAdapterFactory(new JavaFxPropertyTypeAdapterFactory(true, true))
                        .setPrettyPrinting()
                        .create().fromJson(FileUtils.readText(new File(destPath)), Controller.class);
                ((ControllerManagePage) ControllerPageManager.getInstance().getPageById(ControllerPageManager.PAGE_ID_CONTROLLER_MANAGER)).addController(controller);
                Schedulers.androidUIThread().execute(() -> Toast.makeText(getContext(), getContext().getString(R.string.install_success), Toast.LENGTH_SHORT).show());
            }
        }).executor();
        taskDialog.setExecutor(executor);
        taskDialog.createDialog();
        executor.start();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onClick(View view) {
        if (view == retry) {
            refresh();
        }
        if (view == history && controllerVersion != null) {
            if (controllerVersion.getHistory().isEmpty()) {
                Toast.makeText(getContext(), getContext().getString(R.string.control_download_history_empty), Toast.LENGTH_SHORT).show();
            } else {
                OldVersionDialog dialog = new OldVersionDialog(getContext(), controllerVersion.getHistory(), this::download);
                dialog.show();
            }
        }
        if (view == latest && controllerVersion != null) {
            download(controllerVersion.getLatest().getVersionCode());
        }
    }
}
