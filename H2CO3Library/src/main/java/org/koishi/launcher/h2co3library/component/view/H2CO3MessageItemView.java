package org.koishi.launcher.h2co3library.component.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;

import org.koishi.launcher.h2co3core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3library.R;


public class H2CO3MessageItemView extends MaterialCardView {

    private H2CO3MessageManager.NotificationItem.Type type;
    private AttributeSet attributeSet;
    private int colorDebug;
    private int colorWarning;
    private int colorInfo;
    private int colorError;

    public H2CO3MessageItemView(@NonNull Context context) {
        super(context);
        this.attributeSet = null;
        init();
    }

    public H2CO3MessageItemView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attributeSet = attrs;
        init();
    }

    public H2CO3MessageItemView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attributeSet = attrs;
        init();
    }

    public H2CO3MessageItemView(@NonNull Context context, AttributeSet attrs, H2CO3MessageManager.NotificationItem.Type type) {
        super(context, attrs);
        this.type = type;
        init();
    }

    public void setType(H2CO3MessageManager.NotificationItem.Type type) {
        this.type = type;
    }

    public void init() {
        TypedArray ta = null;
        try {
            ta = getContext().obtainStyledAttributes(attributeSet, R.styleable.H2CO3MessageItemView);
            colorDebug = ta.getColor(R.styleable.H2CO3MessageItemView_h2co3_color_message_debug, Color.BLACK);
            colorWarning = ta.getColor(R.styleable.H2CO3MessageItemView_h2co3_color_message_warning, Color.BLACK);
            colorInfo = ta.getColor(R.styleable.H2CO3MessageItemView_h2co3_color_message_info, Color.BLACK);
            colorError = ta.getColor(R.styleable.H2CO3MessageItemView_h2co3_color_message_error, Color.BLACK);
        }catch (Exception e) {
            Log.e("H2CO3MessageItemView", "Error initializing attributes", e);
        } finally {
            if (ta != null) {
                ta.recycle();
            }
        }
    }

    public void updateBackgroundColor() {
        switch (type) {
            case DEBUG:
                setCardBackgroundColor(colorDebug);
                break;
            case WARNING:
                setCardBackgroundColor(colorWarning);
                break;
            case INFO:
                setCardBackgroundColor(colorInfo);
                break;
            case ERROR:
                setCardBackgroundColor(colorError);
                break;
            default:
                setCardBackgroundColor(Color.BLACK);
                break;
        }
    }
}