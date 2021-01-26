package net.airgame.bukkit.api.message;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;

/**
 * 代表一条消息上的附加参数
 * <p>
 * 例如 悬停事件 或 点击事件
 * <p>
 * （↑实际上也只有这两个事件
 */
public class MessageElement {
    private final String name;
    private final ElementType type;
    private final String action;
    private final String value;

    public MessageElement(ConfigurationSection config) {
        name = config.getName();
        type = ElementType.valueOf(config.getString("type"));
        action = config.getString("action", "white");
        value = config.getString("value");
    }

    public MessageElement(String name, ElementType type, String action, String value) {
        this.name = name;
        this.type = type;
        this.action = action;
        this.value = value;
    }

    public void set(JsonObject object) {
        JsonObject elementObject = new JsonObject();
        elementObject.addProperty("action", action);
        elementObject.addProperty("value", value);
        object.add(type.name(), elementObject);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "MessageElement{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", action='" + action + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public enum ElementType {
        hoverEvent,
        clickEvent
    }
}
