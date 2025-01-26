package org.koishi.launcher.h2co3.ui.download;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.setting.Profiles;
import org.koishi.launcher.h2co3.ui.PageManager;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.ModTranslations;
import org.koishi.launcher.h2co3core.mod.LocalModFile;
import org.koishi.launcher.h2co3core.mod.RemoteMod;
import org.koishi.launcher.h2co3core.mod.RemoteModRepository;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.util.SimpleMultimap;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3core.util.versioning.VersionNumber;

import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherTempPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;
import org.koishi.launcher.h2co3core.util.LocaleUtils;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RemoteModInfoPage extends H2CO3LauncherTempPage implements View.OnClickListener {

    private final RemoteModRepository repository;
    private final ModTranslations translations;
    private final RemoteMod addon;
    private final Profile.ProfileVersion version;
    private final RemoteModVersionPage.DownloadCallback callback;
    private final DownloadPage page;

    private SimpleMultimap<String, RemoteMod.Version, List<RemoteMod.Version>> versions;

    private H2CO3LauncherLinearLayout layout;
    private H2CO3LauncherProgressBar progressBar;
    private H2CO3LauncherImageButton retry;
    private ListView versionListView;
    private H2CO3LauncherImageView icon;
    private H2CO3LauncherTextView name;
    private H2CO3LauncherTextView tag;
    private H2CO3LauncherTextView description;
    private H2CO3LauncherTextView mcmod;
    private H2CO3LauncherImageButton website;
    private H2CO3LauncherProgressBar screenshotLoading;
    private H2CO3LauncherImageView screenshotRetry;
    private H2CO3LauncherTextView screenshotNoResult;
    private RecyclerView screenshotView;
    private H2CO3LauncherEditText search;

    public RemoteModInfoPage(Context context, int id, H2CO3LauncherUILayout parent, int resId, DownloadPage page, RemoteMod addon, Profile.ProfileVersion version, @Nullable RemoteModVersionPage.DownloadCallback callback) {
        super(context, id, parent, resId);

        this.page = page;
        this.repository = page.repository;
        this.addon = addon;
        this.translations = ModTranslations.getTranslationsByRepositoryType(repository.getType());
        this.version = version;
        this.callback = callback;

        create();
    }

    public void create() {
        layout = findViewById(R.id.layout);
        progressBar = findViewById(R.id.progress);
        retry = findViewById(R.id.retry);

        versionListView = findViewById(R.id.version_list);
        icon = findViewById(R.id.icon);
        name = findViewById(R.id.name);
        tag = findViewById(R.id.tag);
        description = findViewById(R.id.description);
        mcmod = findViewById(R.id.mcmod);
        website = findViewById(R.id.website);
        screenshotView = findViewById(R.id.screenshot_recyclerView);
        screenshotLoading = findViewById(R.id.screenshot_loading);
        screenshotRetry = findViewById(R.id.screenshot_retry);
        screenshotNoResult = findViewById(R.id.screenshot_no_result);
        search = findViewById(R.id.search);

        retry.setOnClickListener(this);
        mcmod.setOnClickListener(this);
        website.setOnClickListener(this);

        search.stringProperty().addListener(observable -> {
            loadGameVersions();
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        icon.setImageDrawable(null);
        Glide.with(getContext()).load(addon.getIconUrl()).into(icon);
        ModTranslations.Mod mod = translations.getModByCurseForgeId(addon.getSlug());
        mcmod.setVisibility(mod == null ? View.GONE : View.VISIBLE);
        name.setText(mod != null && LocaleUtils.isChinese(getContext()) ? mod.getDisplayName() : addon.getTitle());
        description.setText(addon.getDescription());
        List<String> categories = addon.getCategories().stream().map(page::getLocalizedCategory).collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        categories.forEach(it -> stringBuilder.append(it).append("   "));
        String tag = StringUtils.removeSuffix(stringBuilder.toString(), "   ");
        this.tag.setText(tag);

        loadModVersions();
        loadScreenshots();
    }

    private void loadGameVersions() {
        ModGameVersionAdapter adapter = new ModGameVersionAdapter(getContext(), versions.keys().stream()
                .sorted(Collections.reverseOrder(VersionNumber::compare))
                .filter(it -> it.contains(Optional.ofNullable(search.getStringValue()).orElse("")))
                .collect(Collectors.toList()), v -> {
            RemoteModVersionPage page = new RemoteModVersionPage(getContext(), PageManager.PAGE_ID_TEMP, getParent(), R.layout.page_download_addon_version, new ArrayList<>(versions.get(v)), version, callback, RemoteModInfoPage.this.page);
            DownloadPageManager.getInstance().showTempPage(page);
        });
        versionListView.setAdapter(adapter);
    }

    private void loadModVersions() {
        setLoading(true);

        Task.supplyAsync(() -> {
            Stream<RemoteMod.Version> versions = addon.getData().loadVersions(repository);
            return sortVersions(versions);
        }).whenComplete(Schedulers.androidUIThread(), (result, exception) -> {
            if (exception == null) {
                this.versions = result;
                loadGameVersions();
                checkInstalled();
            } else {
                setFailed();
            }
            setLoading(false);
        }).start();
    }

    private void loadScreenshots() {
        setScreenshotLoading(true);

        Task.supplyAsync(() -> addon.getData().loadScreenshots(repository)).whenComplete(Schedulers.androidUIThread(), ((result, exception) -> {
            if (exception == null) {
                if (result.isEmpty()) {
                    screenshotNoResult.setVisibility(View.VISIBLE);
                } else {
                    RemoteModScreenshotAdapter adapter = new RemoteModScreenshotAdapter(getContext(), result);
                    screenshotView.setLayoutManager(new LinearLayoutManager(getContext()));
                    screenshotView.setAdapter(adapter);
                }
            } else {
                setScreenshotFailed();
            }
            setScreenshotLoading(false);
        })).start();
    }

    private void checkInstalled() {
        Task.supplyAsync(() -> {
            String remoteName = addon.getTitle().replace(" ", "").toLowerCase();
            List<LocalModFile> modFiles = Profiles.getSelectedProfile().getRepository().getModManager(Profiles.getSelectedVersion()).getMods().parallelStream().filter(localModFile -> {
                String localName = localModFile.getName().replace(" ", "").toLowerCase();
                return remoteName.contains(localName);
            }).collect(Collectors.toList());
            for (LocalModFile localModFile : modFiles) {
                Optional<RemoteMod.Version> remoteVersion = repository.getRemoteVersionByLocalFile(localModFile, localModFile.getFile());
                if (remoteVersion.isPresent()) {
                    String modId = remoteVersion.get().getModid();
                    if (addon.getModID().equals(modId)) {
                        return remoteVersion.get();
                    }
                }
            }
            return null;
        }).whenComplete(Schedulers.androidUIThread(), (result, exception) -> {
            if (exception == null && result != null) {
                name.setText(String.format("[%s] %s", getContext().getString(R.string.installed), name.getText()));
            }
        }).start();
    }

    private SimpleMultimap<String, RemoteMod.Version, List<RemoteMod.Version>> sortVersions(Stream<RemoteMod.Version> versions) {
        SimpleMultimap<String, RemoteMod.Version, List<RemoteMod.Version>> classifiedVersions
                = new SimpleMultimap<>(HashMap::new, ArrayList::new);
        versions.forEach(version -> {
            for (String gameVersion : version.getGameVersions()) {
                classifiedVersions.put(gameVersion, version);
            }
        });

        for (String gameVersion : classifiedVersions.keys()) {
            List<RemoteMod.Version> versionList = classifiedVersions.get(gameVersion);
            versionList.sort(Comparator.comparing(RemoteMod.Version::getDatePublished).reversed());
        }
        return classifiedVersions;
    }

    public void setLoading(boolean loading) {
        Schedulers.androidUIThread().execute(() -> {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            layout.setVisibility(loading ? View.GONE : View.VISIBLE);
            if (loading) {
                retry.setVisibility(View.GONE);
            }
        });
    }

    public void setFailed() {
        Schedulers.androidUIThread().execute(() -> {
            retry.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
        });
    }

    private void setScreenshotLoading(boolean loading) {
        Schedulers.androidUIThread().execute(() -> {
            screenshotLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
            if (loading) {
                screenshotRetry.setVisibility(View.GONE);
            }
        });
    }

    private void setScreenshotFailed() {
        Schedulers.androidUIThread().execute(() -> {
            screenshotRetry.setVisibility(View.VISIBLE);
            screenshotLoading.setVisibility(View.GONE);
        });
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
        if (v == retry) {
            loadModVersions();
        }
        if (v == mcmod) {
            ModTranslations.Mod mod = translations.getModByCurseForgeId(addon.getSlug());
            if (mod != null) {
                String url = translations.getMcmodUrl(mod);
                AndroidUtils.openLink(getContext(), url);
            }
        }
        if (v == website && StringUtils.isNotBlank(addon.getPageUrl())) {
            AndroidUtils.openLink(getContext(), addon.getPageUrl());
        }
        if (v == screenshotRetry) {
            loadScreenshots();
        }
    }
}
