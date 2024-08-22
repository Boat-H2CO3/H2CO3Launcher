/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

/*
 * //
 * // Created by cainiaohh on 2024-03-16.
 * //
 */

package org.koishi.launcher.h2co3.resources.component;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.koishi.launcher.h2co3.resources.R;
import org.koishi.launcher.h2co3.resources.component.adapter.H2CO3RecycleAdapter;

import java.util.ArrayList;
import java.util.List;

public class LogcatView extends RecyclerView {

    private LogcatAdapter adapter;

    public LogcatView(Context context) {
        super(context);
        init();
    }

    public LogcatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LogcatView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        adapter = new LogcatAdapter();
        setAdapter(adapter);
        setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void addLog(String log) {
        adapter.addLog(log);
        scrollToPosition(adapter.getItemCount() - 1);
    }

    public void clearLogs() {
        adapter.clearLogs();
    }

    private class LogcatAdapter extends H2CO3RecycleAdapter<String> {

        public LogcatAdapter() {
            super(new ArrayList<>(), getContext());
        }

        public void addLog(String log) {
            data.add(log);
            notifyItemInserted(data.size() - 1);
        }

        public void clearLogs() {
            data.clear();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
            return new LogViewHolder(view);
        }

        @Override
        protected void bindData(BaseViewHolder holder, int position) {
            String log = data.get(position);
            LogViewHolder logViewHolder = (LogViewHolder) holder;
            logViewHolder.logTextView.setText(log);
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_log;
        }

        private class LogViewHolder extends BaseViewHolder implements View.OnLongClickListener {

            private final TextView logTextView;

            public LogViewHolder(@NonNull View itemView) {
                super(itemView);
                logTextView = itemView.findViewById(R.id.logTextView);
                logTextView.setOnLongClickListener(this);
            }

            @Override
            public boolean onLongClick(View v) {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String log = data.get(position);
                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Log", log);
                    clipboard.setPrimaryClip(clip);
                    return true;
                }
                return false;
            }
        }
    }
}