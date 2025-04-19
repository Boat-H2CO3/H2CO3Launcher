package org.koishi.launcher.h2co3library.browser;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import org.koishi.launcher.h2co3library.R;
import org.koishi.launcher.h2co3library.browser.adapter.FileBrowserAdapter;
import org.koishi.launcher.h2co3library.browser.adapter.FileBrowserListener;
import org.koishi.launcher.h2co3library.browser.options.LibMode;
import org.koishi.launcher.h2co3library.browser.options.SelectionMode;
import org.koishi.launcher.h2co3library.component.BaseActivity;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FileBrowserActivity extends BaseActivity implements View.OnClickListener {

    private FileBrowser fileBrowser;

    private H2CO3LauncherButton back;
    private H2CO3LauncherButton close;
    private H2CO3LauncherTextView mode;
    private H2CO3LauncherTextView type;

    private H2CO3LauncherTextView currentText;
    private ListView listView;

    private H2CO3LauncherButton sharedDir;
    private H2CO3LauncherButton privateDir;
    private H2CO3LauncherButton openExternal;
    private H2CO3LauncherButton selectExternal;
    private H2CO3LauncherButton confirm;

    private Path currentPath;

    private ArrayList<String> selectedFiles;
    private ArrayList<Uri> extSelected;

    private final ActivityResultLauncher<Object> launcher = registerForActivityResult(new ActivityResultContract<Object, Uri>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Object o) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, fileBrowser.getSelectionMode() == SelectionMode.MULTIPLE_SELECTION);
            return intent;
        }

        @Override
        public Uri parseResult(int resultCode, @Nullable Intent data) {
            if (data == null || resultCode != Activity.RESULT_OK) {
                return null;
            }
            ClipData clipData = data.getClipData();
            if (clipData != null && clipData.getItemCount() > 0) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    if (uri != null) {
                        extSelected.add(uri);
                    }
                }
            } else {
                extSelected.add(data.getData());
            }
            return null;
        }
    }, result -> {
        if (!extSelected.isEmpty()) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(FileBrowser.SELECTED_FILES, extSelected);
            FileBrowserActivity.this.setResult(Activity.RESULT_OK, intent);
            FileBrowserActivity.this.finish();
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);

        showMessageListView();

        fileBrowser = (FileBrowser) getIntent().getExtras().getSerializable("config");

        /*
        titleView = findViewById(R.id.title);
        titleView.setTitle(fileBrowser.getTitle());

         */
        mode = findViewById(R.id.mode);
        type = findViewById(R.id.type);
        mode.setText(getMode());
        type.setText(getType());
        back = findViewById(R.id.back);
        close = findViewById(R.id.close);
        back.setOnClickListener(this);
        close.setOnClickListener(this);

        sharedDir = findViewById(R.id.shared_dir);
        privateDir = findViewById(R.id.private_dir);
        openExternal = findViewById(R.id.open_external);
        selectExternal = findViewById(R.id.select_external);
        confirm = findViewById(R.id.confirm);
        sharedDir.setOnClickListener(this);
        privateDir.setOnClickListener(this);
        openExternal.setOnClickListener(this);
        selectExternal.setOnClickListener(this);
        confirm.setOnClickListener(this);

        selectedFiles = new ArrayList<>();
        extSelected = new ArrayList<>();
        currentText = findViewById(R.id.current_folder);
        listView = findViewById(R.id.list);
        refreshList(currentPath != null ? currentPath : new File(fileBrowser.getInitDir()).toPath());

        if (fileBrowser.getLibMode() != LibMode.FILE_CHOOSER) {
            selectExternal.setVisibility(View.GONE);
        }
        if (fileBrowser.getLibMode() != LibMode.FILE_BROWSER) {
            openExternal.setVisibility(View.GONE);
        }
        switch (fileBrowser.getCode()) {
            case 100:
            case 150:
            case 200:
            case 500:
            case 600:
            case 700:
            case 750:
                selectExternal.setVisibility(View.GONE);
                break;
                default:
        }
        if (!fileBrowser.isExternalSelection()) {
            selectExternal.setVisibility(View.GONE);
            openExternal.setVisibility(View.GONE);
        }
    }

    private String getMode() {
        switch (fileBrowser.getLibMode()) {
            case FILE_CHOOSER:
                return getString(R.string.file_browser_mode_file);
            case FOLDER_CHOOSER:
                return getString(R.string.file_browser_mode_folder);
            default:
                return getString(R.string.file_browser_mode_browse);
        }
    }

    private String getType() {
        if (fileBrowser.getSelectionMode() == SelectionMode.SINGLE_SELECTION) {
            return getString(R.string.file_browser_selection_simple);
        }
        return getString(R.string.file_browser_selection_multiple);
    }

    private void refreshList(Path path) {
        if (fileBrowser.getLibMode() == LibMode.FOLDER_CHOOSER && !selectedFiles.contains(path.toString())) {
            selectedFiles = new ArrayList<>();
            selectedFiles.add(path.toString());
        }
        currentPath = path;
        currentText.setText(path.toString());
        FileBrowserAdapter adapter = new FileBrowserAdapter(this, fileBrowser, path, selectedFiles, new FileBrowserListener() {
            @Override
            public void onEnterDir(String path) {
                refreshList(new File(path).toPath());
            }

            @Override
            public void onSelect(FileBrowserAdapter adapter1, String path) {
                if (selectedFiles.stream().anyMatch(s -> s.equals(path))) {
                    selectedFiles.remove(path);
                } else {
                    if (fileBrowser.getSelectionMode() == SelectionMode.SINGLE_SELECTION) {
                        selectedFiles = new ArrayList<>();
                    }
                    selectedFiles.add(path);
                }
                adapter1.setSelectedFiles(selectedFiles);
                adapter1.notifyDataSetChanged();
            }
        });
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentPath.getParent() != null && !currentPath.toString().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            refreshList(currentPath.getParent());
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            if (currentPath.getParent() != null && !currentPath.toString().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                refreshList(currentPath.getParent());
            } else {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        }
        if (view == close) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        if (view == sharedDir) {
            refreshList(Environment.getExternalStorageDirectory().toPath());
        }
        if (view == privateDir) {
            if (getExternalCacheDir().getParent() != null) {
                refreshList(new File(getExternalCacheDir().getParent()).toPath());
            } else {
                Toast.makeText(this, getString(R.string.file_browser_private_alert), Toast.LENGTH_SHORT).show();
            }
        }
        if (view == openExternal) {
            if (currentPath.toFile().getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                currentPath = currentPath.resolve("H2CO3Launcher");
            }
            Uri uri = FileProvider.getUriForFile(this, getString(R.string.file_browser_provider), currentPath.toFile());
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setDataAndType(uri, "*/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, getString(R.string.file_browser_open_external)));
        }
        if (view == selectExternal) {
            launcher.launch(null);
        }
        if (view == confirm) {
            if (selectedFiles.size() == 0 && fileBrowser.getLibMode() != LibMode.FILE_BROWSER) {
                Toast.makeText(this, getString(R.string.file_browser_positive_alert), Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(FileBrowser.SELECTED_FILES, (ArrayList<? extends Parcelable>) selectedFiles.stream().map(Uri::parse).collect(Collectors.toList()));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callback = false;
        super.onActivityResult(requestCode, resultCode, data);
    }
}
