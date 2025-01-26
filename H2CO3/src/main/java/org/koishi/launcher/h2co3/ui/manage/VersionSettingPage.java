package org.koishi.launcher.h2co3.ui.manage;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;

import org.koishi.launcher.h2co3.util.RendererUtil;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.SelectControllerDialog;
import org.koishi.launcher.h2co3.game.H2CO3LauncherGameRepository;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.setting.VersionSetting;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.FXUtils;
import org.koishi.launcher.h2co3.util.RequestCodes;
import org.koishi.launcher.h2co3.util.WeakListenerHolder;
import org.koishi.launcher.h2co3launcher.H2CO3LauncherConfig;
import org.koishi.launcher.h2co3launcher.plugins.DriverPlugin;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.event.Event;
import org.koishi.launcher.h2co3core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleBooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleIntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleStringProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.StringProperty;
import org.koishi.launcher.h2co3core.game.JavaVersion;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.util.Lang;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3core.util.io.FileUtils;
import org.koishi.launcher.h2co3core.util.platform.MemoryUtils;
import org.koishi.launcher.h2co3library.browser.FileBrowser;
import org.koishi.launcher.h2co3library.browser.options.LibMode;
import org.koishi.launcher.h2co3library.browser.options.SelectionMode;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherCheckBox;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSeekBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSpinner;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSwitch;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class VersionSettingPage extends H2CO3LauncherCommonPage implements ManageUI.VersionLoadable, View.OnClickListener {

    private final boolean globalSetting;
    private final InvalidationListener specificSettingsListener;
    private final StringProperty selectedVersion = new SimpleStringProperty();
    private final BooleanProperty enableSpecificSettings = new SimpleBooleanProperty(false);
    private final IntegerProperty maxMemory = new SimpleIntegerProperty();
    private final IntegerProperty usedMemory = new SimpleIntegerProperty(0);
    private final BooleanProperty modpack = new SimpleBooleanProperty();
    private VersionSetting lastVersionSetting = null;
    private Profile profile;
    private WeakListenerHolder listenerHolder;
    private String versionId;
    private H2CO3LauncherEditText txtJVMArgs;
    private H2CO3LauncherEditText txtGameArgs;
    private H2CO3LauncherEditText txtMetaspace;
    private H2CO3LauncherEditText txtServerIP;
    private H2CO3LauncherCheckBox chkAutoAllocate;
    private H2CO3LauncherImageView iconView;
    private H2CO3LauncherSeekBar allocateSeekbar;
    private H2CO3LauncherSeekBar scaleFactorSeekbar;
    private H2CO3LauncherSwitch isolateWorkingDirSwitch;
    private H2CO3LauncherSwitch beGestureSwitch;
    private H2CO3LauncherSwitch vulkanDriverSystemSwitch;
    private H2CO3LauncherSwitch pojavBigCoreSwitch;
    private H2CO3LauncherSwitch noGameCheckSwitch;
    private H2CO3LauncherSwitch noJVMCheckSwitch;
    private H2CO3LauncherSpinner<String> javaSpinner;
    private H2CO3LauncherImageButton editIconButton;
    private H2CO3LauncherImageButton deleteIconButton;
    private H2CO3LauncherImageButton controllerButton;
    private H2CO3LauncherImageButton rendererButton;
    private H2CO3LauncherImageButton rendererInstallButton;
    private H2CO3LauncherImageButton driverButton;
    private H2CO3LauncherImageButton driverInstallButton;
    private H2CO3LauncherTextView rendererText;
    private H2CO3LauncherTextView driverText;

    public VersionSettingPage(Context context, int id, H2CO3LauncherUILayout parent, int resId, boolean globalSetting) {
        super(context, id, parent, resId);
        this.globalSetting = globalSetting;
        create();
        specificSettingsListener = any -> enableSpecificSettings.set(!lastVersionSetting.isUsesGlobal());
    }

    private void create() {
        H2CO3LauncherLinearLayout settingTypeLayout = findViewById(R.id.special_setting_layout);
        H2CO3LauncherLinearLayout settingLayout = findViewById(R.id.setting_layout);

        txtJVMArgs = findViewById(R.id.edit_jvm_args);
        txtGameArgs = findViewById(R.id.edit_minecraft_args);
        txtMetaspace = findViewById(R.id.edit_permgen_space);
        txtServerIP = findViewById(R.id.edit_server);

        chkAutoAllocate = findViewById(R.id.edit_auto_allocate);

        iconView = findViewById(R.id.icon);

        allocateSeekbar = findViewById(R.id.edit_memory);
        scaleFactorSeekbar = findViewById(R.id.edit_scale_factor);

        H2CO3LauncherSwitch specialSettingSwitch = findViewById(R.id.enable_per_instance_setting);
        specialSettingSwitch.addCheckedChangeListener();
        isolateWorkingDirSwitch = findViewById(R.id.edit_game_dir);
        beGestureSwitch = findViewById(R.id.edit_controller_injector);
        vulkanDriverSystemSwitch = findViewById(R.id.vulkan_driver_system);
        pojavBigCoreSwitch = findViewById(R.id.pojav_big_core);
        noGameCheckSwitch = findViewById(R.id.edit_not_check_game);
        noJVMCheckSwitch = findViewById(R.id.edit_not_check_java);

        isolateWorkingDirSwitch.disableProperty().bind(modpack);

        javaSpinner = findViewById(R.id.edit_java);

        H2CO3LauncherTextView scaleFactorText = findViewById(R.id.scale_factor_text);

        scaleFactorSeekbar.addProgressListener();
        scaleFactorText.stringProperty().bind(Bindings.createStringBinding(() -> (int) (lastVersionSetting.getScaleFactor() * 100) + " %", scaleFactorSeekbar.percentProgressProperty()));

        // add spinner data
        ArrayList<String> javaVersionDataList = new ArrayList<>();
        javaVersionDataList.add(JavaVersion.JAVA_AUTO.getVersionName());
        javaVersionDataList.add(JavaVersion.JAVA_8.getVersionName());
        javaVersionDataList.add(JavaVersion.JAVA_11.getVersionName());
        javaVersionDataList.add(JavaVersion.JAVA_17.getVersionName());
        javaVersionDataList.add(JavaVersion.JAVA_21.getVersionName());
        javaSpinner.setDataList(javaVersionDataList);

        // add spinner text
        ArrayList<String> javaVersionList = new ArrayList<>();
        javaVersionList.add(getContext().getString(R.string.settings_game_java_version_auto));
        javaVersionList.add("JRE 8");
        javaVersionList.add("JRE 11");
        javaVersionList.add("JRE 17");
        javaVersionList.add("JRE 21");
        ArrayAdapter<String> javaAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_auto_tint, javaVersionList);
        javaAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        javaSpinner.setAdapter(javaAdapter);

        editIconButton = findViewById(R.id.edit_icon);
        deleteIconButton = findViewById(R.id.delete_icon);
        controllerButton = findViewById(R.id.edit_controller);
        rendererButton = findViewById(R.id.edit_renderer);
        rendererInstallButton = findViewById(R.id.install_renderer);
        driverButton = findViewById(R.id.edit_driver);
        driverInstallButton = findViewById(R.id.install_driver);

        editIconButton.setOnClickListener(this);
        deleteIconButton.setOnClickListener(this);
        controllerButton.setOnClickListener(this);
        rendererButton.setOnClickListener(this);
        rendererInstallButton.setOnClickListener(this);
        driverButton.setOnClickListener(this);
        driverInstallButton.setOnClickListener(this);

        rendererText = findViewById(R.id.renderer);
        driverText = findViewById(R.id.driver);

        H2CO3LauncherProgressBar memoryBar = findViewById(R.id.memory_bar);

        H2CO3LauncherTextView memoryStateText = findViewById(R.id.memory_state);
        H2CO3LauncherTextView memoryText = findViewById(R.id.memory_text);
        H2CO3LauncherTextView memoryInfoText = findViewById(R.id.memory_info_text);
        H2CO3LauncherTextView memoryAllocateText = findViewById(R.id.memory_allocate_text);

        memoryStateText.stringProperty().bind(Bindings.createStringBinding(() -> {
            if (chkAutoAllocate.isChecked()) {
                return getContext().getString(R.string.settings_memory_lower_bound);
            } else {
                return getContext().getString(R.string.settings_memory);
            }
        }, chkAutoAllocate.checkProperty()));

        allocateSeekbar.setMax(MemoryUtils.getTotalDeviceMemory(getContext()));
        memoryBar.setMax(MemoryUtils.getTotalDeviceMemory(getContext()));

        allocateSeekbar.addProgressListener();
        allocateSeekbar.progressProperty().bindBidirectional(maxMemory);

        memoryText.stringProperty().bind(Bindings.createStringBinding(() -> allocateSeekbar.progressProperty().intValue() + " MB", allocateSeekbar.progressProperty()));
        memoryText.setOnClickListener(v -> {
            EditDialog dialog = new EditDialog(getContext(), s -> {
                if (s.matches("\\d+(\\.\\d+)?$")) {
                    allocateSeekbar.setProgress(Integer.parseInt(s));
                }
            });
            dialog.getEditText().setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
            dialog.show();
        });

        memoryBar.firstProgressProperty().bind(usedMemory);
        memoryBar.secondProgressProperty().bind(Bindings.createIntegerBinding(() -> {
            int allocate = (int) (H2CO3LauncherGameRepository.getAllocatedMemory(maxMemory.intValue() * 1024L * 1024L, MemoryUtils.getFreeDeviceMemory(getContext()) * 1024L * 1024L, chkAutoAllocate.isChecked()) / 1024. / 1024);
            return usedMemory.intValue() + (chkAutoAllocate.isChecked() ? allocate : maxMemory.intValue());
        }, usedMemory, maxMemory, chkAutoAllocate.checkProperty()));

        memoryInfoText.stringProperty().bind(Bindings.createStringBinding(() -> AndroidUtils.getLocalizedText(getContext(), "settings_memory_used_per_total", MemoryUtils.getUsedDeviceMemory(getContext()) / 1024., MemoryUtils.getTotalDeviceMemory(getContext()) / 1024.), usedMemory));

        memoryAllocateText.stringProperty().bind(Bindings.createStringBinding(() -> {
            long maxMemory = Lang.parseInt(this.maxMemory.get(), 0) * 1024L * 1024L;
            return AndroidUtils.getLocalizedText(getContext(), maxMemory / 1024. / 1024. > MemoryUtils.getFreeDeviceMemory(getContext())
                            ? (chkAutoAllocate.isChecked() ? "settings_memory_allocate_auto_exceeded" : "settings_memory_allocate_manual_exceeded")
                            : (chkAutoAllocate.isChecked() ? "settings_memory_allocate_auto" : "settings_memory_allocate_manual"),
                    maxMemory / 1024. / 1024. / 1024.,
                    H2CO3LauncherGameRepository.getAllocatedMemory(maxMemory, MemoryUtils.getFreeDeviceMemory(getContext()) * 1024L * 1024L, chkAutoAllocate.isChecked()) / 1024. / 1024. / 1024.,
                    MemoryUtils.getFreeDeviceMemory(getContext()) / 1024.);
        }, usedMemory, maxMemory, chkAutoAllocate.checkProperty()));

        settingTypeLayout.setVisibility(globalSetting ? View.GONE : View.VISIBLE);

        if (!globalSetting) {
            specialSettingSwitch.disableProperty().bind(modpack);
            specialSettingSwitch.checkProperty().bindBidirectional(enableSpecificSettings);
            settingLayout.visibilityProperty().bind(enableSpecificSettings);
        }

        enableSpecificSettings.addListener((a, b, newValue) -> {
            if (versionId == null) return;

            // do not call versionSettings.setUsesGlobal(true/false)
            // because versionSettings can be the global one.
            // global versionSettings.usesGlobal is always true.
            if (newValue)
                profile.getRepository().specializeVersionSetting(versionId);
            else
                profile.getRepository().globalizeVersionSetting(versionId);

            Schedulers.androidUIThread().execute(() -> loadVersion(profile, versionId));
        });
        vulkanDriverSystemSwitch.setOnClickListener(v -> {
            if (vulkanDriverSystemSwitch.checkProperty().get() && AndroidUtils.isAdrenoGPU()) {
                H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
                builder.setMessage(getContext().getString(R.string.message_vulkan_driver_system));
                builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                builder.create().show();
            }
        });
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        usedMemory.set(MemoryUtils.getUsedDeviceMemory(getContext()));
    }

    @Override
    public void loadVersion(Profile profile, String versionId) {
        this.profile = profile;
        this.versionId = versionId;
        this.listenerHolder = new WeakListenerHolder();

        if (versionId == null) {
            enableSpecificSettings.set(true);
            listenerHolder.add(FXUtils.onWeakChangeAndOperate(profile.selectedVersionProperty(), this.selectedVersion::setValue));
        }

        VersionSetting versionSetting = profile.getVersionSetting(versionId);
        versionSetting.checkController();

        modpack.set(versionId != null && profile.getRepository().isModpack(versionId));
        usedMemory.set(MemoryUtils.getUsedDeviceMemory(getContext()));

        InvalidationListener listener = observable -> ManagePageManager.getInstance().onRunDirectoryChange(profile, versionId);

        // unbind data fields
        if (lastVersionSetting != null) {
            lastVersionSetting.getIsolateGameDirProperty().removeListener(listener);
            FXUtils.unbind(txtJVMArgs, lastVersionSetting.getJavaArgsProperty());
            FXUtils.unbind(txtGameArgs, lastVersionSetting.getMinecraftArgsProperty());
            FXUtils.unbind(txtMetaspace, lastVersionSetting.getPermSizeProperty());
            FXUtils.unbind(txtServerIP, lastVersionSetting.getServerIpProperty());
            FXUtils.unbindBoolean(chkAutoAllocate, lastVersionSetting.getAutoMemoryProperty());
            FXUtils.unbindBoolean(isolateWorkingDirSwitch, lastVersionSetting.getIsolateGameDirProperty());
            FXUtils.unbindBoolean(pojavBigCoreSwitch, lastVersionSetting.getPojavBigCoreProperty());
            FXUtils.unbindBoolean(noGameCheckSwitch, lastVersionSetting.getNotCheckGameProperty());
            FXUtils.unbindBoolean(noJVMCheckSwitch, lastVersionSetting.getNotCheckJVMProperty());
            FXUtils.unbindBoolean(beGestureSwitch, lastVersionSetting.getBeGestureProperty());
            FXUtils.unbindBoolean(vulkanDriverSystemSwitch, lastVersionSetting.getVkDriverSystemProperty());
            FXUtils.unbindSelection(javaSpinner, lastVersionSetting.getJavaProperty());
            scaleFactorSeekbar.percentProgressProperty().unbindBidirectional(lastVersionSetting.getScaleFactorProperty());
            maxMemory.unbindBidirectional(lastVersionSetting.getMaxMemoryProperty());

            lastVersionSetting.getUsesGlobalProperty().removeListener(specificSettingsListener);
        }

        // bind new data fields
        if (getId() == ManagePageManager.PAGE_ID_MANAGE_SETTING) {
            versionSetting.getIsolateGameDirProperty().addListener(listener);
        }
        FXUtils.bindString(txtJVMArgs, versionSetting.getJavaArgsProperty());
        FXUtils.bindString(txtGameArgs, versionSetting.getMinecraftArgsProperty());
        FXUtils.bindString(txtMetaspace, versionSetting.getPermSizeProperty());
        FXUtils.bindString(txtServerIP, versionSetting.getServerIpProperty());
        FXUtils.bindBoolean(chkAutoAllocate, versionSetting.getAutoMemoryProperty());
        FXUtils.bindBoolean(isolateWorkingDirSwitch, versionSetting.getIsolateGameDirProperty());
        FXUtils.bindBoolean(pojavBigCoreSwitch, versionSetting.getPojavBigCoreProperty());
        FXUtils.bindBoolean(noGameCheckSwitch, versionSetting.getNotCheckGameProperty());
        FXUtils.bindBoolean(noJVMCheckSwitch, versionSetting.getNotCheckJVMProperty());
        FXUtils.bindBoolean(beGestureSwitch, versionSetting.getBeGestureProperty());
        FXUtils.bindBoolean(vulkanDriverSystemSwitch, versionSetting.getVkDriverSystemProperty());
        FXUtils.bindSelection(javaSpinner, versionSetting.getJavaProperty());
        scaleFactorSeekbar.percentProgressProperty().bindBidirectional(versionSetting.getScaleFactorProperty());
        maxMemory.bindBidirectional(versionSetting.getMaxMemoryProperty());
        H2CO3LauncherConfig.Renderer renderer = versionSetting.getRenderer();
        if (renderer == H2CO3LauncherConfig.Renderer.RENDERER_CUSTOM) {
            rendererText.setText(versionSetting.getCustomRenderer());
        } else {
            rendererText.setText(renderer.toString());
        }
        if (!versionSetting.getDriver().equals("Turnip")) {
            boolean isSelected = false;
            for (DriverPlugin.Driver driver : DriverPlugin.getDriverList()) {
                if (driver.getDriver().equals(versionSetting.getDriver())) {
                    DriverPlugin.setSelected(driver);
                    versionSetting.setDriver(driver.getDriver());
                    isSelected = true;
                }
            }
            if (!isSelected) {
                versionSetting.setDriver("Turnip");
            }
        }
        driverText.setText(versionSetting.getDriver());

        versionSetting.getUsesGlobalProperty().addListener(specificSettingsListener);
        if (versionId != null)
            enableSpecificSettings.set(!versionSetting.isUsesGlobal());

        lastVersionSetting = versionSetting;

        loadIcon();
    }

    private void onExploreIcon() {
        if (versionId == null)
            return;

        FileBrowser.Builder builder = new FileBrowser.Builder(getContext());
        ArrayList<String> suffix = new ArrayList<>();
        suffix.add(".png");
        builder.setLibMode(LibMode.FILE_CHOOSER);
        builder.setSelectionMode(SelectionMode.SINGLE_SELECTION);
        builder.setTitle(getContext().getString(R.string.settings_icon));
        builder.setSuffix(suffix);
        builder.create().browse(getActivity(), RequestCodes.SELECT_VERSION_ICON_CODE, (requestCode, resultCode, data) -> {
            if (requestCode == RequestCodes.SELECT_VERSION_ICON_CODE && resultCode == Activity.RESULT_OK && data != null) {
                if (FileBrowser.getSelectedFiles(data).size() == 0)
                    return;

                String path = FileBrowser.getSelectedFiles(data).get(0);
                Uri uri = Uri.parse(path);
                if (AndroidUtils.isDocUri(uri)) {
                    path = AndroidUtils.copyFileToDir(getActivity(), uri, new File(H2CO3LauncherTools.CACHE_DIR));
                }
                if (path == null)
                    return;

                File selectedFile = new File(path);
                File iconFile = profile.getRepository().getVersionIconFile(versionId);
                try {
                    FileUtils.copyFile(selectedFile, iconFile);

                    profile.getRepository().onVersionIconChanged.fireEvent(new Event(this));
                    loadIcon();
                } catch (IOException e) {
                    Logging.LOG.log(Level.SEVERE, "Failed to copy icon file from " + selectedFile + " to " + iconFile, e);
                }
            }
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void onDeleteIcon() {
        if (versionId == null)
            return;

        File iconFile = profile.getRepository().getVersionIconFile(versionId);
        if (iconFile.exists())
            iconFile.delete();
        profile.getRepository().onVersionIconChanged.fireEvent(new Event(this));
        loadIcon();
    }

    private void loadIcon() {
        if (versionId == null) {
            return;
        }

        iconView.setImageDrawable(profile.getRepository().getVersionIconImage(versionId));
    }

    @Override
    public void onClick(View view) {
        if (view == editIconButton) {
            onExploreIcon();
        }
        if (view == deleteIconButton) {
            onDeleteIcon();
        }
        if (view == controllerButton) {
            SelectControllerDialog dialog = new SelectControllerDialog(getContext(), lastVersionSetting.getController(), controller -> lastVersionSetting.setController(controller.getId()));
            dialog.show();
        }
        if (view == rendererButton) {
            int[] pos = new int[2];
            view.getLocationInWindow(pos);
            int windowHeight = getActivity().getWindow().getDecorView().getHeight();
            int y;
            if (pos[1] < windowHeight / 2) {
                y = pos[1];
            } else {
                y = 0;
            }
            RendererUtil.openRendererMenu(getContext(), view, pos[0], y, ConvertUtils.dip2px(getContext(), 200), windowHeight - y, name -> {
                rendererText.setText(name);
            });
        }
        if (view == driverButton) {
            RendererUtil.openDriverMenu(getContext(), view, name -> {
                driverText.setText(name);
            });
        }
        if (view == rendererInstallButton) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.message_install_plugin)
                    .setItems(new String[]{"Github", getContext().getString(R.string.update_netdisk)}, (d, w) -> {
                        String url = null;
                        switch (w) {
                            case 0:
                                url = "https://github.com/Boat-H2CO3/H2CO3LauncherRendererPlugin/releases/tag/Renderer";
                                break;
                            case 1:
                                url = "https://pan.quark.cn/s/a9f6e9d860d9";
                                break;
                        }
                        if (url != null) {
                            AndroidUtils.openLink(getContext(), url);
                        }
                    })
                    .setPositiveButton(R.string.button_cancel, null)
                    .create()
                    .show();
        }
        if (view == driverInstallButton) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.message_install_plugin)
                    .setItems(new String[]{"Github", getContext().getString(R.string.update_netdisk)}, (d, w) -> {
                        String url = null;
                        switch (w) {
                            case 0:
                                url = "https://github.com/Boat-H2CO3/H2CO3LauncherDriverPlugin/releases/tag/Turnip";
                                break;
                            case 1:
                                url = "https://pan.quark.cn/s/d87c59695250";
                                break;
                        }
                        if (url != null) {
                            AndroidUtils.openLink(getContext(), url);
                        }
                    })
                    .setPositiveButton(R.string.button_cancel, null)
                    .create()
                    .show();
        }
    }
}
