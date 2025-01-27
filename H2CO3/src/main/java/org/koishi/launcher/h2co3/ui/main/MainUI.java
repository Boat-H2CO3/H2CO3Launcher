package org.koishi.launcher.h2co3.ui.main;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.game.JarExecutorHelper;
import org.koishi.launcher.h2co3.game.TexturesLoader;
import org.koishi.launcher.h2co3.setting.Accounts;
import org.koishi.launcher.h2co3.setting.Controllers;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.setting.Profiles;
import org.koishi.launcher.h2co3.ui.account.AccountListAdapter;
import org.koishi.launcher.h2co3.ui.account.AccountListItem;
import org.koishi.launcher.h2co3.ui.account.AddAuthlibInjectorServerDialog;
import org.koishi.launcher.h2co3.ui.account.CreateAccountDialog;
import org.koishi.launcher.h2co3.ui.account.ServerListAdapter;
import org.koishi.launcher.h2co3.ui.version.AddProfileDialog;
import org.koishi.launcher.h2co3.ui.version.ProfileListAdapter;
import org.koishi.launcher.h2co3.ui.version.VersionList;
import org.koishi.launcher.h2co3.ui.version.Versions;
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
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableListBase;
import org.koishi.launcher.h2co3core.mod.RemoteMod;
import org.koishi.launcher.h2co3core.mod.RemoteModRepository;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.util.LocaleUtils;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3core.util.fakefx.BindingMapping;
import org.koishi.launcher.h2co3core.util.io.HttpRequest;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3launcher.plugins.DriverPlugin;
import org.koishi.launcher.h2co3launcher.plugins.RendererPlugin;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonUI;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3RecyclerView;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Stream;

public class MainUI extends H2CO3LauncherCommonUI implements View.OnClickListener {

    public static final String ANNOUNCEMENT_URL = "https://raw.githubusercontent.com/Boat-H2CO3/H2CO3Launcher-Repo/refs/heads/main/res/announcement_v2.txt";
    public static final String ANNOUNCEMENT_URL_CN = "https://gitee.com/cainiaohanhanyai/H2CO3Launcher-Repo/raw/main/res/announcement_v2.txt";
    private static WeakReference<MainUI> instance;
    private MaterialCardView announcementLayout;
    private H2CO3LauncherTextView title;
    private H2CO3LauncherTextView announcementView;
    private H2CO3LauncherTextView date;
    private H2CO3LauncherButton hide;
    private H2CO3LauncherTextView accountName;
    private H2CO3LauncherTextView accountHint;
    private H2CO3LauncherImageView avatar;
    private H2CO3LauncherButton executeJar;
    private H2CO3LauncherButton launch;
    private H2CO3LauncherButton launchH2CO3;
    private H2CO3LauncherProgressBar versionProgress;
    private H2CO3LauncherTextView versionName;
    private H2CO3LauncherTextView versionHint;
    private MaterialCardView versionLayout;
    private H2CO3LauncherImageView icon;
    private H2CO3LauncherButton account;
    private MaterialCardView version;
    private Announcement announcement = null;
    private ConstraintLayout root;
    private H2CO3LauncherButton addOfflineAccount;
    private H2CO3LauncherButton addMicrosoftAccount;
    private H2CO3LauncherButton addLoginServer;
    private H2CO3LauncherButton refresh;
    private H2CO3LauncherButton newProfile;
    private H2CO3RecyclerView profileListView;

    private VersionList versionList;
    private ListView accountListView;
    private AccountListAdapter accountListAdapter;
    private ObjectProperty<Account> currentAccount;
    private WeakListenerHolder holder = new WeakListenerHolder();
    private Profile profile;
    private Consumer<Event> onVersionIconChangedListener;
    private MaterialAlertDialogBuilder accountDialogBuilder, versionDialogBuilder;
    private View accountDialogView, versionDialogView;

    public MainUI(Context context, H2CO3LauncherUILayout parent, int id) {
        super(context, parent, id);
    }

    public static MainUI getInstance() {
        return instance != null ? instance.get() : null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getActivity().runOnUiThread(this::initializeViews);
        setupRemoteMod();
        root.post(() -> {
            setupClickListeners();
            setupAccountDisplay();
            setupVersionDisplay();
        });
        checkAnnouncement();
    }

