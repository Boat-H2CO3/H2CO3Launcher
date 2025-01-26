package org.koishi.launcher.h2co3.ui.version;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;

import org.koishi.launcher.h2co3.util.AnimUtil;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.mod.ModManager;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;

import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherRadioButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.util.ArrayList;

public class VersionListAdapter extends H2CO3LauncherAdapter {

    private final ArrayList<VersionListItem> list;

    public VersionListAdapter(Context context, ArrayList<VersionListItem> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_version, null);
            viewHolder.radioButton = view.findViewById(R.id.radio);
            viewHolder.icon = view.findViewById(R.id.icon);
            viewHolder.title = view.findViewById(R.id.title);
            viewHolder.tag = view.findViewById(R.id.tag);
            viewHolder.subtitle = view.findViewById(R.id.subtitle);
            viewHolder.delete = view.findViewById(R.id.delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        VersionListItem versionListItem = list.get(i);
        viewHolder.radioButton.checkProperty().unbind();
        viewHolder.radioButton.checkProperty().bind(versionListItem.selectedProperty());
        viewHolder.icon.setBackground(versionListItem.getDrawable());
        viewHolder.title.setText(versionListItem.getVersion());
        viewHolder.tag.setVisibility(versionListItem.getTag() == null ? View.GONE : View.VISIBLE);
        if (versionListItem.getTag() != null) {
            viewHolder.tag.setText(versionListItem.getTag());
        }
        viewHolder.subtitle.setText(versionListItem.getLibraries());
        viewHolder.radioButton.setOnClickListener(view1 -> versionListItem.getProfile().setSelectedVersion(versionListItem.getVersion()));
        viewHolder.delete.setOnClickListener(view1 -> Versions.deleteVersion(getContext(), versionListItem.getProfile(), versionListItem.getVersion()));
        viewHolder.subtitle.setTag(i);
        Task.supplyAsync(() -> {
            ModManager modManager = versionListItem.getProfile().getRepository().getModManager(versionListItem.getVersion());
            return modManager.getMods().size();
        }).whenComplete(Schedulers.androidUIThread(), (result, exception) -> {
            int modCount = exception == null ? result : 0;
            if (((int) viewHolder.subtitle.getTag()) == i) {
                viewHolder.subtitle.setText(String.format("%s  Mods:%d", viewHolder.subtitle.getText(), modCount));
            }
        }).start();
        AnimUtil.playTranslationX(view,30L, -100f, 0f).start();
        return view;
    }

    static class ViewHolder {
        H2CO3LauncherRadioButton radioButton;
        AppCompatImageView icon;
        H2CO3LauncherTextView title;
        H2CO3LauncherTextView tag;
        H2CO3LauncherTextView subtitle;
        H2CO3LauncherImageButton delete;
    }
}
