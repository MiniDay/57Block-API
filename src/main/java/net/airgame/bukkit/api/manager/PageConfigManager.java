package net.airgame.bukkit.api.manager;

import net.airgame.bukkit.api.AirGameAPI;
import net.airgame.bukkit.api.page.PageConfig;
import net.airgame.bukkit.api.page.handler.Handler;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PageConfigManager {
    private static final HashMap<String, PageConfig> pageConfigs = new HashMap<>();

    public static void registerPageConfig(JavaPlugin plugin, String packageName) {
        AirGameAPI.getLogUtils().info("开始扫描插件 %s", plugin.getName());
        ArrayList<String> classes = new ArrayList<>();
        try {
            Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);

            Enumeration<JarEntry> entries = new JarFile((File) getFileMethod.invoke(plugin)).entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (!entryName.endsWith(".class")) {
                    continue;
                }
                String s = entryName.replace("/", ".");
                s = s.substring(0, s.length() - 6);
                if (s.startsWith(packageName)) {
                    classes.add(s);
                }
            }
        } catch (Exception e) {
            AirGameAPI.getLogUtils().error(e, "从插件的Java包中扫描界面设定时遇到了一个异常: ");
        }
        for (String pageClassName : classes) {
            String yamlName = pageClassName.substring(pageClassName.lastIndexOf('.') + 1) + ".yml";
            File file = new File(plugin.getDataFolder(), yamlName);

            try {
                if (!file.exists() && plugin.getResource(yamlName) != null) {
                    plugin.saveResource(yamlName, false);
                }
                registerPageConfig(pageClassName, new PageConfig(YamlConfiguration.loadConfiguration(file)));
            } catch (Exception e) {
                AirGameAPI.getLogUtils().error(e, "注册插件 %s 的界面设定 %s 时遇到了一个异常: ", plugin.getName(), yamlName);
            }
        }
    }

    public static void registerPageConfig(@NotNull String className, @NotNull PageConfig config) {
        pageConfigs.put(className, config);
        AirGameAPI.getLogUtils().info("已注册 %s 的界面设置.", className);
    }

    public static void registerPageConfig(@NotNull Class<? extends Handler> clazz, @NotNull PageConfig config) {
        registerPageConfig(clazz.getName(), config);
    }

    public static PageConfig getPageConfig(@NotNull Class<? extends Handler> clazz) {
        return pageConfigs.get(clazz.getName());
    }
}
