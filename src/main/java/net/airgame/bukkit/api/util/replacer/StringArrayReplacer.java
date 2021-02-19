package net.airgame.bukkit.api.util.replacer;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("unused")
public class StringArrayReplacer {
    private final String[] strings;
    private final ArrayList<Map.Entry<String, String>> replaces;

    public StringArrayReplacer(String[] strings) {
        this.strings = strings;
        replaces = new ArrayList<>();
    }

    @NotNull
    public static StringArrayReplacer replace(@NotNull String[] strings, @NotNull String key, @NotNull String value) {
        return new StringArrayReplacer(strings).replace(key, value);
    }

    @NotNull
    public StringArrayReplacer replace(@NotNull String key, @NotNull String value) {
        replaces.add(new AbstractMap.SimpleEntry<>(key, value));
        return this;
    }

    @NotNull
    public String[] getStrings() {
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            for (Map.Entry<String, String> entry : replaces) {
                s = s.replace(entry.getKey(), entry.getValue());
            }
            strings[i] = s;
        }
        return strings;
    }
}
