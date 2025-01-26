package org.koishi.launcher.h2co3.upgrade;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.FileProvider;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.ui.TaskDialog;
import org.koishi.launcher.h2co3.util.TaskCancellationAction;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.task.FileDownloadTask;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.task.TaskExecutor;
import org.koishi.launcher.h2co3core.util.io.NetworkUtils;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

import java.io.File;
import java.util.concurrent.CancellationException;

public class UpdateDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final RemoteVersion version;

    private View parent;
    private ScrollView scrollView;
    private H2CO3LauncherLinearLayout layout;

    private H2CO3LauncherTextView versionName;
    private H2CO3LauncherTextView date;
    private H2CO3LauncherTextView type;
    private H2CO3LauncherTextView description;

    private H2CO3LauncherButton ignore;
    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negative;
    private H2CO3LauncherButton netdisk;

    public UpdateDialog(@NonNull Context context, RemoteVersion version) {
        super(context);
        this.version = version;
        setCancelable(false);
        setContentView(R.layout.dialog_update);

        init();
    }

    private void init() {
        parent = findViewById(R.id.parent);
        scrollView = findViewById(R.id.text_scroll);
        layout = findViewById(R.id.layout);

        versionName = findViewById(R.id.version);
        date = findViewById(R.id.date);
        type = findViewById(R.id.type);
        description = findViewById(R.id.description);

        versionName.setText(String.format(getContext().getString(R.string.update_version), version.getVersionName()));
        date.setText(String.format(getContext().getString(R.string.update_date), version.getDate()));
        type.setText(String.format(getContext().getString(R.string.update_type), version.getDisplayType(getContext())));
        description.setText(String.format(getContext().getString(R.string.update_description), version.getDisplayDescription(getContext())));

        ignore = findViewById(R.id.ignore);
        positive = findViewById(R.id.positive);
        negative = findViewById(R.id.negative);
        netdisk = findViewById(R.id.netdisk);
        ignore.setOnClickListener(this);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
        netdisk.setOnClickListener(this);

        checkHeight();
    }

    private void checkHeight() {
        parent.post(() -> layout.post(() -> {
            WindowManager wm = getWindow().getWindowManager();
            Point point = new Point();
            wm.getDefaultDisplay().getSize(point);
            int maxHeight = point.y - ConvertUtils.dip2px(getContext(), 30);
            if (parent.getMeasuredHeight() < maxHeight) {
                ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
                layoutParams.height = layout.getMeasuredHeight();
                scrollView.setLayoutParams(layoutParams);
                getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            } else {
                getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, maxHeight);
            }
        }));
    }

    @Override
    public void onClick(View v) {
        if (v == ignore) {
            UpdateChecker.setIgnore(getContext(), version.getVersionCode());
            dismiss();
        }
        if (v == positive) {
            TaskDialog dialog = new TaskDialog(getContext(), new TaskCancellationAction(TaskDialog::dismissDialog));
            dialog.setTitle(getContext().getString(R.string.update_launcher));
            Schedulers.androidUIThread().execute(() -> {
                TaskExecutor executor = Task.composeAsync(() -> {
                    FileDownloadTask task = new FileDownloadTask(NetworkUtils.toURL(version.getUrl()), new File(H2CO3LauncherTools.CACHE_DIR, "H2CO3Launcher.apk"));
                    task.setName("H2CO3Launcher");
                    return task.whenComplete(Schedulers.androidUIThread(), exception -> {
                        if (exception == null) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Uri apkUri = FileProvider.getUriForFile(getContext(), getContext().getString(org.koishi.launcher.h2co3library.R.string.file_browser_provider), new File(H2CO3LauncherTools.CACHE_DIR, "H2CO3Launcher.apk"));
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                            getContext().startActivity(intent);
                        } else if (!(exception instanceof CancellationException)) {
                            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                            builder.setCancelable(false);
                            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                            builder.setMessage(getContext().getString(R.string.update_failed) + "\n" + exception.getMessage());
                            builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                            builder.create().show();
                        }
                    });
                }).executor();
                dialog.setExecutor(executor);
                dialog.createDialog();
                executor.start();
            });
            dismiss();
        }
        if (v == negative) {
            dismiss();
        }
        if (v == netdisk) {
            H2CO3LauncherBridge.openLink(version.getNetdiskUrl());
            dismiss();
        }
    }
}
