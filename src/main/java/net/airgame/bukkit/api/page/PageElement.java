package net.airgame.bukkit.api.page;

import net.airgame.bukkit.api.page.handler.PageableHandler;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

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

    String replaceDisplayName(HumanEntity player, String displayName);

    default List<String> replaceLore(HumanEntity player, List<String> lore) {
        if (lore == null) {
            return null;
        }
        return lore.stream().map(s -> replaceDisplayName(player, s)).collect(Collectors.toList());
    }
}
