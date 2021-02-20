package net.airgame.bukkit.api.util.replacer;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ItemStackReplacer {
    private final ItemStack stack;
    private final ArrayList<Map.Entry<String, String>> replaces;

    public ItemStackReplacer(@NotNull ItemStack stack) {
        this.stack = stack;
        replaces = new ArrayList<>();
    }

    @NotNull
    public static ItemStackReplacer replace(@NotNull ItemStack stack, @NotNull String key, @NotNull String value) {
        return new ItemStackReplacer(stack).replace(key, value);
    }

    @NotNull
    public ItemStackReplacer replace(@NotNull String key, @NotNull String value) {
        replaces.add(new AbstractMap.SimpleEntry<>(key, value));
        return this;
    }

    public ItemStack getStack() {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return stack;
        }
        String name = meta.getDisplayName();
        for (Map.Entry<String, String> entry : replaces) {
            name = name.replace(entry.getKey(), entry.getValue());
        }
        meta.setDisplayName(name);
        List<String> lore = meta.getLore();
        if (lore != null) {
            for (int i = 0; i < lore.size(); i++) {
                String s = lore.get(i);
                for (Map.Entry<String, String> entry : replaces) {
                    s = s.replace(entry.getKey(), entry.getValue());
                }
                lore.set(i, s);
            }
            meta.setLore(lore);
        }
        stack.setItemMeta(meta);
        return stack;
    }

}
