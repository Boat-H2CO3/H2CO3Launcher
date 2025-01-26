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

public class CommunityPage extends H2CO3LauncherCommonPage implements View.OnClickListener {

    private final static String QQ_GROUP_KEY = "9_Mnxe5x1l6L7giLuRYQyBh0iWBgCUbw";
    private H2CO3LauncherLinearLayout discord;
    private H2CO3LauncherLinearLayout qq;

    public CommunityPage(Context context, int id, H2CO3LauncherUILayout parent, int resId) {
        super(context, id, parent, resId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        discord = findViewById(R.id.discord);
        qq = findViewById(R.id.qq);
        discord.setOnClickListener(this);
        qq.setOnClickListener(this);
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void onClick(View v) {
        if (v == discord) {
            Uri uri = Uri.parse("https://discord.gg/ffhvuXTwyV");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
        }
        if (v == qq) {
            joinQQGroup(QQ_GROUP_KEY);
        }
    }

    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        try {
            getContext().startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
