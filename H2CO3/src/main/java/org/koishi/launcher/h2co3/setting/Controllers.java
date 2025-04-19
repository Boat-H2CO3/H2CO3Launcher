package org.koishi.launcher.h2co3.setting;

import static org.koishi.launcher.h2co3.util.FXUtils.onInvalidating;
import static org.koishi.launcher.h2co3core.fakefx.collections.FXCollections.observableArrayList;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.fakefx.beans.Observable;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyListProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyListWrapper;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3core.util.gson.fakefx.factories.JavaFxPropertyTypeAdapterFactory;
import org.koishi.launcher.h2co3core.util.io.FileUtils;
import org.koishi.launcher.h2co3core.util.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Controllers {

    private static final ObservableList<Controller> controllers = observableArrayList(controller -> new Observable[]{controller});
    private static final ReadOnlyListWrapper<Controller> controllersWrapper = new ReadOnlyListWrapper<>(controllers);
    private static final List<Runnable> CALLBACKS = new ArrayList<>();
    public static Controller DEFAULT_CONTROLLER;
    /**
     * True if {@link #init()} hasn't been called.
     */
    private static boolean initialized = false;

    static {
        controllers.addListener(onInvalidating(Controllers::updateControllerStorages));
        controllers.addListener(onInvalidating(Controllers::checkControllers));
    }

    private Controllers() {
    }

    public static void checkControllers() {
        if (controllers.contains(null)) {
            controllers.remove(null);
        }
        if (controllers.isEmpty()) {
            try {
                if (DEFAULT_CONTROLLER == null) {
                    String str = IOUtils.readFullyAsString(Controllers.class.getResourceAsStream("/assets/controllers/00000000.json"));
                    DEFAULT_CONTROLLER = new GsonBuilder()
                            .registerTypeAdapterFactory(new JavaFxPropertyTypeAdapterFactory(true, true))
                            .setPrettyPrinting()
                            .create().fromJson(str, Controller.class);
                }
                DEFAULT_CONTROLLER.saveToDisk();
            } catch (IOException e) {
                Logging.LOG.log(Level.SEVERE, "Failed to generate default controller!", e.getMessage());
            }
            controllers.addAll(getControllersFromDisk());
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

    private static void updateControllerStorages() {
        // don't update the underlying storage before data loading is completed
        // otherwise it might cause data loss
        if (!initialized)
            return;
        // update storage
        File[] files = new File(H2CO3LauncherTools.CONTROLLER_DIR).listFiles();
        if (files != null) {
            ArrayList<String> fileNames = (ArrayList<String>) controllers.stream().map(Controller::getFileName).collect(Collectors.toList());
            for (File file : files) {
                if (((file.isDirectory() && !file.getName().equals("styles") && !file.getName().equals("input")) || !fileNames.contains(file.getName())) && !file.getName().endsWith(".bak")) {
                    file.delete();
                }
            }
        }
        for (Controller controller : controllers) {
            controller.saveToDisk();
        }
    }

    public static void init() {
        if (initialized)
            return;

        controllers.addAll(getControllersFromDisk());
        checkControllers();

        initialized = true;
        CALLBACKS.forEach(callback -> Schedulers.androidUIThread().execute(callback));
        CALLBACKS.clear();
    }

    private static ArrayList<Controller> getControllersFromDisk() {
        ArrayList<Controller> list = new ArrayList<>();
        List<File> jsons = FileUtils.listFilesByExtension(new File(H2CO3LauncherTools.CONTROLLER_DIR), "json");
        for (File json : jsons) {
            if (json.isFile()) {
                try {
                    String str = FileUtils.readText(json);
                    Controller controller = new GsonBuilder()
                            .registerTypeAdapterFactory(new JavaFxPropertyTypeAdapterFactory(true, true))
                            .setPrettyPrinting()
                            .create().fromJson(str, Controller.class);
                    if (controller == null) {
                        throw new JsonParseException("Controller is null!");
                    }
                    if (!json.getName().equals(controller.getFileName())) {
                        controller.renameFile(json.getName(), controller.getFileName());
                    }
                    list.add(controller);
                } catch (IOException e) {
                    Logging.LOG.log(Level.WARNING, "Can't read file: " + json.getAbsolutePath(), e.getMessage());
                } catch (JsonParseException e) {
                    Logging.LOG.log(Level.WARNING, "File: " + json.getAbsolutePath(), e.getMessage() + " is broken!");
                    json.renameTo(new File(H2CO3LauncherTools.CONTROLLER_DIR, json.getName() + ".bak"));
                }
            }
        }
        return list;
    }

    public static ObservableList<Controller> getControllers() {
        if (controllers.contains(null)) {
            controllers.remove(null);
        }
        if (controllers.isEmpty()) controllers.add(DEFAULT_CONTROLLER);
        return controllers;
    }

    public static ReadOnlyListProperty<Controller> controllersProperty() {
        return controllersWrapper.getReadOnlyProperty();
    }

    public static void addController(Controller controller) {
        if (!initialized) return;
        controllers.add(controller);
    }

    public static void removeControllers(Controller controller) {
        if (!initialized) return;
        controllers.remove(controller);
    }

    public static Controller findControllerById(String id) {
        checkControllers();
        return controllers.stream().filter(it -> it.getId().equals(id)).findFirst().orElse(controllers.get(0));
    }

    public static void addCallback(Runnable callback) {
        if (initialized) {
            callback.run();
            return;
        }
        CALLBACKS.add(callback);
    }

}
