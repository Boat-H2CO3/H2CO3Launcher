package org.koishi.launcher.h2co3.ui.account;

import static org.koishi.launcher.h2co3.setting.ConfigHolder.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Accounts;
import org.koishi.launcher.h2co3core.auth.authlibinjector.AuthlibInjectorServer;
import org.koishi.launcher.h2co3core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

public class ServerListAdapter extends H2CO3LauncherAdapter {

    private final ObservableList<AuthlibInjectorServer> list;

    public ServerListAdapter(Context context) {
        super(context);
        list = config().getAuthlibInjectorServers();

        list.addListener((InvalidationListener) i -> Schedulers.androidUIThread().execute(this::notifyDataSetChanged));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_authlib_injector_server, null);
            viewHolder.parent = view.findViewById(R.id.parent);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.url = view.findViewById(R.id.url);
            viewHolder.delete = view.findViewById(R.id.delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        AuthlibInjectorServer server = list.get(i);
        viewHolder.name.setText(server.getName());
        viewHolder.url.setText(server.getUrl());
        viewHolder.parent.setOnClickListener(v -> {
            CreateAccountDialog dialog = new CreateAccountDialog(getContext(), server);
            dialog.rootAlert.show();
        });
        viewHolder.delete.setOnClickListener(v -> config().getAuthlibInjectorServers().remove(server));
        return view;
    }

    static class ViewHolder {
        ConstraintLayout parent;
        H2CO3LauncherTextView name;
        H2CO3LauncherTextView url;
        H2CO3LauncherImageButton delete;
    }
}
