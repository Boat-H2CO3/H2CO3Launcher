package org.koishi.launcher.h2co3.ui.download;

import static org.koishi.launcher.h2co3.ui.download.DownloadPageManager.PAGE_ID_DOWNLOAD_MOD;
import static org.koishi.launcher.h2co3.ui.download.DownloadPageManager.PAGE_ID_DOWNLOAD_MODPACK;
import static org.koishi.launcher.h2co3.ui.download.DownloadPageManager.PAGE_ID_DOWNLOAD_RESOURCE_PACK;
import static org.koishi.launcher.h2co3.ui.download.DownloadPageManager.getInstance;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.LinearLayoutCompat;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.DownloadProviders;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.ui.PageManager;
import org.koishi.launcher.h2co3.ui.TaskDialog;
import org.koishi.launcher.h2co3.ui.manage.ManageUI;
import org.koishi.launcher.h2co3.ui.version.Versions;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.FXUtils;
import org.koishi.launcher.h2co3.util.TaskCancellationAction;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ListProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleBooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleIntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleListProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleStringProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.StringProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.mod.RemoteMod;
import org.koishi.launcher.h2co3core.mod.RemoteModRepository;
import org.koishi.launcher.h2co3core.task.FileDownloadTask;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.task.TaskExecutor;
import org.koishi.launcher.h2co3core.util.Lang;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3core.util.io.NetworkUtils;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;

import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSpinner;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

public class DownloadPage extends H2CO3LauncherCommonPage implements ManageUI.VersionLoadable, View.OnClickListener {

    protected final BooleanProperty supportChinese = new SimpleBooleanProperty();
    protected final ListProperty<String> downloadSources = new SimpleListProperty<>(this, "downloadSources", FXCollections.observableArrayList());
    protected final StringProperty downloadSource = new SimpleStringProperty();
    private final IntegerProperty pageOffset = new SimpleIntegerProperty(0);
    private final IntegerProperty pageCount = new SimpleIntegerProperty(-1);
    private final RemoteModVersionPage.DownloadCallback callback;
    private final ObjectProperty<Profile.ProfileVersion> version = new SimpleObjectProperty<>();
    private final StringProperty gameVersion = new SimpleStringProperty(this, "gameVersion", "");
    private final ObjectProperty<CategoryIndented> category = new SimpleObjectProperty<>(this, "category", new CategoryIndented(0, null));
    private final ObjectProperty<RemoteModRepository.SortType> sortType = new SimpleObjectProperty<>(this, "sortType", RemoteModRepository.SortType.POPULARITY);
    protected RemoteModRepository repository;
    private TaskExecutor executor;
    private Runnable retrySearch;
    private RemoteModListAdapter adapter;

    private ScrollView searchLayout;

    private H2CO3LauncherEditText nameEditText;
    private H2CO3LauncherTextView sourceText;
    private H2CO3LauncherSpinner<String> sourceSpinner;
    private H2CO3LauncherSpinner<String> gameVersionSpinner;
    private H2CO3LauncherSpinner<CategoryIndented> categorySpinner;
    private H2CO3LauncherSpinner<RemoteModRepository.SortType> sortSpinner;

    private H2CO3LauncherButton search;

    private LinearLayoutCompat listLayout;
    private H2CO3LauncherTextView page;
    private H2CO3LauncherButton next;
    private H2CO3LauncherButton previous;
    private H2CO3LauncherButton first;
    private H2CO3LauncherButton last;
    private ListView listView;
    private H2CO3LauncherProgressBar progressBar;
    private H2CO3LauncherImageButton retry;
    private AlertDialog taskListPaneAlert;
    private TaskDialog taskListPane;

