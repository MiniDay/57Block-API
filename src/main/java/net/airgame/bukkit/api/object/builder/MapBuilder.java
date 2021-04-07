package net.airgame.bukkit.api.object.builder;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class MapBuilder<K, V> {
    private final HashMap<K, V> map;

    public MapBuilder() {
        map = new HashMap<>();
    }

    public MapBuilder(Map<K, V> map) {
        this.map = new HashMap<>(map);
    }

    public static <K, V> MapBuilder<K, V> with(K key, V value) {
        MapBuilder<K, V> builder = new MapBuilder<>();
        return builder.append(key, value);
    }

    public static <K, V> MapBuilder<K, V> with(Map<K, V> map) {
        return new MapBuilder<>(map);
    }

    public MapBuilder<K, V> append(K key, V value) {
        map.put(key, value);
        return this;
    }

    public HashMap<K, V> build() {
        return map;
    }
}
