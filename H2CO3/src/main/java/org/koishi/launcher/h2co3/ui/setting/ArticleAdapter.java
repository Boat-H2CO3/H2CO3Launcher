package org.koishi.launcher.h2co3.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.util.ArrayList;

public class ArticleAdapter extends H2CO3LauncherAdapter {

    private final ArrayList<DocIndex.Item> list;

    public ArticleAdapter(Context context, ArrayList<DocIndex.Item> list) {
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_article, null);
            viewHolder.parent = view.findViewById(R.id.parent);
            viewHolder.title = view.findViewById(R.id.title);
            viewHolder.subtitle = view.findViewById(R.id.subtitle);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        DocIndex.Item item = list.get(i);
        viewHolder.parent.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://h2co3Launcher-team.github.io/pages/documentation.html?path=" + item.getPath());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
        });
        viewHolder.title.setText(item.getTitle());
        viewHolder.subtitle.setText(item.getSubtitle());
        return view;
    }

    private static class ViewHolder {
        H2CO3LauncherLinearLayout parent;
        H2CO3LauncherTextView title;
        H2CO3LauncherTextView subtitle;
    }
}
