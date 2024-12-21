package org.koishi.launcher.h2co3.controller.ckb.support;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

public class CkbThemeMarker {

    public final static int DESIGN_SIGNLE_FILL = 1;
    public final static int DESIGN_SIGNLE_RING = 2;
    public final static int DESIGN_DOUBLE_RING = 3;
    public final static int DESIGN_BLACK_SHADOW = 4;
    public final static String[] DESIGNS = new String[]{"1", "2", "3", "4"};
    private final static String TAG = "CkbThemeMaker";
    private final static int STROKE_WIDTH = 5;
    private final static int DRAWABLE_SIZE = 50;

    public static LayerDrawable getDesign(final CkbThemeRecorder recorder) {

        return switch (recorder.getDesignIndex()) {
            case DESIGN_SIGNLE_FILL -> createSingleFillDesign(recorder);
            case DESIGN_SIGNLE_RING -> createSingleRingDesign(recorder);
            case DESIGN_DOUBLE_RING -> createDoubleRingDesign(recorder);
            case DESIGN_BLACK_SHADOW -> createBlackShadowDesign(recorder);
            default -> null;
        };
    }

    private static LayerDrawable createSingleFillDesign(CkbThemeRecorder recorder) {
        int radiusSize = recorder.getCornerRadius();
        int mainColor = recorder.getColor(0);
        float[] outerR = new float[]{radiusSize, radiusSize, radiusSize, radiusSize, radiusSize, radiusSize, radiusSize, radiusSize};

        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(outerR, null, null));
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setColor(mainColor);

        return new LayerDrawable(new Drawable[]{shapeDrawable});
    }

    private static LayerDrawable createSingleRingDesign(CkbThemeRecorder recorder) {
        int radius = recorder.getCornerRadius();
        int mainColor = recorder.getColor(0);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(STROKE_WIDTH, mainColor);
        drawable.setCornerRadius(radius);
        drawable.setSize(DRAWABLE_SIZE, DRAWABLE_SIZE);

        return new LayerDrawable(new Drawable[]{drawable});
    }

    private static LayerDrawable createDoubleRingDesign(CkbThemeRecorder recorder) {
        int radius = recorder.getCornerRadius();
        int mainColor = recorder.getColor(0);

        GradientDrawable drawable1 = createGradientDrawable(radius, mainColor);
        GradientDrawable drawable2 = createGradientDrawable(radius, mainColor);

        LayerDrawable mainDrawable = new LayerDrawable(new Drawable[]{drawable1, drawable2});
        mainDrawable.setLayerInset(1, STROKE_WIDTH * 2, STROKE_WIDTH * 2, STROKE_WIDTH * 2, STROKE_WIDTH * 2);

        return mainDrawable;
    }

    private static LayerDrawable createBlackShadowDesign(CkbThemeRecorder recorder) {
        int radius = recorder.getCornerRadius();

        GradientDrawable shadowDrawable = new GradientDrawable();
        shadowDrawable.setShape(GradientDrawable.RECTANGLE);
        shadowDrawable.setColor(Color.BLACK);
        shadowDrawable.setCornerRadius(radius);
        shadowDrawable.setSize(DRAWABLE_SIZE, DRAWABLE_SIZE);

        GradientDrawable borderDrawable = createGradientDrawable(radius, Color.WHITE);

        LayerDrawable mainDrawable = new LayerDrawable(new Drawable[]{shadowDrawable, borderDrawable});
        mainDrawable.setLayerInset(1, 1, 1, 1, 1);

        return mainDrawable;
    }

    private static GradientDrawable createGradientDrawable(int radius, int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(STROKE_WIDTH, color);
        drawable.setCornerRadius(radius);
        drawable.setSize(DRAWABLE_SIZE, DRAWABLE_SIZE);
        return drawable;
    }
}