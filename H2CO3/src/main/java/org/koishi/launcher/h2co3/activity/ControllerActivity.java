package org.koishi.launcher.h2co3.activity;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.control.GameMenu;
import org.koishi.launcher.h2co3library.component.BaseActivity;

import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageView;

public class ControllerActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showMessageListView();

        H2CO3LauncherImageView contentView = new H2CO3LauncherImageView(this);
        setContentView(contentView);

        GameMenu menu = new GameMenu();
        menu.setup(this, null);

        addContentView(menu.getLayout(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
