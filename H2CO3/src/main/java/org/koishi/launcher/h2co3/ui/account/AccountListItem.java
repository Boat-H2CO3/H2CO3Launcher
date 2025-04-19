package org.koishi.launcher.h2co3.ui.account;

import static org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings.createBooleanBinding;
import static org.koishi.launcher.h2co3core.util.Logging.LOG;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import static java.util.Collections.emptySet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.H2CO3MainActivity;
import org.koishi.launcher.h2co3.game.TexturesLoader;
import org.koishi.launcher.h2co3.setting.Accounts;
import org.koishi.launcher.h2co3.ui.UIManager;
import org.koishi.launcher.h2co3.ui.main.MainUI;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.RequestCodes;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.auth.Account;
import org.koishi.launcher.h2co3core.auth.AuthInfo;
import org.koishi.launcher.h2co3core.auth.AuthenticationException;
import org.koishi.launcher.h2co3core.auth.ClassicAccount;
import org.koishi.launcher.h2co3core.auth.CredentialExpiredException;
import org.koishi.launcher.h2co3core.auth.OAuthAccount;
import org.koishi.launcher.h2co3core.auth.authlibinjector.AuthlibInjectorAccount;
import org.koishi.launcher.h2co3core.auth.authlibinjector.AuthlibInjectorServer;
import org.koishi.launcher.h2co3core.auth.microsoft.MicrosoftAccount;
import org.koishi.launcher.h2co3core.auth.offline.OfflineAccount;
import org.koishi.launcher.h2co3core.auth.yggdrasil.CompleteGameProfile;
import org.koishi.launcher.h2co3core.auth.yggdrasil.TextureType;
import org.koishi.launcher.h2co3core.auth.yggdrasil.YggdrasilAccount;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.ObjectBinding;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.StringBinding;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleStringProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.StringProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.value.ObservableBooleanValue;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.util.skin.InvalidSkinException;
import org.koishi.launcher.h2co3core.util.skin.NormalizedSkin;
import org.koishi.launcher.h2co3library.browser.FileBrowser;
import org.koishi.launcher.h2co3library.browser.options.LibMode;
import org.koishi.launcher.h2co3library.browser.options.SelectionMode;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

public class AccountListItem {

    private final Context context;
    private final Account account;
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty subtitle = new SimpleStringProperty();
    private final ObjectProperty<Drawable> image = new SimpleObjectProperty<>();
    private final ObjectProperty<Bitmap[]> texture = new SimpleObjectProperty<>();

    public AccountListItem(Context context, Account account) {
        this.context = context;
        this.account = account;

        String loginTypeName = Accounts.getLocalizedLoginTypeName(context, Accounts.getAccountFactory(account));
        if (account instanceof AuthlibInjectorAccount) {
            AuthlibInjectorServer server = ((AuthlibInjectorAccount) account).getServer();
            subtitle.bind(Bindings.concat(
                    loginTypeName, ", ", context.getString(R.string.account_injector_server), ": ",
                    Bindings.createStringBinding(server::getName, server)));
        } else {
            subtitle.set(loginTypeName);
        }

        StringBinding characterName = Bindings.createStringBinding(account::getCharacter, account);
        if (account instanceof OfflineAccount) {
            title.bind(characterName);
        } else {
            title.bind(
                    account.getUsername().isEmpty() ? characterName :
                            Bindings.concat(account.getUsername(), " - ", characterName));
        }

        image.bind(TexturesLoader.avatarBinding(account, ConvertUtils.dip2px(context, 30f)));
        texture.bind(TexturesLoader.textureBinding(account));
    }

    public static AuthInfo logIn(Account account) throws CancellationException, AuthenticationException, InterruptedException {
        if (account instanceof ClassicAccount) {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<AuthInfo> res = new AtomicReference<>(null);
            Schedulers.androidUIThread().execute(() -> {
                ClassicAccountLoginDialog dialog = new ClassicAccountLoginDialog(H2CO3LauncherTools.CONTEXT, (ClassicAccount) account, it -> {
                    res.set(it);
                    latch.countDown();
                }, latch::countDown);
                dialog.show();
            });
            latch.await();
            return Optional.ofNullable(res.get()).orElseThrow(CancellationException::new);
        } else if (account instanceof OAuthAccount) {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<AuthInfo> res = new AtomicReference<>(null);
            Schedulers.androidUIThread().execute(() -> {
                OAuthAccountLoginDialog dialog = new OAuthAccountLoginDialog(H2CO3LauncherTools.CONTEXT, (OAuthAccount) account, it -> {
                    res.set(it);
                    latch.countDown();
                }, latch::countDown);
                dialog.show();
            });
            latch.await();
            return Optional.ofNullable(res.get()).orElseThrow(CancellationException::new);
        }
        return account.logIn();
    }

    public Task<?> refreshAsync() {
        return Task.runAsync(() -> {
            account.clearCache();
            try {
                account.logIn();
            } catch (CredentialExpiredException e) {
                try {
                    logIn(account);
                } catch (CancellationException e1) {
                    // ignore cancellation
                } catch (Exception e1) {
                    LOG.log(Level.WARNING, "Failed to refresh " + account + " with password", e1);
                    throw e1;
                }
            } catch (AuthenticationException e) {
                LOG.log(Level.WARNING, "Failed to refresh " + account + " with token", e);
                throw e;
            }
        });
    }

