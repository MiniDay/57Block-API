package net.airgame.bukkit.api.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

@SuppressWarnings("unused")
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
    public static String getItemName(ItemStack stack) {
        if (stack == null) {
            return "null";
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        return stack.getType().name();
    }

    /**
     * 获取玩家的头颅
     * 在1.11以上的服务端中获取头颅材质是在服务器上运行的
     * 因此建议使用异步线程调用该方法
     *
     * @param uuid 要获取的玩家
     * @return 玩家的头颅物品
     */
    public static ItemStack getPlayerHead(UUID uuid) {
        return getPlayerHead(Bukkit.getOfflinePlayer(uuid));
    }

    /**
     * 获取玩家的头颅
     * 在1.11以上的服务端中获取头颅材质是在服务器上运行的
     * 因此建议使用异步线程调用该方法
     *
     * @param offlinePlayer 要获取的玩家
     * @return 玩家的头颅物品
     */
    @SuppressWarnings("deprecation")
    public static ItemStack getPlayerHead(OfflinePlayer offlinePlayer) {
        ItemStack stack;
        try {
            stack = new ItemStack(Material.valueOf("PLAYER_HEAD"));
        } catch (IllegalArgumentException e) {
            stack = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(offlinePlayer);
        }
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * 获取玩家的头颅
     * 在1.11以上的服务端中获取头颅材质是在服务器上运行的
     * 因此建议使用异步线程调用该方法
     *
     * @param name 要获取的玩家
     * @return 玩家的头颅物品
     */
    @SuppressWarnings({"ConstantConditions", "deprecation"})
    public static ItemStack getPlayerHead(String name) {
        ItemStack stack;
        try {
            stack = new ItemStack(Material.valueOf("PLAYER_HEAD"));
        } catch (IllegalArgumentException e) {
            stack = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(name);
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * 获取该物品是否拥有耐久条
     *
     * @param material 物品材料
     * @return 该物品是否拥有耐久条
     */
    public static boolean isDamageable(Material material) {
        switch (material) {
            case FLINT_AND_STEEL:
            case FISHING_ROD:
            case SHEARS:
            case BOW:

            case WOODEN_SHOVEL:
            case STONE_SHOVEL:
            case IRON_SHOVEL:
            case GOLDEN_SHOVEL:
            case DIAMOND_SHOVEL:
            case NETHERITE_SHOVEL:

            case WOODEN_PICKAXE:
            case STONE_PICKAXE:
            case IRON_PICKAXE:
            case GOLDEN_PICKAXE:
            case DIAMOND_PICKAXE:
            case NETHERITE_PICKAXE:

            case WOODEN_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case GOLDEN_AXE:
            case DIAMOND_AXE:
            case NETHERITE_AXE:

            case WOODEN_HOE:
            case STONE_HOE:
            case IRON_HOE:
            case GOLDEN_HOE:
            case DIAMOND_HOE:
            case NETHERITE_HOE:

            case WOODEN_SWORD:
            case STONE_SWORD:
            case IRON_SWORD:
            case GOLDEN_SWORD:
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:

            case LEATHER_BOOTS:
            case CHAINMAIL_BOOTS:
            case IRON_BOOTS:
            case GOLDEN_BOOTS:
            case DIAMOND_BOOTS:
            case NETHERITE_BOOTS:

            case LEATHER_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case IRON_LEGGINGS:
            case GOLDEN_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case NETHERITE_LEGGINGS:

            case LEATHER_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case IRON_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case NETHERITE_CHESTPLATE:

            case LEATHER_HELMET:
            case CHAINMAIL_HELMET:
            case IRON_HELMET:
            case GOLDEN_HELMET:
            case DIAMOND_HELMET:
            case NETHERITE_HELMET:
                return true;
        }
        return false;
    }
}