    public DownloadPage(Context context, int id, H2CO3LauncherUILayout parent, int resId, RemoteModRepository repository) {
        super(context, id, parent, resId);
        this.repository = repository;

        switch (id) {
            case PAGE_ID_DOWNLOAD_MODPACK:
                this.callback = ((profile, version, file) -> Versions.downloadModpackImpl(context, parent, profile, file));
                break;
            case PAGE_ID_DOWNLOAD_MOD:
                this.callback = (profile, version, file) -> download(context, profile, version, file, "mods");
                break;
            case PAGE_ID_DOWNLOAD_RESOURCE_PACK:
                this.callback = (profile, version, file) -> download(context, profile, version, file, "resourcepacks");
                break;
            default:
                this.callback = null;
                break;
        }

        if (repository != null) {
            create();
        }
    }

    private static void download(Context context, Profile profile, @Nullable String version, RemoteMod.Version file, String subdirectoryName) {
        if (version == null) version = profile.getSelectedVersion();

        Path runDirectory = profile.getRepository().hasVersion(version) ? profile.getRepository().getRunDirectory(version).toPath() : profile.getRepository().getBaseDirectory().toPath();

        DownloadAddonDialog dialog = new DownloadAddonDialog(context, file.getFile().getFilename(), name -> {
            Path dest = runDirectory.resolve(subdirectoryName).resolve(name);

            TaskDialog taskListPane = new TaskDialog(getInstance().getContext(), new TaskCancellationAction(TaskDialog::dismissDialog));
            Schedulers.androidUIThread().execute(() -> {
                TaskExecutor executor = Task.composeAsync(() -> {
                    FileDownloadTask task = new FileDownloadTask(NetworkUtils.toURL(file.getFile().getUrl()), dest.toFile());
                    task.setName(file.getName());
                    return task;
                }).whenComplete(Schedulers.androidUIThread(), exception -> {
                    if (exception != null) {
                        if (exception instanceof CancellationException) {
                            Toast.makeText(context, context.getString(R.string.message_cancelled), Toast.LENGTH_SHORT).show();
                        } else {
                            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(context);
                            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                            builder.setCancelable(false);
                            builder.setTitle(context.getString(R.string.install_failed_downloading));
                            builder.setMessage(DownloadProviders.localizeErrorMessage(context, exception));
                            builder.setNegativeButton(context.getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                            builder.create().show();
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.install_success), Toast.LENGTH_SHORT).show();
                    }
                }).executor();
                taskListPane.setExecutor(executor);
                taskListPane.createDialog();
                executor.start();
            });
        });
        dialog.alertDialog.show();
    }

    private static void resolveCategory(RemoteModRepository.Category category, int indent, List<CategoryIndented> result) {
        result.add(new CategoryIndented(indent, category));
        for (RemoteModRepository.Category subcategory : category.getSubcategories()) {
            resolveCategory(subcategory, indent + 1, result);
        }
    }

    public void setLoading(boolean loading) {
        Schedulers.androidUIThread().execute(() -> {
            search.setEnabled(!loading);
            nameEditText.setEnabled(!loading);
            sourceSpinner.setEnabled(!loading);
            gameVersionSpinner.setEnabled(!loading);
            categorySpinner.setEnabled(!loading);
            sortSpinner.setEnabled(!loading);
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            listLayout.setVisibility(loading ? View.GONE : View.VISIBLE);
            if (loading) {
                retry.setVisibility(View.GONE);
            }
        });
    }

    public void setFailed() {
        Schedulers.androidUIThread().execute(() -> {
            retry.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            listLayout.setVisibility(View.GONE);
        });
    }

    public void search() {
        search(gameVersion.get(),
                category.get().getCategory(),
                pageOffset.get(),
                Objects.requireNonNull(nameEditText.getText()).toString(),
                sortType.get());
    }

