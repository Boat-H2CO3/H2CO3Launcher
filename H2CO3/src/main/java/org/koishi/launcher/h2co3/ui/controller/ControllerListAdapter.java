package org.koishi.launcher.h2co3.ui.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import org.koishi.launcher.h2co3.util.AnimUtil;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.download.ControllerCategory;
import org.koishi.launcher.h2co3.control.download.ControllerIndex;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;

import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.util.ArrayList;
import java.util.List;

public class ControllerListAdapter extends H2CO3LauncherAdapter {

    private final String repoUrl;
    private final ArrayList<ControllerCategory> categories;
    private final ArrayList<ControllerIndex> list;
    private final Callback callback;

    public ControllerListAdapter(Context context, int source, ArrayList<ControllerCategory> categories, ArrayList<ControllerIndex> list, Callback callback) {
        super(context);
        this.repoUrl = source == 0 ? ControllerRepoPage.CONTROLLER_GITHUB : ControllerRepoPage.CONTROLLER_GIT_CN;
        this.categories = categories;
        this.list = list;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_remote_version, null);
            viewHolder.parent = view.findViewById(R.id.parent);
            viewHolder.icon = view.findViewById(R.id.icon);
            viewHolder.name = view.findViewById(R.id.version);
            viewHolder.tag = view.findViewById(R.id.tag);
            viewHolder.description = view.findViewById(R.id.date);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ControllerIndex index = list.get(i);
        viewHolder.parent.setOnClickListener(v -> callback.onItemSelect(index));
        viewHolder.icon.setImageDrawable(null);
        String iconUrl = repoUrl + "repo_json/" + index.getId() + "/icon.png";
        Glide.with(getContext()).load(iconUrl).into(viewHolder.icon);
        viewHolder.name.setText(index.getName());
        List<String> categories = ControllerCategory.getLocaledCategories(getContext(), this.categories, index.getCategories());
        StringBuilder stringBuilder = new StringBuilder();
        categories.forEach(it -> stringBuilder.append(it).append("   "));
        String tag = StringUtils.removeSuffix(stringBuilder.toString(), "   ");
        viewHolder.tag.setText(tag);
        viewHolder.description.setText(index.getIntroduction());
        AnimUtil.playTranslationX(view, 30L, -100f, 0f).start();
        return view;
    }

    public interface Callback {
        void onItemSelect(ControllerIndex mod);
    }

    private static class ViewHolder {
        H2CO3LauncherLinearLayout parent;
        H2CO3LauncherImageView icon;
        H2CO3LauncherTextView name;
        H2CO3LauncherTextView tag;
        H2CO3LauncherTextView description;
    }
}
