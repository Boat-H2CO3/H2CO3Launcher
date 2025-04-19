package org.koishi.launcher.h2co3.control.data;

import static org.koishi.launcher.h2co3.util.FXUtils.onInvalidating;
import static org.koishi.launcher.h2co3core.fakefx.collections.FXCollections.observableArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.fakefx.beans.Observable;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyListProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyListWrapper;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3core.util.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;

public class ButtonStyles {

    private static final ObservableList<ControlButtonStyle> styles = observableArrayList(style -> new Observable[] { style });
    private static final ReadOnlyListWrapper<ControlButtonStyle> stylesWrapper = new ReadOnlyListWrapper<>(styles);
    /**
     * True if {@link #init()} hasn't been called.
     */
    private static boolean initialized = false;

    static {
        styles.addListener(onInvalidating(ButtonStyles::updateStylesStorages));
        styles.addListener(onInvalidating(ButtonStyles::checkStyles));
    }

    private ButtonStyles() {
    }

    public static void checkStyles() {
        if (!initialized)
            return;
        if (styles.isEmpty()) {
            styles.add(ControlButtonStyle.DEFAULT_BUTTON_STYLE);
            saveStyles();
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

    private static void updateStylesStorages() {
        // don't update the underlying storage before data loading is completed
        // otherwise it might cause data loss
        if (!initialized)
            return;
        // update storage
        saveStyles();
    }

    public static void init() {
        if (initialized)
            return;

        getStylesFromDisk().forEach(ButtonStyles::addStyle);
        checkStyles();

        initialized = true;
    }

    private static ArrayList<ControlButtonStyle> getStylesFromDisk() {
        ArrayList<ControlButtonStyle> list = new ArrayList<>();
        try {
            String json = FileUtils.readText(new File(H2CO3LauncherTools.CONTROLLER_DIR + "/styles/button_styles.json"));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            ArrayList<ControlButtonStyle> styles = gson.fromJson(json, new TypeToken<ArrayList<ControlButtonStyle>>() {
            }.getType());
            if (Objects.isNull(styles)) {
                new File(H2CO3LauncherTools.CONTROLLER_DIR + "/styles/button_styles.json").delete();
            } else {
                list.addAll(styles);
            }
        } catch (IOException e) {
            Logging.LOG.log(Level.SEVERE, "Failed to get button styles", e);
        } catch (JsonSyntaxException e) {
            new File(H2CO3LauncherTools.CONTROLLER_DIR + "/styles/button_styles.json").delete();
        }
        return list;
    }

    public static ObservableList<ControlButtonStyle> getStyles() {
        return styles;
    }

    public static ReadOnlyListProperty<ControlButtonStyle> stylesProperty() {
        return stylesWrapper.getReadOnlyProperty();
    }

    public static void saveStyles() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(new ArrayList<>(styles));
        try {
            FileUtils.writeText(new File(H2CO3LauncherTools.CONTROLLER_DIR + "/styles/button_styles.json"), json);
        } catch (IOException e) {
            Logging.LOG.log(Level.SEVERE, "Failed to save button styles", e);
        }
    }

    public static void addStyle(ControlButtonStyle style) {
        if (!initialized) return;
        boolean add = true;
        for (ControlButtonStyle buttonStyle : getStyles())
            if (buttonStyle.getName().equals(style.getName()))
                add = false;
        if (add)
            styles.add(style);
    }

    public static void removeStyles(ControlButtonStyle style) {
        if (!initialized) return;
        styles.remove(style);
    }

    public static ControlButtonStyle findStyleByName(String name) {
        checkStyles();
        return styles.stream().filter(it -> it.getName().equals(name)).findFirst().orElse(styles.get(0));
    }

}
