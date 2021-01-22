package net.airgame.bukkit.api.gui;

import net.airgame.bukkit.api.gui.handler.PageableHandler;
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

    String replaceDisplayName(HumanEntity player, String displayName);

    List<String> replaceLore(HumanEntity player, List<String> lore);
}
