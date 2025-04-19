package org.koishi.launcher.h2co3core.util.gson.fakefx.properties;

public class NullPropertyException extends RuntimeException {

    public NullPropertyException() {
        super("Null properties are forbidden");
    }
}