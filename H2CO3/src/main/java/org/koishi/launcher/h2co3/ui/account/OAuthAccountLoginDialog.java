package org.koishi.launcher.h2co3.ui.account;

import static org.koishi.launcher.h2co3core.util.Logging.LOG;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.game.OAuthServer;
import org.koishi.launcher.h2co3.setting.Accounts;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.FXUtils;
import org.koishi.launcher.h2co3.util.WeakListenerHolder;
import org.koishi.launcher.h2co3core.auth.AuthInfo;
import org.koishi.launcher.h2co3core.auth.OAuthAccount;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;

import java.util.function.Consumer;
import java.util.logging.Level;

public class OAuthAccountLoginDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final OAuthAccount account;
    private final Consumer<AuthInfo> success;
    private final Runnable failed;
    private final ObjectProperty<OAuthServer.GrantDeviceCodeEvent> deviceCode = new SimpleObjectProperty<>();
    private final WeakListenerHolder holder = new WeakListenerHolder();
    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negative;

    public OAuthAccountLoginDialog(@NonNull Context context, OAuthAccount account, Consumer<AuthInfo> success, Runnable failed) {
        super(context);
        this.account = account;
        this.success = success;
        this.failed = failed;

        setContentView(R.layout.dialog_relogin_oauth);
        setCancelable(false);

        FXUtils.onChangeAndOperate(deviceCode, deviceCode -> Schedulers.androidUIThread().execute(() -> {
            if (deviceCode != null) {
                AndroidUtils.copyText(getContext(), deviceCode.getUserCode());
            }
        }));
        holder.add(Accounts.OAUTH_CALLBACK.onGrantDeviceCode.registerWeak(deviceCode::set));

        positive = findViewById(R.id.login);
        negative = findViewById(R.id.cancel);

        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == positive) {
            positive.setEnabled(false);
            negative.setEnabled(false);
            Task.supplyAsync(account::logInWhenCredentialsExpired)
                    .whenComplete(Schedulers.androidUIThread(), (authInfo, exception) -> {
                        if (exception == null) {
                            success.accept(authInfo);
                            dismiss();
                        } else {
                            LOG.log(Level.INFO, "Failed to login when credentials expired: " + account, exception);
                            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                            builder.setMessage(Accounts.localizeErrorMessage(getContext(), exception));
                            builder.setCancelable(false);
                            builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                            builder.create().show();
                        }
                        positive.setEnabled(true);
                        negative.setEnabled(true);
                    }).start();
        }
        if (view == negative) {
            failed.run();
            dismiss();
        }
    }
}
