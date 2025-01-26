package org.koishi.launcher.h2co3.ui.manage;

import static org.koishi.launcher.h2co3.setting.ConfigHolder.config;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Accounts;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.setting.VersionSetting;
import org.koishi.launcher.h2co3.ui.PageManager;
import org.koishi.launcher.h2co3.util.FXUtils;
import org.koishi.launcher.h2co3.util.RequestCodes;
import org.koishi.launcher.h2co3core.auth.Account;
import org.koishi.launcher.h2co3core.auth.authlibinjector.AuthlibInjectorServer;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleBooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleIntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleStringProperty;
import org.koishi.launcher.h2co3core.mod.ModAdviser;
import org.koishi.launcher.h2co3core.mod.ModpackExportInfo;
import org.koishi.launcher.h2co3core.mod.mcbbs.McbbsModpackManifest;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.util.Lang;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3core.util.platform.OperatingSystem;
import org.koishi.launcher.h2co3library.browser.FileBrowser;
import org.koishi.launcher.h2co3library.browser.options.LibMode;
import org.koishi.launcher.h2co3library.browser.options.SelectionMode;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherTempPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSeekBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSpinner;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSwitch;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ModpackInfoPage extends H2CO3LauncherTempPage implements View.OnClickListener {

    private final Profile profile;
    private final String versionName;
    private final String type;
    private final ModpackExportInfo.Options options;

    private final ModpackExportInfo exportInfo = new ModpackExportInfo();

    private final SimpleStringProperty path = new SimpleStringProperty("");
    private final SimpleStringProperty fileName = new SimpleStringProperty("");

    private final SimpleStringProperty name = new SimpleStringProperty("");
    private final SimpleStringProperty author = new SimpleStringProperty("");
    private final SimpleStringProperty version = new SimpleStringProperty("1.0");
    private final SimpleStringProperty description = new SimpleStringProperty("");
    private final SimpleStringProperty url = new SimpleStringProperty("");
    private final SimpleBooleanProperty forceUpdate = new SimpleBooleanProperty();
    private final SimpleStringProperty fileApi = new SimpleStringProperty("");
    private final SimpleIntegerProperty minMemory = new SimpleIntegerProperty(0);
    private final SimpleStringProperty authlibInjectorServer = new SimpleStringProperty();
    private final SimpleStringProperty launchArguments = new SimpleStringProperty("");
    private final SimpleStringProperty javaArguments = new SimpleStringProperty("");
    private final SimpleStringProperty mcbbsThreadId = new SimpleStringProperty("");

    private H2CO3LauncherImageButton pathButton;
    private H2CO3LauncherButton next;

    public ModpackInfoPage(Context context, int id, H2CO3LauncherUILayout parent, int resId, Profile profile, String version, String type, ModpackExportInfo.Options options) {
        super(context, id, parent, resId);
        this.profile = profile;
        this.versionName = version;
        this.type = type;
        this.options = options;

        name.set(version);
        author.set(Optional.ofNullable(Accounts.getSelectedAccount()).map(Account::getUsername).orElse(""));

        VersionSetting versionSetting = profile.getRepository().getVersionSetting(versionName);
        minMemory.set(Optional.ofNullable(versionSetting.getMinMemory()).orElse(0));
        launchArguments.set(versionSetting.getMinecraftArgs());
        javaArguments.set(versionSetting.getJavaArgs());
    }

    @Override
    public void onStart() {
        super.onStart();

        H2CO3LauncherLinearLayout fileApiLayout = findViewById(R.id.file_api_layout);
        H2CO3LauncherLinearLayout launchArgsLayout = findViewById(R.id.minecraft_args_layout);
        H2CO3LauncherLinearLayout jvmArgsLayout = findViewById(R.id.jvm_args_layout);
        H2CO3LauncherLinearLayout originUrlLayout = findViewById(R.id.origin_url_layout);
        H2CO3LauncherLinearLayout mcbbsLayout = findViewById(R.id.mcbbs_layout);
        H2CO3LauncherLinearLayout memoryLayout = findViewById(R.id.memory_layout);
        H2CO3LauncherLinearLayout serverLayout = findViewById(R.id.server_layout);
        H2CO3LauncherLinearLayout forceUpdateLayout = findViewById(R.id.force_update_layout);
        View splitF = findViewById(R.id.split_1);
        View splitS = findViewById(R.id.split_2);
        View splitT = findViewById(R.id.split_3);

        H2CO3LauncherTextView versionNameText = findViewById(R.id.game_version);
        H2CO3LauncherEditText nameText = findViewById(R.id.name);
        H2CO3LauncherEditText authorText = findViewById(R.id.author);
        H2CO3LauncherEditText versionText = findViewById(R.id.version);
        H2CO3LauncherEditText fileApiText = findViewById(R.id.file_api);
        H2CO3LauncherEditText launchArgsText = findViewById(R.id.minecraft_args);
        H2CO3LauncherEditText jvmArgsText = findViewById(R.id.jvm_args);
        H2CO3LauncherEditText originUrlText = findViewById(R.id.origin_url);
        H2CO3LauncherEditText mcbbsText = findViewById(R.id.mcbbs);
        H2CO3LauncherSeekBar memorySeekbar = findViewById(R.id.memory);
        H2CO3LauncherTextView memoryText = findViewById(R.id.memory_text);
        H2CO3LauncherEditText descText = findViewById(R.id.desc);
        H2CO3LauncherSpinner<String> serverSpinner = findViewById(R.id.server);
        H2CO3LauncherSwitch forceUpdateSwitch = findViewById(R.id.force_update);
        H2CO3LauncherTextView pathText = findViewById(R.id.path_text);
        pathButton = findViewById(R.id.path);
        H2CO3LauncherEditText fileNameText = findViewById(R.id.file_name);
        next = findViewById(R.id.next);

        versionNameText.setText(versionName);
        nameText.setText(name.get());
        nameText.stringProperty().bindBidirectional(name);
        authorText.setText(author.get());
        authorText.stringProperty().bindBidirectional(author);
        versionText.setText(version.get());
        versionText.stringProperty().bindBidirectional(version);
        if (options.isRequireFileApi()) {
            if (options.isValidateFileApi()) {
                fileApiText.setHint(getContext().getString(R.string.input_hint_not_empty));
            } else {
                fileApiText.setHint("");
            }
            fileApiText.stringProperty().bindBidirectional(fileApi);
        }
        fileApiLayout.setVisibility(options.isRequireFileApi() ? View.VISIBLE : View.GONE);
        if (options.isRequireLaunchArguments()) {
            launchArgsText.setText(launchArguments.get());
            launchArgsText.stringProperty().bindBidirectional(launchArguments);
        }
        launchArgsLayout.setVisibility(options.isRequireLaunchArguments() ? View.VISIBLE : View.GONE);
        if (options.isRequireJavaArguments()) {
            jvmArgsText.setText(javaArguments.get());
            jvmArgsText.stringProperty().bindBidirectional(javaArguments);
        }
        jvmArgsLayout.setVisibility(options.isRequireJavaArguments() ? View.VISIBLE : View.GONE);
        if (options.isRequireUrl()) {
            originUrlText.stringProperty().bindBidirectional(url);
        }
        originUrlLayout.setVisibility(options.isRequireUrl() ? View.VISIBLE : View.GONE);
        if (options.isRequireOrigins()) {
            mcbbsText.stringProperty().bindBidirectional(mcbbsThreadId);
        }
        mcbbsLayout.setVisibility(options.isRequireOrigins() ? View.VISIBLE : View.GONE);
        if (options.isRequireMinMemory()) {
            memorySeekbar.setProgress(minMemory.get());
            memorySeekbar.addProgressListener();
            memorySeekbar.progressProperty().bindBidirectional(minMemory);
            memoryText.stringProperty().bind(Bindings.createStringBinding(() -> minMemory.get() + " MB", minMemory));
        }
        memoryLayout.setVisibility(options.isRequireMinMemory() ? View.VISIBLE : View.GONE);
        splitF.setVisibility(options.isRequireMinMemory() ? View.VISIBLE : View.GONE);
        descText.stringProperty().bindBidirectional(description);
        if (options.isRequireAuthlibInjectorServer()) {
            ArrayList<String> list = (ArrayList<String>) config().getAuthlibInjectorServers().stream().map(AuthlibInjectorServer::getName).collect(Collectors.toList());
            Map<String, String> map = new HashMap<>();
            list.add(0, "");
            map.put("", null);
            config().getAuthlibInjectorServers().forEach(it -> map.put(it.getName(), it.getUrl()));
            serverSpinner.setDataList(list);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_auto_tint, list);
            adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
            serverSpinner.setAdapter(adapter);
            SimpleStringProperty serverName = new SimpleStringProperty("");
            FXUtils.bindSelection(serverSpinner, serverName);
            serverName.addListener(observable -> authlibInjectorServer.set(map.get(serverName.get())));
        }
        serverLayout.setVisibility(options.isRequireAuthlibInjectorServer() ? View.VISIBLE : View.GONE);
        splitS.setVisibility(options.isRequireAuthlibInjectorServer() ? View.VISIBLE : View.GONE);
        if (options.isRequireForceUpdate()) {
            forceUpdateSwitch.addCheckedChangeListener();
            forceUpdateSwitch.checkProperty().bindBidirectional(forceUpdate);
        }
        forceUpdateLayout.setVisibility(options.isRequireForceUpdate() ? View.VISIBLE : View.GONE);
        splitT.setVisibility(options.isRequireForceUpdate() ? View.VISIBLE : View.GONE);
        pathText.stringProperty().bind(path);
        pathButton.setOnClickListener(this);
        fileNameText.stringProperty().bindBidirectional(fileName);
        next.setOnClickListener(this);
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void onRestart() {

    }

    private void selectPath() {
        FileBrowser.Builder builder = new FileBrowser.Builder(getContext());
        builder.setLibMode(LibMode.FOLDER_CHOOSER);
        builder.setSelectionMode(SelectionMode.SINGLE_SELECTION);
        builder.create().browse(getActivity(), RequestCodes.SELECT_EXPORT_FOLDER_CODE, ((requestCode, resultCode, data) -> {
            if (requestCode == RequestCodes.SELECT_EXPORT_FOLDER_CODE && resultCode == Activity.RESULT_OK && data != null) {
                String p = FileBrowser.getSelectedFiles(data).get(0);
                path.set(p);
            }
        }));
    }

    @Override
    public void onClick(View v) {
        if (v == pathButton) {
            selectPath();
        }
        if (v == next) {
            boolean urlValid = false;
            if (StringUtils.isNotBlank(fileApi.get())) {
                try {
                    new URL(fileApi.get()).toURI();
                    urlValid = true;
                } catch (IOException | URISyntaxException ignored) {
                }
            }
            if (StringUtils.isBlank(name.get()) || StringUtils.isBlank(author.get()) || StringUtils.isBlank(version.get()) || StringUtils.isBlank(fileName.get())
                    || (options.isRequireFileApi() && options.isValidateFileApi() && StringUtils.isBlank(fileApi.get()))) {
                Toast.makeText(getContext(), getContext().getString(R.string.input_not_empty), Toast.LENGTH_SHORT).show();
            } else if (options.isRequireFileApi() && StringUtils.isNotBlank(fileApi.get()) && !urlValid) {
                Toast.makeText(getContext(), getContext().getString(R.string.input_url), Toast.LENGTH_SHORT).show();
            } else if (options.isRequireOrigins() && StringUtils.isNotBlank(mcbbsThreadId.get()) && Lang.toIntOrNull(mcbbsThreadId.get()) == null) {
                Toast.makeText(getContext(), getContext().getString(R.string.input_number), Toast.LENGTH_SHORT).show();
            } else if (!OperatingSystem.isNameValid(fileName.get())) {
                Toast.makeText(getContext(), getContext().getString(R.string.install_new_game_malformed), Toast.LENGTH_SHORT).show();
            } else if (StringUtils.isBlank(path.get())) {
                selectPath();
            } else {
                File file = new File(path.get(), fileName.get() + ".zip");

                if (file.exists()) {
                    Toast.makeText(getContext(), getContext().getString(R.string.message_file_exist), Toast.LENGTH_SHORT).show();
                    return;
                }

                exportInfo.setName(name.get());
                exportInfo.setFileApi(fileApi.get());
                exportInfo.setVersion(version.get());
                exportInfo.setAuthor(author.get());
                exportInfo.setDescription(description.get());
                exportInfo.setPackWithLauncher(false);
                exportInfo.setUrl(url.get());
                exportInfo.setForceUpdate(forceUpdate.get());
                exportInfo.setMinMemory(minMemory.get());
                exportInfo.setLaunchArguments(launchArguments.get());
                exportInfo.setJavaArguments(javaArguments.get());
                exportInfo.setAuthlibInjectorServer(authlibInjectorServer.get());

                if (StringUtils.isNotBlank(mcbbsThreadId.get())) {
                    exportInfo.setOrigins(Collections.singletonList(new McbbsModpackManifest.Origin(
                            "mcbbs", Integer.parseInt(mcbbsThreadId.get())
                    )));
                }

                ModpackFileSelectionPage page = new ModpackFileSelectionPage(getContext(), PageManager.PAGE_ID_TEMP, getParent(), R.layout.page_modpack_file, profile, versionName, type, ModAdviser::suggestMod, exportInfo, file);
                ManagePageManager.getInstance().showTempPage(page);
            }
        }
    }
}
