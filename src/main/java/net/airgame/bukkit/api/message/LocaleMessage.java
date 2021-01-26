package net.airgame.bukkit.api.message;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;

public class LocaleMessage {
    private final String locale;
    private final HashMap<String, MessageEntry> messages;
    private final ArrayList<Object> components;

    public LocaleMessage(String locale, ConfigurationSection config) {
        this.locale = locale;
        messages = new HashMap<>();
        components = new ArrayList<>();
    }

    public String getLocale() {
        return locale;
    }

    public MessageEntry getMessage(String key) {
        return messages.get(key);
    }
}
