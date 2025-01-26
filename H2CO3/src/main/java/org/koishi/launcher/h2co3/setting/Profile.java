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

import static org.koishi.launcher.h2co3.util.FXUtils.onInvalidating;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import org.koishi.launcher.h2co3.game.H2CO3LauncherCacheRepository;
import org.koishi.launcher.h2co3.game.H2CO3LauncherGameRepository;
import org.koishi.launcher.h2co3.util.WeakListenerHolder;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.download.DefaultDependencyManager;
import org.koishi.launcher.h2co3core.download.DownloadProvider;
import org.koishi.launcher.h2co3core.event.EventBus;
import org.koishi.launcher.h2co3core.event.EventPriority;
import org.koishi.launcher.h2co3core.event.RefreshedVersionsEvent;
import org.koishi.launcher.h2co3core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3core.fakefx.beans.Observable;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyObjectWrapper;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleStringProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.StringProperty;
import org.koishi.launcher.h2co3core.game.Version;
import org.koishi.launcher.h2co3core.util.ToStringBuilder;
import org.koishi.launcher.h2co3core.util.fakefx.ObservableHelper;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Optional;

@JsonAdapter(Profile.Serializer.class)
public final class Profile implements Observable {
    private final WeakListenerHolder listenerHolder = new WeakListenerHolder();
    private final H2CO3LauncherGameRepository repository;

    private final StringProperty selectedVersion = new SimpleStringProperty();
    private final ObjectProperty<File> gameDir;
    private final ReadOnlyObjectWrapper<VersionSetting> global = new ReadOnlyObjectWrapper<>(this, "global");
    private final SimpleStringProperty name;
    private ObservableHelper observableHelper = new ObservableHelper(this);

    public Profile(String name) {
        this(name, new File(H2CO3LauncherTools.SHARED_COMMON_DIR));
    }

    public Profile(String name, File initialGameDir) {
        this(name, initialGameDir, new VersionSetting());
    }

    public Profile(String name, File initialGameDir, VersionSetting global) {
        this(name, initialGameDir, global, null);
    }

    public Profile(String name, File initialGameDir, VersionSetting global, String selectedVersion) {
        this.name = new SimpleStringProperty(this, "name", name);
        gameDir = new SimpleObjectProperty<>(this, "gameDir", initialGameDir);
        repository = new H2CO3LauncherGameRepository(this, initialGameDir);
        this.global.set(global == null ? new VersionSetting() : global);
        this.selectedVersion.set(selectedVersion);

        gameDir.addListener((a, b, newValue) -> repository.changeDirectory(newValue));
        this.selectedVersion.addListener(o -> checkSelectedVersion());
        listenerHolder.add(EventBus.EVENT_BUS.channel(RefreshedVersionsEvent.class).registerWeak(event -> checkSelectedVersion(), EventPriority.HIGHEST));

        addPropertyChangedListener(onInvalidating(this::invalidate));
    }

    public StringProperty selectedVersionProperty() {
        return selectedVersion;
    }

    public String getSelectedVersion() {
        return selectedVersion.get();
    }

    public void setSelectedVersion(String selectedVersion) {
        this.selectedVersion.set(selectedVersion);
    }

    public ObjectProperty<File> gameDirProperty() {
        return gameDir;
    }

    public File getGameDir() {
        return gameDir.get();
    }

    public void setGameDir(File gameDir) {
        this.gameDir.set(gameDir);
    }

    public ReadOnlyObjectProperty<VersionSetting> globalProperty() {
        return global.getReadOnlyProperty();
    }

    public VersionSetting getGlobal() {
        return global.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    private void checkSelectedVersion() {
        if (!repository.isLoaded()) return;
        String newValue = selectedVersion.get();
        if (!repository.hasVersion(newValue)) {
            Optional<String> version = repository.getVersions().stream().findFirst().map(Version::getId);
            if (version.isPresent())
                selectedVersion.setValue(version.get());
            else if (newValue != null)
                selectedVersion.setValue(null);
        }
    }

    public H2CO3LauncherGameRepository getRepository() {
        return repository;
    }

    public DefaultDependencyManager getDependency() {
        return getDependency(DownloadProviders.getDownloadProvider());
    }

    public DefaultDependencyManager getDependency(DownloadProvider downloadProvider) {
        return new DefaultDependencyManager(repository, downloadProvider, H2CO3LauncherCacheRepository.REPOSITORY);
    }

    public VersionSetting getVersionSetting(String id) {
        return repository.getVersionSetting(id);
    }

    public VersionSetting getVersionSetting() {
        return repository.getVersionSetting(getSelectedVersion());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("gameDir", getGameDir())
                .append("name", getName())
                .toString();
    }

    private void addPropertyChangedListener(InvalidationListener listener) {
        name.addListener(listener);
        global.addListener(listener);
        gameDir.addListener(listener);
        global.get().addPropertyChangedListener(listener);
        selectedVersion.addListener(listener);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        observableHelper.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        observableHelper.removeListener(listener);
    }

    private void invalidate() {
        observableHelper.invalidate();
    }

    public static class ProfileVersion {
        private final Profile profile;
        private final String version;

        public ProfileVersion(Profile profile, String version) {
            this.profile = profile;
            this.version = version;
        }

        public Profile getProfile() {
            return profile;
        }

        public String getVersion() {
            return version;
        }
    }

    public static final class Serializer implements JsonSerializer<Profile>, JsonDeserializer<Profile> {
        @Override
        public JsonElement serialize(Profile src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null)
                return JsonNull.INSTANCE;

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("global", context.serialize(src.getGlobal()));
            jsonObject.addProperty("gameDir", src.getGameDir().getPath());
            jsonObject.addProperty("selectedMinecraftVersion", src.getSelectedVersion());

            return jsonObject;
        }

        @Override
        public Profile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == JsonNull.INSTANCE || !(json instanceof JsonObject)) return null;
            JsonObject obj = (JsonObject) json;
            String gameDir = Optional.ofNullable(obj.get("gameDir")).map(JsonElement::getAsString).orElse("");

            return new Profile("Default",
                    new File(gameDir),
                    context.deserialize(obj.get("global"), VersionSetting.class),
                    Optional.ofNullable(obj.get("selectedMinecraftVersion")).map(JsonElement::getAsString).orElse(""));
        }

    }
}
