package org.koishi.launcher.h2co3core.util.java

import org.koishi.launcher.h2co3core.game.JavaVersion
import org.koishi.launcher.h2co3core.game.Version
import org.koishi.launcher.h2co3core.util.io.FileUtils
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools
import java.io.File

object JavaManager {
    private var isInit = false;

    @JvmStatic
    val javaList: MutableList<JavaVersion> = mutableListOf()
        get() {
            if (!isInit) {
                init()
            }
            return field
        }

    @JvmStatic
    fun init() {
        isInit = true
        javaList.add(JavaVersion(true, "1.8", "Auto"))
        File(H2CO3LauncherTools.JAVA_PATH).listFiles()?.forEach {
            addToJavaVersion(it)
        }
    }

    @JvmStatic
    fun remove(name: String) {
        FileUtils.deleteDirectory(File(H2CO3LauncherTools.JAVA_PATH, name))
        javaList.removeIf { it.name == name }
    }

    fun addToJavaVersion(javaDir: File) {
        if (javaDir.isDirectory && javaDir.resolve("release").exists()) {
            val version =
                Regex("JAVA_VERSION=\"([^\"]+)\"").find(javaDir.resolve("release").readText())
                    ?.let { match ->
                        match.groupValues[1]
                    } ?: return
            javaList.add(JavaVersion(false, version, javaDir.name))
        }
    }

    @JvmStatic
    fun getJavaFromVersionName(name: String): JavaVersion {
        return javaList.find { it.name == name } ?: javaList[0]
    }

    @JvmStatic
    fun getSuitableJavaVersion(version: Version?): JavaVersion {
        if (version == null) {
            return getJavaFromVersionName("jre8")
        }
        return findExactOrNextGreater(version.javaVersion.majorVersion);
    }

    private fun findExactOrNextGreater(version: Int): JavaVersion {
        var exact: JavaVersion? = null
        var closestGreater: JavaVersion? = null

        for (java in javaList) {
            when {
                java.getVersion() == version -> {
                    exact = java
                    break
                }

                java.getVersion() > version -> {
                    closestGreater = when {
                        closestGreater == null -> java
                        java.getVersion() < closestGreater.getVersion() -> java
                        else -> closestGreater
                    }
                }
            }
        }
        return exact ?: closestGreater!!
    }
}