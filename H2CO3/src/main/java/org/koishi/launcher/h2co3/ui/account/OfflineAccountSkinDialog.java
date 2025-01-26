package org.koishi.launcher.h2co3.ui.account;

import static org.koishi.launcher.h2co3.game.TexturesLoader.getDefaultSkin;
import static org.koishi.launcher.h2co3core.util.Logging.LOG;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.H2CO3MainActivity;
import org.koishi.launcher.h2co3.util.FXUtils;
import org.koishi.launcher.h2co3.util.RequestCodes;
import org.koishi.launcher.h2co3core.auth.offline.OfflineAccount;
import org.koishi.launcher.h2co3core.auth.offline.Skin;
import org.koishi.launcher.h2co3core.auth.yggdrasil.TextureModel;
import org.koishi.launcher.h2co3core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3library.browser.FileBrowser;
import org.koishi.launcher.h2co3library.browser.options.LibMode;
import org.koishi.launcher.h2co3library.browser.options.SelectionMode;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherRadioButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.skin.SkinCanvas;
import org.koishi.launcher.h2co3library.skin.SkinRenderer;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

import java.util.ArrayList;
import java.util.logging.Level;

public class OfflineAccountSkinDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final AccountListItem accountListItem;
    private final OfflineAccount account;
    private final InvalidationListener skinBinding;
    private final ObjectProperty<Skin.Type> typeProperty = new SimpleObjectProperty<>(this, "type", Skin.Type.DEFAULT);
    private ConstraintLayout root;
    private SkinCanvas skinCanvas;
    private SkinRenderer renderer;
    private H2CO3LauncherTextView title;
    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negative;
    private H2CO3LauncherRadioButton defaultSkin;
    private H2CO3LauncherRadioButton steve;
    private H2CO3LauncherRadioButton alex;
    private H2CO3LauncherRadioButton local;
    private H2CO3LauncherRadioButton csl;
    private H2CO3LauncherLinearLayout localLayout;
    private H2CO3LauncherLinearLayout cslLayout;
    private H2CO3LauncherImageButton skinPath;
    private H2CO3LauncherImageButton capePath;
    private H2CO3LauncherTextView skinPathText;
    private H2CO3LauncherTextView capePathText;
    private H2CO3LauncherEditText cslUrl;

    public OfflineAccountSkinDialog(@NonNull Context context, AccountListItem accountListItem) {
        super(context);
        this.accountListItem = accountListItem;
        this.account = (OfflineAccount) accountListItem.getAccount();

        getWindow().setBackgroundDrawable(null);
        WindowManager wm = getWindow().getWindowManager();
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        int maxHeight = point.y - ConvertUtils.dip2px(getContext(), 30);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, maxHeight);

        setContentView(R.layout.dialog_offline_account_skin);
        setCancelable(false);

        renderer = new SkinRenderer(getContext());
        skinCanvas = findViewById(R.id.skin_view);
        skinCanvas.setRenderer(renderer, 5f);

        root = findViewById(R.id.root);
        title = findViewById(R.id.title);

        defaultSkin = findViewById(R.id.default_skin);
        steve = findViewById(R.id.steve);
        alex = findViewById(R.id.alex);
        local = findViewById(R.id.local);
        csl = findViewById(R.id.csl);

        defaultSkin.setOnClickListener(this);
        steve.setOnClickListener(this);
        alex.setOnClickListener(this);
        local.setOnClickListener(this);
        csl.setOnClickListener(this);

        localLayout = findViewById(R.id.local_layout);
        cslLayout = findViewById(R.id.csl_layout);

        skinPath = findViewById(R.id.skin_path);
        capePath = findViewById(R.id.cape_path);
        skinPath.setOnClickListener(this);
        capePath.setOnClickListener(this);
        skinPathText = findViewById(R.id.skin_path_text);
        capePathText = findViewById(R.id.cape_path_text);

        cslUrl = findViewById(R.id.csl_url);

        positive = findViewById(R.id.positive);
        negative = findViewById(R.id.negative);
        negative.post(() -> {
            int size = root.getHeight() - title.getHeight() - positive.getHeight() - ConvertUtils.dip2px(getContext(), 40);
            ViewGroup.LayoutParams layoutParams = skinCanvas.getLayoutParams();
            layoutParams.width = size;
            layoutParams.height = size;
            skinCanvas.setLayoutParams(layoutParams);

            positive.setOnClickListener(this);
            negative.setOnClickListener(this);
        });

        if (account.getSkin() == null) {
            refreshRadio(0);
            typeProperty.set(Skin.Type.DEFAULT);
        } else {
            if (account.getSkin().getType() == Skin.Type.STEVE) {
                refreshRadio(1);
                typeProperty.set(Skin.Type.STEVE);
            } else if (account.getSkin().getType() == Skin.Type.ALEX) {
                refreshRadio(2);
                typeProperty.set(Skin.Type.ALEX);
            } else if (account.getSkin().getType() == Skin.Type.LOCAL_FILE) {
                refreshRadio(3);
                typeProperty.set(Skin.Type.LOCAL_FILE);
            } else if (account.getSkin().getType() == Skin.Type.CUSTOM_SKIN_LOADER_API) {
                refreshRadio(4);
                typeProperty.set(Skin.Type.CUSTOM_SKIN_LOADER_API);
            } else {
                refreshRadio(0);
                typeProperty.set(Skin.Type.DEFAULT);
            }
            skinPathText.setString(account.getSkin().getLocalSkinPath());
            capePathText.setString(account.getSkin().getLocalCapePath());
            cslUrl.setText(account.getSkin().getCslApi());
        }

        refreshSkin();
        skinBinding = FXUtils.observeWeak(this::refreshSkin, typeProperty, cslUrl.stringProperty(), skinPathText.stringProperty(), capePathText.stringProperty());
    }

    private void refreshSkin() {
        getSkin().load(account.getUsername())
                .whenComplete(Schedulers.androidUIThread(), (result, exception) -> {
                    if (exception != null) {
                        LOG.log(Level.WARNING, "Failed to load skin", exception);
                        Toast.makeText(getContext(), getContext().getString(R.string.message_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        if (result == null || result.getSkin() == null && result.getCape() == null) {
                            renderer.setTexture(getDefaultSkin(TextureModel.detectUUID(account.getUUID())).image(), null);
                            return;
                        }
                        renderer.setTexture(result.getSkin() != null ? result.getSkin().getImage() : getDefaultSkin(TextureModel.detectUUID(account.getUUID())).image(),
                                result.getCape() != null ? result.getCape().getImage() : null);
                    }
                }).start();
    }

    private void refreshRadio(int position) {
        defaultSkin.setChecked(position == 0);
        steve.setChecked(position == 1);
        alex.setChecked(position == 2);
        local.setChecked(position == 3);
        csl.setChecked(position == 4);

        localLayout.setVisibility(position == 3 ? View.VISIBLE : View.GONE);
        cslLayout.setVisibility(position == 4 ? View.VISIBLE : View.GONE);
    }

    private Skin getSkin() {
        return new Skin(typeProperty.get(), cslUrl.getStringValue() == null ? "" : cslUrl.getStringValue(), null, StringUtils.isBlank(skinPathText.getString()) ? null : skinPathText.getString(), StringUtils.isBlank(capePathText.getString()) ? null : capePathText.getString());
    }

    @Override
    public void onClick(View view) {
        if (view == defaultSkin) {
            refreshRadio(0);
            typeProperty.set(Skin.Type.DEFAULT);
        }
        if (view == steve) {
            refreshRadio(1);
            typeProperty.set(Skin.Type.STEVE);
        }
        if (view == alex) {
            refreshRadio(2);
            typeProperty.set(Skin.Type.ALEX);
        }
        if (view == local) {
            refreshRadio(3);
            typeProperty.set(Skin.Type.LOCAL_FILE);
        }
        if (view == csl) {
            refreshRadio(4);
            typeProperty.set(Skin.Type.CUSTOM_SKIN_LOADER_API);
        }

        if (view == skinPath) {
            FileBrowser.Builder builder = new FileBrowser.Builder(getContext());
            builder.setLibMode(LibMode.FILE_CHOOSER);
            builder.setSelectionMode(SelectionMode.SINGLE_SELECTION);
            ArrayList<String> suffix = new ArrayList<>();
            suffix.add(".png");
            builder.setSuffix(suffix);
            builder.create().browse(H2CO3MainActivity.getInstance(), RequestCodes.SELECT_SKIN_CODE, ((requestCode, resultCode, data) -> {
                if (requestCode == RequestCodes.SELECT_SKIN_CODE && resultCode == Activity.RESULT_OK && data != null) {
                    String path = FileBrowser.getSelectedFiles(data).get(0);
                    skinPathText.setString(path);
                }
            }));
        }
        if (view == capePath) {
            FileBrowser.Builder builder = new FileBrowser.Builder(getContext());
            builder.setLibMode(LibMode.FILE_CHOOSER);
            builder.setSelectionMode(SelectionMode.SINGLE_SELECTION);
            ArrayList<String> suffix = new ArrayList<>();
            suffix.add(".png");
            builder.setSuffix(suffix);
            builder.create().browse(H2CO3MainActivity.getInstance(), RequestCodes.SELECT_CAPE_CODE, ((requestCode, resultCode, data) -> {
                if (requestCode == RequestCodes.SELECT_CAPE_CODE && resultCode == Activity.RESULT_OK && data != null) {
                    String path = FileBrowser.getSelectedFiles(data).get(0);
                    capePathText.setString(path);
                }
            }));
        }

        if (view == positive) {
            account.setSkin(getSkin());
            accountListItem.refreshSkinBinding();
            dismiss();
        }
        if (view == negative) {
            dismiss();
        }
    }
}
