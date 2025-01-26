package org.koishi.launcher.h2co3.ui.setting;

import static org.koishi.launcher.h2co3.setting.ConfigHolder.config;
import static org.koishi.launcher.h2co3core.util.Lang.thread;
import static org.koishi.launcher.h2co3core.util.Logging.LOG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.DownloadProviders;
import org.koishi.launcher.h2co3.upgrade.UpdateChecker;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.FXUtils;
import org.koishi.launcher.h2co3.util.RequestCodes;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.task.FetchTask;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3core.util.io.FileUtils;
import org.koishi.launcher.h2co3library.browser.FileBrowser;
import org.koishi.launcher.h2co3library.browser.options.LibMode;
import org.koishi.launcher.h2co3library.browser.options.SelectionMode;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherCheckBox;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSeekBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSpinner;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;
import org.koishi.launcher.h2co3core.util.LocaleUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;

public class LauncherSettingPage extends H2CO3LauncherCommonPage implements View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    public static final long ONE_DAY = 1000 * 60 * 60 * 24;

    private H2CO3LauncherSpinner<String> language;
    private H2CO3LauncherButton checkUpdate;
    private H2CO3LauncherButton clearCache;
    private H2CO3LauncherButton exportLog;
    private H2CO3LauncherButton cursor;
    private H2CO3LauncherButton menuIcon;
    private H2CO3LauncherButton resetCursor;
    private H2CO3LauncherButton resetMenuIcon;
    private H2CO3LauncherCheckBox autoSource;
    private H2CO3LauncherSpinner<String> versionList;
    private H2CO3LauncherSpinner<String> downloadType;
    private H2CO3LauncherCheckBox autoThreads;
    private H2CO3LauncherSeekBar threads;
    private H2CO3LauncherTextView threadsText;
    private H2CO3LauncherCheckBox materialYou;

    public LauncherSettingPage(Context context, int id, H2CO3LauncherUILayout parent, int resId) {
        super(context, id, parent, resId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        language = findViewById(R.id.language);
        checkUpdate = findViewById(R.id.check_update);
        clearCache = findViewById(R.id.clear_cache);
        exportLog = findViewById(R.id.export_log);
        cursor = findViewById(R.id.cursor);
        menuIcon = findViewById(R.id.menu_icon);
        resetCursor = findViewById(R.id.reset_cursor);
        resetMenuIcon = findViewById(R.id.reset_menu_icon);
        autoSource = findViewById(R.id.check_auto_source);
        versionList = findViewById(R.id.source_auto);
        downloadType = findViewById(R.id.source);
        autoThreads = findViewById(R.id.check_auto_threads);
        threads = findViewById(R.id.threads);
        threadsText = findViewById(R.id.threads_text);

        checkUpdate.setOnClickListener(this);
        clearCache.setOnClickListener(this);
        exportLog.setOnClickListener(this);
        resetCursor.setOnClickListener(this);
        resetMenuIcon.setOnClickListener(this);

        ArrayList<String> languageList = new ArrayList<>();
        languageList.add(getContext().getString(R.string.settings_launcher_language_system));
        languageList.add(getContext().getString(R.string.settings_launcher_language_english));
        languageList.add(getContext().getString(R.string.settings_launcher_language_simplified_chinese));
        languageList.add(getContext().getString(R.string.settings_launcher_language_russian));
        languageList.add(getContext().getString(R.string.settings_launcher_language_brazilian_portuguese));
        languageList.add(getContext().getString(R.string.settings_launcher_language_persian));
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_auto_tint, languageList);
        languageAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        language.setAdapter(languageAdapter);
        language.setSelection(LocaleUtils.getLanguage(getContext()));
        language.setOnItemSelectedListener(this);

        autoSource.setChecked(config().autoChooseDownloadTypeProperty().get());
        autoSource.addCheckedChangeListener();
        autoSource.checkProperty().bindBidirectional(config().autoChooseDownloadTypeProperty());
        versionList.visibilityProperty().bind(autoSource.checkProperty());
        downloadType.visibilityProperty().bind(autoSource.checkProperty().not());
        versionList.setDataList(new ArrayList<>(DownloadProviders.providersById.keySet()));
        ArrayList<String> versionListSourceList = new ArrayList<>();
        versionListSourceList.add(getContext().getString(R.string.download_provider_official));
        versionListSourceList.add(getContext().getString(R.string.download_provider_balanced));
        versionListSourceList.add(getContext().getString(R.string.download_provider_mirror));
        ArrayAdapter<String> versionListAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_auto_tint, versionListSourceList);
        versionListAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        versionList.setAdapter(versionListAdapter);
        versionList.setSelection(getSourcePosition(config().versionListSourceProperty().get()));
        FXUtils.bindSelection(versionList, config().versionListSourceProperty());
        downloadType.setDataList(new ArrayList<>(DownloadProviders.rawProviders.keySet()));
        ArrayList<String> downloadTypeList = new ArrayList<>();
        downloadTypeList.add(getContext().getString(R.string.download_provider_mojang));
        downloadTypeList.add(getContext().getString(R.string.download_provider_bmclapi));
        ArrayAdapter<String> downloadTypeAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_auto_tint, downloadTypeList);
        downloadTypeAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        downloadType.setAdapter(downloadTypeAdapter);
        downloadType.setSelection(getSourcePosition(config().downloadTypeProperty().get()));
        FXUtils.bindSelection(downloadType, config().downloadTypeProperty());
        autoThreads.setChecked(config().getAutoDownloadThreads());
        autoThreads.addCheckedChangeListener();
        autoThreads.checkProperty().bindBidirectional(config().autoDownloadThreadsProperty());
        autoThreads.checkProperty().addListener(observable -> {
            if (autoThreads.isChecked()) {
                config().downloadThreadsProperty().set(FetchTask.DEFAULT_CONCURRENCY);
            }
        });
        threads.setProgress(config().getDownloadThreads());
        threads.addProgressListener();
        threads.progressProperty().bindBidirectional(config().downloadThreadsProperty());
        threadsText.stringProperty().bind(Bindings.createStringBinding(() -> threads.getProgress() + "", threads.progressProperty()));

        if (System.currentTimeMillis() - getLastClearCacheTime() >= 3 * ONE_DAY) {
            FileUtils.cleanDirectoryQuietly(new File(H2CO3LauncherTools.CACHE_DIR).getParentFile());
            setLastClearCacheTime(System.currentTimeMillis());
        }
    }

    public long getLastClearCacheTime() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("launcher", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("clear_cache", 0L);
    }

    public void setLastClearCacheTime(long time) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("launcher", Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("clear_cache", time);
        editor.apply();
    }

    private int getSourcePosition(String source) {
        switch (source) {
            case "official":
            case "mojang":
                return 0;
            case "mirror":
                return 2;
            default:
                return 1;
        }
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void onClick(View v) {
        if (v == checkUpdate && !UpdateChecker.getInstance().isChecking()) {
            UpdateChecker.getInstance().checkManually(getContext()).whenComplete(Schedulers.androidUIThread(), e -> {
                if (e != null) {
                    H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                    builder.setCancelable(false);
                    builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                    builder.setMessage(getContext().getString(R.string.update_check_failed) + "\n" + e);
                    builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                    builder.create().show();
                }
            }).start();
        }
        if (v == clearCache) {
            FileUtils.cleanDirectoryQuietly(new File(H2CO3LauncherTools.CACHE_DIR).getParentFile());
        }
        if (v == exportLog) {
            thread(() -> {
                Path logFile = new File(new File(H2CO3LauncherTools.SHARED_COMMON_DIR).getParent(), "h2co3Launcher-exported-logs-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss")) + ".log").toPath().toAbsolutePath();
                LOG.info("Exporting logs to " + logFile);
                try {
                    Files.write(logFile, Logging.getRawLogs());
                } catch (IOException e) {
                    Schedulers.androidUIThread().execute(() -> {
                        H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                        builder.setCancelable(false);
                        builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                        builder.setMessage(getContext().getString(R.string.settings_launcher_launcher_log_export_failed) + "\n" + e);
                        builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                        builder.create().show();
                    });
                    LOG.log(Level.WARNING, "Failed to export logs", e);
                    return;
                }
                Schedulers.androidUIThread().execute(() -> {
                    H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                    builder.setCancelable(false);
                    builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
                    builder.setMessage(AndroidUtils.getLocalizedText(getContext(), "settings_launcher_launcher_log_export_success", logFile));
                    builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                    builder.create().show();
                });
            });
        }
        if (v == cursor) {
            FileBrowser.Builder builder = new FileBrowser.Builder(getContext());
            builder.setLibMode(LibMode.FILE_CHOOSER);
            builder.setSelectionMode(SelectionMode.SINGLE_SELECTION);
            ArrayList<String> suffix = new ArrayList<>();
            suffix.add(".png");
            builder.setSuffix(suffix);
            builder.create().browse(getActivity(), RequestCodes.SELECT_CURSOR_CODE, ((requestCode, resultCode, data) -> {
                if (requestCode == RequestCodes.SELECT_CURSOR_CODE && resultCode == Activity.RESULT_OK && data != null) {
                    String path = FileBrowser.getSelectedFiles(data).get(0);
                    Uri uri = Uri.parse(path);
                    if (AndroidUtils.isDocUri(uri)) {
                        AndroidUtils.copyFile(getActivity(), uri, new File(H2CO3LauncherTools.FILES_DIR, "cursor.png"));
                    } else {
                        try {
                            FileUtils.copyFile(new File(path), new File(H2CO3LauncherTools.FILES_DIR, "cursor.png"));
                        } catch (IOException ignore) {
                        }
                    }
                }
            }));
        }
        if (v == menuIcon) {
            FileBrowser.Builder builder = new FileBrowser.Builder(getContext());
            builder.setLibMode(LibMode.FILE_CHOOSER);
            builder.setSelectionMode(SelectionMode.SINGLE_SELECTION);
            ArrayList<String> suffix = new ArrayList<>();
            suffix.add(".png");
            suffix.add(".gif");
            builder.setSuffix(suffix);
            builder.create().browse(getActivity(), RequestCodes.SELECT_CURSOR_CODE, ((requestCode, resultCode, data) -> {
                if (requestCode == RequestCodes.SELECT_CURSOR_CODE && resultCode == Activity.RESULT_OK && data != null) {
                    String path = FileBrowser.getSelectedFiles(data).get(0);
                    Uri uri = Uri.parse(path);
                    String type = getContext().getContentResolver().getType(uri);
                    if (type != null) {
                        if (type.contains("png")) {
                            type = "png";
                        } else if (type.contains("gif")) {
                            type = "gif";
                        }
                    } else {
                        type = "png";
                    }
                    if (AndroidUtils.isDocUri(uri)) {
                        AndroidUtils.copyFile(getActivity(), uri, new File(H2CO3LauncherTools.FILES_DIR, "menu_icon." + type));
                    } else {
                        try {
                            FileUtils.copyFile(new File(path), new File(H2CO3LauncherTools.FILES_DIR, "menu_icon." + type));
                        } catch (IOException ignore) {
                        }
                    }
                }
            }));
        }
        if (v == resetCursor) {
            new File(H2CO3LauncherTools.FILES_DIR, "cursor.png").delete();
        }
        if (v == resetMenuIcon) {
            new File(H2CO3LauncherTools.FILES_DIR, "menu_icon.png").delete();
            new File(H2CO3LauncherTools.FILES_DIR, "menu_icon.gif").delete();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == language) {
            LocaleUtils.changeLanguage(getContext(), position);
            LocaleUtils.setLanguage(getContext());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        /*if (buttonView == ignoreNotch) {
            ThemeEngine.getInstance().applyAndSave(getContext(), getActivity().getWindow(), isChecked);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        }*/
    }
}
