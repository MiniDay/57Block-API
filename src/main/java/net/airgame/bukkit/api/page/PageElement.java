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
    default boolean replaceItem(ItemStack stack) {
        return false;
    }

    default boolean replaceMeta(ItemMeta meta) {
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
