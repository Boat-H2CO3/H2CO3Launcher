package org.koishi.launcher.h2co3.ui.account;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.widget.LinearLayoutCompat;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Accounts;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableListBase;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonUI;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

public class AccountUI extends H2CO3LauncherCommonUI implements View.OnClickListener {

    private H2CO3LauncherButton addOfflineAccount;
    private H2CO3LauncherButton addMicrosoftAccount;
    private H2CO3LauncherButton addLoginServer;

    private ListView listView;
    private AccountListAdapter accountListAdapter;

    public AccountUI(Context context, H2CO3LauncherUILayout parent, int id) {
        super(context, parent, id);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        addOfflineAccount = findViewById(R.id.offline);
        addMicrosoftAccount = findViewById(R.id.microsoft);
        addLoginServer = findViewById(R.id.add_login_server);
        addOfflineAccount.setOnClickListener(this);
        addMicrosoftAccount.setOnClickListener(this);
        addLoginServer.setOnClickListener(this);

        listView = findViewById(R.id.list);

        ListView serverListView = findViewById(R.id.server_list);
        serverListView.setAdapter(new ServerListAdapter(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        addLoadingCallback(() -> {
            refresh().start();
        });
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
                listView.setAdapter(accountListAdapter);
            } else {
                accountListAdapter.notifyDataSetChanged();
            }
        });
        return Task.runAsync(() -> {

        });
    }

    @Override
    public void onClick(View view) {
        if (view == addOfflineAccount) {
            CreateAccountDialog dialog = new CreateAccountDialog(getContext(), Accounts.FACTORY_OFFLINE);
            dialog.rootAlert.show();
        }
        if (view == addMicrosoftAccount) {
            CreateAccountDialog dialog = new CreateAccountDialog(getContext(), Accounts.FACTORY_MICROSOFT);
            dialog.rootAlert.show();
        }
        if (view == addLoginServer) {
            AddAuthlibInjectorServerDialog dialog = new AddAuthlibInjectorServerDialog(getContext());
            dialog.dialog.show();
        }
    }

}
