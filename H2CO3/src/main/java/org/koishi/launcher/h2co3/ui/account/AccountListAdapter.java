package org.koishi.launcher.h2co3.ui.account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Accounts;
import org.koishi.launcher.h2co3.ui.UIManager;
import org.koishi.launcher.h2co3core.auth.Account;
import org.koishi.launcher.h2co3core.auth.authlibinjector.AuthlibInjectorAccount;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherConstraintLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherRadioButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class AccountListAdapter extends H2CO3LauncherAdapter {

    private final ObservableList<AccountListItem> list;

    public AccountListAdapter(Context context, ObservableList<AccountListItem> list) {
        super(context);
        this.list = list;
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
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_account, null);
            viewHolder.parent = view.findViewById(R.id.parent);
            viewHolder.avatar = view.findViewById(R.id.avatar);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.type = view.findViewById(R.id.type);
            viewHolder.refreshProgress = view.findViewById(R.id.refresh_progress);
            viewHolder.skinProgress = view.findViewById(R.id.skin_progress);
            viewHolder.move = view.findViewById(R.id.move);
            viewHolder.refresh = view.findViewById(R.id.refresh);
            viewHolder.skin = view.findViewById(R.id.skin);
            viewHolder.delete = view.findViewById(R.id.delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        AccountListItem account = list.get(i);
        viewHolder.parent.setStrokeWidth(account.getAccount() == Accounts.getSelectedAccount() ? 10 : 0);
        viewHolder.avatar.imageProperty().unbind();
        viewHolder.avatar.imageProperty().bind(account.imageProperty());
        viewHolder.name.stringProperty().unbind();
        viewHolder.name.stringProperty().bind(account.titleProperty());
        viewHolder.type.stringProperty().unbind();
        viewHolder.type.stringProperty().bind(account.subtitleProperty());
        viewHolder.skin.setVisibility(account.canUploadSkin().get() ? View.VISIBLE : View.GONE);
        viewHolder.parent.setOnClickListener(v -> {
            Accounts.setSelectedAccount(account.getAccount());
            UIManager.getInstance().getMainUI().refresh().start();
        });
        viewHolder.move.imageProperty().bind(Bindings.createObjectBinding(() -> account.getAccount().isPortable() ? getContext().getDrawable(R.drawable.ic_baseline_earth_24) : getContext().getDrawable(R.drawable.ic_baseline_output_24), account.getAccount().portableProperty()));
        viewHolder.move.setOnClickListener(v -> {
            Account acc = account.getAccount();
            Accounts.getAccounts().remove(acc);
            if (acc.isPortable()) {
                acc.setPortable(false);
                if (!Accounts.getAccounts().contains(acc))
                    Accounts.getAccounts().add(acc);
            } else {
                acc.setPortable(true);
                if (!Accounts.getAccounts().contains(acc)) {
                    int idx = 0;
                    for (int j = Accounts.getAccounts().size() - 1; j >= 0; j--) {
                        if (Accounts.getAccounts().get(j).isPortable()) {
                            idx = j + 1;
                            break;
                        }
                    }
                    Accounts.getAccounts().add(idx, acc);
                }
            }
        });
        viewHolder.refresh.setOnClickListener(v -> {
            viewHolder.refresh.setVisibility(View.GONE);
            viewHolder.refreshProgress.setVisibility(View.VISIBLE);
            account.refreshAsync()
                    .whenComplete(Schedulers.androidUIThread(), ex -> {
                        viewHolder.refresh.setVisibility(View.VISIBLE);
                        viewHolder.refreshProgress.setVisibility(View.GONE);
                        if (ex != null) {
                            H2CO3LauncherAlertDialog.Builder builder1 = new H2CO3LauncherAlertDialog.Builder(getContext());
                            builder1.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                            builder1.setMessage(Accounts.localizeErrorMessage(getContext(), ex));
                            builder1.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                            builder1.create().show();
                        }
                        account.refreshSkinBinding();
                        UIManager.getInstance().getMainUI().refresh().start();
                    })
                    .start();
        });
        viewHolder.skin.setOnClickListener(v -> {
            try {
                if (account.getAccount() instanceof AuthlibInjectorAccount) {
                    new Thread(() -> {
                        try {
                            Task<?> uploadTask = Objects.requireNonNull(account.uploadSkin()).get();
                            Schedulers.androidUIThread().execute(() -> {
                                if (uploadTask != null) {
                                    viewHolder.skin.setVisibility(View.GONE);
                                    viewHolder.skinProgress.setVisibility(View.VISIBLE);
                                    uploadTask
                                            .whenComplete(Schedulers.androidUIThread(), ex -> {
                                                viewHolder.skin.setVisibility(View.VISIBLE);
                                                viewHolder.skinProgress.setVisibility(View.GONE);
                                                account.refreshSkinBinding();
                                            })
                                            .start();
                                }
                            });
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } else {
                    Task<?> uploadTask = Objects.requireNonNull(account.uploadSkin()).get();
                    if (uploadTask != null) {
                        viewHolder.skin.setVisibility(View.GONE);
                        viewHolder.skinProgress.setVisibility(View.VISIBLE);
                        uploadTask
                                .whenComplete(Schedulers.androidUIThread(), ex -> {
                                    viewHolder.skin.setVisibility(View.VISIBLE);
                                    viewHolder.skinProgress.setVisibility(View.GONE);
                                    account.refreshSkinBinding();
                                })
                                .start();
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        viewHolder.delete.setOnClickListener(v -> {
            account.remove();
            UIManager.getInstance().getMainUI().refresh().start();
        });
        return view;
    }

    static class ViewHolder {
        MaterialCardView parent;
        H2CO3LauncherImageView avatar;
        H2CO3LauncherTextView name;
        H2CO3LauncherTextView type;
        H2CO3LauncherProgressBar refreshProgress;
        H2CO3LauncherProgressBar skinProgress;
        H2CO3LauncherButton move;
        H2CO3LauncherButton refresh;
        H2CO3LauncherButton skin;
        H2CO3LauncherButton delete;
    }
}
