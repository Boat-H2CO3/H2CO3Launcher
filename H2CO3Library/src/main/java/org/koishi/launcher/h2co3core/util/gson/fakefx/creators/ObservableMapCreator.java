package org.koishi.launcher.h2co3core.util.gson.fakefx.creators;

import java.lang.reflect.Type;

import com.google.gson.InstanceCreator;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableMap;

/**
 * An {@link InstanceCreator} for observable maps using {@link FXCollections}.
 */
public class ObservableMapCreator implements InstanceCreator<ObservableMap<?, ?>> {

    public ObservableMap<?, ?> createInstance(Type type) {
        // No need to use a parametrized map since the actual instance will have the raw type anyway.
        return FXCollections.observableHashMap();
    }
}