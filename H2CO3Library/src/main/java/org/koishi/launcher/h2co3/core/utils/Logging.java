package org.koishi.launcher.h2co3.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class Logging {
    public static final Logger LOG = Logger.getLogger("H2CO3Launcher");
    private static final ByteArrayOutputStream storedLogs = new ByteArrayOutputStream();
    private static volatile String[] accessTokens = new String[0];

    private Logging() {
    }

    public static synchronized void registerAccessToken(String token) {
        final String[] oldAccessTokens = accessTokens;
        final String[] newAccessTokens = Arrays.copyOf(oldAccessTokens, oldAccessTokens.length + 1);

        newAccessTokens[oldAccessTokens.length] = token;

        accessTokens = newAccessTokens;
    }

    public static String filterForbiddenToken(String message) {
        for (String token : accessTokens)
            message = message.replace(token, "<access token>");
        return message;
    }

    public static void start(Path logFolder) {
        LOG.setLevel(Level.ALL);
        LOG.setUseParentHandlers(false);
        LOG.setFilter(record -> {
            record.setMessage(filterForbiddenToken(record.getMessage()));
            return true;
        });

        try {
            Files.createDirectories(logFolder);
            FileHandler fileHandler = new FileHandler(logFolder.resolve("client_output.log").toAbsolutePath().toString());
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            LOG.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Unable to create client_output.log\n" + StringUtils.getStackTrace(e));
        }
    }

    public static void initForTest() {
        LOG.setLevel(Level.ALL);
        LOG.setUseParentHandlers(false);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new DefaultFormatter());
        consoleHandler.setLevel(Level.FINER);
        LOG.addHandler(consoleHandler);
    }

    public static byte[] getRawLogs() {
        return storedLogs.toByteArray();
    }

    public static String getLogs() {
        return storedLogs.toString();
    }

    private static final class DefaultFormatter extends Formatter {

        private static final MessageFormat format = new MessageFormat("[{0,date,HH:mm:ss}] [{1}.{2}/{3}] {4}\n");

        @Override
        public synchronized String format(LogRecord record) {
            StringBuilder sb = new StringBuilder(128);
            MessageFormat.format(Arrays.toString(new Object[]{
                    new Date(record.getMillis()),
                    record.getSourceClassName(), record.getSourceMethodName(), record.getLevel().getName(),
                    record.getMessage()
            }), sb, null);
            if (record.getThrown() != null)
                sb.append(StringUtils.getStackTrace(record.getThrown()));

            String formattedLog = sb.toString();
            try {
                storedLogs.write(formattedLog.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return formattedLog;
        }
    }
}