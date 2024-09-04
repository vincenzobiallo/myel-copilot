package com.luxottica.testautomation.authentication;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class PlaywrightFactory {

    private static final String STORAGE_STATE_PATH = "playwright/.auth/workers/{username}.json";

    public static String getStorageState(String username) {
        return STORAGE_STATE_PATH.replace("{username}", username);
    }

    public static boolean areCookiesPresent(String username) {
        return new File(getStorageState(username)).exists();
    }

    public static String getSessionId(String username) {

        if (!areCookiesPresent(username)) {
            throw new IllegalStateException("Cookies are not present for user: " + username);
        }

        try {
            String body = new String(Files.readAllBytes(Paths.get(getStorageState(username))));

            JsonArray cookies = JsonParser.parseString(body).getAsJsonObject().getAsJsonArray("cookies");
            String sessionId = cookies.asList()
                    .stream()
                    .map(JsonObject.class::cast)
                    .filter(cookie -> cookie.get("name").getAsString().equals("myl_frontoffice_sessionid"))
                    .map(cookie -> cookie.get("value").getAsString())
                    .findFirst()
                    .orElse(null);

            return Objects.requireNonNull(sessionId, "sessionId not found in cookies!");
        } catch (Exception e) {
            throw new RuntimeException("Error while reading cookies for user: " + username, e);
        }
    }
}