    public void search(String userGameVersion, RemoteModRepository.Category category, int pageOffset, String searchFilter, RemoteModRepository.SortType sort) {
        retrySearch = null;
        setLoading(true);
        if (executor != null && !executor.isCancelled()) {
            executor.cancel();
        }
        executor = Task.supplyAsync(() -> repository.search(userGameVersion, category, pageOffset, 50, searchFilter, sort, RemoteModRepository.SortOrder.DESC))
                .whenComplete(Schedulers.androidUIThread(), (result, exception) -> {
                    setLoading(false);
                    if (exception == null) {
                        ArrayList<RemoteMod> list = (ArrayList<RemoteMod>) result.getResults().collect(Collectors.toList());
                        pageCount.set(result.getTotalPages());
                        adapter = new RemoteModListAdapter(getContext(), this, list, mod -> {
                            RemoteModInfoPage page = new RemoteModInfoPage(getContext(), PageManager.PAGE_ID_TEMP, getParent(), R.layout.page_download_addon_info, this, mod, version.get(), callback);
                            DownloadPageManager.getInstance().showTempPage(page);
                        });
                        listView.setAdapter(adapter);
                    } else {
                        setFailed();
                        pageCount.set(-1);
                        retrySearch = () -> search(userGameVersion, category, pageOffset, searchFilter, sort);
                    }
        }).executor(true);
    }

    protected String getLocalizedCategory(String category) {
        return AndroidUtils.getLocalizedText(getContext(), "curse_category_" + category);
    }

    protected String getLocalizedCategoryIndent(CategoryIndented category) {
        return StringUtils.repeats(' ', category.getIndent() * 4) +
                (category.getCategory() == null
                        ? getContext().getString(R.string.curse_category_0)
                        : getLocalizedCategory(category.getCategory().getId()));
    }

    protected String getLocalizedOfficialPage() {
        return getContext().getString(R.string.mods_curseforge);
    }

    public void create() {
        searchLayout = findViewById(R.id.search_layout);

        search = findViewById(R.id.search);
        search.setOnClickListener(this);

        nameEditText = findViewById(R.id.name);
        sourceText = findViewById(R.id.download_source_text);
        sourceSpinner = findViewById(R.id.download_source);
        gameVersionSpinner = findViewById(R.id.game_version);
        categorySpinner = findViewById(R.id.category);
        sortSpinner = findViewById(R.id.sort);

        listLayout = findViewById(R.id.list_layout);
        page = findViewById(R.id.page);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        first = findViewById(R.id.first);
        last = findViewById(R.id.last);
        listView = findViewById(R.id.list);
        progressBar = findViewById(R.id.progress);
        retry = findViewById(R.id.retry);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        first.setOnClickListener(this);
        last.setOnClickListener(this);
        retry.setOnClickListener(this);

        nameEditText.setHint(supportChinese.get() ? getContext().getString(R.string.search_hint_chinese) : getContext().getString(R.string.search_hint_english));

        sourceText.setVisibility(downloadSources.getSize() > 1 ? View.VISIBLE : View.GONE);
        sourceSpinner.setVisibility(downloadSources.getSize() > 1 ? View.VISIBLE : View.GONE);
        if (downloadSources.getSize() > 1) {
            sourceSpinner.setDataList(new ArrayList<>(downloadSources));
            ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_auto_tint, new ArrayList<>(downloadSources));
            sourceAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
            sourceSpinner.setAdapter(sourceAdapter);
            sourceSpinner.setSelection(downloadSource.get().equals(getContext().getString(R.string.mods_modrinth)) ? 1 : 0);
            FXUtils.bindSelection(sourceSpinner, downloadSource);
        }

