package org.koishi.launcher.h2co3.core.login.microsoft;

import android.net.Uri;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.Texture.Texture;
import org.koishi.launcher.h2co3.core.login.Texture.TextureType;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.core.utils.NetworkUtils;
import org.koishi.launcher.h2co3.core.utils.gson.JsonUtils;
import org.koishi.launcher.h2co3.core.utils.gson.tools.TolerableValidationException;
import org.koishi.launcher.h2co3.core.utils.gson.tools.Validation;
import org.koishi.launcher.h2co3.core.utils.io.AuthenticationException;
import org.koishi.launcher.h2co3.core.utils.io.ResponseCodeException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class MicrosoftLoginUtils {
    private static final String AUTH_TOKEN_URL = "https://login.live.com/oauth20_token.srf";
    private static final String XBL_AUTH_URL = "https://user.auth.xboxlive.com/user/authenticate";
    private static final String XSTS_AUTH_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";
    private static final String MC_LOGIN_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String MC_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";

    public String mcUuid;
    public String msRefreshToken;
    public String mcName;
    public String mcToken;
    public String tokenType;
    public boolean doesOwnGame;

    public MicrosoftLoginUtils(boolean isRefresh, String authCode) throws IOException, JSONException {
        acquireAccessToken(isRefresh, authCode);
    }

    public static Optional<Map<TextureType, Texture>> getTextures(MinecraftProfileResponse profile) {
        Objects.requireNonNull(profile);
        Map<TextureType, Texture> textures = new EnumMap<>(TextureType.class);
        profile.skins.stream().findFirst().ifPresent(skin -> textures.put(TextureType.SKIN, new Texture(skin.url, null)));
        return Optional.of(textures);
    }

    public static MinecraftProfileResponse getMinecraftProfile(String tokenType, String accessToken)
            throws IOException, AuthenticationException {
        return executeHttpRequest(MC_PROFILE_URL, tokenType, accessToken, MinecraftProfileResponse.class);
    }

    private static <T> T executeHttpRequest(String urlString, String tokenType, String accessToken, Class<T> responseType)
            throws IOException, AuthenticationException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestProperty("Authorization", tokenType + " " + accessToken);
        conn.setUseCaches(false);
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new NoMinecraftJavaEditionProfileException();
        } else if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new ResponseCodeException(new URL(urlString), responseCode);
        }
        String result = NetworkUtils.readData(conn);
        return JsonUtils.fromNonNullJson(result, responseType);
    }

    public static String ofJSONData(Map<String, Object> data) {
        return new JSONObject(data).toString();
    }

    public static String ofFormData(Map<String, String> data) {
        Uri.Builder builder = new Uri.Builder();
        data.forEach(builder::appendQueryParameter);
        return builder.build().getEncodedQuery();
    }

    private static void setRequestProperties(HttpURLConnection conn, String contentType, String req) throws UnsupportedEncodingException {
        conn.setRequestProperty("Content-Type", contentType);
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", String.valueOf(req.getBytes("UTF-8").length));
    }

    private static void setRequestOutput(HttpURLConnection conn, String req) throws IOException {
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();
        try (OutputStream wr = conn.getOutputStream()) {
            wr.write(req.getBytes("UTF-8"));
        }
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        return H2CO3Tools.read(conn.getInputStream());
    }

    private static void throwResponseError(HttpURLConnection conn) throws IOException {
        String errStr = readResponse(conn);
        Log.i("MicroAuth", "Error code: " + conn.getResponseCode() + ": " + conn.getResponseMessage() + "\n" + errStr);
        String otherErrStr = errStr.contains("NOT_FOUND") && errStr.contains("The server has not found anything matching the request URI")
                ? "It seems that this Microsoft Account does not own the game. Make sure that you have bought/migrated to your Microsoft account."
                : "";
        H2CO3Tools.showError(H2CO3MessageManager.NotificationItem.Type.ERROR, otherErrStr + "\n\nMSA Error: " + conn.getResponseCode() + ": " + conn.getResponseMessage() + ", error stream:\n" + errStr);
    }

    private void acquireAccessToken(boolean isRefresh, String authCode) throws IOException, JSONException {
        Log.i("MicroAuth", "isRefresh=" + isRefresh + ", authCode= " + authCode);
        Map<String, String> data = new HashMap<>();
        data.put("client_id", "00000000402b5328");
        data.put(isRefresh ? "refresh_token" : "code", authCode);
        data.put("grant_type", isRefresh ? "refresh_token" : "authorization_code");
        data.put("redirect_url", "https://login.live.com/oauth20_desktop.srf");
        data.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");

        String req = ofFormData(data);
        HttpURLConnection conn = (HttpURLConnection) new URL(AUTH_TOKEN_URL).openConnection();
        setRequestProperties(conn, "application/x-www-form-urlencoded", req);
        setRequestOutput(conn, req);
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(readResponse(conn));
            msRefreshToken = jo.getString("refresh_token");
            Log.i("MicroAuth", "Access Token = " + jo.getString("access_token"));
            acquireXBLToken(jo.getString("access_token"));
        } else {
            throwResponseError(conn);
        }
    }

    private void acquireXBLToken(String accessToken) throws IOException, JSONException {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        properties.put("AuthMethod", "RPS");
        properties.put("SiteName", "user.auth.xboxlive.com");
        properties.put("RpsTicket", accessToken);
        data.put("Properties", properties);
        data.put("RelyingParty", "http://auth.xboxlive.com");
        data.put("TokenType", "JWT");
        String req = ofJSONData(data);
        HttpURLConnection conn = (HttpURLConnection) new URL(XBL_AUTH_URL).openConnection();
        setRequestProperties(conn, "application/json", req);
        setRequestOutput(conn, req);
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(readResponse(conn));
            Log.i("MicroAuth", "Xbl Token = " + jo.getString("Token"));
            acquireXsts(jo.getString("Token"));
        } else {
            throwResponseError(conn);
        }
    }

    private void acquireXsts(String xblToken) throws IOException, JSONException {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        properties.put("SandboxId", "RETAIL");
        properties.put("UserTokens", Collections.singleton(xblToken));
        data.put("Properties", properties);
        data.put("RelyingParty", "rp://api.minecraftservices.com/");
        data.put("TokenType", "JWT");
        String req = ofJSONData(data);
        HttpURLConnection conn = (HttpURLConnection) new URL(XSTS_AUTH_URL).openConnection();
        setRequestProperties(conn, "application/json", req);
        setRequestOutput(conn, req);
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(readResponse(conn));
            String uhs = jo.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs");
            Log.i("MicroAuth", "Xbl Xsts = " + jo.getString("Token") + "; Uhs = " + uhs);
            acquireMinecraftToken(uhs, jo.getString("Token"));
        } else {
            throwResponseError(conn);
        }
    }

    private void acquireMinecraftToken(String xblUhs, String xblXsts) throws IOException, JSONException {
        Map<String, Object> data = new HashMap<>();
        data.put("identityToken", "XBL3.0 x=" + xblUhs + ";" + xblXsts);
        String req = ofJSONData(data);
        HttpURLConnection conn = (HttpURLConnection) new URL(MC_LOGIN_URL).openConnection();
        setRequestProperties(conn, "application/json", req);
        setRequestOutput(conn, req);
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(readResponse(conn));
            Log.i("MicroAuth", "MC token: " + jo.getString("access_token"));
            mcToken = jo.getString("access_token");
            tokenType = jo.getString("token_type");
            checkMcProfile(mcToken);
        } else {
            throwResponseError(conn);
        }
    }

    private void checkMcProfile(String mcAccessToken) throws IOException, JSONException {
        HttpURLConnection conn = (HttpURLConnection) new URL(MC_PROFILE_URL).openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + mcAccessToken);
        conn.setUseCaches(false);
        conn.connect();
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            String s = readResponse(conn);
            Log.i("MicroAuth", "profile:" + s);
            JSONObject jsonObject = new JSONObject(s);
            String name = jsonObject.getString("name");
            String uuid = jsonObject.getString("id");
            String uuidDashes = uuid.replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
            );
            doesOwnGame = true;
            Log.i("MicroAuth", "UserName = " + name);
            Log.i("MicroAuth", "Uuid Minecraft = " + uuidDashes);
            mcName = name;
            mcUuid = uuidDashes;
        } else {
            Log.i("MicroAuth", "It seems that this Microsoft Account does not own the game.");
            doesOwnGame = false;
            H2CO3Tools.showError(H2CO3MessageManager.NotificationItem.Type.ERROR, "It seems that this Microsoft Account does not own the game.");
        }
    }

    public static class MinecraftProfileResponseSkin implements Validation {
        public String id;
        public String state;
        public String url;
        public String variant; // CLASSIC, SLIM
        public String alias;

        @Override
        public void validate() throws JsonParseException, TolerableValidationException {
            Validation.requireNonNull(id, "id cannot be null");
            Validation.requireNonNull(state, "state cannot be null");
            Validation.requireNonNull(url, "url cannot be null");
            Validation.requireNonNull(variant, "variant cannot be null");
        }
    }

    public static class MinecraftProfileResponseCape {}

    public static class MinecraftProfileResponse extends MinecraftErrorResponse implements Validation {
        @SerializedName("id")
        UUID id;
        @SerializedName("name")
        String name;
        @SerializedName("skins")
        List<MinecraftProfileResponseSkin> skins = Collections.emptyList();
        @SerializedName("capes")
        List<MinecraftProfileResponseCape> capes = Collections.emptyList();

        @Override
        public void validate() throws JsonParseException, TolerableValidationException {
            Validation.requireNonNull(id, "id cannot be null");
            Validation.requireNonNull(name, "name cannot be null");
            Validation.requireNonNull(skins, "skins cannot be null");
            Validation.requireNonNull(capes, "capes cannot be null");
        }
    }

    private static class MinecraftErrorResponse {
        public String path;
        public String errorType;
        public String error;
        public String errorMessage;
        public String developerMessage;
    }

    public static class NoMinecraftJavaEditionProfileException extends AuthenticationException {}
}