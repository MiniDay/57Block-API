package net.airgame.bukkit.api.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.airgame.bukkit.api.AirGamePlugin;
import net.airgame.bukkit.api.manager.PersistenceManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("unused")
public class ChatTextUtils {

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
            Class<?> nBTTagCompound = Class.forName("net.minecraft.server." + AirGameUtils.NMS_VERSION + ".NBTTagCompound");
            Object nBTTag = nBTTagCompound.newInstance();
            Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + AirGameUtils.NMS_VERSION + ".inventory.CraftItemStack");
            Method asNMSCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
            Object nmsItem = asNMSCopy.invoke(null, stack);
            Method saveMethod = nmsItem.getClass().getMethod("save", nBTTagCompound);
            saveMethod.invoke(nmsItem, nBTTag);
            return new ComponentBuilder(nBTTag.toString()).create();
        } catch (Exception e) {
            AirGamePlugin.getLogUtils().error(e, "组建物品文本信息时出现了一个异常:");
        }
        return new ComponentBuilder("物品解析失败").create();
    }

    public static BaseComponent[] parseComponentFromJson(String json) {
        return parseComponentFromJson(PersistenceManager.getParser().parse(json));
    }

    public static BaseComponent[] parseComponentFromJson(JsonElement json) {
        if (json.isJsonPrimitive()) {
            return new ComponentBuilder(json.getAsString()).create();
        }
        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            ArrayList<BaseComponent> list = new ArrayList<>();
            for (JsonElement element : array) {
                Collections.addAll(list, parseComponentFromJson(element));
            }
            BaseComponent[] components = new BaseComponent[list.size()];
            for (int i = 0; i < list.size(); i++) {
                components[i] = list.get(i);
            }
            return components;
        }
        if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            ComponentBuilder builder = new ComponentBuilder(object.get("text").getAsString());
            if (object.has("color")) {
                builder.color(getColorByName(object.get("color").getAsString()));
            }
            if (object.has("bold")) {
                builder.bold(object.get("bold").getAsBoolean());
            }
            if (object.has("italic")) {
                builder.italic(object.get("italic").getAsBoolean());
            }
            if (object.has("underlined")) {
                builder.underlined(object.get("underlined").getAsBoolean());
            }
            if (object.has("strikethrough")) {
                builder.strikethrough(object.get("strikethrough").getAsBoolean());
            }
            if (object.has("obfuscated")) {
                builder.obfuscated(object.get("obfuscated").getAsBoolean());
            }
            if (object.has("insertion")) {
                builder.insertion(object.get("insertion").getAsString());
            }
            if (object.has("clickEvent")) {
                builder.event(parseClickEvent(object.getAsJsonObject("clickEvent")));
            }
            if (object.has("hoverEvent")) {
                builder.event(parseHoverEvent(object.getAsJsonObject("hoverEvent")));
            }
            if (object.has("extra")) {
                builder.append(parseComponentFromJson(object.get("extra")));
            }
            return builder.create();
        }
        throw new IllegalArgumentException("非法json字符串: " + json);
    }

    private static ClickEvent parseClickEvent(JsonObject object) {
        return new ClickEvent(
                ClickEvent.Action.valueOf(
                        object
                                .get("action")
                                .getAsString()
                                .toUpperCase()
                ),
                object.get("value").getAsString()
        );
    }

    @SuppressWarnings("deprecation")
    private static HoverEvent parseHoverEvent(JsonObject object) {
        return new HoverEvent(
                HoverEvent.Action.valueOf(
                        object
                                .get("action")
                                .getAsString()
                                .toUpperCase()
                ),
                parseComponentFromJson(object.get("value"))
        );
    }

    @SuppressWarnings("deprecation")
    private static ChatColor getColorByName(String name) {
        for (ChatColor value : ChatColor.values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
}
