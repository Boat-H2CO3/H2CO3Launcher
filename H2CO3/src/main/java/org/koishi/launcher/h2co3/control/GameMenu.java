package org.koishi.launcher.h2co3.control;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.gson.GsonBuilder;
import org.koishi.launcher.h2co3.touch.TouchController;
import org.koishi.launcher.h2co3.BuildConfig;
import org.koishi.launcher.h2co3.H2CO3LauncherApplication;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.JVMCrashActivity;
import org.koishi.launcher.h2co3.control.data.ButtonStyles;
import org.koishi.launcher.h2co3.control.data.ControlButtonData;
import org.koishi.launcher.h2co3.control.data.ControlDirectionData;
import org.koishi.launcher.h2co3.control.data.ControlViewGroup;
import org.koishi.launcher.h2co3.control.data.CustomControl;
import org.koishi.launcher.h2co3.control.data.DirectionStyles;
import org.koishi.launcher.h2co3.control.data.QuickInputTexts;
import org.koishi.launcher.h2co3.control.keyboard.LwjglCharSender;
import org.koishi.launcher.h2co3.control.keyboard.TouchCharInput;
import org.koishi.launcher.h2co3.control.view.GameItemBar;
import org.koishi.launcher.h2co3.control.view.LogWindow;
import org.koishi.launcher.h2co3.control.view.MenuView;
import org.koishi.launcher.h2co3.control.view.TouchPad;
import org.koishi.launcher.h2co3.control.view.ViewManager;
import org.koishi.launcher.h2co3.setting.Controller;
import org.koishi.launcher.h2co3.setting.Controllers;
import org.koishi.launcher.h2co3.setting.GameOption;
import org.koishi.launcher.h2co3.setting.MenuSetting;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.FXUtils;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridgeCallback;
import org.koishi.launcher.h2co3launcher.keycodes.H2CO3LauncherKeycodes;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleBooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleIntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3core.util.io.FileUtils;
import org.koishi.launcher.h2co3library.component.BaseActivity;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;

import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSeekBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSpinner;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSwitch;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import fr.spse.gamepad_remapper.Remapper;
import kotlin.Unit;

public class GameMenu implements MenuCallback, View.OnClickListener {

    private final BooleanProperty editModeProperty = new SimpleBooleanProperty(this, "editMode", false);
    private final IntegerProperty cursorModeProperty = new SimpleIntegerProperty(this, "cursorMode", H2CO3LauncherBridge.CursorEnabled);
    private final BooleanProperty showViewBoundariesProperty = new SimpleBooleanProperty(this, "showViewBoundaries", false);
    private final BooleanProperty hideAllViewsProperty = new SimpleBooleanProperty(this, "hideAllViews", false);
    private final ObjectProperty<Controller> controllerProperty = new SimpleObjectProperty<>(this, "controller", null);
    private final ObjectProperty<ControlViewGroup> viewGroupProperty = new SimpleObjectProperty<>(this, "viewGroup", null);
    private boolean simulated;
    private BaseActivity activity;
    @Nullable
    private H2CO3LauncherBridge h2co3LauncherBridge;
    private H2CO3LauncherInput h2co3LauncherInput;
    private MenuSetting menuSetting;
    private int hitResultType = H2CO3LauncherBridge.HIT_RESULT_TYPE_UNKNOWN;
    private int cursorX;
    private int cursorY;
    private int pointerX;
    private int pointerY;
    private View layout;
    private RelativeLayout baseLayout;
    private TouchPad touchPad;
    private GameItemBar gameItemBar;
    private LogWindow logWindow;
    private H2CO3LauncherTextView fpsText;
    private TouchCharInput touchCharInput;
    private H2CO3LauncherProgressBar launchProgress;
    private H2CO3LauncherImageView cursorView;
    private ViewManager viewManager;
    private Gyroscope gyroscope;
    private H2CO3LauncherButton manageViewGroups;
    private H2CO3LauncherButton addButton;
    private H2CO3LauncherButton addDirection;
    private H2CO3LauncherButton manageButtonStyle;
    private H2CO3LauncherButton manageDirectionStyle;
    private H2CO3LauncherButton manageQuickInput;
    private H2CO3LauncherButton sendKeycode;
    private H2CO3LauncherButton gamepadResetMapper;
    private H2CO3LauncherButton gamepadButtonBinding;
    private H2CO3LauncherButton forceExit;
    private long time = 0;
    private MenuView menuView;
    private TouchController touchController;
    private boolean firstLog = true;

