package net.airgame.bukkit.api.page;

import net.airgame.bukkit.api.page.handler.PageableHandler;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * @see PageableHandler#initPage()
 */
@SuppressWarnings("unused")
public interface PageElement {

    default void replaceInfo(HumanEntity player, ItemStack stack) {
        if (stack == null) {
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.setDisplayName(replaceDisplayName(player, meta.getDisplayName()));
        List<String> lore = meta.getLore();
        if (lore != null) {
            meta.setLore(replaceLore(player, lore));
        }
        stack.setItemMeta(meta);
    }

    default boolean replaceItem(HumanEntity player, ItemStack stack) {
        return false;
    }

    default boolean replaceMeta(HumanEntity player, ItemMeta meta) {
        return false;
    }

    default String replaceDisplayName(HumanEntity player, String displayName) {
        return replacePlaceholder(player, displayName);
    }

    default List<String> replaceLore(HumanEntity player, List<String> lore) {
        if (lore == null) {
            return null;
        }
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, replacePlaceholder(player, lore.get(i)));
        }
        return lore;
    }

    String replacePlaceholder(HumanEntity player, String string);
}
