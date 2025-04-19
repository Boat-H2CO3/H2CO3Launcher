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
package org.koishi.launcher.h2co3core.game;

import org.koishi.launcher.h2co3launcher.H2CO3LauncherConfig;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LaunchOptions implements Serializable {

    private final List<String> gameArguments = new ArrayList<>();
    private final List<String> overrideJavaArguments = new ArrayList<>();
    private final List<String> javaArguments = new ArrayList<>();
    private final List<String> javaAgents = new ArrayList<>(0);
    private File gameDir;
    private JavaVersion java;
    private String versionName;
    private String versionType;
    private String profileName;
    private Integer minMemory;
    private Integer maxMemory;
    private Integer metaspace;
    private Integer width;
    private Integer height;
    private String serverIp;
    private boolean beGesture;
    private boolean vulkanDriverSystem;
    private boolean pojavBigCore;
    private H2CO3LauncherConfig.Renderer renderer;

    /**
     * The game directory
     */
    public File getGameDir() {
        return gameDir;
    }

    /**
     * The Java Environment that Minecraft runs on.
     */
    public JavaVersion getJava() {
        return java;
    }

    /**
     * Will shown in the left bottom corner of the main menu of Minecraft.
     * null if use the id of launch version.
     */
    public String getVersionName() {
        return versionName;
    }

    /**
     * Will shown in the left bottom corner of the main menu of Minecraft.
     * null if use Version.versionType.
     */
    public String getVersionType() {
        return versionType;
    }

    /**
     * Don't know what the hell this is.
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     * User custom additional minecraft command line arguments.
     */
    @NotNull
    public List<String> getGameArguments() {
        return Collections.unmodifiableList(gameArguments);
    }

    /**
     * The highest priority JVM arguments (overrides the version setting)
     */
    @NotNull
    public List<String> getOverrideJavaArguments() {
        return Collections.unmodifiableList(overrideJavaArguments);
    }

    /**
     * User custom additional java virtual machine command line arguments.
     */
    @NotNull
    public List<String> getJavaArguments() {
        return Collections.unmodifiableList(javaArguments);
    }

    @NotNull
    public List<String> getJavaAgents() {
        return Collections.unmodifiableList(javaAgents);
    }

    /**
     * The minimum memory that the JVM can allocate.
     */
    public Integer getMinMemory() {
        return minMemory;
    }

    /**
     * The maximum memory that the JVM can allocate.
     */
    public Integer getMaxMemory() {
        return maxMemory;
    }

    /**
     * The maximum metaspace memory that the JVM can allocate.
     * For Java 7 -XX:PermSize and Java 8 -XX:MetaspaceSize
     * Containing class instances.
     */
    public Integer getMetaspace() {
        return metaspace;
    }

    /**
     * The initial game window width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * The initial game window height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * The server ip that will connect to when enter game main menu.
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * BE Gesture
     */
    public boolean isBeGesture() {
        return beGesture;
    }

    /**
     * vulkan Driver System
     */
    public boolean isVKDriverSystem() {
        return vulkanDriverSystem;
    }

    public boolean isPojavBigCore() {
        return pojavBigCore;
    }

    /**
     * Renderer
     */
    public H2CO3LauncherConfig.Renderer getRenderer() {
        return renderer;
    }

    public static class Builder {

        private final LaunchOptions options = new LaunchOptions();

        public LaunchOptions create() {
            return options;
        }

        /**
         * The game directory
         */
        public File getGameDir() {
            return options.gameDir;
        }

        public Builder setGameDir(File gameDir) {
            options.gameDir = gameDir;
            return this;
        }

        /**
         * The Java Environment that Minecraft runs on.
         */
        public JavaVersion getJava() {
            return options.java;
        }

        public Builder setJava(JavaVersion java) {
            options.java = java;
            return this;
        }

        /**
         * Will shown in the left bottom corner of the main menu of Minecraft.
         * null if use the id of launch version.
         */
        public String getVersionName() {
            return options.versionName;
        }

        public Builder setVersionName(String versionName) {
            options.versionName = versionName;
            return this;
        }

        /**
         * Will shown in the left bottom corner of the main menu of Minecraft.
         * null if use Version.versionType.
         */
        public String getVersionType() {
            return options.versionType;
        }

        public Builder setVersionType(String versionType) {
            options.versionType = versionType;
            return this;
        }

        /**
         * Don't know what the hell this is.
         */
        public String getProfileName() {
            return options.profileName;
        }

        public Builder setProfileName(String profileName) {
            options.profileName = profileName;
            return this;
        }

        /**
         * User custom additional minecraft command line arguments.
         */
        public List<String> getGameArguments() {
            return options.gameArguments;
        }

        public Builder setGameArguments(List<String> gameArguments) {
            options.gameArguments.clear();
            options.gameArguments.addAll(gameArguments);
            return this;
        }

        /**
         * The highest priority JVM arguments (overrides the version setting)
         */
        public List<String> getOverrideJavaArguments() {
            return options.overrideJavaArguments;
        }

        public Builder setOverrideJavaArguments(List<String> overrideJavaArguments) {
            options.overrideJavaArguments.clear();
            options.overrideJavaArguments.addAll(overrideJavaArguments);
            return this;
        }

        /**
         * User custom additional java virtual machine command line arguments.
         */
        public List<String> getJavaArguments() {
            return options.javaArguments;
        }

        public Builder setJavaArguments(List<String> javaArguments) {
            options.javaArguments.clear();
            options.javaArguments.addAll(javaArguments);
            return this;
        }

        public List<String> getJavaAgents() {
            return options.javaAgents;
        }

        public Builder setJavaAgents(List<String> javaAgents) {
            options.javaAgents.clear();
            options.javaAgents.addAll(javaAgents);
            return this;
        }

        /**
         * The minimum memory that the JVM can allocate.
         */
        public Integer getMinMemory() {
            return options.minMemory;
        }

        public Builder setMinMemory(Integer minMemory) {
            options.minMemory = minMemory;
            return this;
        }

        /**
         * The maximum memory that the JVM can allocate.
         */
        public Integer getMaxMemory() {
            return options.maxMemory;
        }

        public Builder setMaxMemory(Integer maxMemory) {
            options.maxMemory = maxMemory;
            return this;
        }

        /**
         * The maximum metaspace memory that the JVM can allocate.
         * For Java 7 -XX:PermSize and Java 8 -XX:MetaspaceSize
         * Containing class instances.
         */
        public Integer getMetaspace() {
            return options.metaspace;
        }

        public Builder setMetaspace(Integer metaspace) {
            options.metaspace = metaspace;
            return this;
        }

        /**
         * The initial game window width
         */
        public Integer getWidth() {
            return options.width;
        }

        public Builder setWidth(Integer width) {
            options.width = width;
            return this;
        }

        /**
         * The initial game window height
         */
        public Integer getHeight() {
            return options.height;
        }

        public Builder setHeight(Integer height) {
            options.height = height;
            return this;
        }

        /**
         * The server ip that will connect to when enter game main menu.
         */
        public String getServerIp() {
            return options.serverIp;
        }

        public Builder setServerIp(String serverIp) {
            options.serverIp = serverIp;
            return this;
        }

        /**
         * BE Gesture
         */
        public boolean isBeGesture() {
            return options.beGesture;
        }

        /**
         * vulkanDriverSystem
         */
        public boolean isVKDriverSystem() {
            return options.vulkanDriverSystem;
        }

        /**
         * Renderer
         */
        public H2CO3LauncherConfig.Renderer getRenderer() {
            return options.renderer;
        }

        public Builder setRenderer(H2CO3LauncherConfig.Renderer renderer) {
            options.renderer = renderer;
            return this;
        }

        public Builder setBEGesture(boolean beGesture) {
            options.beGesture = beGesture;
            return this;
        }

        public Builder setVkDriverSystem(boolean vulkanDriverSystem) {
            options.vulkanDriverSystem = vulkanDriverSystem;
            return this;
        }

        public Builder setPojavBigCore(boolean pojavBigCore) {
            options.pojavBigCore = pojavBigCore;
            return this;
        }

    }
}
