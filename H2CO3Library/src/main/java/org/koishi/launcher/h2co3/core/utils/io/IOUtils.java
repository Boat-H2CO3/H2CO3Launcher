package org.koishi.launcher.h2co3.core.utils.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

/**
 * This utility class consists of some util methods operating on InputStream/OutputStream.
 */
public final class IOUtils {

    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    private IOUtils() {
    }

    /**
     * Read all bytes to a buffer from given input stream. The stream will not be closed.
     *
     * @param stream the InputStream being read.
     * @return all bytes read from the stream
     * @throws IOException if an I/O error occurs.
     */
    public static byte[] readFullyWithoutClosing(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream(Math.max(stream.available(), 32));
        copyTo(stream, result);
        return result.toByteArray();
    }

    public static String readFullyAsStringWithClosing(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream(Math.max(stream.available(), 32));
        copyTo(stream, result);
        return result.toString("UTF-8");
    }

    /**
     * Read all bytes to a buffer from given input stream, and close the input stream finally.
     *
     * @param stream the InputStream being read, closed finally.
     * @return all bytes read from the stream
     * @throws IOException if an I/O error occurs.
     */
    public static ByteArrayOutputStream readFully(InputStream stream) throws IOException {
        try (InputStream is = stream) {
            ByteArrayOutputStream result = new ByteArrayOutputStream(Math.max(is.available(), 32));
            copyTo(is, result);
            return result;
        }
    }

    public static byte[] readFullyAsByteArray(InputStream stream) throws IOException {
        return readFully(stream).toByteArray();
    }

    public static String readFullyAsString(InputStream stream) throws IOException {
        return readFully(stream).toString("UTF-8");
    }

    public static void copyTo(InputStream src, OutputStream dest) throws IOException {
        copyTo(src, dest, new byte[DEFAULT_BUFFER_SIZE]);
    }

    public static void copyTo(InputStream src, OutputStream dest, byte[] buf) throws IOException {
        while (true) {
            int len = src.read(buf);
            if (len == -1)
                break;
            dest.write(buf, 0, len);
        }
    }

    public static InputStream wrapFromGZip(InputStream inputStream) throws IOException {
        return new GZIPInputStream(inputStream);
    }
}
