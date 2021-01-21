package net.airgame.bukkit.api.data;

import net.airgame.bukkit.api.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class StringListReplacer {
    private final List<String> list;

    public StringListReplacer(@NotNull List<String> list) {
        this.list = list;
    }

    @NotNull
    public static StringListReplacer replace(@NotNull List<String> list, @NotNull String key, @NotNull String value) {
        return new StringListReplacer(list).replace(key, value);
    }

    @NotNull
    public StringListReplacer replace(@NotNull String key, @NotNull String value) {
        StringUtils.replaceStringList(list, key, value);
        return this;
    }

    @NotNull
    public List<String> getList() {
        return list;
    }
}
