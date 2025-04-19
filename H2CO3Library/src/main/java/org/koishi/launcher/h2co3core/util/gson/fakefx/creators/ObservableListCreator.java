package org.koishi.launcher.h2co3core.util.gson.fakefx.creators;

import com.google.gson.InstanceCreator;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;

import java.lang.reflect.Type;

public class ObservableListCreator implements InstanceCreator<ObservableList<?>> {

    public ObservableList<?> createInstance(Type type) {
        // No need to use a parametrized list since the actual instance will have the raw type anyway.
        return FXCollections.observableArrayList();
    }
}