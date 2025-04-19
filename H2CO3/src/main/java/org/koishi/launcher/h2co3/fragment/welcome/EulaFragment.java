package org.koishi.launcher.h2co3.fragment.welcome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.H2CO3MainActivity;
import org.koishi.launcher.h2co3.util.RuntimeUtils;
import org.koishi.launcher.h2co3core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3core.util.io.IOUtils;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherFragment;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.io.IOException;

public class EulaFragment extends H2CO3LauncherFragment implements View.OnClickListener {

    //public static final String EULA_URL = "https://gitee.com/cainiaohanhanyai/cnhhfile/raw/master/Documents/eula.txt?inline=false";
    private final Handler handler = new Handler(Looper.getMainLooper());
    NavController navController;
    private ProgressBar progressBar;
    private H2CO3LauncherTextView eula;
    private boolean load = false;
    private boolean cacio = false;
    private boolean cacio11 = false;
    private boolean cacio17 = false;
    private boolean jna = false;
    private boolean lwjgl = false;
    private boolean java8 = false;
    private boolean java11 = false;
    private boolean java17 = false;
    private boolean java21 = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_eula, container, false);
        progressBar = findViewById(view, R.id.loading_progress);
        eula = findViewById(view, R.id.eulaText);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.nextButton).setEnabled(false);
        navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        view.findViewById(R.id.nextButton).setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("h2co3_setting", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.apply();
            checkPermission();
        });

        loadEula(view);
    }

    private void loadEula(View v) {
        new Thread(() -> {
            String str;
            try {
                str = IOUtils.readFullyAsString(requireActivity().getAssets().open( "eula.txt"));
                load = true;
            } catch (IOException e) {
                str = getString(R.string.splash_eula_error);
                load = true;
            }
            final String s = str;
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (load) {
                        v.findViewById(R.id.nextButton).setEnabled(true);
                    }
                    progressBar.setVisibility(View.GONE);
                    eula.setText(s);
                });
            }
        }).start();
    }

    private void checkPermission() {
        if (!XXPermissions.isGranted(requireContext(), Permission.MANAGE_EXTERNAL_STORAGE)) {
            navController.navigate(R.id.action_eulaFragment_to_permissionRequestFragment);
        } else {
            initRuntimeState();
            if (checkRuntime()) {
                handler.postDelayed(() -> startActivity(new Intent(requireActivity(), H2CO3MainActivity.class)), 0);
                requireActivity().finish();
            } else {
                navController.navigate(R.id.action_eulaFragment_to_installFragment);
            }
        }
    }

    private boolean checkRuntime() {
        return cacio && cacio11 && cacio17 && jna && lwjgl && java8 && java11 && java17 && java21;
    }

    private void initRuntimeState() {
        checkRuntimeState(H2CO3LauncherTools.CACIOCAVALLO_8_DIR, "/assets/runtime/caciocavallo");
        checkRuntimeState(H2CO3LauncherTools.CACIOCAVALLO_11_DIR, "/assets/runtime/caciocavallo11");
        checkRuntimeState(H2CO3LauncherTools.CACIOCAVALLO_17_DIR, "/assets/runtime/caciocavallo17");
        checkRuntimeState(H2CO3LauncherTools.JNA_PATH, "/assets/runtime/jna");
        checkLwjglRuntimeState();
        checkRuntimeState(H2CO3LauncherTools.JAVA_8_PATH, "/assets/runtime/java/jre8");
        checkRuntimeState(H2CO3LauncherTools.JAVA_11_PATH, "/assets/runtime/java/jre11");
        checkRuntimeState(H2CO3LauncherTools.JAVA_17_PATH, "/assets/runtime/java/jre17");
        checkRuntimeState(H2CO3LauncherTools.JAVA_21_PATH, "/assets/runtime/java/jre21");
    }

    private void checkRuntimeState(String path, String assetPath) {
        try {
            RuntimeUtils.isLatest(path, assetPath);
        } catch (IOException e) {
            H2CO3LauncherTools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
        }
    }

    private void checkLwjglRuntimeState() {
        try {
            lwjgl = RuntimeUtils.isLatest(H2CO3LauncherTools.LWJGL_DIR, "/assets/runtime/lwjgl") &&
                    RuntimeUtils.isLatest(H2CO3LauncherTools.LWJGL_DIR + "-h2co3", "/assets/runtime/lwjgl-h2co3");
        } catch (IOException e) {
            H2CO3LauncherTools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
        }
    }

    /**
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }
}