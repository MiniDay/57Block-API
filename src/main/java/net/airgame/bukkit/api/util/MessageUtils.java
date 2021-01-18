package net.airgame.bukkit.api.util;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class MessageUtils {
    private static final boolean useOldMethods;

    static {
        useOldMethods = AirUtils.nmsVersion.equalsIgnoreCase("v1_8_R1") || AirUtils.nmsVersion.startsWith("v1_7_");
    }

    /**
     * 向玩家发送一条 actionBar 消息
     *
     * @param player  玩家
     * @param message 要发送的消息
     */
    public static void sendActionBar(@NotNull Player player, @NotNull String message) {
        String nmsVersion = AirUtils.nmsVersion;
        if (!player.isOnline()) {
            return;
        }
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object packet;
            Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
            Class<?> packetClass = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
            if (useOldMethods) {
                Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + nmsVersion + ".ChatSerializer");
                Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");
                Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
                Object cbc = iChatBaseComponentClass.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));
                packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(cbc, (byte) 2);
            } else {
                Class<?> chatComponentTextClass = Class.forName("net.minecraft.server." + nmsVersion + ".ChatComponentText");
                Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");
                try {
                    Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + nmsVersion + ".ChatMessageType");
                    Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
                    Object chatMessageType = null;
                    for (Object obj : chatMessageTypes) {
                        if (obj.toString().equals("GAME_INFO")) {
                            chatMessageType = obj;
                        }
                    }
                    Object chatTest = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                    packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, chatMessageTypeClass}).newInstance(chatTest, chatMessageType);
                } catch (ClassNotFoundException e) {
                    Object chatTest = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                    packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(chatTest, (byte) 2);
                }
            }
            Method craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
            Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer);
            Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(craftPlayerHandle);
            Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
