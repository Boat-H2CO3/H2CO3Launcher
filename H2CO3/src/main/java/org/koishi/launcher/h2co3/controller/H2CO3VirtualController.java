package org.koishi.launcher.h2co3.controller;

import static org.koishi.launcher.h2co3.controller.definitions.id.key.KeyEvent.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.materialswitch.MaterialSwitch;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.controller.ckb.support.CustomizeKeyboardMaker;
import org.koishi.launcher.h2co3.controller.client.H2CO3ControlClient;
import org.koishi.launcher.h2co3.controller.codes.Translation;
import org.koishi.launcher.h2co3.controller.event.BaseKeyEvent;
import org.koishi.launcher.h2co3.controller.input.Input;
import org.koishi.launcher.h2co3.controller.input.OnscreenInput;
import org.koishi.launcher.h2co3.controller.input.log.DebugInfo;
import org.koishi.launcher.h2co3.controller.input.screen.CustomizeKeyboard;
import org.koishi.launcher.h2co3.controller.input.screen.ItemBar;
import org.koishi.launcher.h2co3.controller.input.screen.OnscreenTouchpad;
import org.koishi.launcher.h2co3.core.launch.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.utils.DisplayUtils;
import org.koishi.launcher.h2co3.resources.component.MenuView;
import org.koishi.launcher.h2co3.resources.component.dialog.DialogUtils;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3.resources.component.dialog.support.DialogSupports;

import java.util.HashMap;
import java.util.Optional;

import timber.log.Timber;

