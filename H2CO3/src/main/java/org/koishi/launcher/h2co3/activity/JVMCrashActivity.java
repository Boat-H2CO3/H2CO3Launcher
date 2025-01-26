package org.koishi.launcher.h2co3.activity;

import static org.koishi.launcher.h2co3core.util.Logging.LOG;
import static org.koishi.launcher.h2co3core.util.Pair.pair;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.SplashActivity;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3core.util.Pair;
import org.koishi.launcher.h2co3launcher.utils.Architecture;
import org.koishi.launcher.h2co3core.game.CrashReportAnalyzer;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.util.Lang;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3core.util.io.FileUtils;
import org.koishi.launcher.h2co3library.component.BaseActivity;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JVMCrashActivity extends BaseActivity implements View.OnClickListener {

    private static final Pattern FABRIC_MOD_ID = Pattern.compile("\\{(?<modid>.*?) @ (?<version>.*?)\\}");
    private boolean game;
    private int exitCode;
    private String logPath;
    private String renderer;
    private String java;
    private H2CO3LauncherButton restart;
    private H2CO3LauncherButton close;
    private H2CO3LauncherButton copy;
    private H2CO3LauncherButton share;
    private MaterialToolbar title;
    private H2CO3LauncherTextView error;
    private H2CO3LauncherTextView hint;
    private MaterialCardView hintLayout;
    private H2CO3LauncherProgressBar progressBar;

    public static void startCrashActivity(boolean game, Context context, int exitCode, String logPath, String renderer, String java) {
        Intent intent = new Intent(context, JVMCrashActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isGame", game);
        bundle.putInt("exitCode", exitCode);
        bundle.putString("logPath", logPath);
        bundle.putString("renderer", renderer);
        bundle.putString("java", java);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jvm_crash);
        showMessageListView();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        initializeViews();
        setOnClickListeners();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            game = extras.getBoolean("isGame");
            exitCode = extras.getInt("exitCode");
            logPath = extras.getString("logPath");
            renderer = extras.getString("renderer");
            java = extras.getString("java");
        }

        title.setTitle(game ? getString(R.string.game_crash_title) + getString(R.string.game_crash_title_add) : getString(R.string.jar_executor_crash_title));
        setLoading(true);
        try {
            init();
        } catch (IOException e) {
            handleInitError(e);
        }
    }

    private void initializeViews() {
        restart = findViewById(R.id.restart);
        close = findViewById(R.id.close);
        copy = findViewById(R.id.copy);
        share = findViewById(R.id.share);
        title = findViewById(R.id.title);
        error = findViewById(R.id.error);
        hint = findViewById(R.id.hint);
        hintLayout = findViewById(R.id.hint_layout);
        progressBar = findViewById(R.id.progress);
    }

    private void setOnClickListeners() {
        restart.setOnClickListener(this);
        close.setOnClickListener(this);
        copy.setOnClickListener(this);
        share.setOnClickListener(this);
    }

    private void handleInitError(IOException e) {
        setLoading(false);
        LOG.log(Level.WARNING, "Failed to read log file", e);
        error.setText(e.getMessage());
        hint.setText("Failed to read log file.");
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        hintLayout.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    private void init() throws IOException {
        String summarize = "Exit Normally, exit code = " + exitCode;
        List<String> errorLines = Arrays.stream(FileUtils.readText(new File(logPath)).split("\n")).collect(Collectors.toList());
        if (exitCode != 0 && StringUtils.containsOne(errorLines,
                "Could not create the Java Virtual Machine.",
                "Error occurred during initialization of VM",
                "A fatal exception has occurred. Program will exit.")) {
            summarize = "JVM launch failed, exit code = " + exitCode;
        } else if (exitCode != 0 || StringUtils.containsOne(errorLines, "Unable to launch")) {
            summarize = "Application error, unable to launch, exit code = " + exitCode;
        }
        errorLines.add(0, "");
        errorLines.add(0, "");
        errorLines.add(0, "==================== Basic Information ====================");
        errorLines.add(0, "Summarize: " + summarize);
        errorLines.add(0, "Renderer: " + renderer);
        errorLines.add(0, "Java Version: " + java);
        errorLines.add(0, "Android SDK: " + Build.VERSION.SDK_INT);
        errorLines.add(0, "Architecture: " + Architecture.archAsString(Architecture.getDeviceArchitecture()));
        errorLines.add(0, "H2CO3Launcher Version: " + getString(R.string.app_version));
        errorLines.add(0, "==================== Basic Information ====================");
        errorLines.forEach(it -> error.append(it + "\n"));

        if (game) {
            analyzeCrashReport(FileUtils.readText(new File(logPath)));
        } else {
            setLoading(false);
            hint.setText(getString(R.string.jar_executor_crash_reason));
        }
    }

    private void analyzeCrashReport(String rawLog) {
        setLoading(true);
        CompletableFuture.supplyAsync(() -> {
            Set<String> keywords = Collections.emptySet();
            String crashReport = CrashReportAnalyzer.extractCrashReport(rawLog);
            if (crashReport != null) {
                keywords = CrashReportAnalyzer.findKeywordsFromCrashReport(crashReport);
            }
            return pair(
                    CrashReportAnalyzer.anaylze(rawLog),
                    keywords);
        }).whenCompleteAsync((pair, exception) -> {
            setLoading(false);
            if (exception != null) {
                handleAnalysisError(exception);
            } else {
                handleAnalysisResult(pair);
            }
        }, Schedulers.androidUIThread()).exceptionally(Lang::handleUncaughtException);
    }

    private void handleAnalysisError(Throwable exception) {
        LOG.log(Level.WARNING, "Failed to analyze crash report", exception);
        hint.setText(getString(R.string.game_crash_reason_unknown));
    }

    private void handleAnalysisResult(Pair<List<CrashReportAnalyzer.Result>, Set<String>> pair) {
        List<CrashReportAnalyzer.Result> results = pair.getKey();
        Set<String> keywords = pair.getValue();
        StringBuilder stringBuilder = new StringBuilder();

        boolean hasMultipleRules = results.stream().map(CrashReportAnalyzer.Result::getRule).distinct().count() > 1;
        if (hasMultipleRules) {
            stringBuilder.append(getString(R.string.game_crash_reason_multiple));
            LOG.log(Level.INFO, "Multiple reasons detected");
        }

        for (CrashReportAnalyzer.Result result : results) {
            appendCrashReason(stringBuilder, result);
        }
        if (results.isEmpty()) {
            handleUnknownCrashReason(keywords);
        } else {
            hint.setText(stringBuilder.toString());
        }
    }

    private void appendCrashReason(StringBuilder stringBuilder, CrashReportAnalyzer.Result result) {
        switch (result.getRule()) {
            case MOD_RESOLUTION_CONFLICT:
            case MOD_RESOLUTION_MISSING:
            case MOD_RESOLUTION_COLLECTION:
                stringBuilder.append(AndroidUtils.getLocalizedText(this, "game_crash_reason_" + result.getRule().name().toLowerCase(Locale.ROOT),
                        translateFabricModId(result.getMatcher().group("sourcemod")),
                        parseFabricModId(result.getMatcher().group("destmod")),
                        parseFabricModId(result.getMatcher().group("destmod"))));
                break;
            case MOD_RESOLUTION_MISSING_MINECRAFT:
                stringBuilder.append(AndroidUtils.getLocalizedText(this, "game_crash_reason_" + result.getRule().name().toLowerCase(Locale.ROOT),
                        translateFabricModId(result.getMatcher().group("mod")),
                        result.getMatcher().group("version")));
                break;
            case TWILIGHT_FOREST_OPTIFINE:
            case PERFORMANT_FOREST_OPTIFINE:
            case JADE_FOREST_OPTIFINE:
                stringBuilder.append(AndroidUtils.getLocalizedText(this, "game_crash_reason_mod", "OptiFine"));
                break;
            default:
                stringBuilder.append(AndroidUtils.getLocalizedText(this, "game_crash_reason_" + result.getRule().name().toLowerCase(Locale.ROOT).replaceAll("\\.", "_"),
                        Arrays.stream(result.getRule().getGroupNames()).map(groupName -> result.getMatcher().group(groupName))
                                .toArray()));
                break;
        }
        stringBuilder.append("\n\n");
        LOG.log(Level.INFO, "Crash cause: " + result.getRule());
    }

    private void handleUnknownCrashReason(Set<String> keywords) {
        if (!keywords.isEmpty()) {
            hint.setText(AndroidUtils.getLocalizedText(this, "game_crash_reason_stacktrace", String.join(", ", keywords)));
            LOG.log(Level.INFO, "Crash reason unknown, but some log keywords have been found: " + String.join(", ", keywords));
        } else {
            hint.setText(getString(R.string.game_crash_reason_unknown));
            LOG.log(Level.INFO, "Crash reason unknown");
        }
    }

    private String translateFabricModId(String modName) {
        switch (modName) {
            case "fabricloader":
                return "Fabric";
            case "fabric":
                return "Fabric API";
            case "minecraft":
                return "Minecraft";
            default:
                return modName;
        }
    }

    private String parseFabricModId(String modName) {
        Matcher matcher = FABRIC_MOD_ID.matcher(modName);
        if (matcher.find()) {
            String modid = matcher.group("modid");
            String version = matcher.group("version");
            if ("[*]".equals(version)) {
                return AndroidUtils.getLocalizedText(this, "game_crash_reason_mod_resolution_mod_version_any", translateFabricModId(modid));
            } else {
                return AndroidUtils.getLocalizedText(this, "game_crash_reason_mod_resolution_mod_version", translateFabricModId(modid), version);
            }
        }
        return translateFabricModId(modName);
    }

    @Override
    public void onClick(View v) {
        if (v == restart) {
            restartApplication();
        } else if (v == close) {
            closeApplication();
        } else if (v == copy) {
            copyToClipboard();
        } else if (v == share) {
            shareLog();
        }
    }

    private void restartApplication() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        if (intent.getComponent() != null) {
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        }
        finish();
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    private void closeApplication() {
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    private void copyToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText(null, error.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, org.koishi.launcher.h2co3library.R.string.crash_reporter_toast, Toast.LENGTH_SHORT).show();
        }
    }

    private void shareLog() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            File file = File.createTempFile("h2co3Launcher-latest", ".log");
            FileUtils.writeText(file, error.getText().toString());
            Uri uri = FileProvider.getUriForFile(this, getString(org.koishi.launcher.h2co3library.R.string.file_browser_provider), file);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, getString(org.koishi.launcher.h2co3library.R.string.crash_reporter_share)));
        } catch (Exception e) {
            LOG.log(Level.INFO, "Share error: " + e);
            Toast.makeText(this, "Error sharing log file.", Toast.LENGTH_SHORT).show();
        }
    }
}