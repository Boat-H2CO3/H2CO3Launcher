package org.koishi.launcher.h2co3.control.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import org.koishi.launcher.h2co3.H2CO3LauncherApplication;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.GameMenu;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

import java.io.File;

public class MenuView extends View {

    private final int screenWidth;
    private final int screenHeight;

    private final int DEFAULT_WIDTH = ConvertUtils.dip2px(H2CO3LauncherApplication.getCurrentActivity(), 40);
    private final int DEFAULT_HEIGHT = ConvertUtils.dip2px(H2CO3LauncherApplication.getCurrentActivity(), 40);

    private GameMenu gameMenu;

    private boolean isGif = false;
    private boolean pressed = false;
    private Bitmap icon;
    private Paint strokePaint;
    private Paint areaPaint;
    private Paint iconPaint;
    private Rect srcRect;
    private Rect destRect;
    private float downX;
    private float downY;
    private long downTime;
    public MenuView(Context context) {
        super(context);
        this.screenWidth = AndroidUtils.getScreenWidth(H2CO3LauncherApplication.getCurrentActivity());
        this.screenHeight = AndroidUtils.getScreenHeight(H2CO3LauncherApplication.getCurrentActivity());
    }

    public MenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.screenWidth = AndroidUtils.getScreenWidth(H2CO3LauncherApplication.getCurrentActivity());
        this.screenHeight = AndroidUtils.getScreenHeight(H2CO3LauncherApplication.getCurrentActivity());
    }
    public MenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.screenWidth = AndroidUtils.getScreenWidth(H2CO3LauncherApplication.getCurrentActivity());
        this.screenHeight = AndroidUtils.getScreenHeight(H2CO3LauncherApplication.getCurrentActivity());
    }

    public void setup(GameMenu gameMenu) {
        this.gameMenu = gameMenu;

        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(Color.DKGRAY);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(ConvertUtils.dip2px(H2CO3LauncherApplication.getCurrentActivity(), 2));

        areaPaint = new Paint();
        areaPaint.setAntiAlias(true);

        iconPaint = new Paint();
        iconPaint.setAntiAlias(true);

        initIcon();
        if (!isGif) {
            srcRect = new Rect(0, 0, icon.getWidth(), icon.getHeight());
            destRect = new Rect(ConvertUtils.dip2px(H2CO3LauncherApplication.getCurrentActivity(), 6),
                    ConvertUtils.dip2px(H2CO3LauncherApplication.getCurrentActivity(), 6),
                    ConvertUtils.dip2px(H2CO3LauncherApplication.getCurrentActivity(), 34),
                    ConvertUtils.dip2px(H2CO3LauncherApplication.getCurrentActivity(), 34));
        }
    }

    public void initPosition() {
        post(() -> {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = DEFAULT_WIDTH;
            layoutParams.height = DEFAULT_HEIGHT;
            setLayoutParams(layoutParams);
            setX((float) ((screenWidth - DEFAULT_WIDTH) * gameMenu.getMenuSetting().getMenuPositionX()));
            setY((float) ((screenHeight - DEFAULT_HEIGHT) * gameMenu.getMenuSetting().getMenuPositionY()));
        });
    }

    private void initIcon() {
        if (new File(H2CO3LauncherTools.FILES_DIR, "menu_icon.png").exists()) {
            icon = BitmapFactory.decodeFile(new File(H2CO3LauncherTools.FILES_DIR, "menu_icon.png").getAbsolutePath());
        } else if (new File(H2CO3LauncherTools.FILES_DIR, "menu_icon.gif").exists()) {
            isGif = true;
            Glide.with(this).asGif().skipMemoryCache(true).load(new File(H2CO3LauncherTools.FILES_DIR, "menu_icon.gif")).into(new CustomViewTarget<MenuView, GifDrawable>(this) {
                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                }

                @Override
                public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                    setBackground(resource);
                    resource.start();
                }

                @Override
                protected void onResourceCleared(@Nullable Drawable placeholder) {
                }
            });
        } else {
            icon = BitmapFactory.decodeResource(H2CO3LauncherApplication.getCurrentActivity().getResources(), R.drawable.img_app);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isGif) {
            return;
        }
        if (pressed) {
            areaPaint.setColor(H2CO3LauncherApplication.getCurrentActivity().getColor(org.koishi.launcher.h2co3library.R.color.ui_bg_color));
        } else {
            areaPaint.setColor(Color.TRANSPARENT);
        }
        canvas.drawCircle(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1, (getMeasuredWidth() >> 1) - ConvertUtils.dip2px(H2CO3LauncherApplication.getCurrentActivity(), 1), strokePaint);
        canvas.drawCircle(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1, (getMeasuredWidth() >> 1) - ConvertUtils.dip2px(H2CO3LauncherApplication.getCurrentActivity(), 2), areaPaint);
        canvas.drawBitmap(icon, srcRect, destRect, iconPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                downTime = System.currentTimeMillis();
                pressed = true;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!gameMenu.getMenuSetting().isLockMenuView()) {
                    float targetX = Math.max(0, Math.min(screenWidth - getMeasuredWidth(), getX() + event.getX() - downX));
                    float targetY = Math.max(0, Math.min(screenHeight - getMeasuredHeight(), getY() + event.getY() - downY));
                    setX(targetX);
                    setY(targetY);
                    gameMenu.getMenuSetting().setMenuPositionX(targetX / screenWidth);
                    gameMenu.getMenuSetting().setMenuPositionY(targetY / screenHeight);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (Math.abs(event.getX() - downX) <= 10
                        && Math.abs(event.getY() - downY) <= 10
                        && System.currentTimeMillis() - downTime <= 400) {
                    ((DrawerLayout) gameMenu.getLayout()).openDrawer(GravityCompat.START, true);
                    ((DrawerLayout) gameMenu.getLayout()).openDrawer(GravityCompat.END, true);
                }
                pressed = false;
                invalidate();
                break;
        }
        return true;
    }
}