    @Override
    public void onStart() {
        super.onStart();
        addLoadingCallback(() -> refresh().start());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public Task<?> refresh(Object... param) {
        addLoadingCallback(() -> {
            if (accountListAdapter == null) {
                ObservableList<AccountListItem> list = new ObservableListBase<AccountListItem>() {
                    @Override
                    public AccountListItem get(int i) {
                        return new AccountListItem(getContext(), Accounts.getAccounts().get(i));
                    }

                    @Override
                    public int size() {
                        return Accounts.getAccounts().size();
                    }
                };
                accountListAdapter = new AccountListAdapter(getContext(), list);
                accountListView.setAdapter(accountListAdapter);
            } else {
                accountListAdapter.notifyDataSetChanged();
            }
        });
        return Task.runAsync(() -> {
        });
    }

    private void initializeViews() {
        announcementLayout = findViewById(R.id.announcement_layout);
        title = findViewById(R.id.title);
        announcementView = findViewById(R.id.announcement);
        date = findViewById(R.id.date);
        hide = findViewById(R.id.hide);
        accountName = findViewById(R.id.account_name);
        accountHint = findViewById(R.id.account_hint);
        avatar = findViewById(R.id.avatar);
        executeJar = findViewById(R.id.execute_jar);
        launch = findViewById(R.id.launch);
        launchH2CO3 = findViewById(R.id.launch_h2co3);
        versionProgress = findViewById(R.id.version_progress);
        versionName = findViewById(R.id.version_name);
        versionHint = findViewById(R.id.version_hint);
        icon = findViewById(R.id.icon);
        account = findViewById(R.id.account);
        version = findViewById(R.id.version_layout);
        root = findViewById(R.id.root);

        accountDialogView = inflateDialogView(R.layout.main_dialog_account);
        versionDialogView = inflateDialogView(R.layout.page_version_list);

        addOfflineAccount = accountDialogView.findViewById(R.id.offline);
        addMicrosoftAccount = accountDialogView.findViewById(R.id.microsoft);
        addLoginServer = accountDialogView.findViewById(R.id.add_login_server);
        ListView serverListView = accountDialogView.findViewById(R.id.server_list);
        serverListView.setAdapter(new ServerListAdapter(getContext()));
        accountListView = accountDialogView.findViewById(R.id.list);
        accountDialogBuilder = createDialogBuilder(R.string.account, accountDialogView);

        refresh = versionDialogView.findViewById(R.id.refresh);
        newProfile = versionDialogView.findViewById(R.id.new_profile);
        profileListView = versionDialogView.findViewById(R.id.profile_list);
        H2CO3LauncherProgressBar progressBar = versionDialogView.findViewById(R.id.progress);
        H2CO3RecyclerView versionListView = versionDialogView.findViewById(R.id.version_list);
        refreshProfile();
        versionList = new VersionList(getContext(), versionListView, refresh, progressBar);
        versionDialogBuilder = createDialogBuilder(R.string.version, versionDialogView);

        setClickListeners(hide, addOfflineAccount, addMicrosoftAccount, addLoginServer, refresh, newProfile);
    }

    private View inflateDialogView(int layoutResId) {
        return LayoutInflater.from(getActivity()).inflate(layoutResId, null);
    }

    private MaterialAlertDialogBuilder createDialogBuilder(int titleResId, View view) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(titleResId);
        builder.setView(view);
        return builder;
    }

