package org.koishi.launcher.h2co3.ui.fragment.directory;

import static org.koishi.launcher.h2co3.core.H2CO3Tools.MINECRAFT_DIR;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.adapter.DirectoryListAdapter;
import org.koishi.launcher.h2co3.adapter.MCVersionListAdapter;
import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.core.utils.file.AssetsUtils;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.resources.component.H2CO3LinearProgress;
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DirectoryFragment extends H2CO3Fragment {

    private final String h2co3Directory = MINECRAFT_DIR;
    private MaterialAlertDialogBuilder dialogBuilder;
    private DirectoryListAdapter dirAdapter;
    public MCVersionListAdapter verAdapter;
    private String H2CO3Dir;
    private JSONObject dirsJsonObj;
    private H2CO3Settings gameHelper;
    private TextInputEditText nameEditText;
    private View root;

    private static final int MSG_DIALOG_DISMISS = 0;
    private static final int MSG_ADD_NEW_DIRECTORY = 1;
    private static final int MSG_SHOW_ERROR = 2;

    private H2CO3LinearProgress dirProgressBar, verProgressBar;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_DIALOG_DISMISS -> dialogBuilder.create().dismiss();
                case MSG_ADD_NEW_DIRECTORY -> {
                    dialogBuilder.create().dismiss();
                    updateDirAdapter();
                }
                case MSG_SHOW_ERROR -> H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.INFO, getString(org.koishi.launcher.h2co3.library.R.string.ver_add_done));
            }
        }
    };

    private RecyclerView dirRecyclerView;
    public RecyclerView verRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameHelper = new H2CO3Settings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_directory, container, false);
        H2CO3Tools.loadPaths(requireContext());
        dirRecyclerView = root.findViewById(R.id.dirsRecyclerView);
        verRecyclerView = root.findViewById(R.id.versRecyclerView);
        dirProgressBar = root.findViewById(R.id.dirProgressBar);
        verProgressBar = root.findViewById(R.id.verProgressBar);
        initViews();
        return root;
    }

    private void initViews() {
        verProgressBar.setVisibility(View.VISIBLE);
        dirProgressBar.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            dirsJsonObj = getJsonObj();
            List<String> dirList = getDirList();

            requireActivity().runOnUiThread(() -> {

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    dirAdapter = new DirectoryListAdapter(dirList, requireActivity(), dirsJsonObj, gameHelper, this);
                    dirAdapter.setRvItemOnclickListener(this::removeOrDeleteDirectory);
                    ensureDefaultDirectory();
                    dirRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                    dirRecyclerView.setAdapter(dirAdapter);
                    dirProgressBar.setVisibility(View.GONE);
                    updateVerList(gameHelper.getGameDirectory() + "/versions");
                    FloatingActionButton newDirButton = findViewById(root, R.id.ver_new_dir);
                    newDirButton.show();
                    newDirButton.setOnClickListener(v -> showDirDialog());
                    verProgressBar.setVisibility(View.GONE);
                }, 220);


            });
        });
    }

    private void ensureDefaultDirectory() {
        executorService.execute(() -> {
            if (!hasData(h2co3Directory)) {
                try {
                    JSONObject defaultDir = new JSONObject();
                    defaultDir.put("path", h2co3Directory);
                    defaultDir.put("name", "Default Directory");
                    dirsJsonObj.getJSONArray("dirs").put(defaultDir);
                    saveJsonObj(dirsJsonObj);
                    updateDirAdapter();
                } catch (JSONException e) {
                    logError(e);
                }
            }
        });
    }

    private void addNewDirectory() {
        executorService.execute(() -> {
            if (nameEditText == null || H2CO3Dir == null) return;
            String name = nameEditText.getText().toString().trim();
            if (name.isEmpty()) return;

            try {
                JSONObject newDir = new JSONObject();
                newDir.put("path", H2CO3Dir);
                newDir.put("name", name);
                dirsJsonObj.getJSONArray("dirs").put(newDir);
                saveJsonObj(dirsJsonObj);
                handler.sendEmptyMessage(MSG_ADD_NEW_DIRECTORY);
                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.INFO, getString(org.koishi.launcher.h2co3.library.R.string.ver_add_done));
            } catch (JSONException e) {
                logError(e);
            }
        });
    }

    private void updateDirAdapter() {
        requireActivity().runOnUiThread(() -> dirAdapter.updateData(getDirList()));
    }

    private void showDirDialog() {
        dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.dialog_new_directory, null);
        dialogBuilder.setView(dialogView).setTitle(org.koishi.launcher.h2co3.library.R.string.add_directory);

        MaterialButton cancel = dialogView.findViewById(R.id.custom_dir_cancel);
        MaterialButton add = dialogView.findViewById(R.id.custom_dir_ok);
        TextInputLayout nameLay = dialogView.findViewById(R.id.dialog_dir_name_lay);
        nameEditText = dialogView.findViewById(R.id.dialog_dir_name);
        TextInputLayout pathLay = dialogView.findViewById(R.id.dialog_dir_path_lay);
        pathLay.setError(getString(org.koishi.launcher.h2co3.library.R.string.ver_input_hint));
        add.setEnabled(false);
        TextInputEditText pathEditText = dialogView.findViewById(R.id.dialog_dir_path);
        pathEditText.addTextChangedListener(new DirectoryTextWatcher(pathLay, add));

        AlertDialog dialog = dialogBuilder.create();
        cancel.setOnClickListener(v -> dialog.dismiss());
        add.setOnClickListener(v -> handleAddDirectory(pathEditText, dialog));
        dialog.show();
    }

    private void handleAddDirectory(TextInputEditText pathEditText, AlertDialog dialog) {
        String path = pathEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();

        if (path.isEmpty() || name.isEmpty()) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, "Please input both path and name");
            return;
        }

        if (hasData(path) || isNameExists(name)) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, getString(org.koishi.launcher.h2co3.library.R.string.ver_already_exists));
            return;
        }

        H2CO3Dir = path;
        newDir();
        addNewDirectory();
        dialog.dismiss();
    }

    private void newDir() {
        if (H2CO3Dir == null) return;
        executorService.execute(() -> {
            try {
                AssetsUtils.extractZipFromAssets(requireActivity(), "pack.zip", H2CO3Dir);
                handler.sendEmptyMessage(MSG_ADD_NEW_DIRECTORY);
            } catch (IOException e) {
                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, getString(org.koishi.launcher.h2co3.library.R.string.ver_not_right_dir) + e);
                handler.sendEmptyMessage(MSG_DIALOG_DISMISS);
            }
        });
    }

