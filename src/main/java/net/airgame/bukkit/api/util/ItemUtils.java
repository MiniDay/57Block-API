package net.airgame.bukkit.api.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
    /**
     * 判断物品是否为空
     * 当对象为null时返回true
     * 当物品的Material为AIR时返回true
     * 当物品的数量小于1时返回true
     *
     * @param stack 物品
     * @return 是否为空
     */
    public static boolean isEmptyItemStack(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR || stack.getAmount() < 1;
    }

    /**
     * 获取物品的名称
     * 当物品为null时返回"null"
     * 当物品拥有DisplayName时返回DisplayName
     * 否则返回物品的Material的name
     *
     * @param stack 物品
     * @return 物品的名称
     */
    @SuppressWarnings("ConstantConditions")
    public static String getItemName(ItemStack stack) {
        if (stack == null) {
            return "null";
        }
        if (stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            if (meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
        }
        return stack.getType().name();
    }
}