    private void setClickListeners(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    private void setupRemoteMod() {
        RemoteMod.registerEmptyRemoteMod(new RemoteMod(
                "",
                "",
                getActivity().getString(R.string.mods_broken_dependency_title),
                getActivity().getString(R.string.mods_broken_dependency_desc),
                new ArrayList<>(),
                "",
                "",
                new RemoteMod.IMod() {
                    @Override
                    public List<RemoteMod> loadDependencies(RemoteModRepository modRepository) throws IOException {
                        throw new IOException();
                    }

                    @Override
                    public Stream<RemoteMod.Version> loadVersions(RemoteModRepository modRepository) throws IOException {
                        throw new IOException();
                    }

                    @Override
                    public List<RemoteMod.Screenshot> loadScreenshots(RemoteModRepository modRepository) throws IOException {
                        throw new IOException();
                    }
                }
        ));
    }

    private void setupClickListeners() {
        setClickListeners(accountName, executeJar, account, version, launch, launchH2CO3);
        executeJar.setOnLongClickListener(v -> showJarExecuteDialog());
    }

    private void setupAccountDisplay() {
        root.post(() -> {
            currentAccount = new SimpleObjectProperty<>() {
                @Override
                public void invalidated() {
                    Account account = get();
                    if (account == null) {
                        accountName.setText(getActivity().getString(R.string.account_state_no_account));
                        accountHint.setText(getActivity().getString(R.string.account_state_add));
                        avatar.setBackground(new BitmapDrawable(
                                getActivity().getResources(),
                                TexturesLoader.toAvatar(
                                        TexturesLoader.getDefaultSkin(TextureModel.ALEX).image(),
                                        ConvertUtils.dip2px(getActivity(), 30f)
                                )
                        ));
                    } else {
                        accountName.stringProperty().bind(BindingMapping.of(account, Account::getCharacter));
                        accountHint.stringProperty().bind(accountSubtitle(getActivity(), account));
                        avatar.imageProperty().unbind();
                        avatar.imageProperty().bind(TexturesLoader.avatarBinding(account, ConvertUtils.dip2px(getActivity(), 30f)));
                    }
                }
            };
            currentAccount.bind(Accounts.selectedAccountProperty());
        });
    }

    private void setupVersionDisplay() {
        Profiles.selectedVersionProperty().addListener((observable, oldValue, newValue) -> loadVersion(newValue));
        Runnable listener = () -> loadVersion(Profiles.selectedVersionProperty().get());
        holder.add(listener);
        listener.run();
    }

    private boolean showJarExecuteDialog() {
        View dialogView = inflateDialogView(R.layout.edit_text);
        H2CO3LauncherEditText jarArgumentsEditText = dialogView.findViewById(android.R.id.text1);
        jarArgumentsEditText.setHint("-jar xxx");
        jarArgumentsEditText.setLines(1);
        jarArgumentsEditText.setMaxLines(1);

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getActivity());
        dialogBuilder.setTitle(R.string.jar_execute_custom_args);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton(org.koishi.launcher.h2co3library.R.string.dialog_positive, (dialog, which) ->
                JarExecutorHelper.exec(getActivity(), null, JarExecutorHelper.getJava(null), jarArgumentsEditText.getText().toString())
        );
        dialogBuilder.setNegativeButton(org.koishi.launcher.h2co3library.R.string.dialog_negative, null);

        dialogBuilder.create().show();
        return true;
    }

    private void showDialog(AlertDialog dialog, View view) {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
        dialog.show();
    }

    private void showVersionDialog() {
        AlertDialog dialog = versionDialogBuilder.create();
        showDialog(dialog, versionDialogView);
    }

    private void showAccountDialog() {
        AlertDialog dialog = accountDialogBuilder.create();
        showDialog(dialog, accountDialogView);
    }

    public void refreshProfile() {
        ProfileListAdapter adapter = new ProfileListAdapter(getContext(), Profiles.getProfiles());
        profileListView.setLayoutManager(new LinearLayoutManager(getContext()));
        profileListView.setAdapter(adapter);
    }

