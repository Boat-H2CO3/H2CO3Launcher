package org.koishi.launcher.h2co3.ui.version;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.mod.ModManager;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.H2CO3RecycleAdapter;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.util.ArrayList;

public class VersionListAdapter extends H2CO3RecycleAdapter<VersionListItem> {

    private boolean isItemClickable = true;

    public VersionListAdapter(Context context, ArrayList<VersionListItem> list) {
        super(list, context);
    }

    @Override
    protected void bindData(BaseViewHolder holder, int position) {
        VersionListItem versionListItem = data.get(position);

        ViewGroup.LayoutParams layoutParams = holder.getView(R.id.parent).getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.getView(R.id.parent).setLayoutParams(layoutParams);

        ((MaterialCardView) holder.getView(R.id.parent)).setStrokeWidth(versionListItem.selectedProperty().get() ? 10 : 0);
        holder.getView(R.id.icon).setBackground(versionListItem.getDrawable());
        ((H2CO3LauncherTextView) holder.getView(R.id.title)).setText(versionListItem.getVersion());

        H2CO3LauncherTextView tagView = (H2CO3LauncherTextView) holder.getView(R.id.tag);
        tagView.setVisibility(versionListItem.getTag() == null ? View.GONE : View.VISIBLE);
        if (versionListItem.getTag() != null) {
            tagView.setText(versionListItem.getTag());
        }

        H2CO3LauncherTextView subtitleView = (H2CO3LauncherTextView) holder.getView(R.id.subtitle);
        subtitleView.setText(versionListItem.getLibraries());

        holder.getView(R.id.parent).setOnClickListener(view -> {
            if (isItemClickable) {
                isItemClickable = false;
                versionListItem.getProfile().setSelectedVersion(versionListItem.getVersion());
                for (int i = 0; i < getItemCount(); i++) {
                    notifyItemChanged(i);
                }
                holder.getView(R.id.parent).postDelayed(() -> isItemClickable = true, 200);
            }
        });

        holder.getView(R.id.delete).setOnClickListener(view -> {
            Versions.deleteVersion(mContext, versionListItem.getProfile(), versionListItem.getVersion());
            updateData(data);
        });

        subtitleView.setTag(position);

        Task.supplyAsync(() -> {
            ModManager modManager = versionListItem.getProfile().getRepository().getModManager(versionListItem.getVersion());
            return modManager.getMods().size();
        }).whenComplete(Schedulers.androidUIThread(), (result, exception) -> {
            int modCount = exception == null ? result : 0;
            if (((int) subtitleView.getTag()) == position) {
                subtitleView.setText(String.format("%s  Mods:%d", subtitleView.getText(), modCount));
            }
        }).start();
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_version;
    }
}