package org.koishi.launcher.h2co3library.component.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import rikka.widget.borderview.BorderRecyclerView;

public class H2CO3RecyclerView extends BorderRecyclerView {


    public H2CO3RecyclerView(Context context) {
        super(context);
        setScrollbar(context);
    }

    public H2CO3RecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setScrollbar(context);
    }

    public H2CO3RecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScrollbar(context);
    }

    private void setScrollbar(Context context) {
        this.setVerticalScrollBarEnabled(true);
        this.setScrollBarSize(0);
        this.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

}