    public void setMenuView(MenuView menuView) {
        this.menuView = menuView;
    }

    public BaseActivity getActivity() {
        return activity;
    }

    public boolean isSimulated() {
        return simulated;
    }

    public MenuSetting getMenuSetting() {
        return menuSetting;
    }

    @Override
    public int getCursorMode() {
        return cursorModeProperty.get();
    }

    public int getHitResultType() {
        return hitResultType;
    }

    public int getCursorX() {
        return cursorX;
    }

    public void setCursorX(int cursorX) {
        this.cursorX = cursorX;
    }

    public int getCursorY() {
        return cursorY;
    }

    public void setCursorY(int cursorY) {
        this.cursorY = cursorY;
    }

    public int getPointerX() {
        return pointerX;
    }

    public void setPointerX(int pointerX) {
        this.pointerX = pointerX;
    }

    public int getPointerY() {
        return pointerY;
    }

    public void setPointerY(int pointerY) {
        this.pointerY = pointerY;
    }

    public ViewManager getViewManager() {
        return viewManager;
    }

    public RelativeLayout getBaseLayout() {
        return baseLayout;
    }

    public TouchPad getTouchPad() {
        return touchPad;
    }

    public TouchCharInput getTouchCharInput() {
        return touchCharInput;
    }

    public BooleanProperty editModeProperty() {
        return editModeProperty;
    }

    public boolean isEditMode() {
        return editModeProperty.get();
    }

    public void setEditMode(boolean editMode) {
        editModeProperty.set(editMode);
    }

    public IntegerProperty cursorModeProperty() {
        return cursorModeProperty;
    }

    public BooleanProperty showViewBoundariesProperty() {
        return showViewBoundariesProperty;
    }

    public boolean isShowViewBoundaries() {
        return showViewBoundariesProperty.get();
    }

    public void setShowViewBoundaries(boolean showViewBoundaries) {
        showViewBoundariesProperty.set(showViewBoundaries);
    }

    public BooleanProperty hideAllViewsProperty() {
        return hideAllViewsProperty;
    }

    public boolean isHideAllViews() {
        return hideAllViewsProperty.get();
    }

    public void setHideAllViews(boolean viewVisible) {
        hideAllViewsProperty.set(viewVisible);
    }

    public ObjectProperty<Controller> controllerProperty() {
        return controllerProperty;
    }

    public Controller getController() {
        return controllerProperty.get();
    }

    public void setController(Controller controller) {
        controllerProperty.set(controller);
    }

    public ObjectProperty<ControlViewGroup> viewGroupProperty() {
        return viewGroupProperty;
    }

    @Nullable
    public ControlViewGroup getViewGroup() {
        return viewGroupProperty.get();
    }

    public void setViewGroup(ControlViewGroup viewGroup) {
        viewGroupProperty.set(viewGroup);
    }

    private void initLeftMenu() {
        H2CO3LauncherSwitch editMode = findViewById(R.id.edit_mode);
        H2CO3LauncherSwitch showViewBoundaries = findViewById(R.id.show_boundary);
        H2CO3LauncherSwitch hideAllViews = findViewById(R.id.hide_all);
        H2CO3LauncherSwitch autoFit = findViewById(R.id.auto_fit);

        H2CO3LauncherSeekBar autoFitDist = findViewById(R.id.auto_fit_dist);
        H2CO3LauncherTextView autoFitText = findViewById(R.id.auto_fit_text);

        H2CO3LauncherSpinner<Controller> currentControllerSpinner = findViewById(R.id.current_controller);
        H2CO3LauncherSpinner<ControlViewGroup> currentViewGroupSpinner = findViewById(R.id.current_view_group);
        autoFitText.stringProperty().bind(Bindings.createStringBinding(() -> menuSetting.getAutoFitDistProperty().get() + " dp", menuSetting.getAutoFitDistProperty()));

        H2CO3LauncherLinearLayout editLayout = findViewById(R.id.edit_layout);

        manageViewGroups = findViewById(R.id.manage_view_groups);
        addButton = findViewById(R.id.add_button);
        addDirection = findViewById(R.id.add_direction);
        manageButtonStyle = findViewById(R.id.manage_button_style);
        manageDirectionStyle = findViewById(R.id.manage_direction_style);

        FXUtils.bindBoolean(editMode, editModeProperty);
        FXUtils.bindBoolean(showViewBoundaries, showViewBoundariesProperty);
        FXUtils.bindBoolean(hideAllViews, hideAllViewsProperty);
        FXUtils.bindBoolean(autoFit, menuSetting.getAutoFitProperty());

        autoFitDist.addProgressListener();
        autoFitDist.progressProperty().bindBidirectional(menuSetting.getAutoFitDistProperty());

        ArrayList<String> controllerNameList = Controllers.getControllers().stream().map(Controller::getName).collect(Collectors.toCollection(ArrayList::new));
        currentControllerSpinner.setDataList(new ArrayList<>(Controllers.getControllers()));
        ArrayAdapter<String> controllerNameAdapter = new ArrayAdapter<>(activity, R.layout.item_spinner_small, controllerNameList);
        controllerNameAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_small);
        currentControllerSpinner.setAdapter(controllerNameAdapter);
        FXUtils.bindSelection(currentControllerSpinner, controllerProperty);

