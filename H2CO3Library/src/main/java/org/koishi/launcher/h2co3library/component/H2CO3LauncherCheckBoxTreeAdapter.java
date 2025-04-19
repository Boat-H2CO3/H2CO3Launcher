package org.koishi.launcher.h2co3library.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleBooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3library.R;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherCheckBox;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

public class H2CO3LauncherCheckBoxTreeAdapter<T> extends H2CO3LauncherAdapter {

    private final ObservableList<H2CO3LauncherCheckBoxTreeItem<T>> list;

    private final SimpleBooleanProperty checkHeightProperty = new SimpleBooleanProperty(false);

    public H2CO3LauncherCheckBoxTreeAdapter(Context context, ObservableList<H2CO3LauncherCheckBoxTreeItem<T>> list) {
        super(context);
        this.list = list;
    }

    public static int getListViewHeight(H2CO3LauncherCheckBoxTreeItem<?> item, int splitSize, int baseHeight) {
        int count = getSubItemCount(item);
        return (baseHeight * count) + (splitSize * (count - 1));
    }

    public static int getSubItemCount(H2CO3LauncherCheckBoxTreeItem<?> item) {
        int count = item.isExpanded() ? item.getSubItem().size() : 0;
        if (item.isExpanded()) {
            for (H2CO3LauncherCheckBoxTreeItem<?> subItem : item.getSubItem()) {
                count += getSubItemCount(subItem);
            }
        }
        return count;
    }

    public SimpleBooleanProperty checkHeightProperty() {
        return checkHeightProperty;
    }

    public boolean isCheckHeight() {
        return checkHeightProperty.get();
    }

    public void setCheckHeight(boolean checkHeight) {
        checkHeightProperty.set(checkHeight);
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
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_check_box_tree, null);
            viewHolder.main = view.findViewById(R.id.main);
            viewHolder.switchView = view.findViewById(R.id.switch_view);
            viewHolder.checkBox = view.findViewById(R.id.check);
            viewHolder.textView = view.findViewById(R.id.text);
            viewHolder.comment = view.findViewById(R.id.comment);
            viewHolder.listView = view.findViewById(R.id.list);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        H2CO3LauncherCheckBoxTreeItem<T> item = list.get(i);
        viewHolder.switchView.setVisibility(item.getSubItem().size() == 0 ? View.INVISIBLE : View.VISIBLE);
        viewHolder.switchView.imageProperty().bind(Bindings.createObjectBinding(() -> item.isExpanded() ? getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_down_24) : getContext().getDrawable(R.drawable.ic_baseline_arrow_right_24), item.expandedProperty()));
        viewHolder.switchView.setOnClickListener(v -> item.setExpanded(!item.isExpanded()));
        viewHolder.checkBox.addCheckedChangeListener();
        viewHolder.checkBox.checkProperty().bindBidirectional(item.selectedProperty());
        viewHolder.checkBox.indeterminateProperty().bindBidirectional(item.indeterminateProperty());
        viewHolder.textView.setText(item.getText());
        viewHolder.comment.setVisibility(item.getComment() == null ? View.GONE : View.VISIBLE);
        if (item.getComment() != null)
            viewHolder.comment.setText(item.getComment());
        viewHolder.listView.setVisibility((item.isExpanded() && item.getSubItem().size() > 0) ? View.VISIBLE : View.GONE);
        item.expandedProperty().addListener(observable -> viewHolder.listView.setVisibility((item.isExpanded() && item.getSubItem().size() > 0) ? View.VISIBLE : View.GONE));
        if (item.getSubItem().size() > 0) {
            viewHolder.main.post(() -> {
                viewHolder.listView.setAdapter(new H2CO3LauncherCheckBoxTreeAdapter<>(getContext(), item.getSubItem()));
                ViewGroup.LayoutParams layoutParams = viewHolder.listView.getLayoutParams();
                new Thread(() -> {
                    layoutParams.height = getListViewHeight(item, viewHolder.listView.getDividerHeight(), viewHolder.main.getMeasuredHeight());
                    Schedulers.androidUIThread().execute(() -> viewHolder.listView.setLayoutParams(layoutParams));
                }).start();
                item.expandedProperty().addListener(observable -> setCheckHeight(true));
                ((H2CO3LauncherCheckBoxTreeAdapter<?>) viewHolder.listView.getAdapter()).checkHeightProperty().addListener(observable -> {
                    if (((H2CO3LauncherCheckBoxTreeAdapter<?>) viewHolder.listView.getAdapter()).isCheckHeight()) {
                        ViewGroup.LayoutParams lp = viewHolder.listView.getLayoutParams();
                        new Thread(() -> {
                            lp.height = getListViewHeight(item, viewHolder.listView.getDividerHeight(), viewHolder.main.getMeasuredHeight());
                            Schedulers.androidUIThread().execute(() -> viewHolder.listView.setLayoutParams(lp));
                        }).start();
                        ((H2CO3LauncherCheckBoxTreeAdapter<?>) viewHolder.listView.getAdapter()).setCheckHeight(false);
                        setCheckHeight(true);
                    }
                });
            });
        }
        return view;
    }

    private static class ViewHolder {
        H2CO3LauncherLinearLayout main;
        H2CO3LauncherImageButton switchView;
        H2CO3LauncherCheckBox checkBox;
        H2CO3LauncherTextView textView;
        H2CO3LauncherTextView comment;
        ListView listView;
    }
}
