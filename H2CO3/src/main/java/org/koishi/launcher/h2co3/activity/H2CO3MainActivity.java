package org.koishi.launcher.h2co3.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.game.JarExecutorHelper;
import org.koishi.launcher.h2co3.game.TexturesLoader;
import org.koishi.launcher.h2co3.setting.Accounts;
import org.koishi.launcher.h2co3.setting.ConfigHolder;
import org.koishi.launcher.h2co3.setting.Controllers;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.setting.Profiles;
import org.koishi.launcher.h2co3.ui.UIManager;
import org.koishi.launcher.h2co3.ui.version.Versions;
import org.koishi.launcher.h2co3.upgrade.UpdateChecker;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.WeakListenerHolder;
import org.koishi.launcher.h2co3core.auth.Account;
import org.koishi.launcher.h2co3core.auth.authlibinjector.AuthlibInjectorAccount;
import org.koishi.launcher.h2co3core.auth.authlibinjector.AuthlibInjectorServer;
import org.koishi.launcher.h2co3core.auth.yggdrasil.TextureModel;
import org.koishi.launcher.h2co3core.download.LibraryAnalyzer;
import org.koishi.launcher.h2co3core.event.Event;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.value.ObservableValue;
import org.koishi.launcher.h2co3core.mod.RemoteMod;
import org.koishi.launcher.h2co3core.mod.RemoteModRepository;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3core.util.fakefx.BindingMapping;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3launcher.plugins.DriverPlugin;
import org.koishi.launcher.h2co3launcher.plugins.RendererPlugin;
import org.koishi.launcher.h2co3library.component.BaseActivity;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Stream;

public class H2CO3MainActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static WeakReference<H2CO3MainActivity> instance;
    public UIManager uiManager;
    public MaterialToolbar toolbar;
    public int _menuItem = R.id.navigation_main;
    private UIManager _uiManager = null;
    // UI Components
    private H2CO3LauncherUILayout uiLayout;
    private NavigationView navigationView;

    public static H2CO3MainActivity getInstance() {
        return instance.get();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = new WeakReference<>(this);
        setContentView(R.layout.activity_main);

        showMessageListView();

        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav);
        toolbar.inflateMenu(R.menu.home_toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.navigation_main);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);


        uiLayout = findViewById(R.id.ui_layout);


        try {
            ConfigHolder.init();
        } catch (IOException e) {
            Logging.LOG.log(Level.WARNING, e.getMessage());
        }

        uiLayout.post(() -> {
            uiManager = new UIManager(this, uiLayout);
            _uiManager = uiManager;
            uiManager.registerDefaultBackEvent(() -> {
                if (uiManager.getCurrentUI() == uiManager.mainUI) {
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addCategory(Intent.CATEGORY_HOME);
                    startActivity(i);
                    System.exit(0);
                } else {
                    uiManager.switchUI(uiManager.mainUI);
                    setNavigationItemChecked(R.id.navigation_main);
                }
            });
            uiManager.init(() -> {

                uiManager.switchUI(uiManager.mainUI);
                UpdateChecker.getInstance().checkAuto(this).start();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (_uiManager != null) {
                _uiManager.onBackPressed();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (_uiManager != null) {
            _uiManager.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (_uiManager != null) {
            _uiManager.onResume();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_item_home) {
            if (uiManager.getCurrentUI() != uiManager.getMainUI()) {
                setNavigationItemChecked(R.id.navigation_main);
                uiManager.switchUI(uiManager.getMainUI());
                _menuItem = R.id.navigation_main;
            }
        } else if (item.getItemId() == R.id.action_item_setting) {
            if (uiManager.getCurrentUI() != uiManager.getSettingUI()) {
                setNavigationItemChecked(R.id.navigation_setting);
                uiManager.switchUI(uiManager.getSettingUI());
                _menuItem = R.id.navigation_setting;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void setNavigationItemChecked(int itemId) {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(menu.getItem(i).getItemId() == itemId);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
        if (menuItem.isChecked()) {
            return true;
        }

        setNavigationItemChecked(menuItem.getItemId());

        if (menuItem.getItemId() == R.id.navigation_main) {
            if (uiManager.getCurrentUI() != uiManager.getMainUI()) {
                toolbar.setTitle(getString(R.string.app_name));
                uiManager.switchUI(uiManager.getMainUI());
                _menuItem = R.id.navigation_main;
            }
        } else if (menuItem.getItemId() == R.id.navigation_manage) {
            if (uiManager.getCurrentUI() != uiManager.getManageUI()) {
                toolbar.setTitle(getString(R.string.manage));
                uiManager.switchUI(uiManager.getMultiplayerUI());
                _menuItem = R.id.navigation_manage;
            }
        }else if (menuItem.getItemId() == R.id.navigation_download) {
            if (uiManager.getCurrentUI() != uiManager.getDownloadUI()) {
                toolbar.setTitle(getString(R.string.download));
                uiManager.switchUI(uiManager.getDownloadUI());
                _menuItem = R.id.navigation_download;
            }
        } else if (menuItem.getItemId() == R.id.navigation_controller) {
            if (uiManager.getCurrentUI() != uiManager.getControllerUI()) {
                toolbar.setTitle(getString(R.string.controller));
                uiManager.switchUI(uiManager.getControllerUI());
                _menuItem = R.id.navigation_controller;
            }
        } else if (menuItem.getItemId() == R.id.navigation_setting) {
            if (uiManager.getCurrentUI() != uiManager.getSettingUI()) {
                toolbar.setTitle(getString(R.string.setting));
                uiManager.switchUI(uiManager.getSettingUI());
                _menuItem = R.id.navigation_setting;
            }
        } else if (menuItem.getItemId() == R.id.navigation_back) {
            uiManager.onBackPressed();
            setNavigationItemChecked(_menuItem);
        }
        return true;
    }

    public void cleanItemChecked(){
        _menuItem = -1;
        setNavigationItemChecked(-1);
    }

    @Override
    public void onClick(View view) {

    }
}