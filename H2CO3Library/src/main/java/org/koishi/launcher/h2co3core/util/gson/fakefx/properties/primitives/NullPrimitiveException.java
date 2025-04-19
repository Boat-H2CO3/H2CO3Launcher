package org.koishi.launcher.h2co3core.util.gson.fakefx.properties.primitives;

public class NullPrimitiveException extends RuntimeException {

    public NullPrimitiveException(String pathInJson) {
        super("Illegal null value for a primitive type at path " + pathInJson);
    }
}