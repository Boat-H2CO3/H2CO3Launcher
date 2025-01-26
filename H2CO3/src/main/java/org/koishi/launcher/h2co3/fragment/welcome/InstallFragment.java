package org.koishi.launcher.h2co3.fragment.welcome;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.H2CO3MainActivity;
import org.koishi.launcher.h2co3.util.RuntimeUtils;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.util.LocaleUtils;
import org.koishi.launcher.h2co3core.util.io.FileUtils;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherFragment;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class InstallFragment extends H2CO3LauncherFragment implements View.OnClickListener {
    private FloatingActionButton nextButton;
    private ProgressBar cacioProgress, cacio11Progress, cacio17Progress, jnaProgress, lwjglProgress, java8Progress, java11Progress, java17Progress, java21Progress;
    private AppCompatImageView lwjglIcon, cacioIcon, cacio11Icon, cacio17Icon, jnaIcon, java8Icon, java11Icon, java17Icon, java21Icon;
    private boolean lwjgl = false;
    private boolean cacio = false;
    private boolean cacio11 = false;
    private boolean cacio17 = false;
    private boolean java8 = false;
    private boolean java11 = false;
    private boolean java17 = false;
    private boolean java21 = false;
    private boolean jna = false;
    private ConstraintLayout root;
    private boolean hasEnteredLauncher = false;
    private boolean installing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_install, container, false);
        root = findViewById(view, R.id.root);
        cacioProgress = findViewById(view, R.id.cacio_task_progress);
        cacioIcon = findViewById(view, R.id.cacio_task_icon);
        cacio11Progress = findViewById(view, R.id.cacio11_task_progress);
        cacio11Icon = findViewById(view, R.id.cacio11_task_icon);
        cacio17Progress = findViewById(view, R.id.cacio17_task_progress);
        cacio17Icon = findViewById(view, R.id.cacio17_task_icon);
        jnaProgress = findViewById(view, R.id.jna_task_progress);
        jnaIcon = findViewById(view, R.id.jna_task_icon);
        lwjglProgress = findViewById(view, R.id.lwjgl_task_progress);
        lwjglIcon = findViewById(view, R.id.lwjgl_task_icon);
        java8Progress = findViewById(view, R.id.java8_task_progress);
        java8Icon = findViewById(view, R.id.java8_task_icon);
        java11Progress = findViewById(view, R.id.java11_task_progress);
        java11Icon = findViewById(view, R.id.java11_task_icon);
        java17Progress = findViewById(view, R.id.java17_task_progress);
        java17Icon = findViewById(view, R.id.java17_task_icon);
        java21Progress = findViewById(view, R.id.java21_task_progress);
        java21Icon = findViewById(view, R.id.java21_task_icon);
        nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
        Schedulers.defaultScheduler().execute(() -> {
            initState();
            Schedulers.androidUIThread().execute(() -> {
                refreshDrawables();
                check();
                if (!isLatest()) {
                    nextButton.setEnabled(true);
                }
            });
        });
        return view;
    }

    private void initState() {
        try {
            lwjgl = RuntimeUtils.isLatest(H2CO3LauncherTools.LWJGL_DIR, "/assets/runtime/lwjgl") && RuntimeUtils.isLatest(H2CO3LauncherTools.LWJGL_DIR + "-h2co3", "/assets/runtime/lwjgl-h2co3");
            cacio = RuntimeUtils.isLatest(H2CO3LauncherTools.CACIOCAVALLO_8_DIR, "/assets/runtime/caciocavallo");
            cacio11 = RuntimeUtils.isLatest(H2CO3LauncherTools.CACIOCAVALLO_11_DIR, "/assets/runtime/caciocavallo11");
            cacio17 = RuntimeUtils.isLatest(H2CO3LauncherTools.CACIOCAVALLO_17_DIR, "/assets/runtime/caciocavallo17");
            java8 = RuntimeUtils.isLatest(H2CO3LauncherTools.JAVA_8_PATH, "/assets/runtime/java/jre8");
            java11 = RuntimeUtils.isLatest(H2CO3LauncherTools.JAVA_11_PATH, "/assets/runtime/java/jre11");
            java17 = RuntimeUtils.isLatest(H2CO3LauncherTools.JAVA_17_PATH, "/assets/runtime/java/jre17");
            java21 = RuntimeUtils.isLatest(H2CO3LauncherTools.JAVA_21_PATH, "/assets/runtime/java/jre21");
            jna = RuntimeUtils.isLatest(H2CO3LauncherTools.JNA_PATH, "/assets/runtime/jna");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshDrawables() {
        if (getContext() != null) {
            var stateUpdate = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_update_24);
            var stateDone = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_done_24);

            if (stateUpdate != null) stateUpdate.setTint(Color.GRAY);
            if (stateDone != null) stateDone.setTint(Color.GRAY);

            lwjglIcon.setBackgroundDrawable(lwjgl ? stateDone : stateUpdate);
            cacioIcon.setBackgroundDrawable(cacio ? stateDone : stateUpdate);
            cacio11Icon.setBackgroundDrawable(cacio11 ? stateDone : stateUpdate);
            cacio17Icon.setBackgroundDrawable(cacio17 ? stateDone : stateUpdate);
            java8Icon.setBackgroundDrawable(java8 ? stateDone : stateUpdate);
            java11Icon.setBackgroundDrawable(java11 ? stateDone : stateUpdate);
            java17Icon.setBackgroundDrawable(java17 ? stateDone : stateUpdate);
            java21Icon.setBackgroundDrawable(java21 ? stateDone : stateUpdate);
            jnaIcon.setBackgroundDrawable(jna ? stateDone : stateUpdate);
        }
    }

    private boolean isLatest() {
        return lwjgl && cacio && cacio11 && cacio17 && java8 && java11 && java17 && java21 && jna;
    }

    private void check() {
        if (isLatest()) {
            enterLauncher();
        }
    }

    private void enterLauncher() {

        if (!hasEnteredLauncher) {
            hasEnteredLauncher = true;
            Intent intent = new Intent(requireActivity(), H2CO3MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }
    }

    private void install() {
        if (installing) return;

        root.post(() -> {
            installing = true;
            if (!lwjgl) {
                lwjglIcon.setVisibility(View.GONE);
                lwjglProgress.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    try {
                        RuntimeUtils.install(getContext(), H2CO3LauncherTools.LWJGL_DIR, "runtime/lwjgl");
                        RuntimeUtils.install(getContext(), H2CO3LauncherTools.LWJGL_DIR + "-h2co3", "runtime/lwjgl-h2co3");
                        lwjgl = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    requireActivity().runOnUiThread(() -> {
                        lwjglIcon.setVisibility(View.VISIBLE);
                        lwjglProgress.setVisibility(View.GONE);
                        refreshDrawables();
                        check();
                    });
                }).start();
            }
            if (!cacio) {
                cacioIcon.setVisibility(View.GONE);
                cacioProgress.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    try {
                        RuntimeUtils.install(getContext(), H2CO3LauncherTools.CACIOCAVALLO_8_DIR, "runtime/caciocavallo");
                        cacio = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    requireActivity().runOnUiThread(() -> {
                        cacioIcon.setVisibility(View.VISIBLE);
                        cacioProgress.setVisibility(View.GONE);
                        refreshDrawables();
                        check();
                    });
                }).start();
            }
            if (!cacio11) {
                cacio11Icon.setVisibility(View.GONE);
                cacio11Progress.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    try {
                        RuntimeUtils.install(getContext(), H2CO3LauncherTools.CACIOCAVALLO_11_DIR, "runtime/caciocavallo11");
                        cacio11 = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(() -> {
                        cacio11Icon.setVisibility(View.VISIBLE);
                        cacio11Progress.setVisibility(View.GONE);
                        refreshDrawables();
                        check();
                    });
                }).start();
            }
            if (!cacio17) {
                cacio17Icon.setVisibility(View.GONE);
                cacio17Progress.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    try {
                        RuntimeUtils.install(getContext(), H2CO3LauncherTools.CACIOCAVALLO_17_DIR, "runtime/caciocavallo17");
                        cacio17 = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    requireActivity().runOnUiThread(() -> {
                        cacio17Icon.setVisibility(View.VISIBLE);
                        cacio17Progress.setVisibility(View.GONE);
                        refreshDrawables();
                        check();
                    });
                }).start();
            }
            if (!java8) {
                java8Icon.setVisibility(View.GONE);
                java8Progress.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    try {
                        RuntimeUtils.installJava(requireContext(), H2CO3LauncherTools.JAVA_8_PATH, "runtime/java/jre8");
                        java8 = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    requireActivity().runOnUiThread(() -> {
                        java8Icon.setVisibility(View.VISIBLE);
                        java8Progress.setVisibility(View.GONE);
                        refreshDrawables();
                        check();
                    });
                }).start();
            }
            if (!java11) {
                java11Icon.setVisibility(View.GONE);
                java11Progress.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    try {
                        RuntimeUtils.installJava(requireContext(), H2CO3LauncherTools.JAVA_11_PATH, "runtime/java/jre11");
                        if (!LocaleUtils.getSystemLocale().getDisplayName().equals(Locale.CHINA.getDisplayName())) {
                            FileUtils.writeText(new File(H2CO3LauncherTools.JAVA_11_PATH + "/resolv.conf"), "nameserver 1.1.1.1\nnameserver 1.0.0.1");
                        } else {
                            FileUtils.writeText(new File(H2CO3LauncherTools.JAVA_11_PATH + "/resolv.conf"), "nameserver 8.8.8.8\nnameserver 8.8.4.4");
                        }
                        java11 = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    requireActivity().runOnUiThread(() -> {
                        java11Icon.setVisibility(View.VISIBLE);
                        java11Progress.setVisibility(View.GONE);
                        refreshDrawables();
                        check();
                    });
                }).start();
            }
            if (!java17) {
                java17Icon.setVisibility(View.GONE);
                java17Progress.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    try {
                        RuntimeUtils.installJava(requireContext(), H2CO3LauncherTools.JAVA_17_PATH, "runtime/java/jre17");
                        if (!LocaleUtils.getSystemLocale().getDisplayName().equals(Locale.CHINA.getDisplayName())) {
                            FileUtils.writeText(new File(H2CO3LauncherTools.JAVA_17_PATH + "/resolv.conf"), "nameserver 1.1.1.1\nnameserver 1.0.0.1");
                        } else {
                            FileUtils.writeText(new File(H2CO3LauncherTools.JAVA_17_PATH + "/resolv.conf"), "nameserver 8.8.8.8\nnameserver 8.8.4.4");
                        }
                        java17 = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    requireActivity().runOnUiThread(() -> {
                        java17Icon.setVisibility(View.VISIBLE);
                        java17Progress.setVisibility(View.GONE);
                        refreshDrawables();
                        check();
                    });
                }).start();
            }
            if (!java21) {
                java21Icon.setVisibility(View.GONE);
                java21Progress.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    try {
                        RuntimeUtils.installJava(requireContext(), H2CO3LauncherTools.JAVA_21_PATH, "runtime/java/jre21");
                        if (!LocaleUtils.getSystemLocale().getDisplayName().equals(Locale.CHINA.getDisplayName())) {
                            FileUtils.writeText(new File(H2CO3LauncherTools.JAVA_21_PATH + "/resolv.conf"), "nameserver 1.1.1.1\nnameserver 1.0.0.1");
                        } else {
                            FileUtils.writeText(new File(H2CO3LauncherTools.JAVA_21_PATH + "/resolv.conf"), "nameserver 8.8.8.8\nnameserver 8.8.4.4");
                        }
                        java21 = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    requireActivity().runOnUiThread(() -> {
                        java21Icon.setVisibility(View.VISIBLE);
                        java21Progress.setVisibility(View.GONE);
                        refreshDrawables();
                        check();
                    });
                }).start();
            }
            if (!jna) {
                jnaIcon.setVisibility(View.GONE);
                jnaProgress.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    try {
                        RuntimeUtils.installJna(getContext(), H2CO3LauncherTools.JNA_PATH, "runtime/jna");
                        jna = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    requireActivity().runOnUiThread(() -> {
                        jnaIcon.setVisibility(View.VISIBLE);
                        jnaProgress.setVisibility(View.GONE);
                        refreshDrawables();
                        check();
                    });
                }).start();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == nextButton) {
            nextButton.setEnabled(false);
            install();
        }
    }
}