package net.airgame.bukkit.api.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class MessageUtils {

    /**
     * 向玩家发送一条 actionBar 消息
     *
     * @param player  玩家
     * @param message 要发送的消息
     */
    public static void sendActionBar(@NotNull Player player, @NotNull String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    @SuppressWarnings("deprecation")
    public static TextComponent getItemDisplayInfo(String startText, ItemStack stack, String endText) {
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ITEM, getItemInfo(stack));

        TextComponent startComponent = new TextComponent(startText);
        startComponent.setHoverEvent(hoverEvent);
        TextComponent endComponent = new TextComponent(endText);
        endComponent.setHoverEvent(hoverEvent);

        BaseComponent itemComponent = getItemNameComponent(stack);
        itemComponent.setHoverEvent(hoverEvent);

        return new TextComponent(
                new ComponentBuilder()
                        .append(startComponent)
                        .append(itemComponent)
                        .append(endComponent)
                        .create()
        );
    }

    @SuppressWarnings("ConstantConditions")
    public static BaseComponent getItemNameComponent(ItemStack stack) {
        if (ItemUtils.isEmptyItemStack(stack)) {
            return new TranslatableComponent("block.minecraft.air");
        } else if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
            return new TextComponent(ItemUtils.getItemName(stack));
        }
        Material type = stack.getType();
        if (type.isBlock()) {
            return new TranslatableComponent("block.minecraft." + type.name().toLowerCase());
        } else {
            return new TranslatableComponent("item.minecraft." + type.name().toLowerCase());
        }
    }

    public static BaseComponent[] getItemInfo(ItemStack stack) {
        try {
            Class<?> nBTTagCompound = Class.forName("net.minecraft.server." + AirUtils.nmsVersion + ".NBTTagCompound");
            Object nBTTag = nBTTagCompound.newInstance();
            Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + AirUtils.nmsVersion + ".inventory.CraftItemStack");
            Method asNMSCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
            Object nmsItem = asNMSCopy.invoke(null, stack);
            Method saveMethod = nmsItem.getClass().getMethod("save", nBTTagCompound);
            saveMethod.invoke(nmsItem, nBTTag);
            return new ComponentBuilder(nBTTag.toString()).create();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ComponentBuilder("物品解析失败").create();
    }
}
