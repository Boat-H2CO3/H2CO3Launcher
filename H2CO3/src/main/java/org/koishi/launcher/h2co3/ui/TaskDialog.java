package org.koishi.launcher.h2co3.ui;

import static org.koishi.launcher.h2co3.util.AndroidUtils.getLocalizedText;
import static org.koishi.launcher.h2co3core.util.Lang.tryCast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import org.jetbrains.annotations.NotNull;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.util.TaskCancellationAction;
import org.koishi.launcher.h2co3core.download.fabric.FabricAPIInstallTask;
import org.koishi.launcher.h2co3core.download.fabric.FabricInstallTask;
import org.koishi.launcher.h2co3core.download.forge.ForgeNewInstallTask;
import org.koishi.launcher.h2co3core.download.forge.ForgeOldInstallTask;
import org.koishi.launcher.h2co3core.download.game.GameAssetDownloadTask;
import org.koishi.launcher.h2co3core.download.game.GameInstallTask;
import org.koishi.launcher.h2co3core.download.liteloader.LiteLoaderInstallTask;
import org.koishi.launcher.h2co3core.download.neoforge.NeoForgeInstallTask;
import org.koishi.launcher.h2co3core.download.neoforge.NeoForgeOldInstallTask;
import org.koishi.launcher.h2co3core.download.optifine.OptiFineInstallTask;
import org.koishi.launcher.h2co3core.mod.MinecraftInstanceTask;
import org.koishi.launcher.h2co3core.mod.ModpackInstallTask;
import org.koishi.launcher.h2co3core.mod.ModpackUpdateTask;
import org.koishi.launcher.h2co3core.mod.curse.CurseCompletionTask;
import org.koishi.launcher.h2co3core.mod.curse.CurseInstallTask;
import org.koishi.launcher.h2co3core.mod.mcbbs.McbbsModpackCompletionTask;
import org.koishi.launcher.h2co3core.mod.mcbbs.McbbsModpackExportTask;
import org.koishi.launcher.h2co3core.mod.modrinth.ModrinthCompletionTask;
import org.koishi.launcher.h2co3core.mod.modrinth.ModrinthInstallTask;
import org.koishi.launcher.h2co3core.mod.multimc.MultiMCModpackExportTask;
import org.koishi.launcher.h2co3core.mod.multimc.MultiMCModpackInstallTask;
import org.koishi.launcher.h2co3core.mod.server.ServerModpackCompletionTask;
import org.koishi.launcher.h2co3core.mod.server.ServerModpackExportTask;
import org.koishi.launcher.h2co3core.mod.server.ServerModpackLocalInstallTask;
import org.koishi.launcher.h2co3core.task.FileDownloadTask;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.task.TaskExecutor;
import org.koishi.launcher.h2co3core.task.TaskListener;
import org.koishi.launcher.h2co3core.util.Lang;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3library.component.H2CO3RecycleAdapter;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LinearProgress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TaskDialog extends H2CO3CustomViewDialog implements View.OnClickListener {

    private final RecyclerView leftTaskListView, rightTaskListView;
    private final Consumer<FileDownloadTask.SpeedEvent> speedEventHandler;
    private TaskExecutor executor;
    private TaskCancellationAction onCancel;
    private MaterialToolbar speedView;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public TaskDialog(@NonNull Context context, @NotNull TaskCancellationAction cancel) {
        super(context, org.koishi.launcher.h2co3library.R.style.ThemeOverlay_App_MaterialAlertDialog_FullScreen);
        setCustomView(R.layout.dialog_task);
        alertDialog = create();
        alertDialog.setCancelable(false);

        rightTaskListView = findViewById(R.id.list_right);
        leftTaskListView = findViewById(R.id.list_left);
        speedView = findViewById(R.id.tb_speed);

        setCancel(cancel);

        setNegativeButton(org.koishi.launcher.h2co3library.R.string.dialog_negative, (dialog, which) -> {
            Optional.ofNullable(executor).ifPresent(TaskExecutor::cancel);
            Optional.ofNullable(onCancel).ifPresent(action -> action.getCancellationAction().accept(TaskDialog.this));
            dialog.dismiss();
        });

        speedEventHandler = speedEvent -> {
            String unit = "B/s";
            double speed = speedEvent.getSpeed();
            if (speed > 1024) {
                speed /= 1024;
                unit = "KB/s";
            }
            if (speed > 1024) {
                speed /= 1024;
                unit = "MB/s";
            }
            double finalSpeed = speed;
            String finalUnit = unit;
            Schedulers.androidUIThread().execute(() -> {
                speedView.setTitle(String.format("%.1f %s", finalSpeed, finalUnit));
            });
        };
        FileDownloadTask.speedEvent.channel(FileDownloadTask.SpeedEvent.class).registerWeak(speedEventHandler);
    }

    public void setExecutor(TaskExecutor executor) {
        setExecutor(executor, true);
    }

    public void setExecutor(TaskExecutor executor, boolean autoClose) {
        this.executor = executor;

        if (executor != null) {
            if (autoClose) {
                executor.addTaskListener(new TaskListener() {
                    @Override
                    public void onStop(boolean success, TaskExecutor executor) {
                        Schedulers.androidUIThread().execute(alertDialog::dismiss);
                    }
                });
            }

            LeftTaskListPane leftTaskListPane = new LeftTaskListPane(getContext(), new ArrayList<>());
            leftTaskListView.setLayoutManager(new LinearLayoutManager(getContext()));
            leftTaskListView.setAdapter(leftTaskListPane);
            RightTaskListPane rightTaskListPane = new RightTaskListPane(getContext(), new ArrayList<>());
            rightTaskListView.setLayoutManager(new LinearLayoutManager(getContext()));
            rightTaskListView.setAdapter(rightTaskListPane);
        }
    }

    public void setCancel(TaskCancellationAction onCancel) {
        this.onCancel = onCancel;
    }

    @Override
    public void onClick(View view) {
    }

    public class LeftTaskListPane extends H2CO3RecycleAdapter<View> {

        private final List<StageNode> stageNodes = new ArrayList<>();

        public LeftTaskListPane(Context context, ArrayList<View> listBox) {
            super(listBox, context);
            setExecutor(executor);
        }

        @Override
        protected void bindData(BaseViewHolder holder, int position) {
            View view = data.get(position);
            LinearLayoutCompat container = (LinearLayoutCompat) holder.itemView;
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            container.removeAllViews();
            container.addView(view);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_downloading_state;
        }

        private void setExecutor(TaskExecutor executor) {
            List<String> stages = Lang.removingDuplicates(executor.getStages());
            executor.addTaskListener(new TaskListener() {
                @Override
                public void onStart() {
                    Schedulers.androidUIThread().execute(() -> {
                        stageNodes.clear();
                        data.clear();
                        List<StageNode> newNodes = stages.stream().map(it -> new StageNode(getContext(), it)).collect(Collectors.toList());
                        stageNodes.addAll(newNodes);
                        for (StageNode stageNode : stageNodes) {
                            data.add(stageNode.getView());
                            notifyItemInserted(data.size() - 1);
                        }
                    });
                }

                @Override
                public void onReady(Task<?> task) {
                    if (task.getStage() != null) {
                        updateStageNode(task.getStage(), StageNode::begin);
                    }
                }

                @Override
                public void onRunning(Task<?> task) {
                    if (!task.getSignificance().shouldShow() || task.getName() == null)
                        return;

                    setTaskName(task);
                }

                @Override
                public void onFinished(Task<?> task) {
                    if (task.getStage() != null) {
                        updateStageNode(task.getStage(), StageNode::succeed);
                    }
                }

                @Override
                public void onFailed(Task<?> task, Throwable throwable) {
                    if (task.getStage() != null) {
                        updateStageNode(task.getStage(), StageNode::fail);
                    }
                }

                @Override
                public void onPropertiesUpdate(Task<?> task) {
                    if (task instanceof Task.CountTask) {
                        Schedulers.androidUIThread().execute(() -> stageNodes.stream()
                                .filter(x -> x.stage.equals(((Task<?>.CountTask) task).getCountStage()))
                                .findAny()
                                .ifPresent(StageNode::count));
                        return;
                    }

                    if (task.getStage() != null) {
                        Schedulers.androidUIThread().execute(() -> {
                            int total = tryCast(task.getProperties().get("total"), Integer.class).orElse(0);
                            stageNodes.stream()
                                    .filter(x -> x.stage.equals(task.getStage()))
                                    .findAny()
                                    .ifPresent(stageNode -> stageNode.setTotal(total));
                        });
                    }
                }

                private void updateStageNode(String stage, Consumer<StageNode> action) {
                    Schedulers.androidUIThread().execute(() -> {
                        for (int i = 0; i < stageNodes.size(); i++) {
                            if (stageNodes.get(i).stage.equals(stage)) {
                                action.accept(stageNodes.get(i));
                                notifyItemChanged(i);
                                break;
                            }
                        }
                    });
                }

                private void setTaskName(Task<?> task) {
                    String name = getTaskName(task);
                    if (name != null) {
                        task.setName(name);
                    }
                }

                private String getTaskName(Task<?> task) {
                    if (task instanceof GameAssetDownloadTask) {
                        return getLocalizedText(getContext(), "assets_download_all");
                    } else if (task instanceof GameInstallTask) {
                        return getLocalizedText(getContext(), "install_installer_install", getLocalizedText(getContext(), "install_installer_game"));
                    } else if (task instanceof ForgeNewInstallTask || task instanceof ForgeOldInstallTask) {
                        return getLocalizedText(getContext(), "install_installer_install", getLocalizedText(getContext(), "install_installer_forge"));
                    } else if (task instanceof NeoForgeInstallTask || task instanceof NeoForgeOldInstallTask) {
                        return getLocalizedText(getContext(), "install_installer_install", getLocalizedText(getContext(), "install_installer_neoforge"));
                    } else if (task instanceof LiteLoaderInstallTask) {
                        return getLocalizedText(getContext(), "install_installer_install", getLocalizedText(getContext(), "install_installer_liteloader"));
                    } else if (task instanceof OptiFineInstallTask) {
                        return getLocalizedText(getContext(), "install_installer_install", getLocalizedText(getContext(), "install_installer_optifine"));
                    } else if (task instanceof FabricInstallTask) {
                        return getLocalizedText(getContext(), "install_installer_install", getLocalizedText(getContext(), "install_installer_fabric"));
                    } else if (task instanceof FabricAPIInstallTask) {
                        return getLocalizedText(getContext(), "install_installer_install", getLocalizedText(getContext(), "install_installer_fabric_api"));
                    } else if (task instanceof CurseCompletionTask || task instanceof ModrinthCompletionTask || task instanceof ServerModpackCompletionTask || task instanceof McbbsModpackCompletionTask) {
                        return getLocalizedText(getContext(), "modpack_completion");
                    } else if (task instanceof ModpackInstallTask) {
                        return getLocalizedText(getContext(), "modpack_installing");
                    } else if (task instanceof ModpackUpdateTask) {
                        return getLocalizedText(getContext(), "modpack_update");
                    } else if (task instanceof CurseInstallTask) {
                        return getLocalizedText(getContext(), "modpack_install", getLocalizedText(getContext(), "modpack_type_curse"));
                    } else if (task instanceof MultiMCModpackInstallTask) {
                        return getLocalizedText(getContext(), "modpack_install", getLocalizedText(getContext(), "modpack_type_multimc"));
                    } else if (task instanceof ModrinthInstallTask) {
                        return getLocalizedText(getContext(), "modpack_install", getLocalizedText(getContext(), "modpack_type_modrinth"));
                    } else if (task instanceof ServerModpackLocalInstallTask) {
                        return getLocalizedText(getContext(), "modpack_install", getLocalizedText(getContext(), "modpack_type_server"));
                    } else if (task instanceof McbbsModpackExportTask || task instanceof MultiMCModpackExportTask || task instanceof ServerModpackExportTask) {
                        return getLocalizedText(getContext(), "modpack_export");
                    } else if (task instanceof MinecraftInstanceTask) {
                        return getLocalizedText(getContext(), "modpack_scan");
                    }
                    return null;
                }
            });
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private static class StageNode {
            private final Context context;
            private final String stage;
            private final String message;
            private final View parent;
            private final H2CO3LauncherTextView title;
            private final H2CO3LauncherImageView icon;
            private int count = 0;
            private int total = 0;
            private boolean started = false;

            public StageNode(Context context, String stage) {
                this.context = context;
                this.stage = stage;

                parent = LayoutInflater.from(context).inflate(R.layout.item_downloading_state, null);
                title = parent.findViewById(R.id.title);
                icon = parent.findViewById(R.id.icon);

                String stageKey = StringUtils.substringBefore(stage, ':');
                String stageValue = StringUtils.substringAfter(stage, ':');

                message = getStageMessage(stageKey, stageValue);
                title.setText(message);
                icon.setImageDrawable(context.getDrawable(R.drawable.ic_menu_custom));
            }

            private String getStageMessage(String stageKey, String stageValue) {
                switch (stageKey) {
                    case "h2co3.modpack": return getLocalizedText(context, "install_modpack");
                    case "h2co3.modpack.download": return getLocalizedText(context, "launch_state_modpack");
                    case "h2co3.install.assets": return getLocalizedText(context, "assets_download");
                    case "h2co3.install.game": return getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_game") + " " + stageValue);
                    case "h2co3.install.forge": return getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_forge") + " " + stageValue);
                    case "h2co3.install.neoforge": return getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_neoforge") + " " + stageValue);
                    case "h2co3.install.liteloader": return getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_liteloader") + " " + stageValue);
                    case "h2co3.install.optifine": return getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_optifine") + " " + stageValue);
                    case "h2co3.install.fabric": return getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_fabric") + " " + stageValue);
                    case "h2co3.install.fabric-api": return getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_fabric-api") + " " + stageValue);
                    case "h2co3.install.quilt": return getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_quilt") + " " + stageValue);
                    default: return getLocalizedText(context, stageKey.replace(".", "_").replace("-", "_"));
                }
            }

            public void begin() {
                if (started) return;
                started = true;
                icon.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_right_black));
            }

            public void fail() {
                icon.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_close_24));
            }

            public void succeed() {
                icon.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_done_24));
            }

            public void count() {
                updateCounter(++count, total);
            }

            public void setTotal(int total) {
                this.total = total;
                updateCounter(count, total);
            }

            @SuppressLint("DefaultLocale")
            public void updateCounter(int count, int total) {
                if (total > 0)
                    title.setText(String.format("%s - %d/%d", message, count, total));
                else
                    title.setText(message);
            }

            public View getView() {
                return parent;
            }
        }
    }

    public final class RightTaskListPane extends H2CO3RecycleAdapter<Task<?>> {
        private final Map<Task<?>, ProgressListNode> nodes = new HashMap<>();

        public RightTaskListPane(Context context, ArrayList<Task<?>> taskList) {
            super(taskList, context);
            setExecutor(executor);
        }

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false);
            return new BaseViewHolder(view);
        }

        @Override
        protected void bindData(BaseViewHolder holder, int position) {
            H2CO3LinearProgress bar = holder.itemView.findViewById(R.id.fileProgress);
            H2CO3LauncherTextView title = holder.itemView.findViewById(R.id.fileNameText);
            H2CO3LauncherTextView state = holder.itemView.findViewById(R.id.state);
            Task<?> task = data.get(position);

            if (task.getName() != null) {
                bar.percentProgressProperty().bind(task.progressProperty());
                title.setText(task.getName());
                state.stringProperty().bind(task.messageProperty());
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_download;
        }

        private void setExecutor(TaskExecutor executor) {
            executor.addTaskListener(new TaskListener() {
                @Override
                public void onRunning(Task<?> task) {
                    if (!task.getSignificance().shouldShow() || task.getName() == null) return;

                    Schedulers.androidUIThread().execute(() -> addTask(task));
                }

                @Override
                public void onFinished(Task<?> task) {
                    Schedulers.androidUIThread().execute(() -> removeTask(task, null));
                }

                @Override
                public void onFailed(Task<?> task, Throwable throwable) {
                    Schedulers.androidUIThread().execute(() -> removeTask(task, throwable));
                }
            });
        }

        private void addTask(Task<?> task) {
            ProgressListNode node = new ProgressListNode(getContext(), task);
            nodes.put(task, node);
            data.add(task);
            notifyItemInserted(data.size() - 1);
        }

        private void removeTask(Task<?> task, Throwable throwable) {
            ProgressListNode node = nodes.remove(task);
            if (node != null) {
                if (throwable != null) {
                    node.setThrowable(throwable);
                } else {
                    node.unbind();
                }
                int index = data.indexOf(task);
                if (index != -1) {
                    remove(index);
                }
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private static class ProgressListNode {
            private final H2CO3LinearProgress bar;
            private final H2CO3LauncherTextView state;

            public ProgressListNode(Context context, Task<?> task) {
                View parent = LayoutInflater.from(context).inflate(R.layout.item_download, null);
                bar = parent.findViewById(R.id.fileProgress);
                H2CO3LauncherTextView title = parent.findViewById(R.id.fileNameText);
                state = parent.findViewById(R.id.state);

                if (task.getName() != null) {
                    bar.percentProgressProperty().bind(task.progressProperty());
                    title.setText(task.getName());
                    state.stringProperty().bind(task.messageProperty());
                }
            }

            public void unbind() {
                bar.percentProgressProperty().unbind();
                state.stringProperty().unbind();
            }

            public void setThrowable(Throwable throwable) {
                unbind();
                state.setText(throwable.getLocalizedMessage());
                bar.setProgress(0);
            }
        }
    }
}