    public ObservableBooleanValue canUploadSkin() {
        if (account instanceof YggdrasilAccount) {
            if (account instanceof AuthlibInjectorAccount) {
                AuthlibInjectorAccount aiAccount = (AuthlibInjectorAccount) account;
                ObjectBinding<Optional<CompleteGameProfile>> profile = aiAccount.getYggdrasilService().getProfileRepository().binding(aiAccount.getUUID());
                return createBooleanBinding(() -> {
                    Set<TextureType> uploadableTextures = profile.get()
                            .map(AuthlibInjectorAccount::getUploadableTextures)
                            .orElse(emptySet());
                    return uploadableTextures.contains(TextureType.SKIN);
                }, profile);
            } else {
                return createBooleanBinding(() -> true);
            }
        } else if (account instanceof OfflineAccount || account instanceof MicrosoftAccount) {
            return createBooleanBinding(() -> true);
        } else {
            return createBooleanBinding(() -> false);
        }
    }

    /**
     * @return the skin upload task, null if no file is selected
     */
    @Nullable
    public CompletableFuture<Task<?>> uploadSkin() {
        CompletableFuture<Task<?>> completableFuture = new CompletableFuture<>();
        if (account instanceof OfflineAccount) {
            OfflineAccountSkinDialog dialog = new OfflineAccountSkinDialog(context, this);
            dialog.show();
            completableFuture.complete(null);
            return completableFuture;
        }
        if (account instanceof MicrosoftAccount) {
            AndroidUtils.openLink(context, "https://www.minecraft.net/msaprofile/mygames/editskin");
            completableFuture.complete(null);
            return completableFuture;
        }
        if (!(account instanceof YggdrasilAccount)) {
            completableFuture.complete(null);
            return completableFuture;
        }

        FileBrowser.Builder builder = new FileBrowser.Builder(context);
        builder.setTitle(context.getString(R.string.account_skin_upload));
        builder.setLibMode(LibMode.FILE_CHOOSER);
        builder.setSelectionMode(SelectionMode.SINGLE_SELECTION);
        ArrayList<String> suffix = new ArrayList<>();
        suffix.add(".png");
        builder.setSuffix(suffix);

        CountDownLatch latch = new CountDownLatch(1);

        Schedulers.androidUIThread().execute(() -> builder.create().browse(H2CO3MainActivity.getInstance(), RequestCodes.SELECT_SKIN_CODE, (requestCode, resultCode, data) -> {
            if (requestCode == RequestCodes.SELECT_SKIN_CODE && resultCode == Activity.RESULT_OK && data != null) {
                String selectedFile = FileBrowser.getSelectedFiles(data).get(0);
                if (selectedFile == null) {
                    completableFuture.complete(null);
                }
                completableFuture.complete(
                        refreshAsync()
                                .thenRunAsync(() -> {
                                    Bitmap skinImg;
                                    try {
                                        skinImg = BitmapFactory.decodeFile(selectedFile);
                                    } catch (Exception e) {
                                        throw new InvalidSkinException("Failed to read skin image", e);
                                    }
                                    if (skinImg == null) {
                                        throw new InvalidSkinException("Failed to read skin image");
                                    }
                                    NormalizedSkin skin = new NormalizedSkin(skinImg);
                                    String model = skin.isSlim() ? "slim" : "";
                                    LOG.info("Uploading skin [" + selectedFile + "], model [" + model + "]");
                                    ((YggdrasilAccount) account).uploadSkin(model, new File(selectedFile).toPath());
                                })
                                .thenComposeAsync(refreshAsync())
                                .whenComplete(Schedulers.androidUIThread(), e -> {
                                    if (e != null) {
                                        H2CO3LauncherAlertDialog.Builder builder1 = new H2CO3LauncherAlertDialog.Builder(context);
                                        builder1.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                                        builder1.setMessage(Accounts.localizeErrorMessage(context, e));
                                        builder1.setNegativeButton(context.getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                                        builder1.create().show();
                                    }
                                })
                );
            } else {
                completableFuture.complete(null);
            }
            latch.countDown();
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return completableFuture;
    }

    public void refreshSkinBinding() {
        image.unbind();
        texture.unbind();
        image.bind(TexturesLoader.avatarBinding(account, ConvertUtils.dip2px(context, 30f)));
        texture.bind(TexturesLoader.textureBinding(account));
        MainUI.getInstance().refreshAvatar(account);
    }

    public void remove() {
        Accounts.getAccounts().remove(account);
    }

    public Account getAccount() {
        return account;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getSubtitle() {
        return subtitle.get();
    }

    public void setSubtitle(String subtitle) {
        this.subtitle.set(subtitle);
    }

    public StringProperty subtitleProperty() {
        return subtitle;
    }

    public Drawable getImage() {
        return image.get();
    }

    public void setImage(Drawable image) {
        this.image.set(image);
    }

    public ObjectProperty<Drawable> imageProperty() {
        return image;
    }

    public Bitmap[] getTexture() {
        return texture.get();
    }

    public void setTexture(Bitmap[] texture) {
        this.texture.set(texture);
    }

    public ObjectProperty<Bitmap[]> textureProperty() {
        return texture;
    }
}
