package net.airgame.bukkit.api.manager;

import org.bukkit.plugin.Plugin;

import java.io.File;

public class MessageManager {

    public static void init() {

    }

    public static void load(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File subFile : files) {
                load(subFile);
            }
            return;
        }
        System.out.println("加载文件: " + file);
    }

    public static void load(Plugin plugin) {

    }
}