@Override
public void onResume() {
    super.onResume();
    String currentDir = gameHelper.getGameDirectory();
    File f = new File(currentDir);

    dirsJsonObj = getJsonObj();

    // 更新目录适配器
    dirAdapter = new DirectoryListAdapter(getDirList(), requireActivity(), dirsJsonObj, gameHelper, this);
    dirAdapter.updateData(getDirList());

    // 重新加载版本列表
    List<String> versionList = getVerList(currentDir); // 获取新版本列表
    if (verAdapter == null) {
        verAdapter = new MCVersionListAdapter(requireActivity(), versionList, this, gameHelper, gameHelper.getGameCurrentVersion());
        verRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        verRecyclerView.setAdapter(verAdapter);
    } else {
        verAdapter.updateData(versionList); // 更新版本适配器的数据
    }

    if (!f.isDirectory()) {
        setDir(h2co3Directory);
        H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, getString(org.koishi.launcher.h2co3.library.R.string.ver_null_dir));
    }
}

    private void setDir(String dir) {
        gameHelper.setGameDirectory(dir);
        gameHelper.setGameAssets(dir + "/assets/virtual/legacy");
        gameHelper.setGameAssetsRoot(dir + "/assets");
        gameHelper.setGameCurrentVersion(dir + "/versions");
        updateVerList(dir + "/versions");
    }

    public JSONObject getJsonObj() {
        try {
            File jsonFile = H2CO3Tools.DIRS_CONFIG_FILE;
            if (jsonFile.exists()) {
                String jsonStr = FileTools.readFileToString(jsonFile);
                return new JSONObject(jsonStr);
            }
        } catch (JSONException e) {
            logError(e);
        }
        return createNewJsonObj();
    }

    private JSONObject createNewJsonObj() {
        JSONObject jsonObj = new JSONObject();
        JSONArray dirs = new JSONArray();
        try {
            JSONObject defaultDir = new JSONObject();
            defaultDir.put("path", h2co3Directory);
            defaultDir.put("name", "Default Directory");
            dirs.put(defaultDir);
            jsonObj.put("dirs", dirs);
        } catch (JSONException e) {
            logError(e);
        }
        return jsonObj;
    }

    private void saveJsonObj(JSONObject jsonObj) {
        File jsonFile = H2CO3Tools.DIRS_CONFIG_FILE;
        FileTools.writeFile(jsonFile, jsonObj.toString());
        this.dirsJsonObj = jsonObj;
    }

    private boolean hasData(String dir) {
        return checkDirExists(dir, "path");
    }

    private boolean isNameExists(String name) {
        return checkDirExists(name, "name");
    }

    private boolean checkDirExists(String value, String key) {
        try {
            JSONArray dirs = dirsJsonObj.getJSONArray("dirs");
            for (int i = 0; i < dirs.length(); i++) {
                JSONObject dirObj = dirs.getJSONObject(i);
                if (dirObj.getString(key).equals(value)) {
                    return true;
                }
            }
        } catch (JSONException e) {
            logError(e);
        }
        return false;
    }

    private void removeOrDeleteDirectory(int position) {
        executorService.execute(() -> {
            try {
                JSONArray dirs = dirsJsonObj.getJSONArray("dirs");
                if (position >= 0 && position < dirs.length()) {
                    dirs.remove(position);
                    saveJsonObj(dirsJsonObj);
                    updateDirAdapterOnRemove(position);
                }
            } catch (JSONException e) {
                logError(e);
            }
        });
    }

    private void updateDirAdapterOnRemove(int position) {
        requireActivity().runOnUiThread(() -> {
            dirAdapter.notifyItemRemoved(position);
            dirAdapter.updateData(getDirList());
        });
    }

    public void logError(Exception e) {
        H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
    }

    private class DirectoryTextWatcher implements TextWatcher {
        private final TextInputLayout pathLay;
        private final MaterialButton addButton;

        public DirectoryTextWatcher(TextInputLayout pathLay, MaterialButton addButton) {
            this.pathLay = pathLay;
            this.addButton = addButton;
        }

        @Override
        public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
        }

        @Override
        public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
        }

        @Override
        public void afterTextChanged(Editable p1) {
            String value = p1.toString();
            if (value.matches("(/storage/emulated/0|/sdcard|/mnt/sdcard).*")) {
                pathLay.setErrorEnabled(false);
                addButton.setEnabled(true);
            } else {
                pathLay.setError(getString(org.koishi.launcher.h2co3.library.R.string.ver_input_hint));
                addButton.setEnabled(false);
            }
        }
    }

    private List<String> getDirList() {
        List<String> updatedList = new ArrayList<>();
        try {
            JSONArray dirs = dirsJsonObj.getJSONArray("dirs");
            for (int i = 0; i < dirs.length(); i++) {
                updatedList.add(dirs.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            logError(e);
        }
        return updatedList;
    }

    public void updateVerList(String path) {
        executorService.execute(() -> {
            List<String> files = getVerList(path);
            requireActivity().runOnUiThread(() -> {
                verRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                verAdapter = new MCVersionListAdapter(requireActivity(), files, this, gameHelper, path);
                verRecyclerView.setAdapter(verAdapter);
                verAdapter.updatePath(path);
            });
        });
    }

    public List<String> getVerList(String path) {
        File directory = new File(path);
        if (!directory.isDirectory()) {
            return Collections.emptyList();
        }
        String[] fileList = directory.list();
        if (fileList == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(fileList)
                .sorted(Collator.getInstance(Locale.CHINA)::compare)
                .collect(Collectors.toList());
    }
}
