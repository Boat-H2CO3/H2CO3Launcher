package org.koishi.launcher.h2co3.ui.manage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.ui.TaskDialog;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.TaskCancellationAction;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.game.World;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.task.TaskExecutor;
import org.koishi.launcher.h2co3core.task.TaskListener;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3core.util.platform.OperatingSystem;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;

import java.io.File;
import java.nio.file.Paths;

public class WorldExportDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final World world;
    private final String parent;

    private H2CO3LauncherEditText editFileName;
    private H2CO3LauncherEditText editName;
    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negative;

    @SuppressLint("SetTextI18n")
    public WorldExportDialog(@NonNull Context context, World world, String parent) {
        super(context);
        this.world = world;
        this.parent = parent;
        setCancelable(false);
        setContentView(R.layout.dialog_world_export);

        editFileName = findViewById(R.id.file_name);
        editName = findViewById(R.id.name);
        editFileName.setStringValue(world.getWorldName() + ".zip");
        editName.setStringValue(world.getWorldName());
        editFileName.setText(world.getWorldName() + ".zip");
        editName.setText(world.getWorldName());
        positive = findViewById(R.id.positive);
        negative = findViewById(R.id.negative);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);

        positive.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        editName.getStringValue().isEmpty()
                                || StringUtils.isBlank(editFileName.getStringValue())
                                || !OperatingSystem.isNameValid(editFileName.getStringValue())
                                || new File(parent, editFileName.getStringValue()).exists(),
                editName.stringProperty().isEmpty(), editFileName.stringProperty()));
    }

    @Override
    public void onClick(View v) {
        if (v == positive) {
            TaskDialog taskDialog = new TaskDialog(getContext(), new TaskCancellationAction(TaskDialog::dismissDialog));
            taskDialog.setTitle(getContext().getString(R.string.message_doing));

            Task<?> task = Task.runAsync(AndroidUtils.getLocalizedText(getContext(), "world.export.wizard", editName.getStringValue()), () -> world.export(Paths.get(new File(parent, editFileName.getText().toString()).getAbsolutePath()), editName.getStringValue()));
            TaskExecutor executor = task.executor(new TaskListener() {
                @Override
                public void onStop(boolean success, TaskExecutor executor) {
                    Schedulers.androidUIThread().execute(() -> {
                        if (success) {
                            H2CO3LauncherAlertDialog.Builder builder1 = new H2CO3LauncherAlertDialog.Builder(getContext());
                            builder1.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
                            builder1.setCancelable(false);
                            builder1.setMessage(getContext().getString(R.string.message_success));
                            builder1.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), () -> ManagePageManager.getInstance().dismissAllTempPagesCreatedByPage(ManagePageManager.PAGE_ID_MANAGE_MANAGE));
                            builder1.create().show();
                        } else {
                            if (executor.getException() == null)
                                return;
                            String appendix = StringUtils.getStackTrace(executor.getException());
                            H2CO3LauncherAlertDialog.Builder builder1 = new H2CO3LauncherAlertDialog.Builder(getContext());
                            builder1.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                            builder1.setCancelable(false);
                            builder1.setTitle(getContext().getString(R.string.message_failed));
                            builder1.setMessage(appendix);
                            builder1.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                            builder1.create().show();
                        }
                    });
                }
            });
            taskDialog.setExecutor(executor);
            taskDialog.createDialog();
            executor.start();
            dismiss();
        }
        if (v == negative) {
            dismiss();
        }
    }
}