public class H2CO3VirtualController extends BaseController implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String SP_FILE_NAME = "gamecontroller_config";
    private static final int SP_MODE = Context.MODE_PRIVATE;
    private static final String SP_ENABLE_CKB = "enable_customize_keyboard";
    private static final String SP_ENABLE_ITEMBAR = "enable_mcpe_itembar";
    private static final String SP_ENABLE_ONSCREEN_TOUCHPAD = "enable_touchpad";
    private static final String SP_ENABLE_DEBUG_INFO = "enable_debuginfo";
    private static final String SP_FIRST_LOADER = "first_loaded";

    private final Translation mTranslation;
    private final int screenWidth;
    private final int screenHeight;
    public OnscreenInput itemBar;
    public OnscreenInput customizeKeyboard;
    public OnscreenInput onscreenTouchpad;
    public Input debugInfo;
    public H2CO3LauncherBridge h2co3LauncherBridge;
    public AlertDialog settingDialogAlert;
    private VirtualControllerSetting settingDialog;
    private ImageButton buttonCustomizeKeyboard;
    private MaterialSwitch switchCustomizeKeyboard;
    private ImageButton buttonPEItembar;
    private MaterialSwitch switchPEItembar;
    private ImageButton buttonTouchpad;
    private MaterialSwitch switchTouchpad;
    private MaterialSwitch switchDebugInfo;
    private Button buttonOK;
    private CheckBox checkboxLock;
    private Button buttonResetPos;

    private HashMap<View, Input> bindingViews;

    public H2CO3VirtualController(H2CO3ControlClient h2CO3ControlClient, H2CO3LauncherBridge bridge, int transType) {
        super(h2CO3ControlClient, bridge, true);
        this.mTranslation = new Translation(transType);
        screenWidth = this.getConfig().getScreenWidth();
        screenHeight = this.getConfig().getScreenHeight();
        init();
    }

    @Override
    public void saveConfig() {
        super.saveConfig();
        this.saveConfigToFile();
    }

    public void init() {
        settingDialog = new VirtualControllerSetting(context);
        settingDialogAlert = settingDialog.create();

        onscreenTouchpad = new OnscreenTouchpad();
        itemBar = new ItemBar();
        customizeKeyboard = new CustomizeKeyboard();
        debugInfo = new DebugInfo();

        this.addInput(onscreenTouchpad);
        this.addInput(debugInfo);
        this.addInput(itemBar);
        this.addInput(customizeKeyboard);

        inputs.forEach(input -> input.setEnabled(false));

        MenuView dButton = new MenuView(context);
        dButton.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(context, 30), DisplayUtils.getPxFromDp(context, 30)));
        dButton.setTodo(() -> settingDialogAlert.show());
        dButton.setY((float) (screenHeight / 2));
        h2CO3ControlClient.addContentView(dButton, dButton.getLayoutParams());

        buttonCustomizeKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_button_customize_keyboard);
        buttonPEItembar = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pe_itembar);
        buttonTouchpad = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pc_touchpad);

        switchCustomizeKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_customize_keyboard);
        switchPEItembar = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pe_itembar);
        switchTouchpad = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pc_touchpad);
        switchDebugInfo = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_debug_info);

        buttonOK = settingDialog.findViewById(R.id.virtual_controller_dialog_button_ok);
        checkboxLock = settingDialog.findViewById(R.id.virtual_controller_dialog_checkbox_lock);
        buttonResetPos = settingDialog.findViewById(R.id.virtual_controller_dialog_button_reset_pos);

        for (View v : new View[]{buttonCustomizeKeyboard, buttonOK, buttonResetPos, buttonPEItembar, buttonTouchpad}) {
            v.setOnClickListener(this);
        }

        for (MaterialSwitch s : new MaterialSwitch[]{switchCustomizeKeyboard, switchPEItembar, switchTouchpad, switchDebugInfo}) {
            s.setOnCheckedChangeListener(this);
        }

        checkboxLock.setOnCheckedChangeListener(this);
        bindViewWithInput();
        loadConfigFromFile();
    }

    public void bindViewWithInput() {
        bindingViews = new HashMap<>();
        bindingViews.put(buttonCustomizeKeyboard, customizeKeyboard);
        bindingViews.put(switchCustomizeKeyboard, customizeKeyboard);
        bindingViews.put(buttonPEItembar, itemBar);
        bindingViews.put(switchPEItembar, itemBar);
        bindingViews.put(buttonTouchpad, onscreenTouchpad);
        bindingViews.put(switchTouchpad, onscreenTouchpad);
        bindingViews.put(switchDebugInfo, debugInfo);
    }

    @Override
    public void sendKey(BaseKeyEvent e) {
        toLog(e);
        switch (e.getType()) {
            case KEYBOARD_BUTTON, MOUSE_BUTTON -> {
                String[] strs = e.getKeyName().split(MARK_KEYNAME_SPLIT);
                for (String str : strs) {
                    sendKeyEvent(new BaseKeyEvent(e.getTag(), str, e.isPressed(), e.getType(), e.getPointer()));
                }
            }
            case MOUSE_POINTER, MOUSE_POINTER_INC, TYPE_WORDS -> sendKeyEvent(e);
            default -> {}
        }
    }

    private void toLog(BaseKeyEvent event) {
        String info = switch (event.getType()) {
            case KEYBOARD_BUTTON -> String.format("Type: %s KeyName: %s Pressed: %s", event.getType(), event.getKeyName(), event.isPressed());
            case MOUSE_BUTTON -> String.format("Type: %s MouseName: %s Pressed: %s", event.getType(), event.getKeyName(), event.isPressed());
            case MOUSE_POINTER -> String.format("Type: %s PointerX: %s PointerY: %s", event.getType(), event.getPointer()[0], event.getPointer()[1]);
            case TYPE_WORDS -> String.format("Type: %s Char: %s", event.getType(), event.getChars());
            case MOUSE_POINTER_INC -> String.format("Type: %s IncX: %s IncY: %s", event.getType(), event.getPointer()[0], event.getPointer()[1]);
            default -> "Unknown: " + event;
        };
        Timber.tag(event.getTag()).e(info);
    }

    private void sendKeyEvent(BaseKeyEvent e) {
        switch (e.getType()) {
            case KEYBOARD_BUTTON -> h2CO3ControlClient.setKey(mTranslation.trans(e.getKeyName()), e.isPressed());
            case MOUSE_BUTTON -> h2CO3ControlClient.setMouseButton(mTranslation.trans(e.getKeyName()), e.isPressed());
            case MOUSE_POINTER, MOUSE_POINTER_INC -> Optional.ofNullable(e.getPointer()).ifPresent(pointer -> {
                if (pointer.length >= 2) {
                    if (e.getType() == MOUSE_POINTER) {
                        h2CO3ControlClient.setPointer(pointer[0], pointer[1]);
                    } else {
                        h2CO3ControlClient.setPointerInc(pointer[0], pointer[1]);
                    }
                }
            });
            case TYPE_WORDS -> typeWords(e.getChars());
            default -> {}
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof ImageButton && bindingViews.containsKey(v)) {
            Optional.ofNullable(bindingViews.get(v)).ifPresent(Input::runConfigure);
            return;
        }

        if (v == buttonOK) {
            saveConfigToFile();
            settingDialogAlert.dismiss();
            return;
        }

        if (v == buttonResetPos) {
            DialogUtils.createBothChoicesDialog(context, context.getString(org.koishi.launcher.h2co3.library.R.string.title_note), context.getString(org.koishi.launcher.h2co3.library.R.string.tips_are_you_sure_to_auto_config_layout), context.getString(org.koishi.launcher.h2co3.library.R.string.title_ok), context.getString(org.koishi.launcher.h2co3.library.R.string.title_cancel), new DialogSupports() {
                @Override
                public void runWhenPositive() {
                    resetAllPosOnScreen();
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView instanceof MaterialSwitch && bindingViews.containsKey(buttonView)) {
            Optional.ofNullable(bindingViews.get(buttonView)).ifPresent(input -> input.setEnabled(isChecked));
        }
        if (buttonView == checkboxLock) {
            inputs.stream()
                    .filter(input -> input instanceof OnscreenInput)
                    .forEach(input -> ((OnscreenInput) input).setUiMoveable(isChecked));
        }
    }

    private int[] calculateMarginsOnScreen(OnscreenInput i, float leftScale, float topScale) {
        if (i.getSize() == null || i.getSize().length < 2) {
            return null;
        }

        int viewWidth = i.getSize()[0];
        int viewHeight = i.getSize()[1];

        int leftMargin = (int) (screenWidth * leftScale - viewWidth / 2);
        int topMargin = (int) (screenHeight * topScale - viewHeight / 2);

        leftMargin = Math.max(0, Math.min(leftMargin, screenWidth - viewWidth));
        topMargin = Math.max(0, Math.min(topMargin, screenHeight - viewHeight));

        return new int[]{leftMargin, topMargin};
    }

    private void resetAllPosOnScreen() {
        Optional.ofNullable(calculateMarginsOnScreen(itemBar, 0.5f, 1))
                .ifPresent(i -> itemBar.setMargins(i[0], i[1], 0, 0));
    }

    private void saveConfigToFile() {
        SharedPreferences.Editor editor = context.getSharedPreferences(SP_FILE_NAME, SP_MODE).edit();
        editor.putBoolean(SP_ENABLE_CKB, switchCustomizeKeyboard.isChecked());
        editor.putBoolean(SP_ENABLE_ITEMBAR, switchPEItembar.isChecked());
        editor.putBoolean(SP_ENABLE_ONSCREEN_TOUCHPAD, switchTouchpad.isChecked());
        editor.putBoolean(SP_ENABLE_DEBUG_INFO, switchDebugInfo.isChecked());
        if (!context.getSharedPreferences(SP_FILE_NAME, SP_MODE).contains(SP_FIRST_LOADER)) {
            editor.putBoolean(SP_FIRST_LOADER, false);
        }
        editor.apply();
    }

    private void loadConfigFromFile() {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, SP_MODE);
        if (sp != null) {
            switchCustomizeKeyboard.setChecked(sp.getBoolean(SP_ENABLE_CKB, true));
            switchPEItembar.setChecked(sp.getBoolean(SP_ENABLE_ITEMBAR, true));
            switchTouchpad.setChecked(sp.getBoolean(SP_ENABLE_ONSCREEN_TOUCHPAD, true));
            switchDebugInfo.setChecked(sp.getBoolean(SP_ENABLE_DEBUG_INFO, false));
            if (!sp.contains(SP_FIRST_LOADER)) {
                resetAllPosOnScreen();
                ((CustomizeKeyboard) customizeKeyboard).mManager.loadKeyboard(new CustomizeKeyboardMaker(context).createDefaultKeyboard());
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        saveConfigToFile();
    }

    private static class VirtualControllerSetting extends H2CO3CustomViewDialog {
        public VirtualControllerSetting(@NonNull Context context) {
            super(context);
            setCustomView(R.layout.dialog_controller_functions);
        }
    }
}