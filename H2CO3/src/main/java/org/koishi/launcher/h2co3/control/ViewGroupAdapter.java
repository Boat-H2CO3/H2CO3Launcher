package org.koishi.launcher.h2co3.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.util.AnimUtil;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.data.ControlViewGroup;
import org.koishi.launcher.h2co3core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;

import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherCheckBox;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.util.Collections;
import java.util.stream.Collectors;

public class ViewGroupAdapter extends H2CO3LauncherAdapter {

    private final ObservableList<ControlViewGroup> list;
    private final GameMenu menu;
    private final boolean select;

    private final ObservableList<ControlViewGroup> selectedGroups;
    private ObservableList<String> selectedIds;

    public ViewGroupAdapter(Context context, ObservableList<ControlViewGroup> list, GameMenu menu, boolean select, ObservableList<ControlViewGroup> selectedGroups) {
        super(context);
        this.list = list;
        this.menu = menu;
        this.select = select;
        this.selectedGroups = selectedGroups;

        this.selectedIds = FXCollections.observableList(selectedGroups.stream().map(ControlViewGroup::getId).collect(Collectors.toList()));
        selectedGroups.addListener((InvalidationListener)  i -> selectedIds = FXCollections.observableList(selectedGroups.stream().map(ControlViewGroup::getId).collect(Collectors.toList())));
    }

    public ObservableList<ControlViewGroup> getSelectedGroups() {
        return selectedGroups;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_view_group, null);
            viewHolder.checkBox = view.findViewById(R.id.check);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.up = view.findViewById(R.id.up);
            viewHolder.down = view.findViewById(R.id.down);
            viewHolder.edit = view.findViewById(R.id.edit);
            viewHolder.delete = view.findViewById(R.id.delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ControlViewGroup group = list.get(i);
        viewHolder.name.setText(group.getName());
        if (select) {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.up.setVisibility(View.GONE);
            viewHolder.down.setVisibility(View.GONE);
            viewHolder.edit.setVisibility(View.GONE);
            viewHolder.delete.setVisibility(View.GONE);
        } else {
            viewHolder.checkBox.setVisibility(View.GONE);
            viewHolder.up.setVisibility(View.VISIBLE);
            viewHolder.down.setVisibility(View.VISIBLE);
            viewHolder.edit.setVisibility(View.VISIBLE);
            viewHolder.delete.setVisibility(View.VISIBLE);
        }
        viewHolder.checkBox.setOnCheckedChangeListener(null);
        viewHolder.checkBox.setChecked(selectedIds.contains(group.getId()));
        viewHolder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if (!selectedIds.contains(group.getId())) {
                    selectedGroups.add(group);
                }
            } else {
                selectedGroups.removeIf(it -> it.getId().equals(group.getId()));
            }
        });
        View.OnClickListener upDownListener = v -> {
            int pos = v == viewHolder.up ? i - 1 : i + 1;
            if (pos < 0 || pos > list.size() - 1) {
                return;
            }
            Collections.swap(list, i, pos);
            menu.getController().updateViewGroup(group);
            notifyDataSetChanged();
        };
        viewHolder.up.setOnClickListener(upDownListener);
        viewHolder.down.setOnClickListener(upDownListener);
        viewHolder.edit.setOnClickListener(v -> {
            EditViewGroupDialog dialog = new EditViewGroupDialog(getContext(), menu, group, (n, vi) -> {
                group.setName(n);
                group.setVisibility(vi);
                menu.getController().updateViewGroup(group);
                notifyDataSetChanged();
            });
            dialog.show();
        });
        viewHolder.delete.setOnClickListener(v -> {
            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
            builder.setMessage(getContext().getString(R.string.menu_control_view_group_delete));
            builder.setPositiveButton(() -> {
                menu.getController().removeViewGroup(group);
                notifyDataSetChanged();
            });
            builder.setNegativeButton(null);
            builder.create().show();
        });
        AnimUtil.playTranslationX(view,30L, -100f, 0f).start();
        return view;
    }

    static class ViewHolder {
        H2CO3LauncherCheckBox checkBox;
        H2CO3LauncherTextView name;
        H2CO3LauncherImageButton up;
        H2CO3LauncherImageButton down;
        H2CO3LauncherImageButton edit;
        H2CO3LauncherImageButton delete;
    }
}
