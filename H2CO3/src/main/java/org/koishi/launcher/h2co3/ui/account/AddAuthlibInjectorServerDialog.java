package org.koishi.launcher.h2co3.ui.account;

import static org.koishi.launcher.h2co3.setting.ConfigHolder.config;
import static org.koishi.launcher.h2co3core.util.Logging.LOG;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.auth.authlibinjector.AuthlibInjectorServer;
import org.koishi.launcher.h2co3core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherConstraintLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

import javax.net.ssl.SSLException;

public class AddAuthlibInjectorServerDialog extends MaterialAlertDialogBuilder implements View.OnClickListener {

    public AlertDialog dialog;
    private H2CO3LauncherConstraintLayout firstLayout;
    private H2CO3LauncherConstraintLayout secondLayout;
    private H2CO3LauncherEditText editText;
    private H2CO3LauncherTextView url;
    private H2CO3LauncherTextView name;
    private H2CO3LauncherButton next;
    private H2CO3LauncherButton back;
    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negativePri;
    private H2CO3LauncherButton negativeSec;
    private AuthlibInjectorServer serverBeingAdded;

    public AddAuthlibInjectorServerDialog(@NonNull Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_authlib_injector_server, null);
        setView(view);
        dialog = create();
        dialog.setCancelable(false);
        firstLayout = view.findViewById(R.id.first_layout);
        secondLayout = view.findViewById(R.id.second_layout);

        editText = view.findViewById(R.id.url);
        url = view.findViewById(R.id.address);
        name = view.findViewById(R.id.name);

        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.prev);
        positive = view.findViewById(R.id.positive);
        negativePri = view.findViewById(R.id.negative_pri);
        negativeSec = view.findViewById(R.id.negative_sec);

        next.setOnClickListener(this);
        back.setOnClickListener(this);
        positive.setOnClickListener(this);
        negativePri.setOnClickListener(this);
        negativeSec.setOnClickListener(this);

        firstLayout.setVisibility(View.VISIBLE);
        secondLayout.setVisibility(View.GONE);
    }

    private void next() {
        next.setEnabled(false);
        negativePri.setEnabled(false);
        String url = Objects.requireNonNull(editText.getText()).toString();
        Task.runAsync(() -> {
            serverBeingAdded = AuthlibInjectorServer.locateServer(url);
        }).whenComplete(Schedulers.androidUIThread(), exception -> {
            next.setEnabled(true);
            negativePri.setEnabled(true);

            if (exception == null) {
                this.name.setText(serverBeingAdded.getName());
                this.url.setText(serverBeingAdded.getUrl());

                firstLayout.setVisibility(View.GONE);
                secondLayout.setVisibility(View.VISIBLE);
            } else {
                LOG.log(Level.WARNING, "Failed to resolve auth server: " + url, exception);
                H2CO3LauncherTools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, resolveFetchExceptionMessage(exception));
            }
        }).start();
    }

    private String resolveFetchExceptionMessage(Throwable exception) {
        if (exception instanceof SSLException) {
            return getContext().getString(R.string.account_failed_ssl);
        } else if (exception instanceof IOException) {
            return getContext().getString(R.string.account_failed_connect_injector_server);
        } else {
            return exception.getClass().getName() + ": " + exception.getLocalizedMessage();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == next) {
            next();
        }
        if (v == back) {
            firstLayout.setVisibility(View.VISIBLE);
            secondLayout.setVisibility(View.GONE);
        }
        if (v == positive) {
            if (!config().getAuthlibInjectorServers().contains(serverBeingAdded)) {
                config().getAuthlibInjectorServers().add(serverBeingAdded);
            }
            dialog.dismiss();
        }
        if (v == negativePri || v == negativeSec) {
            dialog.dismiss();
        }
    }
}
