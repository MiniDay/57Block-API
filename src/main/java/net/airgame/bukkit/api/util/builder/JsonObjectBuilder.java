package net.airgame.bukkit.api.util.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@SuppressWarnings("unused")
public class JsonObjectBuilder {
    private final JsonObject object;

    public JsonObjectBuilder() {
        this(new JsonObject());
    }

    public JsonObjectBuilder(JsonObject object) {
        this.object = object;
    }

    public static JsonObjectBuilder create() {
        return new JsonObjectBuilder();
    }

    public JsonObjectBuilder append(String key, String value) {
        object.addProperty(key, value);
        return this;
    }

    public JsonObjectBuilder append(String key, Number value) {
        object.addProperty(key, value);
        return this;
    }

    public JsonObjectBuilder append(String key, Boolean value) {
        object.addProperty(key, value);
        return this;
    }

    public JsonObjectBuilder append(String key, Character value) {
        object.addProperty(key, value);
        return this;
    }

    public JsonObjectBuilder append(String key, JsonElement value) {
        object.add(key, value);
        return this;
    }

    public JsonObject build() {
        return object;
    }
}
