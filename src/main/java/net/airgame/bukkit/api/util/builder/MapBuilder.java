package net.airgame.bukkit.api.util.builder;

import java.util.HashMap;

@SuppressWarnings("unused")
public class MapBuilder<K, V> {
    private final HashMap<K, V> map;

    public MapBuilder() {
        this(new HashMap<>());
    }

    public MapBuilder(HashMap<K, V> map) {
        this.map = map;
    }

    public static <K, V> MapBuilder<K, V> with(K key, V value) {
        MapBuilder<K, V> builder = new MapBuilder<>();
        return builder.append(key, value);
    }

    public MapBuilder<K, V> append(K key, V value) {
        map.put(key, value);
        return this;
    }

    public HashMap<K, V> build() {
        return map;
    }
}
