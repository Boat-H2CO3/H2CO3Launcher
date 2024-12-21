package org.koishi.launcher.h2co3.controller.ckb.achieve;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.koishi.launcher.h2co3.controller.ckb.button.GameButton;
import org.koishi.launcher.h2co3.controller.ckb.support.CallCustomizeKeyboard;
import org.koishi.launcher.h2co3.controller.ckb.support.GameButtonArray;
import org.koishi.launcher.h2co3.controller.ckb.support.GameButtonConverter;
import org.koishi.launcher.h2co3.controller.ckb.support.GameButtonRecorder;
import org.koishi.launcher.h2co3.controller.ckb.support.KeyboardRecorder;
import org.koishi.launcher.h2co3.controller.Controller;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.core.utils.DisplayUtils;
import org.koishi.launcher.h2co3.resources.component.dialog.DialogUtils;
import org.koishi.launcher.h2co3.resources.component.dialog.support.DialogSupports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class CkbManager {

    public final static int MAX_KEYBOARD_SIZE = 160;
    public final static String LAST_KEYBOARD_LAYOUT_NAME = "default";
    public final static int SHOW_BUTTON = 1;
    public final static int HIDE_BUTTON = 2;
    private final Context mContext;
    private final CallCustomizeKeyboard mCall;
    private final Controller mController;
    private boolean hasHide = false;

    private GameButtonArray<GameButton> buttonList;

    private int buttonMode = GameButton.MODE_MOVEABLE_EDITABLE;
    private int displayWidth;
    private int displayHeight;

    public CkbManager(@NonNull Context context, @NonNull CallCustomizeKeyboard call, Controller controller) {
        H2CO3Tools.loadPaths(context);
        this.mContext = context;
        this.mCall = call;
        this.mController = controller;
        init();
    }

    public static void outputFile(KeyboardRecorder kr, String fileName) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(kr);
        File dir = new File(H2CO3Tools.H2CO3_CONTROL_DIR);
        dir.mkdirs();
        File file = new File(dir, fileName + ".json");
        try (BufferedWriter out = Files.newBufferedWriter(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            out.write(jsonString);
        } catch (IOException e) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
        }
    }

    public int[] getDisplaySize() {
        return new int[]{displayWidth, displayHeight};
    }

    private void init() {
        buttonList = new GameButtonArray<>();
        displayWidth = DisplayUtils.checkDeviceHasNavigationBar(mContext) ? DisplayUtils.getWindowSize(mContext)[0] + DisplayUtils.getNavigationBarHeight(mContext) : DisplayUtils.getWindowSize(mContext)[0];
        displayHeight = DisplayUtils.getWindowSize(mContext)[1];
        autoLoadKeyboard();
    }

    public Controller getController() {
        return mController;
    }

    public void addGameButton(GameButton button) {
        if (button == null) {
            button = new GameButton(mContext, mCall, mController, this).setButtonMode(buttonMode).setFirstAdded();
            (new GameButtonDialog(mContext, button, this)).show();
        } else if (!containGameButton(button) && buttonList.size() < MAX_KEYBOARD_SIZE) {
            buttonList.add(button);
            addView(button);
        } else {
            button.unsetFirstAdded();
        }
    }

    public boolean containGameButton(GameButton button) {
        return buttonList.contains(button);
    }

    public void removeGameButton(GameButton button) {
        if (containGameButton(button)) {
            buttonList.remove(button);
            removeView(button);
        }
    }

    private void addView(GameButton button) {
        if (button != null && button.getParent() == null) {
            mCall.addView(button);
        }
    }

    private void removeView(GameButton button) {
        if (button != null && button.getParent() != null) {
            ViewGroup vg = (ViewGroup) button.getParent();
            vg.removeView(button);
        }
    }

    public int getButtonCount() {
        return buttonList.size();
    }

    public int getButtonMode() {
        return buttonMode;
    }

    public void setButtonMode(int mode) {
        if (mode == GameButton.MODE_GAME || mode == GameButton.MODE_MOVEABLE_EDITABLE || mode == GameButton.MODE_PREVIEW) {
            for (GameButton button : buttonList) {
                button.setButtonMode(mode);
            }
            buttonMode = mode;
        }
    }

    public GameButton[] getGameButtons() {
        return buttonList.toArray(new GameButton[0]);
    }

    public GameButton getGameButton(int index) {
        if (index >= 0 && index < buttonList.size()) {
            return buttonList.get(index);
        } else {
            return null;
        }
    }

    public void setInputMode(boolean mode) {
        for (GameButton button : buttonList) {
            button.setGrabbed(mode);
        }
    }

    public void autoSaveKeyboard() {
        exportKeyboard(LAST_KEYBOARD_LAYOUT_NAME);
    }

    public void autoLoadKeyboard() {
        loadKeyboard(LAST_KEYBOARD_LAYOUT_NAME + ".json");
    }

    public boolean loadKeyboard(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        try (InputStream inputStream = Files.newInputStream(file.toPath());
             Reader reader = new InputStreamReader(inputStream)) {
            Gson gson = new Gson();
            KeyboardRecorder kr = gson.fromJson(reader, KeyboardRecorder.class);
            loadKeyboard(kr);
            return true;
        } catch (IOException e) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
            handleLoadError(file);
            return false;
        }
    }

    private void handleConversionResult(File file) {
        GameButtonConverter converter = new GameButtonConverter(mContext);
        if (converter.convertAndOutput(file)) {
            DialogUtils.createSingleChoiceDialog(mContext,
                    mContext.getString(org.koishi.launcher.h2co3.library.R.string.title_note),
                    String.format(mContext.getString(org.koishi.launcher.h2co3.library.R.string.tips_successed_to_convert_keyboard_file), file.getName()),
                    mContext.getString(org.koishi.launcher.h2co3.library.R.string.title_ok),
                    null);
        } else {
            DialogUtils.createSingleChoiceDialog(mContext,
                    mContext.getString(org.koishi.launcher.h2co3.library.R.string.title_note),
                    mContext.getString(org.koishi.launcher.h2co3.library.R.string.tips_failed_to_convert_keyboard_file),
                    mContext.getString(org.koishi.launcher.h2co3.library.R.string.title_ok),
                    null);
        }
    }

    public void loadKeyboard(KeyboardRecorder kr) {
        if (kr == null) return;

        GameButtonRecorder[] gbr = kr.getRecorderDatas();
        if (kr.getVersionCode() == KeyboardRecorder.VERSION_UNKNOWN) {
            for (GameButtonRecorder tgbr : gbr) {
                tgbr.keyPos[0] = DisplayUtils.getDpFromPx(mContext, tgbr.keyPos[0]);
                tgbr.keyPos[1] = DisplayUtils.getDpFromPx(mContext, tgbr.keyPos[1]);
            }
        }

        clearKeyboard();
        for (GameButtonRecorder tgbr : gbr) {
            addGameButton(tgbr.recoverData(mContext, mCall, mController, this));
        }
    }

    public void clearKeyboard() {
        for (GameButton button : buttonList) {
            removeView(button);
        }
        buttonList.clear();
    }

    public void showOrHideGameButtons(int i) {
        boolean shouldShow = (i == SHOW_BUTTON && hasHide) || (i == HIDE_BUTTON && !hasHide);
        if (shouldShow) {
            for (GameButton button : buttonList) {
                if (i == SHOW_BUTTON) {
                    addView(button);
                } else {
                    removeView(button);
                }
            }
            hasHide = (i == HIDE_BUTTON);
        }
    }

    public boolean loadKeyboard(String fileName) {
        File file = new File(H2CO3Tools.H2CO3_CONTROL_DIR + "/" + fileName);
        return loadKeyboard(file);
    }

    private void handleLoadError(File file) {
        DialogUtils.createBothChoicesDialog(mContext,
                mContext.getString(org.koishi.launcher.h2co3.library.R.string.title_note),
                mContext.getString(org.koishi.launcher.h2co3.library.R.string.tips_try_to_convert_keyboard_layout),
                mContext.getString(org.koishi.launcher.h2co3.library.R.string.title_ok),
                mContext.getString(org.koishi.launcher.h2co3.library.R.string.title_cancel),
                new DialogSupports() {
                    @Override
                    public void runWhenPositive() {
                        handleConversionResult(file);
                    }
                });
    }

    public void exportKeyboard(String fileName) {
        GameButtonRecorder[] gbrs = new GameButtonRecorder[buttonList.size()];
        for (int a = 0; a < buttonList.size(); a++) {
            GameButtonRecorder gbr = new GameButtonRecorder();
            gbr.recordData(buttonList.get(a));
            gbrs[a] = gbr;
        }
        KeyboardRecorder kr = new KeyboardRecorder();
        kr.setScreenArgs(mContext.getResources().getDisplayMetrics().widthPixels, mContext.getResources().getDisplayMetrics().heightPixels);
        kr.setRecorderDatas(gbrs);
        kr.setVersionCode(KeyboardRecorder.VERSION_THIS);

        outputFile(kr, fileName);
    }
}