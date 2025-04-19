/*
 * Hello Minecraft! Launcher
 * Copyright (C) 2020  huangyuhui <huanghongxun2008@126.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.koishi.launcher.h2co3.setting;

import static org.koishi.launcher.h2co3.setting.ConfigHolder.config;
import static org.koishi.launcher.h2co3.util.FXUtils.onInvalidating;
import static org.koishi.launcher.h2co3core.fakefx.collections.FXCollections.observableArrayList;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.util.WeakListenerHolder;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.event.EventBus;
import org.koishi.launcher.h2co3core.event.RefreshedVersionsEvent;
import org.koishi.launcher.h2co3core.fakefx.beans.Observable;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyListProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyListWrapper;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyStringProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyStringWrapper;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;

public final class Profiles {

    private static final ObservableList<Profile> profiles = observableArrayList(profile -> new Observable[] { profile });
    private static final ReadOnlyListWrapper<Profile> profilesWrapper = new ReadOnlyListWrapper<>(profiles);
    /**
     * Called when it's ready to load profiles from {@link ConfigHolder#config()}.
     */
    private static final WeakListenerHolder holder = new WeakListenerHolder();
    private static final ReadOnlyStringWrapper selectedVersion = new ReadOnlyStringWrapper();
    private static final List<Consumer<Profile>> versionsListeners = new ArrayList<>(4);
    /**
     * True if {@link #init()} hasn't been called.
     */
    private static boolean initialized = false;
    private static ObjectProperty<Profile> selectedProfile = new SimpleObjectProperty<Profile>() {
        {
            profiles.addListener(onInvalidating(this::invalidated));
        }

        @Override
        protected void invalidated() {
            if (!initialized)
                return;

            Profile profile = get();

            if (profiles.isEmpty()) {
                if (profile != null) {
                    set(null);
                    return;
                }
            } else {
                if (!profiles.contains(profile)) {
                    set(profiles.get(0));
                    return;
                }
            }

            config().setSelectedProfile(profile == null ? "" : profile.getName());
            if (profile != null) {
                if (profile.getRepository().isLoaded())
                    selectedVersion.bind(profile.selectedVersionProperty());
                else {
                    selectedVersion.unbind();
                    selectedVersion.set(null);
                    // bind when repository was reloaded.
                    profile.getRepository().refreshVersionsAsync().start();
                }
            } else {
                selectedVersion.unbind();
                selectedVersion.set(null);
            }
        }
    };

    static {
        profiles.addListener(onInvalidating(Profiles::updateProfileStorages));
        profiles.addListener(onInvalidating(Profiles::checkProfiles));

        selectedProfile.addListener((a, b, newValue) -> {
            if (newValue != null)
                newValue.getRepository().refreshVersionsAsync().start();
        });
    }

    private Profiles() {
    }

    private static void checkProfiles() {
        if (profiles.isEmpty()) {
            Profile current = new Profile(H2CO3LauncherTools.CONTEXT.getString(R.string.profile_shared), new File(H2CO3LauncherTools.SHARED_COMMON_DIR), new VersionSetting(), null);
            Profile home = new Profile(H2CO3LauncherTools.CONTEXT.getString(R.string.profile_private), new File(H2CO3LauncherTools.PRIVATE_COMMON_DIR));
            profiles.addAll(current, home);
        }
    }

    private static void updateProfileStorages() {
        // don't update the underlying storage before data loading is completed
        // otherwise it might cause data loss
        if (!initialized)
            return;
        // update storage
        TreeMap<String, Profile> newConfigurations = new TreeMap<>();
        for (Profile profile : profiles) {
            newConfigurations.put(profile.getName(), profile);
        }
        config().getConfigurations().setValue(FXCollections.observableMap(newConfigurations));
    }

    static void init() {
        if (initialized)
            return;

        HashSet<String> names = new HashSet<>();
        config().getConfigurations().forEach((name, profile) -> {
            if (!names.add(name)) return;
            profiles.add(profile);
            profile.setName(name);
        });
        checkProfiles();

        initialized = true;

        selectedProfile.set(
                profiles.stream()
                        .filter(it -> it.getName().equals(config().getSelectedProfile()))
                        .findFirst()
                        .orElse(profiles.get(0)));

        holder.add(EventBus.EVENT_BUS.channel(RefreshedVersionsEvent.class).registerWeak(event -> {
            Profile profile = selectedProfile.get();
            if (profile != null && profile.getRepository() == event.getSource()) {
                selectedVersion.bind(profile.selectedVersionProperty());
                for (Consumer<Profile> listener : versionsListeners)
                    listener.accept(profile);
            }
        }));
    }

    public static ObservableList<Profile> getProfiles() {
        return profiles;
    }

    public static ReadOnlyListProperty<Profile> profilesProperty() {
        return profilesWrapper.getReadOnlyProperty();
    }

    public static Profile getSelectedProfile() {
        return selectedProfile.get();
    }

    public static void setSelectedProfile(Profile profile) {
        selectedProfile.set(profile);
    }

    public static ObjectProperty<Profile> selectedProfileProperty() {
        return selectedProfile;
    }

    public static ReadOnlyStringProperty selectedVersionProperty() {
        return selectedVersion.getReadOnlyProperty();
    }

    // Guaranteed that the repository is loaded.
    public static String getSelectedVersion() {
        return selectedVersion.get();
    }

    public static void registerVersionsListener(Consumer<Profile> listener) {
        Profile profile = getSelectedProfile();
        if (profile != null && profile.getRepository().isLoaded())
            listener.accept(profile);
        versionsListeners.add(listener);
    }

    public static String getSelectedGameVersion() {
        Optional<String> gameVersion = getSelectedProfile().getRepository().getGameVersion(getSelectedVersion());
        return gameVersion.orElse("");
    }
}