        gameVersionSpinner.setDataList(new ArrayList<>(Arrays.stream(RemoteModRepository.DEFAULT_GAME_VERSIONS).collect(Collectors.toList())));
        ArrayAdapter<String> gameVersionAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_auto_tint, new ArrayList<>(Arrays.stream(RemoteModRepository.DEFAULT_GAME_VERSIONS).collect(Collectors.toList())));
        gameVersionAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        gameVersionSpinner.setAdapter(gameVersionAdapter);
        gameVersionSpinner.setSelection(0);
        FXUtils.bindSelection(gameVersionSpinner, gameVersion);

        ArrayList<CategoryIndented> categoryDataList = new ArrayList<>();
        categoryDataList.add(new CategoryIndented(0, null));
        categorySpinner.setDataList(categoryDataList);
        ArrayList<String> categoryStringList = categoryDataList.stream().map(this::getLocalizedCategoryIndent).collect(Collectors.toCollection(ArrayList::new));
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_auto_tint, categoryStringList);
        categoryAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(0);
        FXUtils.bindSelection(categorySpinner, category);
        downloadSource.addListener(observable -> Task.supplyAsync(() -> {
            setLoading(true);
            return repository.getCategories();
        }).thenAcceptAsync(Schedulers.androidUIThread(), categories -> {
            ArrayList<CategoryIndented> result = new ArrayList<>();
            result.add(new CategoryIndented(0, null));
            for (RemoteModRepository.Category category : Lang.toIterable(categories)) {
                resolveCategory(category, 0, result);
            }
            categorySpinner.setDataList(result);
            ArrayList<String> resultStr = result.stream().map(this::getLocalizedCategoryIndent).collect(Collectors.toCollection(ArrayList::new));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_auto_tint, resultStr);
            adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
            categorySpinner.setAdapter(adapter);
            FXUtils.unbindSelection(categorySpinner, category);
            categorySpinner.setSelection(0);
            category.set(result.get(0));
            FXUtils.bindSelection(categorySpinner, category);
            search();
        }).start());

        sortSpinner.setDataList(new ArrayList<>(Arrays.stream(RemoteModRepository.SortType.values()).collect(Collectors.toList())));
        ArrayList<String> sorts = new ArrayList<>();
        sorts.add(getContext().getString(R.string.curse_sort_popularity));
        sorts.add(getContext().getString(R.string.curse_sort_name));
        sorts.add(getContext().getString(R.string.curse_sort_date_created));
        sorts.add(getContext().getString(R.string.curse_sort_last_updated));
        sorts.add(getContext().getString(R.string.curse_sort_author));
        sorts.add(getContext().getString(R.string.curse_sort_total_downloads));
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_auto_tint, sorts);
        sortAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        sortSpinner.setAdapter(sortAdapter);
        sortSpinner.setSelection(0);
        FXUtils.bindSelection(sortSpinner, sortType);

        pageOffset.addListener(observable -> page.setText(AndroidUtils.getLocalizedText(
                getContext(), "search_page_n", pageOffset.get() + 1, pageCount.get() == -1 ? "-" : pageCount.getValue().toString()
        )));
        pageCount.addListener(observable -> page.setText(AndroidUtils.getLocalizedText(
                getContext(), "search_page_n", pageOffset.get() + 1, pageCount.get() == -1 ? "-" : pageCount.getValue().toString()
        )));

        search("", null, 0, "", RemoteModRepository.SortType.POPULARITY);
    }

    @Override
    public void loadVersion(Profile profile, String version) {
        this.version.set(new Profile.ProfileVersion(profile, version));
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void onClick(View v) {
        if (v == search) {
            pageOffset.set(0);
            search();
        }
        if (v == next && pageCount.get() > 1 && pageOffset.get() < pageCount.get() - 1) {
            pageOffset.set(pageOffset.get() + 1);
            search();
        }
        if (v == previous && pageOffset.get() > 0) {
            pageOffset.set(pageOffset.get() - 1);
            search();
        }
        if (v == first && pageCount.get() != 0 && pageCount.get() != -1) {
            pageOffset.set(0);
            search();
        }
        if (v == last && pageCount.get() != 0 && pageCount.get() != -1) {
            pageOffset.set(pageCount.get() - 1);
            search();
        }
        if (v == retry && retrySearch != null) {
            retrySearch.run();
        }
    }

    public RemoteModRepository getRepository() {
        return repository;
    }

    private static class CategoryIndented {
        private final int indent;
        private final RemoteModRepository.Category category;

        public CategoryIndented(int indent, RemoteModRepository.Category category) {
            this.indent = indent;
            this.category = category;
        }

        public int getIndent() {
            return indent;
        }

        public RemoteModRepository.Category getCategory() {
            return category;
        }
    }
}
