package org.koishi.launcher.h2co3.core;

import static org.koishi.launcher.h2co3.core.H2CO3Settings.userList;
import static org.koishi.launcher.h2co3.core.H2CO3Settings.usersFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

public class H2CO3Auth {

    public static void addUserToJson(String name, String email, String password, String userType, String apiUrl, String authSession, String uuid, String skinTexture, String token, String refreshToken, String clientToken, Boolean isOffline, boolean isSelected) {
        try {
            JSONObject json;
            if (usersFile.exists()) {
                json = new JSONObject(readFileContent(usersFile));
            } else {
                json = new JSONObject();
            }
            JSONObject userData = new JSONObject();
            userData.put(H2CO3Tools.LOGIN_USER_EMAIL, email);
            userData.put(H2CO3Tools.LOGIN_USER_PASSWORD, password);
            userData.put(H2CO3Tools.LOGIN_USER_TYPE, userType);
            userData.put(H2CO3Tools.LOGIN_API_URL, apiUrl);
            userData.put(H2CO3Tools.LOGIN_AUTH_SESSION, authSession);
            userData.put(H2CO3Tools.LOGIN_UUID, uuid);
            userData.put(H2CO3Tools.LOGIN_USER_SKINTEXTURE, skinTexture);
            userData.put(H2CO3Tools.LOGIN_TOKEN, token);
            userData.put(H2CO3Tools.LOGIN_REFRESH_TOKEN, refreshToken);
            userData.put(H2CO3Tools.LOGIN_CLIENT_TOKEN, clientToken);
            userData.put(H2CO3Tools.LOGIN_IS_OFFLINE, isOffline);
            userData.put(H2CO3Tools.LOGIN_IS_SELECTED, isSelected);
            userData.put(H2CO3Tools.LOGIN_INFO, new JSONArray().put(0, name).put(1, isOffline));
            json.put(name, userData);

            writeFileContent(usersFile, json.toString());
            parseJsonToUser(json);
        } catch (JSONException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void parseJsonToUser(JSONObject usersObj) throws IOException {
        if (usersObj == null || usersObj.length() == 0) {
            return;
        }

        try {
            Iterator<String> keys = usersObj.keys();
            while (keys.hasNext()) {
                String userName = keys.next();
                JSONObject userObj = usersObj.getJSONObject(userName);

                UserBean user = new UserBean();
                user.setUserName(userName);
                user.setUserEmail(userObj.optString(H2CO3Tools.LOGIN_USER_EMAIL, ""));
                user.setUserPassword(userObj.optString(H2CO3Tools.LOGIN_USER_PASSWORD, ""));
                user.setUserType(userObj.optString(H2CO3Tools.LOGIN_USER_TYPE, ""));
                user.setApiUrl(userObj.optString(H2CO3Tools.LOGIN_API_URL, ""));
                user.setAuthSession(userObj.optString(H2CO3Tools.LOGIN_AUTH_SESSION, ""));
                user.setUuid(userObj.optString(H2CO3Tools.LOGIN_UUID, ""));
                user.setSkinTexture(userObj.optString(H2CO3Tools.LOGIN_USER_SKINTEXTURE, ""));
                user.setToken(userObj.optString(H2CO3Tools.LOGIN_TOKEN, ""));
                user.setRefreshToken(userObj.optString(H2CO3Tools.LOGIN_REFRESH_TOKEN, ""));
                user.setClientToken(userObj.optString(H2CO3Tools.LOGIN_CLIENT_TOKEN, ""));
                user.setIsSelected(userObj.optBoolean(H2CO3Tools.LOGIN_IS_SELECTED, false));
                user.setIsOffline(userObj.optBoolean(H2CO3Tools.LOGIN_IS_OFFLINE, true));

                JSONArray loginInfoArray = userObj.optJSONArray(H2CO3Tools.LOGIN_INFO);
                if (loginInfoArray != null && loginInfoArray.length() >= 2) {
                    user.setUserInfo(loginInfoArray.optString(0, ""));
                    user.setUserPassword(loginInfoArray.optString(1, ""));
                }

                userList.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void resetUserState() {
        UserBean emptyUser = new UserBean();
        setUserState(emptyUser);
    }

    public static List<UserBean> getUserList(JSONObject obj) throws IOException {
        userList.clear();
        parseJsonToUser(obj);
        return userList;
    }

    public static void setUserState(UserBean user) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, user.getUserName());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_EMAIL, user.getUserEmail());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_PASSWORD, user.getUserPassword());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_TYPE, user.getUserType());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_API_URL, user.getApiUrl());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_AUTH_SESSION, user.getAuthSession());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_UUID, user.getUuid());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_SKINTEXTURE, user.getSkinTexture());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_TOKEN, user.getToken());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_REFRESH_TOKEN, user.getRefreshToken());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_CLIENT_TOKEN, user.getClientToken());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_INFO, user.getUserInfo());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_IS_OFFLINE, user.getIsOffline());
    }

    public static String getUserJson() {
        try {
            return readFileContent(usersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setUserJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            writeFileContent(usersFile, json);
            parseJsonToUser(jsonObject);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeFileContent(File file, String content) throws IOException {
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
    }

    public static String readFileContent(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }
}