    private void loadVersion(String version) {
        Schedulers.androidUIThread().execute(() -> versionProgress.setVisibility(View.VISIBLE));
        if (Profiles.getSelectedProfile() != profile) {
            profile = Profiles.getSelectedProfile();
            if (profile != null) {
                onVersionIconChangedListener = profile.getRepository().onVersionIconChanged.registerWeak(event -> loadVersion(Profiles.getSelectedVersion()));
            }
        }
        if (version != null && Profiles.getSelectedProfile() != null && Profiles.getSelectedProfile().getRepository().hasVersion(version)) {
            Schedulers.defaultScheduler().execute(() -> {
                String game;
                try {
                    game = Profiles.getSelectedProfile().getRepository().getGameVersion(version)
                            .orElse(getActivity().getString(R.string.message_unknown));
                } catch (Exception e) {
                    game = null;
                }
                if (game == null) return;

                StringBuilder libraries = new StringBuilder(game);
                LibraryAnalyzer analyzer = LibraryAnalyzer.analyze(
                        Profiles.getSelectedProfile().getRepository().getResolvedPreservingPatchesVersion(version)
                );

                for (LibraryAnalyzer.LibraryMark mark : analyzer) {
                    String libraryId = mark.getLibraryId();
                    String libraryVersion = mark.getLibraryVersion();
                    if (libraryId.equals(LibraryAnalyzer.LibraryType.MINECRAFT.getPatchId()))
                        continue;

                    if (AndroidUtils.hasStringId(getActivity(), "install_installer_" + libraryId.replace("-", "_"))) {
                        libraries.append(", ").append(AndroidUtils.getLocalizedText(getActivity(), "install_installer_" + libraryId.replace("-", "_")));
                        if (libraryVersion != null) {
                            libraries.append(": ").append(libraryVersion.replaceAll("(?i)" + libraryId, ""));
                        }
                    }
                }

                final Drawable drawable = Profiles.getSelectedProfile().getRepository().getVersionIconImage(version);
                Schedulers.androidUIThread().execute(() -> {
                    versionProgress.setVisibility(View.GONE);
                    versionName.setText(version);
                    versionHint.setText(libraries.toString());
                    icon.setBackground(drawable);
                });
            });
        } else {
            versionProgress.setVisibility(View.GONE);
            versionName.setText(getActivity().getString(R.string.version_no_version));
            versionHint.setText(getActivity().getString(R.string.version_manage));
            icon.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.img_grass));
        }
    }

    private ObservableValue<String> accountSubtitle(Context context, Account account) {
        if (account instanceof AuthlibInjectorAccount) {
            return BindingMapping.of(((AuthlibInjectorAccount) account).getServer(),
                    AuthlibInjectorServer::getName);
        } else {
            return Bindings.createStringBinding(() ->
                    Accounts.getLocalizedLoginTypeName(context, Accounts.getAccountFactory(account))
            );
        }
    }

    public void refreshAvatar(Account account) {
        if (currentAccount.get() == account) {
            getActivity().runOnUiThread(() -> {
                avatar.imageProperty().bind(TexturesLoader.avatarBinding(account, ConvertUtils.dip2px(getActivity(), 30f)));
            });
        }
    }

    private void checkAnnouncement() {
        try {
            String url = LocaleUtils.isChinese(getContext()) ? ANNOUNCEMENT_URL_CN : ANNOUNCEMENT_URL;
            Task.supplyAsync(() -> HttpRequest.HttpGetRequest.GET(url).getJson(Announcement.class))
                    .thenAcceptAsync(Schedulers.androidUIThread(), announcement -> {
                        this.announcement = announcement;
                        if (!announcement.shouldDisplay(getContext()))
                            return;
                        announcementLayout.setVisibility(View.VISIBLE);
                        title.setText(AndroidUtils.getLocalizedText(getContext(), "announcement", announcement.getDisplayTitle(getContext())));
                        announcementView.setText(announcement.getDisplayContent(getContext()));
                        date.setText(AndroidUtils.getLocalizedText(getContext(), "update_date", announcement.getDate()));
                    }).start();
        } catch (Exception e) {
            Logging.LOG.log(Level.WARNING, "Failed to get announcement!", e);
        }
    }

    private void hideAnnouncement() {
        announcementLayout.setVisibility(View.GONE);
        if (announcement != null) {
            announcement.hide(getContext());
        }
    }

    @Override
    public void onClick(View view) {
        if (view == hide) {
            if (announcement != null && announcement.isSignificant()) {
                H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                builder.setCancelable(false);
                builder.setMessage(getContext().getString(R.string.announcement_significant));
                builder.setPositiveButton(this::hideAnnouncement);
                builder.setNegativeButton(null);
                builder.create().show();
            } else {
                hideAnnouncement();
            }
        } else if (view == account) {
            showAccountDialog();
        } else if (view == version) {
            showVersionDialog();
        } else if (view == executeJar) {
            JarExecutorHelper.start(getActivity(), getActivity());
        } else if (view == launch) {
            launchGame();
        } else if (view == launchH2CO3) {
            H2CO3LauncherBridge.BACKEND_IS_H2CO3 = true;
            launchGame();
        } else if (view == addOfflineAccount) {
            CreateAccountDialog dialog = new CreateAccountDialog(getContext(), Accounts.FACTORY_OFFLINE);
            dialog.createDialog();
        } else if (view == addMicrosoftAccount) {
            CreateAccountDialog dialog = new CreateAccountDialog(getContext(), Accounts.FACTORY_MICROSOFT);
            dialog.createDialog();
        } else if (view == addLoginServer) {
            AddAuthlibInjectorServerDialog dialog = new AddAuthlibInjectorServerDialog(getContext());
            dialog.dialog.show();
        } else if (view == refresh) {
            versionList.refreshList();
        } else if (view == newProfile) {
            AddProfileDialog dialog = new AddProfileDialog(getContext());
            dialog.createDialog();
        }
    }

    private void launchGame() {
        if (!Controllers.isInitialized()) {
            return;
        }
        Profile profile = Profiles.getSelectedProfile();
        RendererPlugin.getRendererList().forEach(renderer -> {
            if (renderer.getDes().equals(profile.getVersionSetting(profile.getSelectedVersion()).getCustomRenderer())) {
                RendererPlugin.setSelected(renderer);
            }
        });
        DriverPlugin.getDriverList().forEach(driver -> {
            if (driver.getDriver().equals(profile.getVersionSetting(profile.getSelectedVersion()).getDriver())) {
                DriverPlugin.setSelected(driver);
            }
        });
        Versions.launch(getActivity(), profile);
    }
}