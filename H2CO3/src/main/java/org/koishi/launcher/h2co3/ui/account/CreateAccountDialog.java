package org.koishi.launcher.h2co3.ui.account;

import static org.koishi.launcher.h2co3.setting.Accounts.FACTORY_AUTHLIB_INJECTOR;
import static org.koishi.launcher.h2co3.setting.Accounts.FACTORY_MICROSOFT;
import static org.koishi.launcher.h2co3.setting.Accounts.FACTORY_OFFLINE;
import static org.koishi.launcher.h2co3.setting.ConfigHolder.config;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.game.OAuthServer;
import org.koishi.launcher.h2co3.game.TexturesLoader;
import org.koishi.launcher.h2co3.setting.Accounts;
import org.koishi.launcher.h2co3.ui.UIManager;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.FXUtils;
import org.koishi.launcher.h2co3.util.WeakListenerHolder;
import org.koishi.launcher.h2co3core.auth.AccountFactory;
import org.koishi.launcher.h2co3core.auth.CharacterSelector;
import org.koishi.launcher.h2co3core.auth.NoSelectedCharacterException;
import org.koishi.launcher.h2co3core.auth.authlibinjector.AuthlibInjectorAccountFactory;
import org.koishi.launcher.h2co3core.auth.authlibinjector.AuthlibInjectorServer;
import org.koishi.launcher.h2co3core.auth.authlibinjector.BoundAuthlibInjectorAccountFactory;
import org.koishi.launcher.h2co3core.auth.microsoft.MicrosoftAccountFactory;
import org.koishi.launcher.h2co3core.auth.offline.OfflineAccountFactory;
import org.koishi.launcher.h2co3core.auth.yggdrasil.GameProfile;
import org.koishi.launcher.h2co3core.auth.yggdrasil.YggdrasilService;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.task.TaskExecutor;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTabLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