        refreshViewGroupList(currentViewGroupSpinner);
        getController().addListener(i -> refreshViewGroupList(currentViewGroupSpinner));
        controllerProperty.addListener(invalidate -> {
            refreshViewGroupList(currentViewGroupSpinner);
            getController().addListener(i -> refreshViewGroupList(currentViewGroupSpinner));
        });

        editLayout.visibilityProperty().bind(editModeProperty);

        manageViewGroups.setOnClickListener(this);
        addButton.setOnClickListener(this);
        addDirection.setOnClickListener(this);
        manageButtonStyle.setOnClickListener(this);
        manageDirectionStyle.setOnClickListener(this);
    }

    private void refreshViewGroupList(H2CO3LauncherSpinner<ControlViewGroup> spinner) {
        ArrayList<String> viewGroupNameList = controllerProperty.get().viewGroups().stream().map(ControlViewGroup::getName).collect(Collectors.toCollection(ArrayList::new));
        spinner.setDataList(new ArrayList<>(controllerProperty.get().viewGroups()));
        ArrayAdapter<String> viewGroupNameAdapter = new ArrayAdapter<>(activity, R.layout.item_spinner_small, viewGroupNameList);
        viewGroupNameAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_small);
        spinner.setAdapter(viewGroupNameAdapter);
        FXUtils.bindSelection(spinner, viewGroupProperty);
    }

    @SuppressLint("SetTextI18n")
    private void initRightMenu() {
        H2CO3LauncherSwitch lockMenuSwitch = findViewById(R.id.switch_lock_view);
        H2CO3LauncherSwitch hideMenuSwitch = findViewById(R.id.switch_hide_view);
        H2CO3LauncherSwitch showFps = findViewById(R.id.switch_show_fps);
        H2CO3LauncherSwitch disableSoftKeyAdjustSwitch = findViewById(R.id.switch_soft_keyboard_adjust);
        H2CO3LauncherSwitch disableGestureSwitch = findViewById(R.id.switch_gesture);
        H2CO3LauncherSwitch disableBEGestureSwitch = findViewById(R.id.switch_be_gesture);
        H2CO3LauncherSwitch gyroSwitch = findViewById(R.id.switch_gyro);
        H2CO3LauncherSwitch showLogSwitch = findViewById(R.id.switch_show_log);

        H2CO3LauncherSpinner<GestureMode> gestureModeSpinner = findViewById(R.id.gesture_mode_spinner);
        H2CO3LauncherSpinner<MouseMoveMode> mouseMoveModeSpinner = findViewById(R.id.mouse_mode_spinner);

        H2CO3LauncherSeekBar itemBarScaleSeekbar = findViewById(R.id.item_bar_scale);
        H2CO3LauncherSeekBar windowScaleSeekbar = findViewById(R.id.window_scale);
        H2CO3LauncherSeekBar cursorOffsetSeekbar = findViewById(R.id.cursor_offset);
        H2CO3LauncherSeekBar mouseSensitivitySeekbar = findViewById(R.id.mouse_sensitivity);
        H2CO3LauncherSeekBar mouseSizeSeekbar = findViewById(R.id.mouse_size);
        H2CO3LauncherSeekBar gamepadDeadzoneSeekbar = findViewById(R.id.gamepad_deadzone_size);
        H2CO3LauncherSeekBar gamepadAimZoneSeekbar = findViewById(R.id.gamepad_aimzone_size);
        H2CO3LauncherSeekBar gyroSensitivitySeekbar = findViewById(R.id.gyro_sensitivity);

        H2CO3LauncherTextView itemBarScaleText = findViewById(R.id.item_bar_scale_text);
        H2CO3LauncherTextView windowScaleText = findViewById(R.id.window_scale_text);
        H2CO3LauncherTextView cursorOffsetText = findViewById(R.id.cursor_offset_text);
        H2CO3LauncherTextView mouseSensitivityText = findViewById(R.id.mouse_sensitivity_text);
        H2CO3LauncherTextView mouseSizeText = findViewById(R.id.mouse_size_text);
        H2CO3LauncherTextView gamepadDeadzoneText = findViewById(R.id.gamepad_deadzone_text);
        H2CO3LauncherTextView gamepadAimZoneText = findViewById(R.id.gamepad_aimzone_text);
        H2CO3LauncherTextView gyroSensitivityText = findViewById(R.id.gyro_sensitivity_text);

        manageQuickInput = findViewById(R.id.open_quick_input);
        sendKeycode = findViewById(R.id.open_send_key);
        gamepadResetMapper = findViewById(R.id.gamepad_reset_mapper);
        gamepadButtonBinding = findViewById(R.id.gamepad_reset_button_binding);
        forceExit = findViewById(R.id.force_exit);

        FXUtils.bindBoolean(lockMenuSwitch, menuSetting.getLockMenuViewProperty());
        FXUtils.bindBoolean(hideMenuSwitch, menuSetting.getHideMenuViewViewProperty());
        FXUtils.bindBoolean(disableSoftKeyAdjustSwitch, menuSetting.getDisableSoftKeyAdjustProperty());
        FXUtils.bindBoolean(disableGestureSwitch, menuSetting.getDisableGestureProperty());
        FXUtils.bindBoolean(disableBEGestureSwitch, menuSetting.getDisableBEGestureProperty());
        FXUtils.bindBoolean(gyroSwitch, menuSetting.getEnableGyroscopeProperty());
        FXUtils.bindBoolean(showLogSwitch, menuSetting.getShowLogProperty());

        menuSetting.getHideMenuViewViewProperty().addListener(observable -> {
            menuView.setVisibility(menuSetting.isHideMenuView() ? View.INVISIBLE : View.VISIBLE);
            if (!isHideAllViews()) {
                ((DrawerLayout) getLayout()).setDrawerLockMode(menuSetting.isHideMenuView() ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        });

        showFps.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isSimulated()) {
                return;
            }
            if (isChecked) {
                Schedulers.io().execute(() -> {
                    H2CO3LauncherBridge.getFps();
                    while (showFps.isChecked()) {
                        if (System.currentTimeMillis() - time >= 1000) {
                            Schedulers.androidUIThread().execute(() -> fpsText.setText("FPS:" + H2CO3LauncherBridge.getFps()));
                            time = System.currentTimeMillis();
                        }
                    }
                });
            } else {
                fpsText.setText("");
            }
        });

        logWindow.visibilityProperty().setValue(menuSetting.isShowLog());
        menuSetting.getShowLogProperty().addListener(observable -> {
            logWindow.visibilityProperty().setValue(menuSetting.isShowLog());
        });

        ArrayList<GestureMode> gestureModeDataList = new ArrayList<>();
        gestureModeDataList.add(GestureMode.BUILD);
        gestureModeDataList.add(GestureMode.FIGHT);
        gestureModeSpinner.setDataList(gestureModeDataList);
        ArrayList<MouseMoveMode> mouseMoveModeDataList = new ArrayList<>();
        mouseMoveModeDataList.add(MouseMoveMode.CLICK);
        mouseMoveModeDataList.add(MouseMoveMode.SLIDE);
        mouseMoveModeSpinner.setDataList(mouseMoveModeDataList);
        ArrayList<String> gestureModeList = new ArrayList<>();
        gestureModeList.add(activity.getString(R.string.menu_settings_gesture_mode_build));
        gestureModeList.add(activity.getString(R.string.menu_settings_gesture_mode_fight));
        ArrayAdapter<String> gestureModeAdapter = new ArrayAdapter<>(activity, R.layout.item_spinner_small, gestureModeList);
        gestureModeAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_small);
        gestureModeSpinner.setAdapter(gestureModeAdapter);
        ArrayList<String> mouseMoveModeList = new ArrayList<>();
        mouseMoveModeList.add(activity.getString(R.string.menu_settings_mouse_mode_click));
        mouseMoveModeList.add(activity.getString(R.string.menu_settings_mouse_mode_slide));
        ArrayAdapter<String> mouseMoveModeAdapter = new ArrayAdapter<>(activity, R.layout.item_spinner_small, mouseMoveModeList);
        mouseMoveModeAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_small);
        mouseMoveModeSpinner.setAdapter(mouseMoveModeAdapter);
        FXUtils.bindSelection(gestureModeSpinner, menuSetting.getGestureModeProperty());
        FXUtils.bindSelection(mouseMoveModeSpinner, menuSetting.getMouseMoveModeProperty());

        itemBarScaleSeekbar.addProgressListener();
        IntegerProperty itemBarScaleProperty = new SimpleIntegerProperty(menuSetting.getItemBarScale()) {
            @Override
            protected void invalidated() {
                super.invalidated();
                menuSetting.setItemBarScale(get());
                GameOption.GameOptionListener optionListener = gameItemBar.getOptionListener();
                if (optionListener != null) {
                    optionListener.onOptionChanged();
                }
            }
        };
        itemBarScaleSeekbar.progressProperty().bindBidirectional(itemBarScaleProperty);

        windowScaleSeekbar.addProgressListener();
        IntegerProperty windowScaleProperty = new SimpleIntegerProperty((int) (menuSetting.getWindowScale() * 100)) {
            @Override
            protected void invalidated() {
                super.invalidated();
                double doubleValue = get() / 100d;
                menuSetting.setWindowScale(doubleValue);
                int screenWidth = AndroidUtils.getScreenWidth(H2CO3LauncherApplication.getCurrentActivity());
                int screenHeight = AndroidUtils.getScreenHeight(H2CO3LauncherApplication.getCurrentActivity());
                if (h2co3LauncherBridge != null) {
                    h2co3LauncherBridge.setScaleFactor(doubleValue);
                    int width = (int) ((screenWidth + menuSetting.getCursorOffset()) * doubleValue);
                    int height = (int) (screenHeight * doubleValue);
                    h2co3LauncherBridge.getSurfaceTexture().setDefaultBufferSize(width, height);
                    h2co3LauncherBridge.pushEventWindow(width, height);
                }
            }
        };
        windowScaleSeekbar.progressProperty().bindBidirectional(windowScaleProperty);

        cursorOffsetSeekbar.addProgressListener();
        IntegerProperty cursorOffsetProperty = new SimpleIntegerProperty((int) (menuSetting.getCursorOffset())) {
            @Override
            protected void invalidated() {
                super.invalidated();
                menuSetting.setCursorOffset(get());
                int screenWidth = AndroidUtils.getScreenWidth(H2CO3LauncherApplication.getCurrentActivity());
                int screenHeight = AndroidUtils.getScreenHeight(H2CO3LauncherApplication.getCurrentActivity());
                if (h2co3LauncherBridge != null) {
                    double scaleFactor = h2co3LauncherBridge.getScaleFactor();
                    int width = (int) ((screenWidth + get()) * scaleFactor);
                    int height = (int) (screenHeight * scaleFactor);
                    h2co3LauncherBridge.getSurfaceTexture().setDefaultBufferSize(width, height);
                    h2co3LauncherBridge.pushEventWindow(width, height);
                }
            }
        };
        cursorOffsetSeekbar.progressProperty().bindBidirectional(cursorOffsetProperty);

        mouseSensitivitySeekbar.addProgressListener();
        IntegerProperty mouseSensitivityProperty = new SimpleIntegerProperty((int) (menuSetting.getMouseSensitivity() * 100)) {
            @Override
            protected void invalidated() {
                super.invalidated();
                double doubleValue = get() / 100d;
                menuSetting.setMouseSensitivity(doubleValue);
            }
        };
        mouseSensitivitySeekbar.progressProperty().bindBidirectional(mouseSensitivityProperty);
        mouseSizeSeekbar.addProgressListener();
        mouseSizeSeekbar.progressProperty().bindBidirectional(menuSetting.getMouseSizeProperty());

        gamepadDeadzoneSeekbar.addProgressListener();
        IntegerProperty gamepadDeadzoneProperty = new SimpleIntegerProperty((int) (menuSetting.getGamepadDeadzone() * 100)) {
            @Override
            protected void invalidated() {
                super.invalidated();
                double doubleValue = get() / 100d;
                menuSetting.setGamepadDeadzone(doubleValue);
            }
        };
        gamepadDeadzoneSeekbar.progressProperty().bindBidirectional(gamepadDeadzoneProperty);

        gamepadAimZoneSeekbar.addProgressListener();
        IntegerProperty gamepadAimZoneProperty = new SimpleIntegerProperty((int) (menuSetting.getGamepadAimAssistZone() * 100)) {
            @Override
            protected void invalidated() {
                super.invalidated();
                double doubleValue = get() / 100d;
                menuSetting.setGamepadAimAssistZone(doubleValue);
            }
        };
        gamepadAimZoneSeekbar.progressProperty().bindBidirectional(gamepadAimZoneProperty);

        gyroSensitivitySeekbar.addProgressListener();
        gyroSensitivitySeekbar.progressProperty().bindBidirectional(menuSetting.getGyroscopeSensitivityProperty());

        itemBarScaleText.stringProperty().bind(Bindings.createStringBinding(() -> String.valueOf(itemBarScaleProperty.get()), itemBarScaleProperty));
        windowScaleText.stringProperty().bind(Bindings.createStringBinding(() -> windowScaleProperty.get() + " %", windowScaleProperty));
        cursorOffsetText.stringProperty().bind(Bindings.createStringBinding(() -> String.valueOf(cursorOffsetProperty.get()), cursorOffsetProperty));
        mouseSensitivityText.stringProperty().bind(Bindings.createStringBinding(() -> mouseSensitivityProperty.get() + " %", mouseSensitivityProperty));
        mouseSizeText.stringProperty().bind(Bindings.createStringBinding(() -> menuSetting.getMouseSizeProperty().get() + " dp", menuSetting.getMouseSizeProperty()));
        gamepadDeadzoneText.stringProperty().bind(Bindings.createStringBinding(() -> gamepadDeadzoneProperty.get() + " %", gamepadDeadzoneProperty));
        gamepadAimZoneText.stringProperty().bind(Bindings.createStringBinding(() -> gamepadAimZoneProperty.get() + " %", gamepadAimZoneProperty));
        gyroSensitivityText.stringProperty().bind(Bindings.createStringBinding(() -> menuSetting.getGyroscopeSensitivityProperty().get() + "", menuSetting.getGyroscopeSensitivityProperty()));

        manageQuickInput.setOnClickListener(this);
        sendKeycode.setOnClickListener(this);
        gamepadResetMapper.setOnClickListener(this);
        gamepadButtonBinding.setOnClickListener(this);
        forceExit.setOnClickListener(this);
    }

    @Override
    public void setup(BaseActivity activity, H2CO3LauncherBridge h2co3LauncherBridge) {
        this.activity = activity;
        this.h2co3LauncherBridge = h2co3LauncherBridge;
        this.simulated = h2co3LauncherBridge == null;
        this.h2co3LauncherInput = new H2CO3LauncherInput(this);
        if (!Controllers.isInitialized()) {
            Controllers.init();
        }
        if (!ButtonStyles.isInitialized()) {
            ButtonStyles.init();
        }
        if (!DirectionStyles.isInitialized()) {
            DirectionStyles.init();
        }
        if (!QuickInputTexts.isInitialized()) {
            QuickInputTexts.init();
        }

        if (Files.exists(new File(H2CO3LauncherTools.FILES_DIR + "/menu_setting.json").toPath())) {
            try {
                this.menuSetting = new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .fromJson(FileUtils.readText(new File(H2CO3LauncherTools.FILES_DIR + "/menu_setting.json")), MenuSetting.class);
                //如果文件损坏，menuSetting可能为空
                if (this.menuSetting == null) {
                    this.menuSetting = new MenuSetting();
                    new File(H2CO3LauncherTools.FILES_DIR + "/menu_setting.json").delete();
                }
            } catch (IOException e) {
                Logging.LOG.log(Level.WARNING, "Failed to load menu setting, use default", e);
                this.menuSetting = new MenuSetting();
            }
        } else {
            this.menuSetting = new MenuSetting();
        }

        this.menuSetting.addPropertyChangedListener(observable -> {
            String content = new GsonBuilder().setPrettyPrinting().create().toJson(menuSetting);
            try {
                FileUtils.writeText(new File(H2CO3LauncherTools.FILES_DIR + "/menu_setting.json"), content);
            } catch (IOException e) {
                Logging.LOG.log(Level.SEVERE, "Failed to save menu setting", e);
            }
        });

        editModeProperty.set(isSimulated());
        controllerProperty.set(Controllers.findControllerById(activity.getIntent().getExtras().getString("controller")));

        baseLayout = findViewById(R.id.base_layout);
        touchPad = findViewById(R.id.touch_pad);
        gameItemBar = findViewById(R.id.game_item_bar);
        logWindow = findViewById(R.id.log_window);
        fpsText = findViewById(R.id.fps);
        touchCharInput = findViewById(R.id.input_scanner);
        launchProgress = findViewById(R.id.launch_progress);
        cursorView = findViewById(R.id.cursor);

        if (!isSimulated()) {
            launchProgress.setVisibility(View.VISIBLE);
            touchPad.post(() -> gameItemBar.setup(this));
        }
        touchPad.init(this);
        touchCharInput.setCharacterSender(this, new LwjglCharSender(this));
        ViewGroup.LayoutParams layoutParams = cursorView.getLayoutParams();
        layoutParams.width = ConvertUtils.dip2px(activity, menuSetting.getMouseSizeProperty().get());
        layoutParams.height = ConvertUtils.dip2px(activity, menuSetting.getMouseSizeProperty().get());
        cursorView.setLayoutParams(layoutParams);
        menuSetting.getMouseSizeProperty().addListener(observable -> {
            ViewGroup.LayoutParams params = cursorView.getLayoutParams();
            params.width = ConvertUtils.dip2px(activity, menuSetting.getMouseSizeProperty().get());
            params.height = ConvertUtils.dip2px(activity, menuSetting.getMouseSizeProperty().get());
            cursorView.setLayoutParams(params);
        });

        gyroscope = new Gyroscope(this);
        gyroscope.enableProperty().bind(menuSetting.getEnableGyroscopeProperty());

        viewManager = new ViewManager(this);

        initLeftMenu();
        initRightMenu();

        viewManager.setup();

        if (new File(H2CO3LauncherTools.FILES_DIR, "cursor.png").exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(new File(H2CO3LauncherTools.FILES_DIR, "cursor.png").getAbsolutePath());
            BitmapDrawable drawable = new BitmapDrawable(getActivity().getResources(), bitmap);
            getCursor().setImageDrawable(drawable);
        }

        if (getBridge() != null && getBridge().hasTouchController()) {
            touchController = new TouchController(getActivity(), AndroidUtils.getScreenWidth(getActivity()), AndroidUtils.getScreenHeight(getActivity()));
        }
    }

    @Override
    public View getLayout() {
        if (layout == null) {
            layout = LayoutInflater.from(activity).inflate(R.layout.view_game_menu, null);
            ((DrawerLayout) layout).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        return layout;
    }

    @Override
    @Nullable
    public H2CO3LauncherBridge getBridge() {
        return h2co3LauncherBridge;
    }

    @Override
    public H2CO3LauncherBridgeCallback getCallbackBridge() {
        return new H2CO3LauncherProcessListener(this);
    }

    @Override
    public H2CO3LauncherInput getInput() {
        return h2co3LauncherInput;
    }

    @Override
    public H2CO3LauncherImageView getCursor() {
        return cursorView;
    }

    @Override
    public void onPause() {
        if (cursorModeProperty.get() == H2CO3LauncherBridge.CursorDisabled) {
            h2co3LauncherInput.sendKeyEvent(H2CO3LauncherKeycodes.KEY_ESC, true);
            h2co3LauncherInput.sendKeyEvent(H2CO3LauncherKeycodes.KEY_ESC, false);
        }
        gyroscope.disableSensor();
    }

    @Override
    public void onResume() {
        if (menuSetting != null && menuSetting.isEnableGyroscope() && gyroscope != null) {
            gyroscope.enableSensor();
        }
    }

    @Override
    public void onGraphicOutput() {
        baseLayout.setBackground(null);
        baseLayout.removeView(launchProgress);
    }

    @Override
    public void onCursorModeChange(int mode) {
        this.cursorModeProperty.set(mode);
        activity.runOnUiThread(() -> {
            if (mode == H2CO3LauncherBridge.CursorEnabled) {
                getCursor().setVisibility(View.VISIBLE);
                gameItemBar.setVisibility(View.GONE);
                getInput().setPointer(AndroidUtils.getScreenWidth(H2CO3LauncherApplication.getCurrentActivity()) / 2, AndroidUtils.getScreenHeight(H2CO3LauncherApplication.getCurrentActivity()) / 2, "Gyro");
            } else {
                getCursor().setVisibility(View.GONE);
                if (getBridge() != null && !getBridge().hasTouchController()) {
                    gameItemBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onLog(String log) {
        if (h2co3LauncherBridge != null) {
            if (log.contains("version string:") || log.contains("OR:") || log.contains("ERROR:") || log.contains("INTERNAL ERROR:")) {
                return;
            }
            logWindow.appendLog(log);
            if (BuildConfig.DEBUG) {
                Log.d("H2CO3Launcher Debug", log);
            }
            try {
                if (firstLog) {
                    FileUtils.writeText(new File(h2co3LauncherBridge.getLogPath()), log);
                    firstLog = false;
                } else {
                    FileUtils.writeTextWithAppendMode(new File(h2co3LauncherBridge.getLogPath()), log);
                }
            } catch (IOException e) {
                Logging.LOG.log(Level.WARNING, "Can't log game log to target file", e.getMessage());
            }
        }
    }

    @Override
    public void onExit(int exitCode) {
        if (exitCode != 0 && h2co3LauncherBridge != null) {
            JVMCrashActivity.startCrashActivity(true, activity, exitCode, h2co3LauncherBridge.getLogPath(), h2co3LauncherBridge.getRenderer(), h2co3LauncherBridge.getJava());
            Logging.LOG.log(Level.INFO, "JVM crashed, start jvm crash activity to show errors now!");
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @NonNull
    public final <T extends View> T findViewById(int id) {
        return getLayout().findViewById(id);
    }

    public void openQuickInput() {
        QuickInputDialog dialog = new QuickInputDialog(activity, this);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v == manageViewGroups) {
            ViewGroupDialog dialog = new ViewGroupDialog(getActivity(), this, false, FXCollections.observableList(new ArrayList<>()), null);
            dialog.show();
        }
        if (v == addButton) {
            if (getViewGroup() == null) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.edit_view_no_group), Toast.LENGTH_SHORT).show();
            } else {
                EditViewDialog dialog = new EditViewDialog(getActivity(), new ControlButtonData(UUID.randomUUID().toString()), this, new EditViewDialog.Callback() {
                    @Override
                    public void onPositive(CustomControl view) {
                        viewManager.addView(view);
                    }

                    @Override
                    public void onClone(CustomControl view) {
                        // Ignore
                    }
                }, false);
                dialog.show();
            }
        }
        if (v == addDirection) {
            if (getViewGroup() == null) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.edit_view_no_group), Toast.LENGTH_SHORT).show();
            } else {
                EditViewDialog dialog = new EditViewDialog(getActivity(), new ControlDirectionData(UUID.randomUUID().toString()), this, new EditViewDialog.Callback() {
                    @Override
                    public void onPositive(CustomControl view) {
                        viewManager.addView(view);
                    }

                    @Override
                    public void onClone(CustomControl view) {
                        // Ignore
                    }
                }, false);
                dialog.show();
            }
        }
        if (v == manageButtonStyle) {
            ButtonStyleDialog dialog = new ButtonStyleDialog(getActivity(), false, null, null);
            dialog.show();
        }
        if (v == manageDirectionStyle) {
            DirectionStyleDialog dialog = new DirectionStyleDialog(getActivity(), false, null, null);
            dialog.show();
        }
        if (v == manageQuickInput) {
            openQuickInput();
        }
        if (v == sendKeycode) {
            ObservableList<Integer> list = FXCollections.observableList(new ArrayList<>());
            new SelectKeycodeDialog(getActivity(), list, false, true, (dialog) -> {
                new Thread(() -> {
                    list.forEach(key -> {
                        getInput().sendKeyEvent(key, true);
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ignore) {
                    }
                    list.forEach(key -> {
                        getInput().sendKeyEvent(key, false);
                    });
                }).start();
                return Unit.INSTANCE;
            }).show();
        }
        if (v == gamepadResetMapper) {
            Remapper.wipePreferences(getActivity());
            getInput().resetMapper();
        }
        if (v == gamepadButtonBinding) {
            new GamepadButtonBindingDialog(getActivity(), menuSetting.getGamepadButtonBindingProperty()).show();
        }
        if (v == forceExit) {
            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(activity);
            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
            builder.setMessage(activity.getString(R.string.menu_settings_force_exit_msg));
            builder.setPositiveButton(() -> android.os.Process.killProcess(android.os.Process.myPid()));
            builder.setNegativeButton(null);
            builder.setCancelable(false);
            builder.create().show();
        }
    }

    @Nullable
    public TouchController getTouchController() {
        return touchController;
    }

    static class H2CO3LauncherProcessListener implements H2CO3LauncherBridgeCallback {

        private final GameMenu gameMenu;

        public H2CO3LauncherProcessListener(GameMenu gameMenu) {
            this.gameMenu = gameMenu;
        }

        @Override
        public void onCursorModeChange(int mode) {
            gameMenu.onCursorModeChange(mode);
        }

        @Override
        public void onHitResultTypeChange(int type) {
            gameMenu.hitResultType = type;
        }

        @Override
        public void onLog(String log) {
            gameMenu.onLog(log);
        }

        @Override
        public void onExit(int code) {
            gameMenu.onExit(code);
        }
    }
}
