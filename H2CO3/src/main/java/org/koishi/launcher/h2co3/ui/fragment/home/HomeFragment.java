package org.koishi.launcher.h2co3.ui.fragment.home;

import static org.koishi.launcher.h2co3.ui.H2CO3LauncherClientActivity.attachControllerInterface;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.circularreveal.CircularRevealFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.adapter.HomeListUserAdapter;
import org.koishi.launcher.h2co3.application.H2CO3Application;
import org.koishi.launcher.h2co3.core.H2CO3Auth;
import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;
import org.koishi.launcher.h2co3.core.login.other.AuthResult;
import org.koishi.launcher.h2co3.core.login.other.LoginUtils;
import org.koishi.launcher.h2co3.core.login.other.Servers;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.handler.HomeLoginHandler;
import org.koishi.launcher.h2co3.resources.component.H2CO3Button;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3ProgressDialog;
import org.koishi.launcher.h2co3.ui.H2CO3LauncherClientActivity;
import org.koishi.launcher.h2co3.ui.MicrosoftLoginActivity;
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class HomeFragment extends H2CO3Fragment implements View.OnClickListener {

    private static final int MICROSOFT_LOGIN_REQUEST_CODE = 1001;

    private final Handler uiHandler = new Handler();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final HomeLoginHandler loginHandler = new HomeLoginHandler(this);

    private HomeListUserAdapter userAdapter;

    public H2CO3CustomViewDialog loginDialog;
    public H2CO3ProgressDialog progressDialog;
    public H2CO3TextView userNameTextView;
    public H2CO3TextView userStateTextView;
    public AppCompatImageView userIconImageView;
    private RecyclerView userRecyclerView;
    private H2CO3Button playButton;
    private H2CO3Button userListButton;
    private H2CO3CardView userListLayout;
    private CircularRevealFrameLayout loginLayout;
    private TextInputEditText loginNameInput, loginPasswordInput;
    private ConstraintLayout serverSelectorLayout;
    private TextInputLayout loginPasswordLayout;
    private H2CO3Button registerButton;
    private LinearProgressIndicator loadingIndicator;
    private Spinner serverSpinner;
    private H2CO3TextView noticeTextView;

    private ArrayList<UserBean> users = new ArrayList<>();
    private ArrayAdapter<String> serverAdapter;
    private List<String> serverNames;
    private Servers serverData;
    private String currentBaseUrl;
    private String currentRegisterUrl;
    private String username;
    private String password;
    private boolean isLoginDialogVisible = false;

    public AlertDialog loginDialogAlert;

    private H2CO3Settings h2co3Settings;
    public H2CO3Auth h2co3Auth;

    private final LoginUtils.Listener loginListener = new LoginUtils.Listener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSuccess(AuthResult authResult) {
            requireActivity().runOnUiThread(() -> {
                progressDialog.dismiss();
                handleAuthResult(authResult);
            });
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onFailed(String error) {
            requireActivity().runOnUiThread(() -> {
                progressDialog.dismiss();
                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, error);
                loginDialogAlert.dismiss();
            });
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeUIComponents(view);
        try {
            initializeFragment();
        } catch (IOException e) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
        }
        return view;
    }

    private void initializeUIComponents(View view) {
        playButton = findViewById(view, R.id.home_game_play_button);
        userNameTextView = findViewById(view, R.id.home_user_name);
        userStateTextView = findViewById(view, R.id.home_user_state);
        userIconImageView = findViewById(view, R.id.home_user_icon);
        userListButton = findViewById(view, R.id.home_user_open_list);
        userListLayout = findViewById(view, R.id.home_user_list_layout);
        userRecyclerView = findViewById(view, R.id.recycler_view_user_list);
        noticeTextView = findViewById(view, R.id.home_notice_text);
        loadingIndicator = findViewById(view, R.id.progressIndicator);

        playButton.setOnClickListener(this);
        userListButton.setOnClickListener(this);
    }

    private void initializeFragment() throws IOException {
        this.h2co3Settings = new H2CO3Settings();
        this.h2co3Auth = new H2CO3Auth(h2co3Settings);
        initializeUserState();
        setupRecyclerView();
        loadUsers();
        fetchNotifications();
    }

    private void initializeUserState() {
        String userJson = h2co3Auth.getUserJson();
        if (TextUtils.isEmpty(userJson) || "{}".equals(userJson)) {
            FileTools.writeFile(h2co3Settings.usersFile, "{}");
            setDefaultUserState();
        } else {
            setUserStateFromJson();
        }
    }

    private void setupRecyclerView() {
        userRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }

    private void fetchNotifications() {
        H2CO3Application.sExecutorService.execute(() -> {
            try {
                URL url = new URL("https://gitee.com/cainiaohanhanyai/cnhhfile/raw/master/Documents/Notification.txt");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                try (InputStream inputStream = connection.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    StringBuilder messageBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        messageBuilder.append(line).append("\n");
                    }
                    final String message = messageBuilder.toString();
                    uiHandler.post(() -> {
                        noticeTextView.setVisibility(View.VISIBLE);
                        loadingIndicator.hide();
                        noticeTextView.setText(message);
                    });
                }
            } catch (IOException e) {
                final String errorMessage = e.getMessage();
                uiHandler.post(() -> {
                    noticeTextView.setVisibility(View.VISIBLE);
                    loadingIndicator.hide();
                    noticeTextView.setText(errorMessage);
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == playButton) {
            showGameLaunchDialog();
        } else if (v == userListButton) {
            toggleUserListVisibility();
        }
    }

    private void showGameLaunchDialog() {
        MaterialAlertDialogBuilder dialog = new H2CO3MessageDialog(requireContext())
                .setTitle("请选择启动游戏的方式")
                .setPositiveButton("POJAV", (dialogInterface, which) -> {
                    launchGame(false);
                })
                .setNegativeButton("H2CO3Launcher", (dialogInterface, which) -> {
                    launchGame(true);
                });
        dialog.show();
    }

    private void launchGame(boolean isH2CO3Launch) {
        h2co3Settings.setH2CO3Launch(isH2CO3Launch);
        startActivity(new Intent(requireActivity(), H2CO3LauncherClientActivity.class));
        attachControllerInterface();
    }

    private void toggleUserListVisibility() {
        userListLayout.setVisibility(userListLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    public void showLoginDialog() {
        if (isLoginDialogVisible) return;

        isLoginDialogVisible = true;
        loginDialog = new H2CO3CustomViewDialog(requireActivity());
        loginDialog.setCustomView(R.layout.dialog_home_login);
        loginDialog.setTitle(getString(org.koishi.launcher.h2co3.library.R.string.title_activity_login));
        initializeLoginDialogViews(loginDialog);
        loginDialogAlert = loginDialog.create();
        loginDialogAlert.show();
        loginDialog.setOnDismissListener(dialog -> isLoginDialogVisible = false);
        loginDialogAlert.setOnDismissListener(dialog -> isLoginDialogVisible = false);
    }

    private void initializeLoginDialogViews(H2CO3CustomViewDialog loginDialog) {
        TabLayout tabLayout = loginDialog.findViewById(R.id.login_tab);
        loginDialog.setNegativeButton(org.koishi.launcher.h2co3.library.R.string.title_activity_login, (dialog, which) -> {
            try {
                handleLogin(tabLayout);
            } catch (JSONException | IOException e) {
                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
            }
        });
        loginNameInput = loginDialog.findViewById(R.id.login_name);
        loginPasswordInput = loginDialog.findViewById(R.id.login_password);
        serverSelectorLayout = loginDialog.findViewById(R.id.server_selector);
        loginLayout = loginDialog.findViewById(R.id.login_name_layout);
        loginPasswordLayout = loginDialog.findViewById(R.id.login_password_layout);
        progressDialog = new H2CO3ProgressDialog(requireActivity());
        progressDialog.setCancelable(false);
        serverSpinner = loginDialog.findViewById(R.id.server_spinner);
        registerButton = loginDialog.findViewById(R.id.register);


        initializeTabLayout(tabLayout);
        setLoginListeners();
    }

    private void initializeTabLayout(TabLayout tabLayout) {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                handleTabSelection(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        refreshServerList();
        serverSpinner.setAdapter(serverAdapter);
        serverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                updateCurrentBaseUrl(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void handleTabSelection(int position) {
        switch (position) {
            case 1:
                loginLayout.setVisibility(View.GONE);
                loginPasswordLayout.setVisibility(View.GONE);
                serverSelectorLayout.setVisibility(View.GONE);
                break;
            case 2:
                loginLayout.setVisibility(View.VISIBLE);
                loginPasswordLayout.setVisibility(View.VISIBLE);
                serverSelectorLayout.setVisibility(View.VISIBLE);
                break;
            case 0:
            default:
                loginLayout.setVisibility(View.VISIBLE);
                loginPasswordLayout.setVisibility(View.GONE);
                serverSelectorLayout.setVisibility(View.GONE);
        }
    }

    private void updateCurrentBaseUrl(int selectedIndex) {
        if (serverData != null) {
            for (Servers.Server server : serverData.getServer()) {
                if (server.getServerName().equals(serverNames.get(selectedIndex))) {
                    currentBaseUrl = server.getBaseUrl();
                    currentRegisterUrl = server.getRegister();
                }
            }
        }
    }

    private void setLoginListeners() {
        registerButton.setOnClickListener(v -> showServerTypeDialog());
    }

    private void handleLogin(TabLayout tabLayout) throws JSONException, IOException {
        int selectedTabPosition = tabLayout.getSelectedTabPosition();

        switch (selectedTabPosition) {
            case 1:
                startActivityForResult(new Intent(requireActivity(), MicrosoftLoginActivity.class), MICROSOFT_LOGIN_REQUEST_CODE);
                break;
            case 2:
                performLogin();
                break;
            case 0:
            default:
                username = Objects.requireNonNull(loginNameInput.getText()).toString();
                if (isValidUsername(username)) {
                    addUserAndReload(username);
                } else {
                    H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, "Invalid username");
                }
        }
    }

    private void performLogin() {
        progressDialog.showWithProgress();
        H2CO3Application.sExecutorService.execute(() -> {
            username = Objects.requireNonNull(loginNameInput.getText()).toString();
            password = Objects.requireNonNull(loginPasswordInput.getText()).toString();
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                try {
                    LoginUtils.getINSTANCE().setBaseUrl(currentBaseUrl);
                    LoginUtils.getINSTANCE().login(username, password, loginListener);
                } catch (IOException e) {
                    requireActivity().runOnUiThread(progressDialog::dismiss);
                }
            } else {
                requireActivity().runOnUiThread(progressDialog::dismiss);
            }
        });
    }

    private void addUserAndReload(String username) {
        h2co3Auth.addUserToJson(
                username,
                "0",
                "0",
                "0",
                "0",
                "0",
                UUID.randomUUID().toString(),
                "0",
                "0",
                "0",
                "0",
                true,
                false);
        reLoadUsers();
        loginDialogAlert.dismiss();
    }

    private void showServerTypeDialog() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
        alertDialogBuilder.setTitle("请选择认证服务器类型");
        alertDialogBuilder.setItems(new String[]{"外置登录", "统一通行证"}, (dialog, which) -> showInputDialog(which));
        alertDialogBuilder.setNegativeButton(getString(org.koishi.launcher.h2co3.library.R.string.button_cancel), null);
        alertDialogBuilder.show();
    }

    private void showInputDialog(int selection) {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("提示")
                .setView(R.layout.edit_text)
                .setPositiveButton(
                        getString(org.koishi.launcher.h2co3.library.R.string.button_ok),
                        (dialog, which) -> {
                            TextInputEditText input = ((AlertDialog) dialog).findViewById(R.id.input);
                            if (input != null) {
                                input.setMaxLines(1);
                                input.setInputType(InputType.TYPE_CLASS_TEXT);
                                handleServerSelection(selection, String.valueOf(input.getText()));
                            } else {
                                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.WARNING, "输入内容不能为空");
                            }

                        })
                .setNegativeButton(getString(org.koishi.launcher.h2co3.library.R.string.button_cancel), null)
                .show();
    }

    private void handleServerSelection(int selection, String inputText) {
        if (inputText == null) return;

        progressDialog.showWithProgress();
        H2CO3Application.sExecutorService.execute(() -> {
            String baseUrl = selection == 0 ? inputText : "https://auth.mc-user.com:233/" + inputText;
            String data = LoginUtils.getINSTANCE().getServeInfo(baseUrl);
            requireActivity().runOnUiThread(() -> handleServerResponse(selection, data, inputText));
        });
    }

    private void handleServerResponse(int selection, String data, String inputText) {
        progressDialog.dismiss();
        if (data != null) {
            try {
                Servers.Server server = createServer(selection, data, inputText);
                if (serverData == null) {
                    serverData = new Servers();
                    serverData.setInfo("Made by CaiNull");
                    serverData.setServer(new ArrayList<>());
                }
                serverData.getServer().add(server);
                H2CO3Tools.write(h2co3Settings.serversFile.getAbsolutePath(), gson.toJson(serverData, Servers.class));
                refreshServerList();
                currentBaseUrl = server.getBaseUrl();
                currentRegisterUrl = server.getRegister();
            } catch (Exception e) {
                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
            }
        }
    }

    @NonNull
    private static Servers.Server createServer(int selection, String data, String inputText) throws JSONException {
        Servers.Server server = new Servers.Server();
        JSONObject jsonObject = new JSONObject(data);
        JSONObject meta = jsonObject.optJSONObject("meta");
        server.setServerName(meta.optString("serverName"));
        server.setBaseUrl(inputText);
        if (selection == 0) {
            JSONObject links = meta.optJSONObject("links");
            server.setRegister(links.optString("register"));
        } else {
            server.setBaseUrl("https://auth.mc-user.com:233/" + inputText);
            server.setRegister("https://login.mc-user.com:233/" + inputText + "/loginreg");
        }
        return server;
    }

    private boolean isValidUsername(String username) {
        return !TextUtils.isEmpty(username) && username.length() >= 3 && username.length() <= 16 && username.matches("\\w+");
    }

    public void refreshServerList() {
        serverNames = new ArrayList<>();
        if (h2co3Settings.serversFile.exists() && h2co3Settings.serversFile.canRead()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(h2co3Settings.serversFile))) {
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                serverData = new Gson().fromJson(json.toString(), Servers.class);
                if (serverData != null && !serverData.getServer().isEmpty()) {
                    currentBaseUrl = serverData.getServer().get(0).getBaseUrl();
                    for (Servers.Server server : serverData.getServer()) {
                        serverNames.add(server.getServerName());
                    }
                } else {
                    serverNames.add("无认证服务器");
                }
            } catch (IOException e) {
                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
            }
        } else {
            serverNames.add("无法读取服务器列表");
        }

        if (serverAdapter == null) {
            serverAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, serverNames);
            serverSpinner.setAdapter(serverAdapter);
        } else {
            serverAdapter.clear();
            serverAdapter.addAll(serverNames);
            serverAdapter.notifyDataSetChanged();
        }
    }

    public void loadUsers() {
        try {
            initializeUserList();
            updateUserAdapter();
        } catch (JSONException | IOException e) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
        }
    }

    public void reLoadUsers() {
        try {
            updateUserList();
            updateUserAdapter();
        } catch (JSONException | IOException e) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
        }
    }

    private void initializeUserList() throws JSONException, IOException {
        users = new ArrayList<>();
        updateUserList();
    }

    private void updateUserAdapter() {
        requireActivity().runOnUiThread(() -> {
            if (userAdapter == null || users.isEmpty()) {
                userAdapter = new HomeListUserAdapter(this, h2co3Auth, users);
                userRecyclerView.setAdapter(userAdapter);
            } else {
                for (int i = 0; i < userAdapter.getItemCount(); i++) {
                    userAdapter.notifyItemChanged(i);
                }
            }
        });
    }

    private void updateUserList() throws JSONException, IOException {
        users.clear();
        users.addAll(h2co3Auth.getUserList(new JSONObject(h2co3Auth.getUserJson())));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MICROSOFT_LOGIN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            progressDialog.showWithProgress();
            progressDialog.setCancelable(false);
            loginHandler.login(data);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setUserStateFromJson() {
        String apiUrl = H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_API_URL, H2CO3Tools.LOGIN_ERROR, String.class);
        userNameTextView.setText(H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, "", String.class));
        String userType = H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_USER_TYPE, "0", String.class);
        String userSkinTexture = H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_USER_SKINTEXTURE, "", String.class);

        final String MICROSOFT_USER_STATE = getString(org.koishi.launcher.h2co3.library.R.string.user_state_microsoft);
        final String OTHER_USER_STATE = getString(org.koishi.launcher.h2co3.library.R.string.user_state_other);
        final String OFFLINE_USER_STATE = getString(org.koishi.launcher.h2co3.library.R.string.user_state_offline);

        switch (userType) {
            case "1":
                userStateTextView.setText(MICROSOFT_USER_STATE);
                h2co3Auth.getHead(requireActivity(), userSkinTexture, userIconImageView);
                break;
            case "2":
                userStateTextView.setText(OTHER_USER_STATE + apiUrl);
                userIconImageView.setImageDrawable(ContextCompat.getDrawable(requireActivity(), org.koishi.launcher.h2co3.library.R.drawable.ic_home_user));
                break;
            default:
                userStateTextView.setText(OFFLINE_USER_STATE);
                userIconImageView.setImageDrawable(ContextCompat.getDrawable(requireActivity(), org.koishi.launcher.h2co3.library.R.drawable.ic_home_user));
                break;
        }
        if (TextUtils.isEmpty(userNameTextView.getText())) {
            setDefaultUserState();
        }
    }

    private void setDefaultUserState() {
        userNameTextView.setText(getString(org.koishi.launcher.h2co3.library.R.string.user_add));
        userStateTextView.setText(getString(org.koishi.launcher.h2co3.library.R.string.user_add));
        userIconImageView.setImageDrawable(ContextCompat.getDrawable(requireActivity(), org.koishi.launcher.h2co3.library.R.drawable.xicon));
    }

    private void handleAuthResult(AuthResult authResult) {
        if (authResult.getSelectedProfile() != null) {
            h2co3Auth
                    .addUserToJson(
                            authResult.getSelectedProfile().getName(),
                            username,
                            password,
                            "2",
                            currentBaseUrl,
                            authResult.getSelectedProfile().getId(),
                            UUID.randomUUID().toString(),
                            "0",
                            authResult.getAccessToken(),
                            "0",
                            "0",
                            true,
                            false
                    );
            reLoadUsers();
            loginDialogAlert.dismiss();
        } else {
            String[] profileNames = authResult.getAvailableProfiles().stream()
                    .map(AuthResult.AvailableProfiles::getName)
                    .toArray(String[]::new);

            MaterialAlertDialogBuilder profileSelectionDialog = new MaterialAlertDialogBuilder(requireActivity());
            profileSelectionDialog.setTitle("请选择角色");
            profileSelectionDialog.setItems(profileNames, (dialog, which) -> {
                AuthResult.AvailableProfiles selectedProfile = authResult.getAvailableProfiles().get(which);
                h2co3Auth.addUserToJson(
                        selectedProfile.getName(),
                        username,
                        password,
                        "2",
                        currentBaseUrl,
                        selectedProfile.getId(),
                        UUID.randomUUID().toString(),
                        "0",
                        authResult.getAccessToken(),
                        "0",
                        "0",
                        true,
                        false
                );
                reLoadUsers();
                loginDialogAlert.dismiss();
            });
            profileSelectionDialog.setNegativeButton(requireActivity().getString(org.koishi.launcher.h2co3.library.R.string.button_cancel), null);
            profileSelectionDialog.show();
        }
    }
}
