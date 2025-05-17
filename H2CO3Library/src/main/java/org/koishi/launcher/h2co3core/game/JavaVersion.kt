package org.koishi.launcher.h2co3core.game;

import org.koishi.launcher.h2co3core.util.java.JavaManager
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools

class JavaVersion(val isAuto: Boolean, val versionName: String, val name: String) {

    fun getJavaPath(version: Version): String {
        val javaVersion = if (isAuto) getSuitableJavaVersion(version) else this
        return "${H2CO3LauncherTools.JAVA_PATH}/${javaVersion.name}"
    }

    fun getVersion(): Int {
        val split = versionName.split(".")
        return if (split[0] == "1") {
            split[1].toInt()
        } else {
            split[0].toInt()
        }
    }

    companion object {
        const val JAVA_VERSION_8: Int = 8
        const val JAVA_VERSION_11: Int = 11
        const val JAVA_VERSION_17: Int = 17
        const val JAVA_VERSION_21: Int = 21

        @JvmField
        val JAVA_AUTO: JavaVersion = JavaVersion(true, "1.8", "Auto")

        @JvmStatic
        fun getSuitableJavaVersion(version: Version): JavaVersion {
            return JavaManager.getSuitableJavaVersion(version)
        }
    }
}
