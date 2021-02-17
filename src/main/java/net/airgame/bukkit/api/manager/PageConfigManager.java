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
        AirGameAPI.getLogUtils().info("开始扫描插件 %s 中的包 %s", plugin.getName(), packageName);
        ArrayList<String> pageClassNames = new ArrayList<>();
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
                String classSimpleName = entryName.replace("/", ".");
                classSimpleName = classSimpleName.substring(0, classSimpleName.length() - 6);
                if (classSimpleName.startsWith(packageName)) {
                    pageClassNames.add(classSimpleName);
                }
            }
        } catch (Exception e) {
            AirGameAPI.getLogUtils().error(e, "从插件的Java包中扫描界面设定时遇到了一个异常: ");
        }
        for (String pageClassName : pageClassNames) {
            String yamlName = pageClassName.substring(pageClassName.lastIndexOf('.') + 1) + ".yml";
            File file = new File(plugin.getDataFolder(), yamlName);
            // 如果插件存档文件夹和插件 jar 包内都不存在该 yaml 文件则跳过
            if (!file.exists() && plugin.getResource(yamlName) == null) {
                continue;
            }
            // 如果插件存档文件夹内不存在该文件则生成一份
            if (!file.exists()) {
                plugin.saveResource(yamlName, true);
            }
            try {
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

    @SuppressWarnings("unused")
    public static void registerPageConfig(@NotNull Class<? extends Handler> clazz, @NotNull PageConfig config) {
        registerPageConfig(clazz.getName(), config);
        AirGameAPI.getLogUtils().info("已注册 %s 的界面设置.", clazz.getName());
    }

    public static PageConfig getPageConfig(@NotNull Class<? extends Handler> clazz) {
        return pageConfigs.get(clazz.getName());
    }
}
