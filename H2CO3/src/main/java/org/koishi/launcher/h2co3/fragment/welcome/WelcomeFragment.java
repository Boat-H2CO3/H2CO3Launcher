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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.H2CO3MainActivity;
import org.koishi.launcher.h2co3.util.RuntimeUtils;
import org.koishi.launcher.h2co3core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherFragment;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.io.IOException;

public class WelcomeFragment extends H2CO3LauncherFragment {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private H2CO3LauncherTextView title, description;
    private NavController navController;
    private ConstraintLayout bottomLayout;
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
        return inflater.inflate(R.layout.fragment_welcome_welcome, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isAdded()) {
            // Only proceed if the fragment is added to the activity
            navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
            initViews(view);
            checkFirstLaunch();
        } else {
            // Handle the case where the fragment is not added to the activity
        }
    }

    private void initViews(@NonNull View view) {
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        LinearProgressIndicator progressIndicator = view.findViewById(R.id.linearProgressIndicator);
        FloatingActionButton nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> navController.navigate(R.id.action_welcomeFragment_to_eulaFragment));
        bottomLayout = view.findViewById(R.id.bottom_layout);
    }

    private void checkFirstLaunch() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("h2co3_setting", Context.MODE_PRIVATE);

        boolean isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);

        if (isFirstLaunch) {
            showWelcomeUI();
        } else {
            checkPermission();
        }
    }

    private void showWelcomeUI() {
        title.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
    }

    private void checkPermission() {
        if (!XXPermissions.isGranted(requireContext(), Permission.MANAGE_EXTERNAL_STORAGE)) {
            navController.navigate(R.id.action_welcomeFragment_to_permissionRequestFragment);
        } else {
            initRuntimeState();
            if (checkRuntime()) {
                handler.postDelayed(this::proceedToMainActivity, 0);
            } else {
                navController.navigate(R.id.action_welcomeFragment_to_installFragment);
            }
        }
    }

    private void proceedToMainActivity() {
        Intent intent = new Intent(requireActivity(), H2CO3MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
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
}