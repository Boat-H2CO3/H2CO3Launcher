package org.koishi.launcher.h2co3core.fakefx.util.converter;

import org.koishi.launcher.h2co3core.fakefx.util.StringConverter;

/**
 * <p>{@link StringConverter} implementation for {@link String} values.</p>
 * @since JavaFX 2.1
 */
public class DefaultStringConverter extends StringConverter<String> {

    /**
     * Creates a default {@code DefaultStringConverter}.
     */
    public DefaultStringConverter() {
    }

    /** {@inheritDoc} */
    @Override public String toString(String value) {
        return (value != null) ? value : "";
    }

    /** {@inheritDoc} */
    @Override public String fromString(String value) {
        return value;
    }
}
