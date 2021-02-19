package net.airgame.bukkit.api.util.replacer;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class StringListReplacer {
    private final List<String> list;
    private final ArrayList<Map.Entry<String, String>> replaces;

    public StringListReplacer(@NotNull List<String> list) {
        this.list = list;
        replaces = new ArrayList<>();
    }

    @NotNull
    public static StringListReplacer replace(@NotNull List<String> list, @NotNull String key, @NotNull String value) {
        return new StringListReplacer(list).replace(key, value);
    }

    @NotNull
    public StringListReplacer replace(@NotNull String key, @NotNull String value) {
        replaces.add(new AbstractMap.SimpleEntry<>(key, value));
        return this;
    }

    @NotNull
    public List<String> getList() {
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            for (Map.Entry<String, String> entry : replaces) {
                s = s.replace(entry.getKey(), entry.getValue());
            }
            list.set(i, s);
        }
        return list;
    }
}
