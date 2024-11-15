/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.ui;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.controller.ckb.support.CallCustomizeKeyboard;
import org.koishi.launcher.h2co3.controller.client.H2CO3ControlClient;
import org.koishi.launcher.h2co3.controller.Controller;
import org.koishi.launcher.h2co3.controller.H2CO3VirtualController;
import org.koishi.launcher.h2co3.controller.definitions.id.key.KeyEvent;
import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.core.launch.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.utils.PicUtils;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class CustomizeKeyboardEditorActivity extends H2CO3Activity implements View.OnSystemUiVisibilityChangeListener, View.OnClickListener, DrawerLayout.DrawerListener, CallCustomizeKeyboard, H2CO3ControlClient {

    private static final int SYSTEM_UI_HIDE_DELAY_MS = 3000;
    private static final int BLUR_RADIUS = 10;
    private final int[] pointer = new int[]{0, 0};
    private Toolbar mToolbar;
    private Controller mController;
    private boolean isGrabbed;
    private TimerTask systemUiTimerTask;
    private Timer mTimer;
    private Float viewPosY;
    private ViewGroup mLayoutMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ckbe);
        showMessageListView();
        initUI();

        getWindow().getDecorView().post(() -> {
            hideSystemUI(getWindow().getDecorView());
            mTimer = new Timer();
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setOnSystemUiVisibilityChangeListener(this);
            hideSystemUI(decorView);
        } else {
            decorView.setOnSystemUiVisibilityChangeListener(null);
            if (systemUiTimerTask != null) systemUiTimerTask.cancel();
        }
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            if (systemUiTimerTask != null) {
                systemUiTimerTask.cancel();
            }
            systemUiTimerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> hideSystemUI(getWindow().getDecorView()));
                }
            };
            mTimer.schedule(systemUiTimerTask, SYSTEM_UI_HIDE_DELAY_MS);
        }
    }

    private void initUI() {
        mToolbar = findViewById(R.id.ckbe_toolbar);
        mLayoutMain = findViewById(R.id.ckbe_layout_main);
        DrawerLayout mDrawerLayout = findViewById(R.id.ckbe_drawerlayout);
        AppCompatToggleButton toggleButtonMode = findViewById(R.id.activity_ckbe_toggle_mode);

        //设定工具栏
        setSupportActionBar(mToolbar);

        //设定监听
        mLayoutMain.setOnClickListener(this);
        mDrawerLayout.addDrawerListener(this);
        toggleButtonMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mController != null) {
                mController.setGrabCursor(isChecked);
                CustomizeKeyboardEditorActivity.this.isGrabbed = isChecked;
            }
        });

        //设置背景
        mLayoutMain.setBackground(new BitmapDrawable(getResources(), PicUtils.blur(this, BLUR_RADIUS, ((BitmapDrawable) Objects.requireNonNull(ContextCompat.getDrawable(this, org.koishi.launcher.h2co3.library.R.drawable.background))).getBitmap())));

        //初始化控制器
        mController = new H2CO3VirtualController(this, new H2CO3LauncherBridge(new H2CO3Settings()), KeyEvent.KEYMAP_TO_X) {
            @Override
            public void init() {
                super.init();
                bindViewWithInput();
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v == mLayoutMain) {
            switchToolbar();
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        if (viewPosY == null) {
            viewPosY = mToolbar.getY();
        }
        int viewHeight = mToolbar.getHeight();
        float slideSize = viewHeight * slideOffset;
        mToolbar.setY(viewPosY - slideSize);
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        // Do nothing
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        // Do nothing
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        // Do nothing
    }

    private void switchToolbar() {
        int visibility = mToolbar.getVisibility();
        int newVisibility = visibility == View.VISIBLE ? View.GONE : View.VISIBLE;
        mToolbar.setVisibility(newVisibility);
    }

    @Override
    public void setKey(int keyCode, boolean pressed) {
        //stub
    }

    @Override
    public void setMouseButton(int mouseCode, boolean pressed) {
        //stub
    }

    @Override
    public void setPointer(int x, int y) {
        //stub
    }

    @Override
    public void setPointerInc(int xInc, int yInc) {
        //stub
    }

    @Override
    public H2CO3Activity getActivity() {
        return this;
    }

    @Override
    public void addView(View view) {
        if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(view.getLayoutParams().width, view.getLayoutParams().height);
            view.setLayoutParams(params);
        }
        this.mLayoutMain.addView(view);
    }

    @Override
    public void typeWords(String str) {
        //stub
    }

    @Override
    public int[] getGrabbedPointer() {
        return pointer;
    }

    @Override
    public int[] getLoosenPointer() {
        return mController.getLossenPointer();
    }

    @Override
    public ViewGroup getViewsParent() {
        return mLayoutMain;
    }

    @Override
    public View getSurfaceLayerView() {
        return mLayoutMain;
    }

    @Override
    public boolean isGrabbed() {
        return this.isGrabbed;
    }

    @Override
    public void onStop() {
        super.onStop();
        mController.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mController.onResumed();
    }

    @Override
    public void onPause() {
        super.onPause();
        mController.onPaused();
    }

    private void hideSystemUI(View decorView) {
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}