package net.airgame.bukkit.api.object.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@SuppressWarnings("unused")
public class JsonArrayBuilder {
    private final JsonArray array;

    public JsonArrayBuilder() {
        this(new JsonArray());
    }

    public JsonArrayBuilder(JsonArray array) {
        this.array = array;
    }

    public static JsonArrayBuilder create() {
        return new JsonArrayBuilder();
    }

    public JsonArrayBuilder append(String value) {
        array.add(value);
        return this;
    }

    public JsonArrayBuilder append(Number value) {
        array.add(value);
        return this;
    }

    public JsonArrayBuilder append(Boolean value) {
        array.add(value);
        return this;
    }

    public JsonArrayBuilder append(Character value) {
        array.add(value);
        return this;
    }

    public JsonArrayBuilder append(JsonElement value) {
        array.add(value);
        return this;
    }

    public JsonArrayBuilder appendAll(JsonArray value) {
        array.addAll(value);
        return this;
    }

    public JsonArray build() {
        return array;
    }
}
