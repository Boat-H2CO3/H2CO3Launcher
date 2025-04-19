package org.koishi.launcher.h2co3.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

public class AboutPage extends H2CO3LauncherCommonPage implements View.OnClickListener {

    private H2CO3LauncherLinearLayout launcher;
    private H2CO3LauncherLinearLayout developer;
    private H2CO3LauncherLinearLayout sponsor;
    private H2CO3LauncherLinearLayout source;

    public AboutPage(Context context, int id, H2CO3LauncherUILayout parent, int resId) {
        super(context, id, parent, resId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        launcher = findViewById(R.id.launcher);
        developer = findViewById(R.id.developer);
        sponsor = findViewById(R.id.sponsor);
        source = findViewById(R.id.source);
        launcher.setOnClickListener(this);
        developer.setOnClickListener(this);
        sponsor.setOnClickListener(this);
        source.setOnClickListener(this);
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void onClick(View v) {
        Uri uri = null;

        if (v == launcher) {
            uri = Uri.parse("https://h2co3Launcher-team.github.io/");
        }
        if (v == developer) {
            uri = Uri.parse("https://github.com/Boat-H2CO3");
        }
        if (v == sponsor) {
            uri = Uri.parse("https://afdian.com/@tungs");
        }
        if (v == source) {
            uri = Uri.parse("https://github.com/Boat-H2CO3/H2CO3Launcher");
        }

        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
        }
    }
}
