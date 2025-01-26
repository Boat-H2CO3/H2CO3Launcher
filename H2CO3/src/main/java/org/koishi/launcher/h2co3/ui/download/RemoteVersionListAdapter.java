package org.koishi.launcher.h2co3.ui.download;

import static org.koishi.launcher.h2co3core.util.LocaleUtils.formatDateTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.util.AnimUtil;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3core.download.RemoteVersion;
import org.koishi.launcher.h2co3core.download.fabric.FabricAPIRemoteVersion;
import org.koishi.launcher.h2co3core.download.fabric.FabricRemoteVersion;
import org.koishi.launcher.h2co3core.download.forge.ForgeRemoteVersion;
import org.koishi.launcher.h2co3core.download.game.GameRemoteVersion;
import org.koishi.launcher.h2co3core.download.liteloader.LiteLoaderRemoteVersion;
import org.koishi.launcher.h2co3core.download.neoforge.NeoForgeRemoteVersion;
import org.koishi.launcher.h2co3core.download.optifine.OptiFineRemoteVersion;
import org.koishi.launcher.h2co3core.download.quilt.QuiltAPIRemoteVersion;
import org.koishi.launcher.h2co3core.download.quilt.QuiltRemoteVersion;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;

import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.util.ArrayList;
import java.util.List;

public class RemoteVersionListAdapter extends H2CO3LauncherAdapter {

    private final ArrayList<RemoteVersion> list;
    private final OnRemoteVersionSelectListener listener;

    public RemoteVersionListAdapter(Context context, ArrayList<RemoteVersion> list, OnRemoteVersionSelectListener listener) {
        super(context);
        this.list = list;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_remote_version, null);
            viewHolder.parent = view.findViewById(R.id.parent);
            viewHolder.icon = view.findViewById(R.id.icon);
            viewHolder.version = view.findViewById(R.id.version);
            viewHolder.tag = view.findViewById(R.id.tag);
            viewHolder.date = view.findViewById(R.id.date);
            viewHolder.wiki = view.findViewById(R.id.wiki);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        RemoteVersion remoteVersion = list.get(i);
        viewHolder.parent.setOnClickListener(view1 -> listener.onSelect(remoteVersion));
        viewHolder.icon.setBackground(getIcon(remoteVersion));
        viewHolder.version.setText(remoteVersion.getSelfVersion());
        viewHolder.tag.setBackground(getContext().getDrawable(R.drawable.bg_container_white));
        viewHolder.tag.setAutoBackgroundTint(true);
        viewHolder.tag.setText(getTag(remoteVersion));
        viewHolder.date.setVisibility(remoteVersion.getReleaseDate() == null ? View.GONE : View.VISIBLE);
        viewHolder.date.setText(remoteVersion.getReleaseDate() == null ? "" : formatDateTime(getContext(), remoteVersion.getReleaseDate()));
        if (remoteVersion instanceof GameRemoteVersion && (remoteVersion.getVersionType() == RemoteVersion.Type.RELEASE || remoteVersion.getVersionType() == RemoteVersion.Type.SNAPSHOT)) {
            viewHolder.wiki.setVisibility(View.VISIBLE);
            viewHolder.wiki.setOnClickListener(v -> AndroidUtils.openLink(getContext(), remoteVersion.getVersionType() == RemoteVersion.Type.RELEASE ? String.format(getContext().getString(R.string.wiki_release), remoteVersion.getGameVersion()) : String.format(getContext().getString(R.string.wiki_snapshot), remoteVersion.getGameVersion())));
        } else {
            viewHolder.wiki.setVisibility(View.GONE);
        }
        AnimUtil.playTranslationX(view, 30L, -100f, 0f).start();
        return view;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getIcon(RemoteVersion remoteVersion) {
        if (remoteVersion instanceof LiteLoaderRemoteVersion)
            return getContext().getDrawable(R.drawable.img_chicken);
        else if (remoteVersion instanceof OptiFineRemoteVersion)
            return getContext().getDrawable(R.drawable.img_optifine);
        else if (remoteVersion instanceof ForgeRemoteVersion)
            return getContext().getDrawable(R.drawable.img_forge);
        else if (remoteVersion instanceof NeoForgeRemoteVersion)
            return getContext().getDrawable(R.drawable.img_neoforge);
        else if (remoteVersion instanceof FabricRemoteVersion || remoteVersion instanceof FabricAPIRemoteVersion)
            return getContext().getDrawable(R.drawable.img_fabric);
        else if (remoteVersion instanceof QuiltRemoteVersion || remoteVersion instanceof QuiltAPIRemoteVersion)
            return getContext().getDrawable(R.drawable.img_quilt);
        else if (remoteVersion instanceof GameRemoteVersion) {
            switch (remoteVersion.getVersionType()) {
                case RELEASE:
                    return getContext().getDrawable(R.drawable.img_grass);
                case SNAPSHOT:
                    return getContext().getDrawable(R.drawable.img_command);
                default:
                    return getContext().getDrawable(R.drawable.img_craft_table);
            }
        } else {
            return getContext().getDrawable(R.drawable.img_grass);
        }
    }

    private String getTag(RemoteVersion remoteVersion) {
        if (remoteVersion instanceof GameRemoteVersion) {
            switch (remoteVersion.getVersionType()) {
                case RELEASE:
                    return getContext().getString(R.string.version_game_release);
                case SNAPSHOT:
                    return getContext().getString(R.string.version_game_snapshot);
                default:
                    return getContext().getString(R.string.version_game_old);
            }
        } else {
            return remoteVersion.getGameVersion();
        }
    }

    public List<RemoteVersion> getList() {
        return list;
    }

    public interface OnRemoteVersionSelectListener {
        void onSelect(RemoteVersion remoteVersion);
    }

    private static class ViewHolder {
        H2CO3LauncherLinearLayout parent;
        H2CO3LauncherImageView icon;
        H2CO3LauncherTextView version;
        H2CO3LauncherTextView tag;
        H2CO3LauncherTextView date;
        H2CO3LauncherImageButton wiki;
    }
}
