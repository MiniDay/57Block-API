package net.airgame.bukkit.api.util.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@SuppressWarnings("unused")
public class JsonBuilder {
    private final JsonObject object;

    public JsonBuilder() {
        this(new JsonObject());
    }

    public JsonBuilder(JsonObject object) {
        this.object = object;
    }

    public static JsonBuilder create() {
        return new JsonBuilder();
    }

    public JsonBuilder append(String key, String value) {
        object.addProperty(key, value);
        return this;
    }

    public JsonBuilder append(String key, Number value) {
        object.addProperty(key, value);
        return this;
    }

    public JsonBuilder append(String key, Boolean value) {
        object.addProperty(key, value);
        return this;
    }

    public JsonBuilder append(String key, Character value) {
        object.addProperty(key, value);
        return this;
    }

    public JsonBuilder append(String key, JsonElement value) {
        object.add(key, value);
        return this;
    }

    public JsonObject build() {
        return object;
    }
}
