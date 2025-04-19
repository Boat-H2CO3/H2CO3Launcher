package org.koishi.launcher.h2co3.ui.account;

import android.content.Context;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3core.auth.AuthInfo;
import org.koishi.launcher.h2co3core.auth.ClassicAccount;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;

import java.util.function.Consumer;

public class ClassicAccountLoginDialog extends H2CO3LauncherDialog {
    public ClassicAccountLoginDialog(@NonNull Context context, ClassicAccount oldAccount, Consumer<AuthInfo> success, Runnable failed) {
        super(context);
    }
}
