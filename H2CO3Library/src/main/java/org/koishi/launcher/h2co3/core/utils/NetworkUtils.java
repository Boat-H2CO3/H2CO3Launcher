package org.koishi.launcher.h2co3.core.utils;

import static org.koishi.launcher.h2co3.core.utils.Pair.pair;
import static org.koishi.launcher.h2co3.core.utils.StringUtils.removeSurrounding;
import static org.koishi.launcher.h2co3.core.utils.StringUtils.substringAfter;
import static org.koishi.launcher.h2co3.core.utils.StringUtils.substringAfterLast;

import android.os.Build;

import org.koishi.launcher.h2co3.core.utils.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * @author huangyuhui
 */
public final class NetworkUtils {
    public static final String PARAMETER_SEPARATOR = "&";
    public static final String NAME_VALUE_SEPARATOR = "=";
    private static final int TIME_OUT = 8000;

    private NetworkUtils() {
    }

    public static String withQuery(String baseUrl, Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(baseUrl);
        boolean first = true;
        for (Entry<String, String> param : params.entrySet()) {
            if (param.getValue() == null)
                continue;
            if (first) {
                if (!baseUrl.isEmpty()) {
                    sb.append('?');
                }
                first = false;
            } else {
                sb.append(PARAMETER_SEPARATOR);
            }
            sb.append(encodeURL(param.getKey()));
            sb.append(NAME_VALUE_SEPARATOR);
            sb.append(encodeURL(param.getValue()));
        }
        return sb.toString();
    }

    public static List<Pair<String, String>> parseQuery(URI uri) throws UnsupportedEncodingException {
        return parseQuery(uri.getRawQuery());
    }

    public static List<Pair<String, String>> parseQuery(String queryParameterString) throws UnsupportedEncodingException {
        if (queryParameterString == null) return Collections.emptyList();

        List<Pair<String, String>> result = new ArrayList<>();

        try (Scanner scanner = new Scanner(queryParameterString)) {
            scanner.useDelimiter("&");
            while (scanner.hasNext()) {
                String[] nameValue = scanner.next().split(NAME_VALUE_SEPARATOR);
                if (nameValue.length == 0 || nameValue.length > 2) {
                    throw new IllegalArgumentException("bad query string");
                }

                String name = decodeURL(nameValue[0]);
                String value = nameValue.length == 2 ? decodeURL(nameValue[1]) : null;
                result.add(pair(name, value));
            }
        }
        return result;
    }

    private static boolean endsWithDomainSuffix(String host, String domainSuffix) {
        return host.endsWith(domainSuffix.toLowerCase());
    }

    public static URLConnection createConnection(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        String host = url.getHost().toLowerCase();
        if (endsWithDomainSuffix(host, "d.pcs.baidu.com") || endsWithDomainSuffix(host, "baidupcs.com")) {
            connection.setRequestProperty("User-Agent", "pan.baidu.com");
        } else {
            connection.setRequestProperty("User-Agent", "H2CO3Launcher/" + Build.getRadioVersion());
        }
        connection.setUseCaches(false);
        connection.setConnectTimeout(TIME_OUT);
        connection.setReadTimeout(TIME_OUT);
        connection.setRequestProperty("Accept-Language", Locale.getDefault().toString());
        return connection;
    }

    public static HttpURLConnection createHttpConnection(URL url) throws IOException {
        return (HttpURLConnection) createConnection(url);
    }

    /**
     * @param location the url to be URL encoded
     * @return encoded URL
     * @see <a href=
     * "https://github.com/curl/curl/blob/3f7b1bb89f92c13e69ee51b710ac54f775aab320/lib/transfer.c#L1427-L1461">Curl</a>
     */
    public static String encodeLocation(String location) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean left = true;
        for (char ch : location.toCharArray()) {
            switch (ch) {
                case ' ':
                    if (left)
                        sb.append("%20");
                    else
                        sb.append('+');
                    break;
                case '?':
                    left = false;
                    // fallthrough
                default:
                    if (ch >= 0x80)
                        sb.append(encodeURL(Character.toString(ch)));
                    else
                        sb.append(ch);
                    break;
            }
        }

