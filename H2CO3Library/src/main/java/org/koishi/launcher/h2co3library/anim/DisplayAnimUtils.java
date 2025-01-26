package org.koishi.launcher.h2co3library.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


public class DisplayAnimUtils {

    public static void showViewFromLeft(View view, boolean animation) {
        view.setVisibility(View.VISIBLE);
        if (animation) {
            Animation animation1 = AnimationUtils.makeInAnimation(view.getContext(), true);
            view.setAnimation(animation1);
        }
    }

    public static void hideViewToLeft(View view, boolean animation) {
        view.setVisibility(View.GONE);
        if (animation) {
            Animation animation1 = AnimationUtils.makeOutAnimation(view.getContext(), false);
            view.setAnimation(animation1);
        }
    }

    public static void showViewFromRight(View view, boolean animation) {
        view.setVisibility(View.VISIBLE);
        if (animation) {
            Animation animation1 = AnimationUtils.makeInAnimation(view.getContext(), false);
            view.setAnimation(animation1);
        }
    }

    public static void hideViewToRight(View view, boolean animation) {
        view.setVisibility(View.GONE);
        if (animation) {
            Animation animation1 = AnimationUtils.makeOutAnimation(view.getContext(), true);
            view.setAnimation(animation1);
        }
    }

    public static void showViewWithAnim(View view, int animId) {
        view.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), animId);
        view.setAnimation(animation);
    }

    public static void hideViewWithAnim(View view, int animId) {
        if (view != null) {
            view.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(view.getContext(), animId);
            view.setAnimation(animation);
        }
    }
}
