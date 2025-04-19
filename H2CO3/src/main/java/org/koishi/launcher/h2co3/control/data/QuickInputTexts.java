package org.koishi.launcher.h2co3.control.data;

import static org.koishi.launcher.h2co3.util.FXUtils.onInvalidating;
import static org.koishi.launcher.h2co3core.fakefx.collections.FXCollections.observableArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyListProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyListWrapper;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3core.util.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class QuickInputTexts {

    private static final ObservableList<String> inputTexts = observableArrayList(new ArrayList<>());
    private static final ReadOnlyListWrapper<String> inputTextsWrapper = new ReadOnlyListWrapper<>(inputTexts);
    /**
     * True if {@link #init()} hasn't been called.
     */
    private static boolean initialized = false;

    static {
        inputTexts.addListener(onInvalidating(QuickInputTexts::updateInputTextsStorages));
    }

    private QuickInputTexts() {
    }

    public static boolean isInitialized() {
        return initialized;
    }

    private static void updateInputTextsStorages() {
        // don't update the underlying storage before data loading is completed
        // otherwise it might cause data loss
        if (!initialized)
            return;
        // update storage
        saveInputTexts();
    }

    public static void init() {
        if (initialized)
            throw new IllegalStateException("Already initialized");

        inputTexts.addAll(getInputTextsFromDisk());

        initialized = true;
    }

    private static ArrayList<String> getInputTextsFromDisk() {
        try {
            File file = new File(H2CO3LauncherTools.CONTROLLER_DIR + "/input/input_text.json");
            if (file.exists()) {
                String json = FileUtils.readText(file);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                return gson.fromJson(json, new TypeToken<ArrayList<String>>() {
                }.getType());
            }
        } catch (IOException e) {
            Logging.LOG.log(Level.SEVERE, "Failed to get quick input text", e);
        }
        return new ArrayList<>();
    }

    public static ObservableList<String> getInputTexts() {
        return inputTexts;
    }

    public static ReadOnlyListProperty<String> inputTextsProperty() {
        return inputTextsWrapper.getReadOnlyProperty();
    }

    public static void saveInputTexts() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(new ArrayList<>(inputTexts));
        try {
            FileUtils.writeText(new File(H2CO3LauncherTools.CONTROLLER_DIR + "/input/input_text.json"), json);
        } catch (IOException e) {
            Logging.LOG.log(Level.SEVERE, "Failed to save quick input text", e);
        }
    }

    public static void addInputText(String inputText) {
        if (!initialized) return;
        inputTexts.add(inputText);
    }

    public static void removeInputText(String inputText) {
        if (!initialized) return;
        inputTexts.remove(inputText);
    }

}
