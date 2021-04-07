package net.airgame.bukkit.api.page;

import net.airgame.bukkit.api.page.handler.PageableHandler;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @see PageableHandler#initPage()
 */
@SuppressWarnings("unused")
public interface PageElement {

    /**
     * 获取展示物品
     * <p>
     * 若返回 null 则使用 config 中的全局设置值
     *
     * @param player 占位符显示的目标玩家
     * @return 展示物品
     */
    default ItemStack getDisplayItem(HumanEntity player) {
        return null;
    }

    /**
     * 替换物品的信息
     *
     * @param player 玩家
     * @param stack  物品
     */
    default void replaceItemInfo(HumanEntity player, ItemStack stack) {
        if (stack == null) {
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }

        replaceMetaInfo(player, meta);

        stack.setItemMeta(meta);
    }

    /**
     * 替换物品的信息
     *
     * @param player 玩家
     * @param meta   物品信息
     */
    default void replaceMetaInfo(HumanEntity player, ItemMeta meta) {
        if (meta == null) {
            return;
        }

        Map<String, String> replacer = getReplacer(player);
        if (replacer == null) {
            return;
        }

        String displayName = meta.getDisplayName();
        for (Map.Entry<String, String> entry : replacer.entrySet()) {
            displayName = displayName.replace(entry.getKey(), entry.getValue());
        }

        List<String> lore = meta.getLore();
        if (lore != null) {
            for (int i = 0; i < lore.size(); i++) {
                String s = lore.get(i);
                for (Map.Entry<String, String> entry : replacer.entrySet()) {
                    s = s.replace(entry.getKey(), entry.getValue());
                }
                lore.set(i, s);
            }
            meta.setLore(lore);
        }
    }

    default String replaceDisplayName(HumanEntity player, String displayName) {
        return replacePlaceholder(player, displayName);
    }

    default List<String> replaceLore(HumanEntity player, List<String> lore) {
        if (lore == null) {
            return null;
        }
        Map<String, String> replacer = getReplacer(player);
        if (replacer == null) {
            return lore;
        }
        for (int i = 0; i < lore.size(); i++) {
            String s = lore.get(i);
            for (Map.Entry<String, String> entry : replacer.entrySet()) {
                s = s.replace(entry.getKey(), entry.getValue());
            }
            lore.set(i, s);
        }
        return lore;
    }

    default String replacePlaceholder(HumanEntity player, String string) {
        Map<String, String> replacer = getReplacer(player);
        if (replacer == null) {
            return string;
        }
        for (Map.Entry<String, String> entry : replacer.entrySet()) {
            string = string.replace(entry.getKey(), entry.getValue());
        }
        return string;
    }

    @Nullable
    default Map<String, String> getReplacer(HumanEntity player) {
        return null;
    }

}
