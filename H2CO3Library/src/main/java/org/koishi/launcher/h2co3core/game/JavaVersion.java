package org.koishi.launcher.h2co3core.game;

import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;

public class JavaVersion {

    public static final int JAVA_VERSION_8 = 8;
    public static final int JAVA_VERSION_11 = 11;
    public static final int JAVA_VERSION_17 = 17;
    public static final int JAVA_VERSION_21 = 21;
    public static final JavaVersion JAVA_AUTO = new JavaVersion(true, JAVA_VERSION_8, "Auto");
    public static final JavaVersion JAVA_8 = new JavaVersion(false, JAVA_VERSION_8, "1.8");
    public static final JavaVersion JAVA_11 = new JavaVersion(false, JAVA_VERSION_11, "11");
    public static final JavaVersion JAVA_17 = new JavaVersion(false, JAVA_VERSION_17, "17");
    public static final JavaVersion JAVA_21 = new JavaVersion(false, JAVA_VERSION_21, "21");
    private final boolean auto;
    private final int version;
    private final String versionName;

    public JavaVersion(boolean auto, int version, String versionName) {
        this.auto = auto;
        this.version = version;
        this.versionName = versionName;
    }

    public static JavaVersion[] getAllJavaVersion() {
        return new JavaVersion[] {
                JAVA_AUTO,
                JAVA_8,
                JAVA_11,
                JAVA_17,
                JAVA_21
        };
    }

    public static JavaVersion getJavaFromVersionName(String versionName) {
        for (JavaVersion javaVersion : getAllJavaVersion()) {
            if (javaVersion.getVersionName().equals(versionName)) {
                return javaVersion;
            }
        }
        return JAVA_AUTO;
    }

    public static JavaVersion getSuitableJavaVersion(Version version) {
        if (version.getJavaVersion() == null || version.getJavaVersion().getMajorVersion() == 8) {
            return JAVA_8;
        } else if (version.getJavaVersion().getMajorVersion() == 11) {
            return JAVA_11;
        } else if (version.getJavaVersion().getMajorVersion() == 21) {
            return JAVA_21;
        }
        return JAVA_17;
    }

    public static boolean checkJavaVersion(Version version, JavaVersion javaVersion) {
        if (version.getJavaVersion() == null || version.getJavaVersion().getMajorVersion() == 8) {
            return javaVersion.getVersion() == JAVA_VERSION_8;
        } else if (version.getJavaVersion().getMajorVersion() == 11) {
            return javaVersion.getVersion() == JAVA_VERSION_11;
        } else if (version.getJavaVersion().getMajorVersion() == 21) {
            return javaVersion.getVersion() == JAVA_VERSION_21;
        }
        return javaVersion.getVersion() == JAVA_VERSION_17;
    }

    public boolean isAuto() {
        return auto;
    }

    public int getVersion() {
        return version;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getJavaPath(Version version) {
        JavaVersion javaVersion = auto ? getSuitableJavaVersion(version) : this;
        if (javaVersion.getVersion() == JAVA_VERSION_8) {
            return H2CO3LauncherTools.JAVA_8_PATH;
        } else if (javaVersion.getVersion() == JAVA_VERSION_11) {
            return H2CO3LauncherTools.JAVA_11_PATH;
        } else if (javaVersion.getVersion() == JAVA_VERSION_17) {
            return H2CO3LauncherTools.JAVA_17_PATH;
        }
        return H2CO3LauncherTools.JAVA_21_PATH;
    }

}
