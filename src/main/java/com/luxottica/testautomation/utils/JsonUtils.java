package com.luxottica.testautomation.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils {

    public static String getAttributeInJsonTree(JsonObject object, String jsonPath) {

        String[] pathSegments = jsonPath.split("\\.");

        if (pathSegments.length == 0) {
            return null;
        }

        JsonElement element = object.get(pathSegments[0]);

        if (pathSegments.length > 1) {
            return getAttributeInJsonTree(element.getAsJsonObject(), jsonPath.substring(jsonPath.indexOf('.') + 1));
        }

        return element.getAsString();
    }

    public static void setAttributeInJsonTree(JsonObject object, String jsonPath, String value) {

        String[] pathSegments = jsonPath.split("\\.");

        if (pathSegments.length == 0) {
            return;
        }

        String currentSegment = pathSegments[0];

        if (pathSegments.length == 1) {
            object.addProperty(currentSegment, value);
            return;
        }

        JsonElement element = object.get(currentSegment);
        if (element == null) {
            JsonObject newObject = new JsonObject();
            object.add(currentSegment, newObject);
            element = newObject;
        }

        setAttributeInJsonTree(element.getAsJsonObject(), jsonPath.substring(jsonPath.indexOf('.') + 1), value);
    }
}
