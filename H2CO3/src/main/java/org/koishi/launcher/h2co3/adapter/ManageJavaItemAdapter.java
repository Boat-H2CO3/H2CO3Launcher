package org.koishi.launcher.h2co3.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.game.JavaVersion;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;

import java.io.File;
import java.util.List;

public class ManageJavaItemAdapter extends RecyclerView.Adapter<ManageJavaItemAdapter.ViewHolder> {
    private final Context context;
    private final List<JavaVersion> versions;
    private final ActionCallback action;

    public ManageJavaItemAdapter(Context context, List<JavaVersion> versions, ActionCallback action) {
        this.context = context;
        this.versions = versions;
        this.action = action;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_java, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JavaVersion data = versions.get(position);
        holder.javaName.setText(data.getName());
        holder.javaVersion.setText(data.getVersionName());
        if (new File(H2CO3LauncherTools.JAVA_PATH, data.getName()).toPath().resolve("version").toFile().exists()) {
            holder.delete.setVisibility(View.INVISIBLE);
            holder.javaName.setText(data.getName() + " (" + context.getString(R.string.internal) + ")");
        } else {
            holder.delete.setOnClickListener(v -> action.invoke(data, true));
        }
        holder.itemView.setOnClickListener(v -> action.invoke(data, false));
    }

    @Override
    public int getItemCount() {
        return versions.size();
    }

    public interface ActionCallback {
        void invoke(JavaVersion javaVersion, boolean isDelete);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView javaName;
        TextView javaVersion;
        View delete;

        public ViewHolder(View view) {
            super(view);
            javaName = view.findViewById(R.id.java_name);
            javaVersion = view.findViewById(R.id.java_version);
            delete = view.findViewById(R.id.delete);
        }
    }
}