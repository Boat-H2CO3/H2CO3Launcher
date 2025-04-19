package org.koishi.launcher.h2co3library.component;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.koishi.launcher.h2co3core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.util.LocaleUtils;
import org.koishi.launcher.h2co3library.R;
import org.koishi.launcher.h2co3library.util.ThemeUtils;

import java.util.ArrayList;

import rikka.material.app.MaterialActivity;

public class BaseActivity extends MaterialActivity {
    public boolean callback = true;
    public H2CO3MessageManager h2co3MessageManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean hasStoragePermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hasStoragePermission = Environment.isExternalStorageManager();
        } else {
            hasStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        if (hasStoragePermission) {
            H2CO3LauncherTools.loadPaths(this);
        }
        setTheme(Build.VERSION.SDK_INT >= 31
                ? R.style.Theme_H2CO3_DynamicColors : R.style.Theme_H2CO3);
        ThemeUtils.applyFullscreen(getWindow(), true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.setLanguage(base));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.setLanguage(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callback) {
            ResultListener.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void showMessageListView() {
        RecyclerView messageListView = new RecyclerView(this);
        messageListView.setBackgroundColor(Color.TRANSPARENT);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                (int) (screenWidth / 3.3),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.END;

        FrameLayout rootLayout = findViewById(android.R.id.content);
        if (rootLayout != null) {
            rootLayout.addView(messageListView, params);
            H2CO3MessageManager.NotificationAdapter adapter = new H2CO3MessageManager.NotificationAdapter(this, new ArrayList<>());
            h2co3MessageManager = new H2CO3MessageManager(adapter, messageListView);
            H2CO3LauncherTools.setH2CO3MessageManager(h2co3MessageManager);
            messageListView.setLayoutManager(new LinearLayoutManager(this));
            messageListView.setAdapter(adapter);
        }
    }
}