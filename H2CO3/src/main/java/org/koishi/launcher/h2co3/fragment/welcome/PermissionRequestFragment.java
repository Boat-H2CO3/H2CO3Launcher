package org.koishi.launcher.h2co3.fragment.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.H2CO3MainActivity;
import org.koishi.launcher.h2co3.adapter.PermissionRequestCardAdapter;
import org.koishi.launcher.h2co3.util.RuntimeUtils;
import org.koishi.launcher.h2co3.view.PermissionRequestCard;
import org.koishi.launcher.h2co3core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PermissionRequestFragment extends H2CO3LauncherFragment {

    NavController navController;
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
        return inflater.inflate(R.layout.fragment_welcome_permission_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        setupRecyclerView();

        view.findViewById(R.id.nextButton).setOnClickListener(v -> {
            view.findViewById(R.id.nextButton).setEnabled(false);
            XXPermissions.with(requireActivity())
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            initRuntimeState();
                            check();
                        }

                        @Override
                        public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                            view.findViewById(R.id.nextButton).setEnabled(true);
                        }
                    });

        });
    }

    private void check() {
        if (cacio && cacio11 && cacio17 && jna && lwjgl && java8 && java11 && java17 && java21) {
            startActivity(new Intent(requireActivity(), H2CO3MainActivity.class));
            requireActivity().finish();
        } else {
            navController.navigate(R.id.action_permissionRequestFragment_to_installFragment);
        }
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

    private void setupRecyclerView() {
        RecyclerView recyclerView = requireView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        PermissionRequestCardAdapter permissionRequestCardAdapter = new PermissionRequestCardAdapter(requireContext(), getPermissionRequestCards());
        recyclerView.setAdapter(permissionRequestCardAdapter);
    }

    private List<PermissionRequestCard> getPermissionRequestCards() {
        List<PermissionRequestCard> cards = new ArrayList<>();
        cards.add(new PermissionRequestCard(R.drawable.ic_btm_manager_normal, R.string.splash_storage_permission, R.string.storage_permission_description));
        return cards;
    }
}