        return sb.toString();
    }

    /**
     * This method is a work-around that aims to solve problem when "Location" in
     * stupid server's response is not encoded.
     *
     * @param conn the stupid http connection.
     * @return manually redirected http connection.
     * @throws IOException if an I/O error occurs.
     * @see <a href="https://github.com/curl/curl/issues/473">Issue with libcurl</a>
     */
    public static HttpURLConnection resolveConnection(HttpURLConnection conn) throws IOException {
        int redirect = 0;
        while (true) {

            conn.setUseCaches(false);
            conn.setConnectTimeout(TIME_OUT);
            conn.setReadTimeout(TIME_OUT);
            conn.setInstanceFollowRedirects(false);
            Map<String, List<String>> properties = conn.getRequestProperties();
            String method = conn.getRequestMethod();
            int code = conn.getResponseCode();
            if (code >= 300 && code <= 307 && code != 306 && code != 304) {
                String newURL = conn.getHeaderField("Location");
                conn.disconnect();

                if (redirect > 20) {
                    throw new IOException("Too much redirects");
                }

                HttpURLConnection redirected = (HttpURLConnection) new URL(conn.getURL(), encodeLocation(newURL))
                        .openConnection();
                properties
                        .forEach((key, value) -> value.forEach(element -> redirected.addRequestProperty(key, element)));
                redirected.setRequestMethod(method);
                conn = redirected;
                ++redirect;
            } else {
                break;
            }
        }
        return conn;
    }

    public static String doGet(URL url) throws IOException {
        HttpURLConnection con = createHttpConnection(url);
        con = resolveConnection(con);
        return IOUtils.readFullyAsString(con.getInputStream());
    }

    public static String doPost(URL u, Map<String, String> params) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (params != null) {
            for (Map.Entry<String, String> e : params.entrySet())
                sb.append(e.getKey()).append("=").append(e.getValue()).append("&");
            sb.deleteCharAt(sb.length() - 1);
        }
        return doPost(u, sb.toString());
    }

    public static String doPost(URL u, String post) throws IOException {
        return doPost(u, post, "application/x-www-form-urlencoded");
    }

    public static String doPost(URL url, String post, String contentType) throws IOException {
        byte[] bytes = post.getBytes("UTF-8");

        HttpURLConnection con = createHttpConnection(url);
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", contentType + "; charset=utf-8");
        con.setRequestProperty("Content-Length", String.valueOf(bytes.length));
        try (OutputStream os = con.getOutputStream()) {
            os.write(bytes);
        }
        return readData(con);
    }

    public static String readData(HttpURLConnection con) throws IOException {
        try {
            try (InputStream stdout = con.getInputStream()) {
                return IOUtils.readFullyAsString("gzip".equals(con.getContentEncoding()) ? IOUtils.wrapFromGZip(stdout) : stdout);
            }
        } catch (IOException e) {
            try (InputStream stderr = con.getErrorStream()) {
                if (stderr == null)
                    throw e;
                return IOUtils.readFullyAsString("gzip".equals(con.getContentEncoding()) ? IOUtils.wrapFromGZip(stderr) : stderr);
            }
        }
    }

    public static String detectFileName(URL url) throws IOException {
        HttpURLConnection conn = resolveConnection(createHttpConnection(url));
        int code = conn.getResponseCode();
        if (code / 100 == 4)
            throw new FileNotFoundException();
        if (code / 100 != 2)
            throw new IOException(url + ": response code " + conn.getResponseCode());

        return detectFileName(conn);
    }

    public static String detectFileName(HttpURLConnection conn) throws UnsupportedEncodingException {
        String disposition = conn.getHeaderField("Content-Disposition");
        if (disposition == null || !disposition.contains("filename=")) {
            String u = conn.getURL().toString();
            return decodeURL(substringAfterLast(u, '/'));
        } else {
            return decodeURL(removeSurrounding(substringAfter(disposition, "filename="), "\""));
        }
    }

    public static URL toURL(String str) {
        try {
            return new URL(str);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean isURL(String str) {
        try {
            new URL(str);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean urlExists(URL url) throws IOException {
        HttpURLConnection con = createHttpConnection(url);
        con = resolveConnection(con);
        int responseCode = con.getResponseCode();
        con.disconnect();
        return responseCode / 100 == 2;
    }

    // ==== Shortcut methods for encoding/decoding URLs in UTF-8 ====
    public static String encodeURL(String toEncode) throws UnsupportedEncodingException {
        return URLEncoder.encode(toEncode, "UTF-8");
    }

    public static String decodeURL(String toDecode) throws UnsupportedEncodingException {
        return URLDecoder.decode(toDecode, "UTF-8");
    }
    // ====
}