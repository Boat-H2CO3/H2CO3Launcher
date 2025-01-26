package org.koishi.launcher.h2co3library.component;

import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleBooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.fakefx.util.StringConverter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class H2CO3LauncherCheckBoxTreeItem<T> {

    private final T data;
    private final StringConverter<T> stringConverter;
    @NotNull
    private final ObservableList<H2CO3LauncherCheckBoxTreeItem<T>> subItem;
    private final SimpleBooleanProperty expandedProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty selectedProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty indeterminateProperty = new SimpleBooleanProperty(false);
    @Nullable
    private String comment;
    private boolean fromCheck = false;

    public H2CO3LauncherCheckBoxTreeItem(T data, StringConverter<T> stringConverter, @NotNull ObservableList<H2CO3LauncherCheckBoxTreeItem<T>> subItem) {
        this.data = data;
        this.stringConverter = stringConverter;
        this.subItem = subItem;

        selectedProperty().addListener(observable -> {
            if (!fromCheck) {
                subItem.forEach(it -> it.setSelected(isSelected()));
            }
        });
    }

    public void checkProperty() {
        if (subItem.stream().anyMatch(H2CO3LauncherCheckBoxTreeItem::isIndeterminate)) {
            if (!isIndeterminate()) {
                setIndeterminate(true);
            }
        } else if (subItem.stream().noneMatch(H2CO3LauncherCheckBoxTreeItem::isSelected)) {
            if (isIndeterminate()) {
                setIndeterminate(false);
            }
            if (isSelected()) {
                fromCheck = true;
                setSelected(false);
                fromCheck = false;
            }
        } else if (subItem.stream().allMatch(H2CO3LauncherCheckBoxTreeItem::isSelected)) {
            if (isIndeterminate()) {
                setIndeterminate(false);
            }
            if (!isSelected()) {
                fromCheck = true;
                setSelected(true);
                fromCheck = false;
            }
        } else if (subItem.stream().anyMatch(H2CO3LauncherCheckBoxTreeItem::isSelected) && subItem.stream().anyMatch(it -> !it.isSelected())) {
            if (!isIndeterminate()) {
                setIndeterminate(true);
            }
        }
    }

    public T getData() {
        return data;
    }

    public String getText() {
        if (data instanceof String && stringConverter == null)
            return (String) data;
        else if (stringConverter == null)
            return data.toString();
        return stringConverter.toString(data);
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    public void setComment(@Nullable String comment) {
        this.comment = comment;
    }

    @NotNull
    public ObservableList<H2CO3LauncherCheckBoxTreeItem<T>> getSubItem() {
        return subItem;
    }

    public SimpleBooleanProperty expandedProperty() {
        return expandedProperty;
    }

    public boolean isExpanded() {
        return expandedProperty.get();
    }

    public void setExpanded(boolean expanded) {
        this.expandedProperty.set(expanded);
    }

    public SimpleBooleanProperty selectedProperty() {
        return selectedProperty;
    }

    public boolean isSelected() {
        return selectedProperty.get();
    }

    public void setSelected(boolean selected) {
        this.selectedProperty.set(selected);
    }

    public SimpleBooleanProperty indeterminateProperty() {
        return indeterminateProperty;
    }

    public boolean isIndeterminate() {
        return indeterminateProperty.get();
    }

    public void setIndeterminate(boolean indeterminate) {
        this.indeterminateProperty.set(indeterminate);
    }
}
