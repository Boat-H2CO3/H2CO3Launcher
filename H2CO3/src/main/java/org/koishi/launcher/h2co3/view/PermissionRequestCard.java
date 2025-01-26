package org.koishi.launcher.h2co3.view;

public class PermissionRequestCard {
    private int iconRes;
    private int titleRes;
    private int descriptionRes;

    public PermissionRequestCard(int iconRes, int titleRes, int descriptionRes) {
        this.iconRes = iconRes;
        this.titleRes = titleRes;
        this.descriptionRes = descriptionRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public void setTitleRes(int titleRes) {
        this.titleRes = titleRes;
    }

    public int getDescriptionRes() {
        return descriptionRes;
    }

    public void setDescriptionRes(int descriptionRes) {
        this.descriptionRes = descriptionRes;
    }
}