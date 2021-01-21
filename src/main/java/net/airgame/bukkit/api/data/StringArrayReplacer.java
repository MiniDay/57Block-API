package net.airgame.bukkit.api.data;

import net.airgame.bukkit.api.util.StringUtils;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class StringArrayReplacer {
    private final String[] strings;

    public StringArrayReplacer(String[] strings) {
        this.strings = strings;
    }

    @NotNull
    public static StringArrayReplacer replace(@NotNull String[] strings, @NotNull String key, @NotNull String value) {
        return new StringArrayReplacer(strings).replace(key, value);
    }

    @NotNull
    public StringArrayReplacer replace(@NotNull String key, @NotNull String value) {
        StringUtils.replaceStringList(strings, key, value);
        return this;
    }

    @NotNull
    public String[] getStrings() {
        return strings;
    }
}
