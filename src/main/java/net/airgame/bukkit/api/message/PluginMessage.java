package net.airgame.bukkit.api.message;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class PluginMessage {
    private Plugin plugin;
    private LocaleMessage defaultLocale;
    private ArrayList<LocaleMessage> localeMessages;

    public PluginMessage() {

    }

    public Plugin getPlugin() {
        return plugin;
    }

    public LocaleMessage getLocaleMessage(String locale) {
        for (LocaleMessage localeMessage : localeMessages) {
            if (localeMessage.getLocale().equals(locale)) {
                return localeMessage;
            }
        }
        return defaultLocale;
    }

}
