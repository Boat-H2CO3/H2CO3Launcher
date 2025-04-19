package org.koishi.launcher.h2co3.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3library.component.BaseActivity;

public class InstallActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showMessageListView();
        Uri uri = getIntent().getData();
        String path = uri != null ? uri.getEncodedPath() : null;
        if (path != null) {
            path = path.toLowerCase();
            if (path.endsWith(".apk") || path.endsWith(".apk.1")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                startActivity(intent);
            }
        }
        finish();
    }
}
