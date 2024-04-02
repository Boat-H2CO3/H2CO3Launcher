/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.dialog;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.launcher.utils.H2CO3GameHelper;
import org.koishi.launcher.h2co3.utils.download.DownloadItem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DownloadDialog extends MaterialAlertDialogBuilder {
    private static final int BUFFER_SIZE = 1024;
    private static final String DOWNLOAD_PATH = H2CO3GameHelper.getGameDirectory() + "/libraries";
    private static final String LIBRARY_URL_PREFIX = "https://libraries.minecraft.net/";
    private static final String LIBRARY_URL_REPLACE = "https://bmclapi2.bangbang93.com/maven/";

    private final Context context;
    private final RecyclerView recyclerView;
    private final List<DownloadItem> downloadItems;
    private String jsonString;
    private AlertDialog dialog;
    private DownloadTask downloadTask;

    public DownloadDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        this.downloadItems = new ArrayList<>();

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_download, null);
        this.recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        setView(view);
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString.replace(LIBRARY_URL_PREFIX, LIBRARY_URL_REPLACE);
    }

    @Override
    public AlertDialog create() {
        parseJsonString();

        DownloadAdapter adapter = new DownloadAdapter(context, downloadItems);
        recyclerView.setAdapter(adapter);

        int threadCount = getThreadCount();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        downloadTask = new DownloadTask(executorService, adapter);
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        dialog = super.create();
        dialog.setOnDismissListener(dialog -> {
            if (downloadTask != null) {
                downloadTask.cancel(true);
                executorService.shutdownNow();
            }
        });

        return dialog;
    }

    private void parseJsonString() {
        try {
            JSONObject rootJsonObject = new JSONObject(jsonString);
            JSONArray librariesArray = rootJsonObject.getJSONArray("libraries");
            JSONArray versionsArray = rootJsonObject.getJSONArray("versions");
            JSONArray assetsArray = rootJsonObject.getJSONArray("assets");

            // 处理库文件下载
            for (int i = 0; i < librariesArray.length(); i++) {
                // ... 保持原有的库文件解析逻辑 ...
            }

            // 处理版本信息下载
            for (int i = 0; i < versionsArray.length(); i++) {
                JSONObject versionObject = versionsArray.getJSONObject(i);
                String versionId = versionObject.getString("id");
                String versionUrl = versionObject.getString("url");
                String versionPath = "versions/" + versionId + "/" + versionId + ".jar";
                DownloadItem versionItem = new DownloadItem("Version: " + versionId, versionPath, versionUrl, -1);
                downloadItems.add(versionItem);
            }

            // 处理资源文件下载
            for (int i = 0; i < assetsArray.length(); i++) {
                JSONObject assetObject = assetsArray.getJSONObject(i);
                String hash = assetObject.getString("hash");
                String path = assetObject.getString("path");
                String assetUrl = "https://resources.download.minecraft.net/" + path + "/" + hash;
                String assetPath = "assets/" + path + "/" + hash;
                DownloadItem assetItem = new DownloadItem("Asset: " + path, assetPath, assetUrl, -1);
                downloadItems.add(assetItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean shouldFilterLibrary(JSONObject library) throws JSONException {
        String name = library.getString("name");
        return name.contains("windows") || name.contains("macos");
    }

    private int getThreadCount() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return 5;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return 2;
                }
            }
        }
        return 1;
    }

    private static class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
        private final Context context;
        private final CopyOnWriteArrayList<DownloadItem> downloadItems;

        public DownloadAdapter(Context context, List<DownloadItem> downloadItems) {
            this.context = context;
            this.downloadItems = new CopyOnWriteArrayList<>(downloadItems);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_download, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position >= 0 && position < downloadItems.size()) {
                DownloadItem item = downloadItems.get(position);
                holder.bind(item);
            }
        }

        @Override
        public int getItemCount() {
            return downloadItems.size();
        }

        public void removeCompletedItems() {
            List<DownloadItem> itemsToRemove = new ArrayList<>();
            for (DownloadItem item : downloadItems) {
                File file = new File(DOWNLOAD_PATH + File.separator + item.getPath());
                if (item.getProgress() == 100 && file.exists() && file.length() == item.getSize()) {
                    itemsToRemove.add(item);
                }
            }
            for (DownloadItem itemToRemove : itemsToRemove) {
                int index = downloadItems.indexOf(itemToRemove);
                if (index != -1) {
                    downloadItems.remove(itemToRemove);
                    notifyItemRemoved(index);
                }
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView fileNameText;
            LinearProgressIndicator progress;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                fileNameText = itemView.findViewById(R.id.fileNameText);
                progress = itemView.findViewById(R.id.fileProgress);
            }

            public void bind(DownloadItem item) {
                fileNameText.setText(item.getName());
                progress.setProgress(item.getProgress());
            }
        }
    }

    private class DownloadTask extends AsyncTask<Void, Integer, Void> {
        private final ExecutorService executorService;
        private final DownloadAdapter adapter;

        public DownloadTask(ExecutorService executorService, DownloadAdapter adapter) {
            this.executorService = executorService;
            this.adapter = adapter;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<Future<?>> futures = new ArrayList<>(); // 修改为Future<?>以接收任何类型的Future
            for (DownloadItem item : downloadItems) {
                // 根据下载项的类型执行不同的下载任务
                if (item.getPath().startsWith("libraries/")) {
                    futures.add(executorService.submit(() -> downloadLibrary(item)));
                } else if (item.getPath().startsWith("versions/")) {
                    futures.add(executorService.submit(() -> downloadVersion(item)));
                } else if (item.getPath().startsWith("assets/")) {
                    futures.add(executorService.submit(() -> downloadAsset(item)));
                }
            }

            // 等待所有下载任务完成
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    Log.e("DownloadTask", "Error executing download task", e);
                    break;
                }
            }

            return null;
        }

        private void downloadLibrary(DownloadItem item) {
            URL url = null;
            InputStream input = null;
            OutputStream output = null;

            try {
                url = new URL(item.getUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                File file = new File(DOWNLOAD_PATH + File.separator + item.getPath());
                if (!file.exists() && !file.createNewFile()) {
                    throw new IOException("Failed to create file for library download.");
                }

                input = new BufferedInputStream(connection.getInputStream());
                output = new BufferedOutputStream(new FileOutputStream(file));

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                long totalBytesRead = 0;
                while ((bytesRead = input.read(buffer)) != -1) {
                    totalBytesRead += bytesRead;
                    int currentProgress = (int) ((totalBytesRead * 100) / item.getSize());
                    item.setProgress(currentProgress); // 使用DownloadItem的setProgress方法更新进度
                    output.write(buffer, 0, bytesRead);
                }
                item.setProgress(100); // 下载完成后设置进度为100%
            } catch (IOException e) {
                Log.e("DownloadTask", "Error downloading library file", e);
                // 这里可以添加错误处理逻辑，例如更新UI来反映下载失败
            } finally {
                // 关闭流资源
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (url != null) {
                    connection.disconnect(); // 断开连接
                }
            }
        }

        private void downloadVersion(DownloadItem item) {
            // 版本文件下载逻辑与库文件类似，可以复用downloadLibrary方法
            downloadLibrary(item);
        }

        private void downloadAsset(DownloadItem item) {
            // 资源文件下载逻辑与库文件类似，可以复用downloadLibrary方法
            downloadLibrary(item);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            adapter.notifyItemChanged(values[0]);
            adapter.removeCompletedItems();
        }

        // 在 DownloadTask 类中的某个合适的地方调用这个方法
        protected void publishProgress(Integer progress) {
            if (progress != null) {
                this.publishProgress(progress);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.removeCompletedItems();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            showErrorDialogOnUIThread("下载失败");
        }

        private boolean isFileValid(DownloadItem item) {
            File file = new File(DOWNLOAD_PATH + "/" + item.getPath());
            return file.exists() && file.length() == item.getSize();
        }

        private void createDirectoryForItem(DownloadItem item) throws IOException {
            int lastIndex = item.getPath().lastIndexOf("/");
            Path folderPath = Paths.get(DOWNLOAD_PATH, item.getPath().substring(0, lastIndex + 1));
            Files.createDirectories(folderPath);
        }

        private AlertDialog dialog;

        private void showErrorDialogOnUIThread(String message) {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (dialog == null || !dialog.isShowing()) {
                    dialog = new AlertDialog.Builder(context)
                            .setTitle("错误")
                            .setMessage(message)
                            .setPositiveButton("确定", null)
                            .create();
                }
                dialog.show();
            });
        }
    }
}