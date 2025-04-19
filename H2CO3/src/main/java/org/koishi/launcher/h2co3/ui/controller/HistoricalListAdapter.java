package org.koishi.launcher.h2co3.ui.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.download.ControllerVersion;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

import java.util.ArrayList;

public class HistoricalListAdapter extends H2CO3LauncherAdapter {

    private ArrayList<ControllerVersion.VersionInfo> list;
    private Callback callback;

    public HistoricalListAdapter(Context context, ArrayList<ControllerVersion.VersionInfo> list, Callback callback) {
        super(context);
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

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = new H2CO3LauncherLinearLayout(getContext());
            viewHolder.parent = new H2CO3LauncherLinearLayout(getContext());
            viewHolder.version = new H2CO3LauncherTextView(getContext());
            ((H2CO3LauncherLinearLayout) view).addView(viewHolder.parent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            int padding = ConvertUtils.dip2px(getContext(), 10);
            viewHolder.parent.setPadding(padding, padding, padding, padding);
            viewHolder.parent.setBackground(getContext().getDrawable(R.drawable.bg_container_transparent_clickable));
            viewHolder.parent.addView(viewHolder.version, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            viewHolder.version.setSingleLine(true);
            viewHolder.version.setTextColor(Color.GRAY);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.parent.setOnClickListener(v -> callback.onSelect(list.get(i)));
        viewHolder.version.setText(list.get(i).getVersionName());
        return view;
    }

    public interface Callback {
        void onSelect(ControllerVersion.VersionInfo versionInfo);
    }

    private static class ViewHolder {
        H2CO3LauncherLinearLayout parent;
        H2CO3LauncherTextView version;
    }
}
