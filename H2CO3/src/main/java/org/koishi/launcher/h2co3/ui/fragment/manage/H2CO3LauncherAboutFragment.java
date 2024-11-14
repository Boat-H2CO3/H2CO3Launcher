package org.koishi.launcher.h2co3.ui.fragment.manage;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.launch.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;

public class H2CO3LauncherAboutFragment extends H2CO3Fragment {
    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage_launcher_about, container, false);
        H2CO3CardView openGithubView = findViewById(view, R.id.open_github);
        openGithubView.setOnClickListener(v -> openUrl("https://github.com/Boat-H2CO3/H2CO3Launcher"));
        H2CO3TextView appVersion = findViewById(view, R.id.appVersion);
        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            runOnUiThread(() -> appVersion.setText(versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            runOnUiThread(() -> appVersion.setText("Unknown"));
        }
        return view;
    }

    private void openUrl(String url){
        H2CO3LauncherBridge.openLink(url);
    }
}
