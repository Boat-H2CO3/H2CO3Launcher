package org.koishi.launcher.h2co3.ui.manage;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ListView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.util.RequestCodes;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ListProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleListProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.mod.Datapack;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3core.util.fakefx.MappedObservableList;
import org.koishi.launcher.h2co3library.browser.FileBrowser;
import org.koishi.launcher.h2co3library.browser.options.LibMode;
import org.koishi.launcher.h2co3library.browser.options.SelectionMode;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherTempPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DatapackListPage extends H2CO3LauncherTempPage implements View.OnClickListener {

    private final ListProperty<DatapackInfoObject> itemsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final Path worldDir;
    private final Datapack datapack;
    boolean first = true;
    private H2CO3LauncherButton deleteButton;
    private H2CO3LauncherButton enableButton;
    private H2CO3LauncherButton disableButton;
    private H2CO3LauncherButton addButton;
    private H2CO3LauncherButton refreshButton;
    private H2CO3LauncherProgressBar progressBar;
    private ListView listView;
    private DatapackListAdapter adapter;

    public DatapackListPage(Context context, int id, H2CO3LauncherUILayout parent, int resId, String worldName, Path worldDir) {
        super(context, id, parent, resId);
        this.worldDir = worldDir;

        datapack = new Datapack(worldDir.resolve("datapacks"));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        deleteButton = findViewById(R.id.delete);
        enableButton = findViewById(R.id.enable);
        disableButton = findViewById(R.id.disable);
        addButton = findViewById(R.id.add);
        refreshButton = findViewById(R.id.refresh);
        progressBar = findViewById(R.id.progress);
        listView = findViewById(R.id.list);

        deleteButton.setOnClickListener(this);
        enableButton.setOnClickListener(this);
        disableButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        refreshButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter = new DatapackListAdapter(getContext());
        listView.setAdapter(adapter);
        Bindings.bindContent(adapter.listProperty(), itemsProperty);

        refresh();
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onClick(View v) {
        if (v == deleteButton) {
            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
            builder.setMessage(getContext().getString(R.string.button_remove_confirm));
            builder.setPositiveButton(getContext().getString(R.string.button_remove), () -> removeSelected(adapter.selectedItemsProperty()));
            builder.setNegativeButton(null);
            builder.create().show();
        }
        if (v == enableButton) {
            enableSelected(adapter.selectedItemsProperty());
        }
        if (v == disableButton) {
            disableSelected(adapter.selectedItemsProperty());
        }
        if (v == addButton) {
            add();
        }
        if (v == refreshButton) {
            refresh();
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        listView.setVisibility(loading ? View.GONE : View.VISIBLE);
        deleteButton.setEnabled(!loading);
        enableButton.setEnabled(!loading);
        disableButton.setEnabled(!loading);
        addButton.setEnabled(!loading);
        refreshButton.setEnabled(!loading);
    }

    private void installSingleDatapack(File datapack) throws IOException {
        Datapack zip = new Datapack(datapack.toPath());
        zip.loadFromZip();
        zip.installTo(worldDir);
    }

    public void refresh() {
        setLoading(true);
        adapter.selectedItemsProperty().clear();
        Task.runAsync(datapack::loadFromDir)
                .withRunAsync(Schedulers.androidUIThread(), () -> {
                    if (first) {
                        itemsProperty.set(MappedObservableList.create(datapack.getInfo(), DatapackInfoObject::new));
                        first = false;
                    }
                    setLoading(false);

                    System.gc();
                })
                .start();
    }

    public void add() {
        FileBrowser.Builder builder = new FileBrowser.Builder(getContext());
        builder.setLibMode(LibMode.FILE_CHOOSER);
        builder.setSelectionMode(SelectionMode.MULTIPLE_SELECTION);
        ArrayList<String> suffix = new ArrayList<>();
        suffix.add(".zip");
        builder.setSuffix(suffix);
        builder.create().browse(getActivity(), RequestCodes.SELECT_DATAPACK_CODE, ((requestCode, resultCode, data) -> {
            if (requestCode == RequestCodes.SELECT_DATAPACK_CODE && resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<String> path = FileBrowser.getSelectedFiles(data);
                List<File> res = path.stream().map(File::new).collect(Collectors.toList());
                H2CO3LauncherAlertDialog.Builder builder1 = new H2CO3LauncherAlertDialog.Builder(getContext());
                builder1.setCancelable(false);
                builder1.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
                builder1.setMessage(getContext().getString(R.string.datapack_add));
                H2CO3LauncherAlertDialog installDialog = builder1.create();
                installDialog.show();
                new Thread(() -> {
                    res.forEach(it -> {
                        try {
                            installSingleDatapack(it);
                        } catch (IOException e) {
                            Logging.LOG.log(Level.WARNING, "Unable to parse datapack file " + datapack, e);
                        }
                    });
                    Schedulers.androidUIThread().execute(() -> {
                        installDialog.dismiss();
                        refresh();
                    });
                }).start();
            }
        }));
    }

    void removeSelected(ObservableList<DatapackInfoObject> selectedItems) {
        ObservableList<DatapackInfoObject> items = FXCollections.observableArrayList();
        items.setAll(selectedItems);
        items.stream()
                .map(DatapackInfoObject::getPackInfo)
                .forEach(pack -> {
                    try {
                        datapack.deletePack(pack);
                    } catch (IOException e) {
                        // Fail to remove mods if the game is running or the datapack is absent.
                        Logging.LOG.warning("Failed to delete datapack " + pack);
                    }
                });
    }

    void enableSelected(ObservableList<DatapackInfoObject> selectedItems) {
        selectedItems.stream()
                .map(DatapackInfoObject::getPackInfo)
                .forEach(info -> info.setActive(true));
    }

    void disableSelected(ObservableList<DatapackInfoObject> selectedItems) {
        selectedItems.stream()
                .map(DatapackInfoObject::getPackInfo)
                .forEach(info -> info.setActive(false));
    }

    public static class DatapackInfoObject {
        private final BooleanProperty active;
        private final Datapack.Pack packInfo;

        DatapackInfoObject(Datapack.Pack packInfo) {
            this.packInfo = packInfo;
            this.active = packInfo.activeProperty();
        }

        public BooleanProperty getActive() {
            return active;
        }

        String getTitle() {
            return packInfo.getId();
        }

        String getSubtitle() {
            return StringUtils.parseColorEscapes(packInfo.getDescription().toString());
        }

        Datapack.Pack getPackInfo() {
            return packInfo;
        }
    }
}
