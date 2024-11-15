package org.koishi.launcher.h2co3.ui.fragment.manage;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.resources.component.preference.H2CO3EditTextPreference;
import org.koishi.launcher.h2co3.resources.component.preference.H2CO3ListPreference;
import org.koishi.launcher.h2co3.resources.component.preference.H2CO3RangeSliderPreference;
import org.koishi.launcher.h2co3.resources.component.preference.H2CO3SliderPreference;
import org.koishi.launcher.h2co3.resources.component.preference.H2CO3SwitchPreference;
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;

public class GlobalGameSettingFragment extends H2CO3Fragment {
    private View view;

    private static final int JAVA_AUTO = 0;
    private static final int RENDER_DEF = 0;
    private H2CO3ListPreference preferenceChooseJava, preferenceChooseRenderer;
    private H2CO3SwitchPreference preferenceSetPriVerDir;
    private H2CO3RangeSliderPreference preferenceSetGameMemory;
    private H2CO3SliderPreference preferenceSetWindowResolution;
    private H2CO3Settings settings;
    private H2CO3EditTextPreference preferenceSetJoinServer;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settings = new H2CO3Settings();
        view = inflater.inflate(R.layout.fragment_manage_global_setting, container, false);
        preferenceChooseJava = view.findViewById(R.id.preference_choose_java);
        preferenceChooseRenderer = view.findViewById(R.id.preference_choose_renderer);
        preferenceSetPriVerDir = view.findViewById(R.id.preference_set_pri_ver_dir);
        preferenceSetGameMemory = view.findViewById(R.id.preference_set_game_memory);
        preferenceSetWindowResolution = view.findViewById(R.id.preference_set_window_resolution);
        preferenceSetJoinServer = view.findViewById(R.id.preference_set_join_server);

        initChooseJavaPreference();
        initChooseRendererPreference();
        initSetPriVerDirPreference();
        initSetGameMemoryPreference();
        initSetWindowResolution();
        initSetJoinServer();
        return view;
    }

    private void initSetJoinServer() {
        preferenceSetJoinServer.setTitle(getString(org.koishi.launcher.h2co3.library.R.string.title_join_server));
        preferenceSetJoinServer.setHint("AAAAAAAAAAAAA");
        preferenceSetJoinServer.setText(settings.getJoinServer());
        preferenceSetJoinServer.setOnPreferenceChangeListener((preference, newValue) -> {
            settings.setJoinServer(newValue);
        });
    }

    private void initSetWindowResolution() {
        preferenceSetWindowResolution.setTitle(getString(org.koishi.launcher.h2co3.library.R.string.title_window_resolution));
        preferenceSetWindowResolution.initValue(25, 100);
        preferenceSetWindowResolution.setSliderValue((int) settings.getWindowResolution());
        preferenceSetWindowResolution.setStepSize(1);
        preferenceSetWindowResolution.setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    settings.setWindowResolution((int) newValue);
                }
        );
    }

    private void initSetGameMemoryPreference() {
        preferenceSetGameMemory.setTitle(getString(org.koishi.launcher.h2co3.library.R.string.title_game_memory));
        ActivityManager activityManager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        float maxMemoryMB = (float) (memoryInfo.totalMem / (1024 * 1024));
        if (settings.getGameMemoryMax() > maxMemoryMB){
            preferenceSetGameMemory.setValues(256, maxMemoryMB);
        }
        preferenceSetGameMemory.setInitValues(256, maxMemoryMB);
        preferenceSetGameMemory.setStepSize(1);
        int[] initialValue = {settings.getGameMemoryMin(), settings.getGameMemoryMax()};
        preferenceSetGameMemory.setValues(initialValue[0], initialValue[1]);
        preferenceSetGameMemory.setOnRangeChangeListener((preference, valueFrom, valueTo) -> {
            settings.setGameMemoryMin((int) valueFrom);
            settings.setGameMemoryMax((int) valueTo);
        });
    }

    private void initSetPriVerDirPreference() {
        preferenceSetPriVerDir.setChecked(settings.getIsPriVerDir());
        preferenceSetPriVerDir.setOnPreferenceChangeListener((preference, newValue) -> {
            settings.setSetPriVerDir(newValue);
        });
        preferenceSetPriVerDir.setTitle(getString(org.koishi.launcher.h2co3.library.R.string.title_pri_ver_dir));
    }

    private void initChooseJavaPreference() {
        Context context = requireContext();
        String titleJavaVersion = context.getString(org.koishi.launcher.h2co3.library.R.string.title_java_vesion);
        preferenceChooseJava.setTitle(titleJavaVersion);

        String[] entries = {
                context.getString(org.koishi.launcher.h2co3.library.R.string.title_menu_java_auto),
                context.getString(org.koishi.launcher.h2co3.library.R.string.runtime_java8),
                context.getString(org.koishi.launcher.h2co3.library.R.string.runtime_java11),
                context.getString(org.koishi.launcher.h2co3.library.R.string.runtime_java17),
                context.getString(org.koishi.launcher.h2co3.library.R.string.runtime_java21)
        };

        preferenceChooseJava.setEntries(entries, (preference, newValue) -> {
            int javaVersion = JAVA_AUTO;
            for (int i = 1; i < entries.length; i++) {
                if (newValue.equals(entries[i])) {
                    javaVersion = i;
                    break;
                }
            }
            settings.setJavaVer(javaVersion);
        });

        preferenceChooseJava.setValue(entries[settings.getJavaVer()]);
    }

    private void initChooseRendererPreference() {
        Context context = requireContext();
        String titleChooseRenderer = "Renderer";
            preferenceChooseRenderer.setTitle(titleChooseRenderer);

        String[] entries = {
                H2CO3Settings.Renderer.RENDERER_GL4ES.name(),
                H2CO3Settings.Renderer.RENDERER_LTW.name(),
                H2CO3Settings.Renderer.RENDERER_VGPU.name(),
                H2CO3Settings.Renderer.RENDERER_ZINK.name(),
                H2CO3Settings.Renderer.RENDERER_FREEDRENO.name(),
                H2CO3Settings.Renderer.RENDERER_CUSTOM.name()
        };

        preferenceChooseRenderer.setEntries(entries, (preference, newValue) -> {
            int renderer = RENDER_DEF;
            for (int i = 1; i < entries.length; i++) {
                if (newValue.equals(entries[i])) {
                    renderer = i;
                    break;
                }
            }
            settings.setRendererI(renderer);
        });

        preferenceChooseRenderer.setValue(entries[settings.getRendererI()]);
    }
}