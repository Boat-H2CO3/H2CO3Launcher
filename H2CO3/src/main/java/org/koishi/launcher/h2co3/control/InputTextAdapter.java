package org.koishi.launcher.h2co3.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.data.QuickInputTexts;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

public class InputTextAdapter extends H2CO3LauncherAdapter {

    private final ObservableList<String> list;
    private final Callback callback;

    public InputTextAdapter(Context context, ObservableList<String> list, Callback callback) {
        super(context);
        this.list = list;
        this.callback = callback;
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
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_input_text, null);
            viewHolder.parent = view.findViewById(R.id.parent);
            viewHolder.textView = view.findViewById(R.id.text);
            viewHolder.delete = view.findViewById(R.id.delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String inputText = list.get(i);
        viewHolder.textView.setText(inputText);
        viewHolder.parent.setOnClickListener(v -> callback.onTextInput(inputText));
        viewHolder.delete.setOnClickListener(v -> {
            QuickInputTexts.removeInputText(inputText);
            notifyDataSetChanged();
        });
        return view;
    }

    public interface Callback {
        void onTextInput(String string);
    }

    static class ViewHolder {
        H2CO3LauncherLinearLayout parent;
        H2CO3LauncherTextView textView;
        H2CO3LauncherImageButton delete;
    }
}
