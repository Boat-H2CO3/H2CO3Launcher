package org.koishi.launcher.h2co3.ui.controller;

import static org.koishi.launcher.h2co3.util.FXUtils.onInvalidating;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.FileProvider;

import com.google.gson.GsonBuilder;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.ControllerActivity;
import org.koishi.launcher.h2co3.setting.Controller;
import org.koishi.launcher.h2co3.setting.Controllers;
import org.koishi.launcher.h2co3.ui.PageManager;
import org.koishi.launcher.h2co3.ui.UIManager;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.RequestCodes;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleBooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3core.util.io.FileUtils;
import org.koishi.launcher.h2co3library.browser.FileBrowser;
import org.koishi.launcher.h2co3library.browser.options.LibMode;
import org.koishi.launcher.h2co3library.browser.options.SelectionMode;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class ControllerManagePage extends H2CO3LauncherCommonPage implements View.OnClickListener {

    private final BooleanProperty refreshProperty;

    private ObjectProperty<Controller> selectedController;
    private ListView listView;
    private LinearLayoutCompat importController;
    private LinearLayoutCompat createController;
    private LinearLayoutCompat downloadController;
    private H2CO3LauncherButton upload;
    private H2CO3LauncherButton share;
    private H2CO3LauncherButton editInfo;
    private H2CO3LauncherButton editController;
    public ControllerManagePage(Context context, int id, H2CO3LauncherUILayout parent, int resId) {
        super(context, id, parent, resId);
        refreshProperty = new SimpleBooleanProperty(false);
        create();
    }

    public Controller getSelectedController() {
        return selectedController.get();
    }

    public void setSelectedController(Controller selectedController) {
        this.selectedController.set(selectedController);
    }

    public void create() {
        Controllers.addCallback(this::init);
    }

    private void init() {
        selectedController = new SimpleObjectProperty<Controller>() {
            {
                Controllers.getControllers().addListener(onInvalidating(this::invalidated));
            }

            @Override
            protected void invalidated() {
                if (!Controllers.isInitialized()) return;

                Controller controller = get();
                if (Controllers.getControllers().isEmpty()) {
                    if (controller != null) {
                        set(null);
                    }
                } else {
                    if (!Controllers.getControllers().contains(controller)) {
                        set(Controllers.getControllers().get(0));
                    }
                }
            }
        };
        if (!Controllers.controllersProperty().isEmpty()) {
            selectedController.set(Controllers.controllersProperty().get(0));
        } else {
            selectedController.set(Controllers.DEFAULT_CONTROLLER);
        }

        listView = findViewById(R.id.controller_list);
        importController = findViewById(R.id.import_controller);
        createController = findViewById(R.id.create_controller);
        downloadController = findViewById(R.id.download_controller);
        importController.setOnClickListener(this);
        createController.setOnClickListener(this);
        downloadController.setOnClickListener(this);

        H2CO3LauncherLinearLayout infoLayout = findViewById(R.id.info_layout);
        infoLayout.visibilityProperty().bind(Bindings.createBooleanBinding(() -> selectedController.get() != null, selectedController));

        H2CO3LauncherTextView nameText = findViewById(R.id.name);
        H2CO3LauncherTextView versionText = findViewById(R.id.version);
        H2CO3LauncherTextView authorText = findViewById(R.id.author);
        H2CO3LauncherTextView descriptionText = findViewById(R.id.description);
        nameText.stringProperty().bind(Bindings.createStringBinding(() -> selectedController.get() == null ? "" : selectedController.get().getName(), selectedController, refreshProperty));
        versionText.stringProperty().bind(Bindings.createStringBinding(() -> selectedController.get() == null ? "" : selectedController.get().getVersion(), selectedController, refreshProperty));
        authorText.stringProperty().bind(Bindings.createStringBinding(() -> selectedController.get() == null ? "" : selectedController.get().getAuthor(), selectedController, refreshProperty));
        descriptionText.stringProperty().bind(Bindings.createStringBinding(() -> selectedController.get() == null ? "" : selectedController.get().getDescription(), selectedController, refreshProperty));

        upload = findViewById(R.id.upload);
        share = findViewById(R.id.share);
        editInfo = findViewById(R.id.edit_info);
        editController = findViewById(R.id.edit_controller);
        upload.setOnClickListener(this);
        share.setOnClickListener(this);
        editInfo.setOnClickListener(this);
        editController.setOnClickListener(this);

        refreshList();

        H2CO3LauncherProgressBar progress = findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
    }

    private void refreshList() {
        EditableControllerListAdapter adapter = new EditableControllerListAdapter(getContext(), Controllers.controllersProperty());
        listView.setAdapter(adapter);
    }

    public void addController(Controller controller) {
        Schedulers.androidUIThread().execute(() -> {
            Controllers.addController(controller);
            refreshList();
            selectedController.set(controller);
        });
    }

    public void removeController(Controller controller) {
        Schedulers.androidUIThread().execute(() -> {
            Controllers.removeControllers(controller);
            refreshList();
            if (controller == selectedController.get()) {
                selectedController.set(null);
            }
        });
    }

    public void changeControllerInfo(Controller old, Controller newValue) {
        old.setName(newValue.getName());
        old.setVersion(newValue.getVersion());
        old.setVersionCode(newValue.getVersionCode());
        old.setAuthor(newValue.getAuthor());
        old.setDescription(newValue.getDescription());

        if (!old.getId().equals(newValue.getId())) {
            try {
                old.changeId(newValue.getId());
            } catch (IOException e) {
                Logging.LOG.log(Level.SEVERE, "Failed to change controller id!", e.getMessage());
            }
        }

        refreshProperty.set(!refreshProperty.get());
        old.saveToDisk();
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void onClick(View view) {
        if (view == importController) {
            FileBrowser.Builder builder = new FileBrowser.Builder(getContext());
            builder.setLibMode(LibMode.FILE_CHOOSER);
            builder.setSelectionMode(SelectionMode.SINGLE_SELECTION);
            ArrayList<String> suffix = new ArrayList<>();
            suffix.add(".json");
            builder.setSuffix(suffix);
            builder.setTitle(getContext().getString(R.string.control_import));
            builder.create().browse(getActivity(), RequestCodes.SELECT_CONTROLLER_CODE, ((requestCode, resultCode, data) -> {
                if (requestCode == RequestCodes.SELECT_CONTROLLER_CODE && resultCode == Activity.RESULT_OK && data != null) {
                    String path = FileBrowser.getSelectedFiles(data).get(0);
                    Uri uri = Uri.parse(path);
                    if (AndroidUtils.isDocUri(uri)) {
                        path = AndroidUtils.copyFileToDir(getActivity(), uri, new File(H2CO3LauncherTools.CACHE_DIR));
                    }
                    try {
                        String content = FileUtils.readText(new File(path));
                        Controller controller = new GsonBuilder().setPrettyPrinting().create().fromJson(content, Controller.class);
                        if (controller.getName().equals("Error")) {
                            Toast.makeText(getContext(), getContext().getString(R.string.control_import_failed), Toast.LENGTH_SHORT).show();
                        } else {
                            addController(controller);
                        }
                    } catch (IOException e) {
                        Logging.LOG.log(Level.SEVERE, "Failed to import controller", e);
                    }
                }
            }));
        }
        if (view == createController) {
            ControllerInfoDialog dialog = new ControllerInfoDialog(getContext(), true, new Controller(""), this::addController);
            dialog.show();
        }
        if (view == downloadController) {
            UIManager.getInstance().getControllerUI().getPageManager().switchPage(ControllerPageManager.PAGE_ID_CONTROLLER_REPO);
        }
        if (view == upload) {
            ControllerUploadPage page = new ControllerUploadPage(getContext(), PageManager.PAGE_ID_TEMP, getParent(), R.layout.page_controller_upload, selectedController.get());
            ControllerPageManager.getInstance().showTempPage(page);
        }
        if (view == share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getString(org.koishi.launcher.h2co3library.R.string.file_browser_provider), new File(H2CO3LauncherTools.CONTROLLER_DIR, getSelectedController().getFileName()));
            intent.setType(AndroidUtils.getMimeType(H2CO3LauncherTools.CONTROLLER_DIR + "/" + getSelectedController().getFileName()));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            getActivity().startActivity(Intent.createChooser(intent, getContext().getString(R.string.control_share)));
        }
        if (view == editInfo) {
            ControllerInfoDialog dialog = new ControllerInfoDialog(getContext(), false, selectedController.get(), (controller) -> changeControllerInfo(selectedController.get(), controller));
            dialog.show();
        }
        if (view == editController) {
            Intent intent = new Intent(getContext(), ControllerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("controller", getSelectedController().getId());
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        }
    }
}
