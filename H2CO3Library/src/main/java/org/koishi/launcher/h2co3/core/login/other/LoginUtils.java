package org.koishi.launcher.h2co3.core.login.other;

import com.google.gson.Gson;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import java.io.IOException;
import java.util.Objects;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginUtils {
    private static final LoginUtils INSTANCE = new LoginUtils();
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private String baseUrl;

    private LoginUtils() {
    }

    public static LoginUtils getINSTANCE() {
        return INSTANCE;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        System.out.println(this.baseUrl);
    }

    public void login(String userName, String password, Listener listener) throws IOException {
        Objects.requireNonNull(baseUrl, "no baseUrl");
        AuthRequest authRequest = createAuthRequest(userName, password);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), gson.toJson(authRequest));
        Request request = new Request.Builder()
                .url(baseUrl + "/authserver/authenticate")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            System.out.println(res);
            if (response.isSuccessful()) {
                AuthResult result = gson.fromJson(res, AuthResult.class);
                listener.onSuccess(result);
            } else {
                listener.onFailed(response.code() + "\n" + res);
            }
        }
    }

    private AuthRequest createAuthRequest(String userName, String password) {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(userName);
        authRequest.setPassword(password);
        AuthRequest.Agent agent = new AuthRequest.Agent();
        agent.setName("H2CO3Launcher");
        agent.setVersion(1.0);
        authRequest.setAgent(agent);
        authRequest.setRequestUser(true);
        authRequest.setClientToken("H2CO3Launcher");
        return authRequest;
    }

    public String getServeInfo(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String res = response.body().string();
                System.out.println(res);
                return response.isSuccessful() ? res : null;
            }
        } catch (IOException e) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
        }
        return null;
    }

    public interface Listener {
        void onSuccess(AuthResult authResult);
        void onFailed(String error);
    }
}