public class CreateAccountDialog extends H2CO3CustomViewDialog implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    private static final Pattern USERNAME_CHECKER_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");
    private static CreateAccountDialog instance;
    private final ObjectProperty<OAuthServer.GrantDeviceCodeEvent> deviceCode = new SimpleObjectProperty<>();
    private H2CO3LauncherTextView title;
    private H2CO3LauncherTabLayout tabLayout;
    private RelativeLayout detailsContainer;
    private H2CO3LauncherButton login;
    private H2CO3LauncherButton cancel;
    private boolean showMethodSwitcher;
    private AccountFactory<?> factory;
    private TaskExecutor loginTask;
    private Details details;

    public CreateAccountDialog(@NonNull Context context, AccountFactory<?> factory) {
        super(context);
        instance = this;
        setCustomView(R.layout.dialog_create_account);
        title = getCustomView().findViewById(R.id.title);
        tabLayout = getCustomView().findViewById(R.id.tab_layout);
        detailsContainer = getCustomView().findViewById(R.id.detail_container);
        login = getCustomView().findViewById(R.id.login);
        cancel = getCustomView().findViewById(R.id.cancel);
        alertDialog = create();
        alertDialog.setCancelable(false);
        login.setOnClickListener(this);
        cancel.setOnClickListener(this);
        init(factory);
    }

    public CreateAccountDialog(@NonNull Context context, AuthlibInjectorServer authServer) {
        this(context, Accounts.getAccountFactoryByAuthlibInjectorServer(authServer));
    }

    public static CreateAccountDialog getInstance() {
        return instance;
    }

    private void init(AccountFactory<?> factory) {
        if (factory == null) {
            showMethodSwitcher = true;
            String preferred = config().getPreferredLoginType();
            try {
                factory = Accounts.getAccountFactory(preferred);
            } catch (IllegalArgumentException e) {
                factory = FACTORY_OFFLINE;
            }
        } else {
            showMethodSwitcher = false;
        }
        this.factory = factory;

        int titleId;
        if (showMethodSwitcher) {
            titleId = R.string.account_create;
        } else {
            if (factory instanceof OfflineAccountFactory) {
                titleId = R.string.account_create_offline;
            } else if (factory instanceof MicrosoftAccountFactory) {
                titleId = R.string.account_create_microsoft;
            } else {
                titleId = R.string.account_create_external;
            }
        }
        title.setText(getContext().getString(titleId));
        tabLayout.setVisibility(showMethodSwitcher ? View.VISIBLE : View.GONE);
        tabLayout.addOnTabSelectedListener(this);

        initDetails();
    }

    private void initDetails() {
        if (factory instanceof OfflineAccountFactory) {
            details = new OfflineDetails(getContext());
        }
        if (factory instanceof MicrosoftAccountFactory) {
            details = new MicrosoftDetails(getContext());
        }
        if (factory instanceof AuthlibInjectorAccountFactory) {
            details = new ExternalDetails(getContext());
        }
        if (factory instanceof BoundAuthlibInjectorAccountFactory) {
            details = new ExternalDetails(getContext(), ((BoundAuthlibInjectorAccountFactory) factory).getServer());
        }
        detailsContainer.removeAllViews();
        detailsContainer.addView(details.getView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void login() {
        login.setEnabled(false);
        cancel.setEnabled(false);

        String username;
        String password;
        Object additionalData;
        try {
            username = details.getUsername();
            password = details.getPassword();
            additionalData = details.getAdditionalData();
        } catch (IllegalStateException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            login.setEnabled(true);
            cancel.setEnabled(true);
            return;
        }

        Runnable doCreate = () -> {
            deviceCode.set(null);

            CharacterSelector selector = new DialogCharacterSelector(getContext());
            loginTask = Task.supplyAsync(() -> factory.create(selector, username, password, null, additionalData))
                    .whenComplete(Schedulers.androidUIThread(), account -> {
                        int oldIndex = Accounts.getAccounts().indexOf(account);
                        if (oldIndex == -1) {
                            Accounts.getAccounts().add(account);
                        } else {
                            Accounts.getAccounts().remove(oldIndex);
                            Accounts.getAccounts().add(oldIndex, account);
                        }

                        Accounts.setSelectedAccount(account);

                        login.setEnabled(true);
                        cancel.setEnabled(true);
                        UIManager.getInstance().getMainUI().refresh().start();
                        dismissDialog();
                    }, exception -> {
                        login.setEnabled(true);
                        cancel.setEnabled(true);
                        if (exception instanceof NoSelectedCharacterException) {
                            dismissDialog();
                        } else {
                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                            builder.setMessage(Accounts.localizeErrorMessage(getContext(), exception))
                                    .setCancelable(false)
                                    .setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null)
                                    .create()

                                    .show();
                            builder.setTitle(getContext().getString(R.string.message_error));
                        }
                    }).executor(true);
        };

        if (factory instanceof OfflineAccountFactory && username != null && !USERNAME_CHECKER_PATTERN.matcher(username).matches()) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle(getContext().getString(R.string.message_warning))
                    .setMessage(getContext().getString(R.string.account_methods_offline_name_invalid))
                    .setCancelable(false)
                    .setPositiveButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), (dialog, which) -> {
                        doCreate.run();
                    })
                    .setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_negative), (dialog, which) -> {
                        login.setEnabled(true);
                        cancel.setEnabled(true);
                    })
                    .create()
                    .show();
        } else {
            doCreate.run();
        }
    }

    private void onCancel() {
        if (loginTask != null) {
            loginTask.cancel();
        }
        dismissDialog();
        create().dismiss();
    }

    @Override
    public void onClick(View view) {
        if (view == login) {
            login();
        }
        if (view == cancel) {
            onCancel();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        AccountFactory<?> newMethod = FACTORY_OFFLINE;
        if (tab.getPosition() == 1) {
            newMethod = FACTORY_MICROSOFT;
        }
        if (tab.getPosition() == 2) {
            newMethod = FACTORY_AUTHLIB_INJECTOR;
        }
        config().setPreferredLoginType(Accounts.getLoginType(newMethod));
        this.factory = newMethod;
        initDetails();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    // details panel
    private interface Details {
        String getUsername();

        String getPassword();

        Object getAdditionalData();

        View getView();
    }

    private static class OfflineDetails implements Details {

        private final Context context;
        private final View view;

        private final H2CO3LauncherEditText username;

        public OfflineDetails(Context context) {
            this.context = context;
            this.view = LayoutInflater.from(context).inflate(R.layout.view_create_account_offline, null);
            username = view.findViewById(R.id.username);
        }

        @Override
        public String getUsername() throws IllegalStateException {
            if (StringUtils.isBlank(username.getText().toString())) {
                throw new IllegalStateException(context.getString(R.string.account_create_alert));
            }
            return username.getText().toString();
        }

        @Override
        public String getPassword() throws IllegalStateException {
            return null;
        }

        @Override
        public Object getAdditionalData() throws IllegalStateException {
            return null;
        }

        @Override
        public View getView() throws IllegalStateException {
            return view;
        }
    }

    private static class MicrosoftDetails implements Details {

        private final Context context;
        private final View view;

        private final WeakListenerHolder holder = new WeakListenerHolder();

        public MicrosoftDetails(Context context) {
            this.context = context;
            this.view = LayoutInflater.from(context).inflate(R.layout.view_create_account_microsoft, null);

            Handler handler = new Handler();
            FXUtils.onChangeAndOperate(CreateAccountDialog.getInstance().deviceCode, deviceCode -> handler.post(() -> {
                if (deviceCode != null) {
                    AndroidUtils.copyText(context, deviceCode.getUserCode());
                }
            }));
            holder.add(Accounts.OAUTH_CALLBACK.onGrantDeviceCode.registerWeak(value -> CreateAccountDialog.getInstance().deviceCode.set(value)));
        }

        @Override
        public String getUsername() throws IllegalStateException {
            return null;
        }

        @Override
        public String getPassword() throws IllegalStateException {
            return null;
        }

        @Override
        public Object getAdditionalData() throws IllegalStateException {
            return null;
        }

        @Override
        public View getView() throws IllegalStateException {
            return view;
        }
    }

    private static class ExternalDetails implements Details {

        private static final String[] ALLOWED_LINKS = {"homepage", "register"};

        private final Context context;
        private final View view;
        private final H2CO3LauncherEditText username;
        private final H2CO3LauncherEditText password;
        @Nullable
        private final AuthlibInjectorServer server;
        private H2CO3LauncherTextView serverName;
        private H2CO3LauncherImageButton home;
        private H2CO3LauncherImageButton register;

        public ExternalDetails(Context context) {
            this(context, config().getAuthlibInjectorServers().isEmpty() ? null : config().getAuthlibInjectorServers().get(0));
        }

        public ExternalDetails(Context context, @Nullable AuthlibInjectorServer server) {
            this.context = context;
            this.view = LayoutInflater.from(context).inflate(R.layout.view_create_account_external, null);

            serverName = view.findViewById(R.id.server_name);
            home = view.findViewById(R.id.home);
            register = view.findViewById(R.id.register);
            username = view.findViewById(R.id.username);
            password = view.findViewById(R.id.password);

            this.server = server;
            refreshAuthenticateServer(server);
        }

        public void refreshAuthenticateServer(AuthlibInjectorServer authlibInjectorServer) {
            if (authlibInjectorServer == null) {
                serverName.setText(context.getString(R.string.account_create_server_not_select));
                home.setVisibility(View.GONE);
                register.setVisibility(View.GONE);
            } else {
                serverName.setText(authlibInjectorServer.getName());
                Map<String, String> links = authlibInjectorServer.getLinks();
                if (links.get("homepage") != null) {
                    home.setVisibility(View.VISIBLE);
                    home.setOnClickListener(view -> {
                        Uri uri = Uri.parse(links.get("homepage"));
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    });
                } else {
                    home.setVisibility(View.GONE);
                }
                if (links.get("register") != null) {
                    register.setVisibility(View.VISIBLE);
                    register.setOnClickListener(view -> {
                        Uri uri = Uri.parse(links.get("register"));
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    });
                } else {
                    register.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public String getUsername() throws IllegalStateException {
            if (StringUtils.isBlank(username.getText().toString())) {
                throw new IllegalStateException(context.getString(R.string.account_create_alert));
            }
            return username.getText().toString();
        }

        @Override
        public String getPassword() throws IllegalStateException {
            if (StringUtils.isBlank(password.getText().toString())) {
                throw new IllegalStateException(context.getString(R.string.account_create_alert));
            }
            return password.getText().toString();
        }

        @Override
        public Object getAdditionalData() throws IllegalStateException {
            if (server == null) {
                throw new IllegalStateException(context.getString(R.string.account_create_server_not_select));
            }
            return server;
        }

        @Override
        public View getView() throws IllegalStateException {
            return view;
        }
    }

    // character selector
    private static class DialogCharacterSelector implements CharacterSelector, View.OnClickListener {

        private final Handler handler;
        private final CountDownLatch latch = new CountDownLatch(1);
        private final Context context;
        private GameProfile selectedProfile = null;

        public DialogCharacterSelector(Context context) {
            this.context = context;
            handler = new Handler();
        }

        public void refresh(YggdrasilService service, List<GameProfile> profiles) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setCancelable(false);

            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_character_selector, null);
            ListView listView = dialogView.findViewById(R.id.list);
            H2CO3LauncherButton cancel = dialogView.findViewById(R.id.negative);
            cancel.setOnClickListener(this);

            Adapter adapter = new Adapter(context, service, profiles, profile -> {
                selectedProfile = profile;
                latch.countDown();
            });
            listView.setAdapter(adapter);
            builder.setView(dialogView);

            builder.setOnDismissListener(dialog -> latch.countDown());
            builder.show();
        }

        @Override
        public GameProfile select(YggdrasilService service, List<GameProfile> profiles) throws NoSelectedCharacterException {
            handler.post(() -> refresh(service, profiles));

            try {
                latch.await();

                if (selectedProfile == null)
                    throw new NoSelectedCharacterException();

                return selectedProfile;
            } catch (InterruptedException ignored) {
                throw new NoSelectedCharacterException();
            }
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.negative) {
                latch.countDown();
            }
        }

        private static class Adapter extends H2CO3LauncherAdapter {

            private final YggdrasilService service;
            private final List<GameProfile> profiles;
            private final Listener listener;

            public Adapter(Context context, YggdrasilService service, List<GameProfile> profiles, Listener listener) {
                super(context);
                this.service = service;
                this.profiles = profiles;
                this.listener = listener;
            }

            @Override
            public int getCount() {
                return profiles.size();
            }

            @Override
            public Object getItem(int i) {
                return profiles.get(i);
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                final ViewHolder viewHolder;
                if (view == null) {
                    viewHolder = new ViewHolder();
                    view = LayoutInflater.from(getContext()).inflate(R.layout.item_character, null);
                    viewHolder.parent = view.findViewById(R.id.parent);
                    viewHolder.avatar = view.findViewById(R.id.avatar);
                    viewHolder.name = view.findViewById(R.id.name);
                    view.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }
                GameProfile gameProfile = profiles.get(i);
                viewHolder.name.setText(gameProfile.getName());
                viewHolder.avatar.imageProperty().bind(TexturesLoader.avatarBinding(service, gameProfile.getId(), 32));
                viewHolder.parent.setOnClickListener(view1 -> listener.onSelect(gameProfile));
                return view;
            }

            interface Listener {
                void onSelect(GameProfile profile);
            }

            static class ViewHolder {
                ConstraintLayout parent;
                H2CO3LauncherImageView avatar;
                H2CO3LauncherTextView name;
            }
        }
    }
}
