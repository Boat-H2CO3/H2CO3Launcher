/*
 * Hello Minecraft! Launcher
 * Copyright (C) 2020  huangyuhui <huanghongxun2008@126.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.koishi.launcher.h2co3.game;

import static org.koishi.launcher.h2co3.util.AndroidUtils.getLocalizedText;
import static org.koishi.launcher.h2co3.util.AndroidUtils.hasStringId;
import static org.koishi.launcher.h2co3core.util.Logging.LOG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.JVMActivity;
import org.koishi.launcher.h2co3.control.MenuType;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.setting.Profiles;
import org.koishi.launcher.h2co3.setting.VersionSetting;
import org.koishi.launcher.h2co3.ui.TaskDialog;
import org.koishi.launcher.h2co3.util.TaskCancellationAction;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.auth.Account;
import org.koishi.launcher.h2co3core.auth.AuthInfo;
import org.koishi.launcher.h2co3core.auth.AuthenticationException;
import org.koishi.launcher.h2co3core.auth.CharacterDeletedException;
import org.koishi.launcher.h2co3core.auth.CredentialExpiredException;
import org.koishi.launcher.h2co3core.auth.authlibinjector.AuthlibInjectorDownloadException;
import org.koishi.launcher.h2co3core.download.DefaultDependencyManager;
import org.koishi.launcher.h2co3core.download.MaintainTask;
import org.koishi.launcher.h2co3core.download.game.GameAssetIndexDownloadTask;
import org.koishi.launcher.h2co3core.download.game.GameVerificationFixTask;
import org.koishi.launcher.h2co3core.download.game.LibraryDownloadException;
import org.koishi.launcher.h2co3core.game.JavaVersion;
import org.koishi.launcher.h2co3core.game.LaunchOptions;
import org.koishi.launcher.h2co3core.game.Version;
import org.koishi.launcher.h2co3core.mod.ModpackCompletionException;
import org.koishi.launcher.h2co3core.mod.ModpackConfiguration;
import org.koishi.launcher.h2co3core.mod.ModpackProvider;
import org.koishi.launcher.h2co3core.task.DownloadException;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.task.TaskExecutor;
import org.koishi.launcher.h2co3core.task.TaskListener;
import org.koishi.launcher.h2co3core.util.Lang;
import org.koishi.launcher.h2co3core.util.LibFilter;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3core.util.io.ResponseCodeException;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;

import org.lwjgl.glfw.CallbackBridge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public final class LauncherHelper {

    private final Context context;
    private final Profile profile;
    private final Account account;
    private final String selectedVersion;
    private final VersionSetting setting;
    private final TaskDialog launchingStepsPane;

    public LauncherHelper(Context context, Profile profile, Account account, String selectedVersion) {
        this.context = Objects.requireNonNull(context);
        this.profile = Objects.requireNonNull(profile);
        this.account = Objects.requireNonNull(account);
        this.selectedVersion = Objects.requireNonNull(selectedVersion);
        this.setting = profile.getVersionSetting(selectedVersion);
        this.launchingStepsPane = new TaskDialog(context, new TaskCancellationAction(TaskDialog::dismissDialog));
        this.launchingStepsPane.setTitle(context.getString(R.string.version_launch));
    }

    private static Task<JavaVersion> checkGameState(Context context, VersionSetting setting, Version version) {
        if (setting.isNotCheckJVM()) {
            return Task.composeAsync(() -> setting.getJavaVersion(version))
                    .withStage("launch.state.java");
        }

        return Task.composeAsync(() -> setting.getJavaVersion(version))
                .thenComposeAsync(javaVersion -> Task.allOf(Task.completed(javaVersion), Task.supplyAsync(() -> JavaVersion.getSuitableJavaVersion(version))))
                .thenComposeAsync(Schedulers.androidUIThread(), javaVersions -> {
                    JavaVersion javaVersion = (JavaVersion) javaVersions.get(0);
                    JavaVersion suggestedJavaVersion = (JavaVersion) javaVersions.get(1);
                    if (setting.getJava().equals(JavaVersion.JAVA_AUTO.getVersionName()) || javaVersion.getVersion() == suggestedJavaVersion.getVersion()) {
                        return Task.completed(suggestedJavaVersion);
                    }

                    CompletableFuture<JavaVersion> future = new CompletableFuture<>();
                    Runnable continueAction = () -> future.complete(javaVersion);
                    H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage(context.getString(R.string.launch_error_java));
                    builder.setPositiveButton(context.getString(R.string.launch_error_java_auto), () -> {
                        setting.setJava(JavaVersion.JAVA_AUTO.getVersionName());
                        future.complete(suggestedJavaVersion);
                    });
                    builder.setNegativeButton(context.getString(R.string.launch_error_java_continue), continueAction::run);
                    builder.create().show();
                    return Task.fromCompletableFuture(future);
                }).withStage("launch.state.java");
    }

    private static Task<AuthInfo> logIn(Context context, Account account) {
        return Task.composeAsync(() -> {
            try {
                return Task.completed(account.logIn());
            } catch (CredentialExpiredException e) {
                LOG.log(Level.INFO, "Credential has expired", e);

                CompletableFuture<Task<AuthInfo>> future = new CompletableFuture<>();
                Schedulers.androidUIThread().execute(() -> {
                    TipReLoginLoginDialog dialog = new TipReLoginLoginDialog(context, account, future);
                    dialog.show();
                });
                return Task.fromCompletableFuture(future).thenComposeAsync(task -> task);
            } catch (AuthenticationException e) {
                LOG.log(Level.WARNING, "Authentication failed, try skipping refresh", e);

                CompletableFuture<Task<AuthInfo>> future = new CompletableFuture<>();
                Schedulers.androidUIThread().execute(() -> {
                    SkipLoginDialog dialog = new SkipLoginDialog(context, account, future);
                    dialog.show();
                });
                return Task.fromCompletableFuture(future).thenComposeAsync(task -> task);
            }
        });
    }

    public void launch() {
        LOG.info("Launching game version: " + selectedVersion);

        launchingStepsPane.createDialog();
        launch0();
    }

    private void launch0() {
        H2CO3LauncherGameRepository repository = profile.getRepository();
        DefaultDependencyManager dependencyManager = profile.getDependency();
        AtomicReference<Version> version = new AtomicReference<>(MaintainTask.maintain(repository, repository.getResolvedVersion(selectedVersion)));
        Optional<String> gameVersion = repository.getGameVersion(version.get());
        boolean integrityCheck = repository.unmarkVersionLaunchedAbnormally(selectedVersion);
        List<String> javaAgents = new ArrayList<>(0);

        AtomicReference<JavaVersion> javaVersionRef = new AtomicReference<>();

        TaskExecutor executor = checkGameState(context, setting, version.get())
                .thenComposeAsync(javaVersion -> {
                    javaVersionRef.set(Objects.requireNonNull(javaVersion));
                    version.set(LibFilter.filter(version.get()));
                    if (setting.isNotCheckGame())
                        return null;
                    return Task.allOf(
                            dependencyManager.checkGameCompletionAsync(version.get(), integrityCheck),
                            Task.composeAsync(() -> {
                                try {
                                    ModpackConfiguration<?> configuration = ModpackHelper.readModpackConfiguration(repository.getModpackConfiguration(selectedVersion));
                                    ModpackProvider provider = ModpackHelper.getProviderByType(configuration.getType());
                                    if (provider == null) return null;
                                    else
                                        return provider.createCompletionTask(dependencyManager, selectedVersion);
                                } catch (IOException e) {
                                    return null;
                                }
                            }),
                            Task.composeAsync(() -> null)
                    );
                }).withStage("launch.state.dependencies")
                .thenComposeAsync(() -> {
                    try (InputStream input = LauncherHelper.class.getResourceAsStream("/assets/game/H2CO3LibFixer.jar")) {
                        Files.copy(input, new File(H2CO3LauncherTools.LIB_FIXER_PATH).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        Logging.LOG.log(Level.WARNING, "Unable to unpack H2CO3LibFixer.jar", e);
                    }
                    return null;
                })
                .thenComposeAsync(() -> {
                    try (InputStream input = LauncherHelper.class.getResourceAsStream("/assets/game/H2CO3LaunchWrapper.jar")) {
                        Files.copy(input, new File(H2CO3LauncherTools.LAUNCH_WRAPPER).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        Logging.LOG.log(Level.WARNING, "Unable to unpack H2CO3LaunchWrapper.jar", e);
                    }
                    return null;
                })
                .thenComposeAsync(() -> gameVersion.map(s -> new GameVerificationFixTask(dependencyManager, s, version.get())).orElse(null))
                .thenComposeAsync(() -> logIn(context, account).withStage("launch.state.logging_in"))
                .thenComposeAsync(authInfo -> Task.supplyAsync(() -> {
                    LaunchOptions launchOptions = repository.getLaunchOptions(selectedVersion, javaVersionRef.get(), profile.getGameDir(), javaAgents);
                    H2CO3LauncherGameLauncher launcher = new H2CO3LauncherGameLauncher(
                            context,
                            repository,
                            version.get(),
                            authInfo,
                            launchOptions
                    );
                    version.get().getLibraries().forEach(library -> {
                        if (library.getName().startsWith("net.java.dev.jna:jna:")) {
                            launcher.setJnaVersion(library.getVersion());
                        }
                    });
                    return launcher;
                }).thenComposeAsync(launcher -> { // launcher is prev task's result
                    return Task.supplyAsync(launcher::launch);
                }).thenAcceptAsync(h2co3LauncherBridge -> Schedulers.androidUIThread().execute(() -> {
                    CallbackBridge.nativeSetUseInputStackQueue(version.get().getArguments().isPresent());
                    Intent intent = new Intent(context, JVMActivity.class);
                    h2co3LauncherBridge.setScaleFactor(repository.getVersionSetting(selectedVersion).getScaleFactor());
                    h2co3LauncherBridge.setController(repository.getVersionSetting(selectedVersion).getController());
                    h2co3LauncherBridge.setGameDir(repository.getRunDirectory(selectedVersion).getAbsolutePath());
                    h2co3LauncherBridge.setRenderer(repository.getVersionSetting(selectedVersion).getRenderer().toString());
                    h2co3LauncherBridge.setJava(Integer.toString(javaVersionRef.get().getVersion()));
                    checkMod(h2co3LauncherBridge);
                    JVMActivity.setH2CO3LauncherBridge(h2co3LauncherBridge, MenuType.GAME);
                    Bundle bundle = new Bundle();
                    bundle.putString("controller", repository.getVersionSetting(selectedVersion).getController());
                    intent.putExtras(bundle);
                    LOG.log(Level.INFO, "Start JVMActivity!");
                    context.startActivity(intent);
                })).withStage("launch.state.waiting_launching"))
                .withStagesHint(Lang.immutableListOf(
                        "launch.state.java",
                        "launch.state.dependencies",
                        "launch.state.logging_in",
                        "launch.state.waiting_launching"))
                .executor();
        launchingStepsPane.setExecutor(executor, false);
        executor.addTaskListener(new TaskListener() {

            @Override
            public void onStop(boolean success, TaskExecutor executor) {
                launchingStepsPane.alertDialog.dismiss();
                if (!success) {
                    Exception ex = executor.getException();
                    if (ex != null && !(ex instanceof CancellationException)) {
                        Schedulers.androidUIThread().execute(() -> {
                            String message;
                            if (ex instanceof ModpackCompletionException) {
                                if (ex.getCause() instanceof FileNotFoundException)
                                    message = getLocalizedText(context, "modpack_type_curse_not_found");
                                else
                                    message = getLocalizedText(context, "modpack_type_curse_error");
                            } else if (ex instanceof LibraryDownloadException) {
                                message = getLocalizedText(context, "launch_failed_download_library", ((LibraryDownloadException) ex).getLibrary().getName()) + "\n";
                                if (ex.getCause() instanceof ResponseCodeException) {
                                    ResponseCodeException rce = (ResponseCodeException) ex.getCause();
                                    int responseCode = rce.getResponseCode();
                                    URL url = rce.getUrl();
                                    if (responseCode == 404)
                                        message += getLocalizedText(context, "download_code_404", url);
                                    else
                                        message += getLocalizedText(context, "download_failed", url, responseCode);
                                } else {
                                    message += StringUtils.getStackTrace(ex.getCause());
                                }
                            } else if (ex instanceof DownloadException) {
                                URL url = ((DownloadException) ex).getUrl();
                                if (ex.getCause() instanceof SocketTimeoutException) {
                                    message = getLocalizedText(context, "install_failed_downloading_timeout", url);
                                } else if (ex.getCause() instanceof ResponseCodeException) {
                                    ResponseCodeException responseCodeException = (ResponseCodeException) ex.getCause();
                                    if (hasStringId(context, "download_code_" + responseCodeException.getResponseCode())) {
                                        message = getLocalizedText(context, "download_code_" + responseCodeException.getResponseCode(), url);
                                    } else {
                                        message = getLocalizedText(context, "install_failed_downloading_detail", url) + "\n" + StringUtils.getStackTrace(ex.getCause());
                                    }
                                } else {
                                    message = getLocalizedText(context, "install_failed_downloading_detail", url) + "\n" + StringUtils.getStackTrace(ex.getCause());
                                }
                            } else if (ex instanceof GameAssetIndexDownloadTask.GameAssetIndexMalformedException) {
                                message = getLocalizedText(context, "assets_index_malformed");
                            } else if (ex instanceof AuthlibInjectorDownloadException) {
                                message = getLocalizedText(context, "account_failed_injector_download_failure");
                            } else if (ex instanceof CharacterDeletedException) {
                                message = getLocalizedText(context, "account_failed_character_deleted");
                            } else if (ex instanceof ResponseCodeException) {
                                ResponseCodeException rce = (ResponseCodeException) ex;
                                int responseCode = rce.getResponseCode();
                                URL url = rce.getUrl();
                                if (responseCode == 404)
                                    message = getLocalizedText(context, "download_code_404", url);
                                else
                                    message = getLocalizedText(context, "download_failed", url, responseCode);
                            } else if (ex instanceof AccessDeniedException) {
                                message = getLocalizedText(context, "exception_access_denied", ((AccessDeniedException) ex).getFile());
                            } else {
                                if (ex == null) {
                                    message = "Task failed without exception!";
                                } else {
                                    message = StringUtils.getStackTrace(ex);
                                }
                            }

                            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(context);
                            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                            builder.setCancelable(false);
                            builder.setTitle(context.getString(R.string.launch_failed));
                            builder.setMessage(message);
                            builder.setNegativeButton(context.getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                            builder.create().show();
                        });
                    }
                }
            }
        });

        executor.start();
    }

    private void checkMod(H2CO3LauncherBridge bridge) {
        try {
            StringBuilder sb = new StringBuilder();
            Profiles.getSelectedProfile().getRepository().getModManager(Profiles.getSelectedVersion()).getMods().forEach(mod -> {
                if (!mod.isActive()) {
                    return;
                }
                sb.append(mod.getFileName());
                sb.append(" | ");
                sb.append(mod.getId());
                sb.append(" | ");
                sb.append(mod.getVersion());
                sb.append(" | ");
                sb.append(mod.getModLoaderType());
                sb.append("\n");
                if (mod.getId().equals("touchcontroller")) {
                    bridge.setHasTouchController(true);
                }
            });
            bridge.setModSummary(sb.toString());
        } catch (Throwable ignore) {
        }
    }

    static class SkipLoginDialog extends H2CO3LauncherDialog implements View.OnClickListener {

        private final Account account;
        private final CompletableFuture<Task<AuthInfo>> future;

        private H2CO3LauncherButton retry;
        private H2CO3LauncherButton skip;
        private H2CO3LauncherButton cancel;

        public SkipLoginDialog(@NonNull Context context, Account account, CompletableFuture<Task<AuthInfo>> future) {
            super(context);
            this.account = account;
            this.future = future;
            setContentView(R.layout.dialog_skip_login);
            setCancelable(false);

            retry = findViewById(R.id.retry);
            skip = findViewById(R.id.skip);
            cancel = findViewById(R.id.cancel);
            retry.setOnClickListener(this);
            skip.setOnClickListener(this);
            cancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == retry) {
                future.complete(logIn(getContext(), account));
            }
            if (view == skip) {
                try {
                    future.complete(Task.completed(account.playOffline()));
                } catch (AuthenticationException e2) {
                    future.completeExceptionally(e2);
                }
            }
            if (view == cancel) {
                future.completeExceptionally(new CancellationException());
            }
            dismiss();
        }
    }

    static class TipReLoginLoginDialog extends H2CO3LauncherDialog implements View.OnClickListener {

        private final Account account;
        private final CompletableFuture<Task<AuthInfo>> future;

        private H2CO3LauncherButton skip;
        private H2CO3LauncherButton ok;

        public TipReLoginLoginDialog(@NonNull Context context, Account account, CompletableFuture<Task<AuthInfo>> future) {
            super(context);
            this.account = account;
            this.future = future;
            setContentView(R.layout.dialog_tip_relogin);
            setCancelable(false);

            skip = findViewById(R.id.skip);
            ok = findViewById(R.id.ok);
            skip.setOnClickListener(this);
            ok.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == skip) {
                try {
                    future.complete(Task.completed(account.playOffline()));
                } catch (AuthenticationException e2) {
                    future.completeExceptionally(e2);
                }
            }
            if (view == ok) {
                future.completeExceptionally(new CancellationException());
            }
            dismiss();
        }
    }

}
