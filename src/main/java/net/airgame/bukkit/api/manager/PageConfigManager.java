package net.airgame.bukkit.api.manager;

import net.airgame.bukkit.api.AirGameAPI;
import net.airgame.bukkit.api.annotation.PageScan;
import net.airgame.bukkit.api.page.PageConfig;
import net.airgame.bukkit.api.page.handler.PageHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PageConfigManager {
    private static final HashMap<String, PageConfig> pageConfigs = new HashMap<>();
    private static Method getFileMethod;

    /**
     * 初始化这个页面管理器
     */
    public static void init() {
        AirGameAPI.getLogUtils().info("开始初始化页面管理器.");
        try {
            getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
            AirGameAPI.getLogUtils().info("已获取 getFile 方法: %s", getFileMethod);
        } catch (Exception e) {
            AirGameAPI.getLogUtils().error(e, "初始化页面管理器时遇到了一个错误: ");
        }
        AirGameAPI.getLogUtils().info("页面管理器初始化完成.");
    }

    public static void reload() {
        pageConfigs.clear();

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (!(plugin instanceof JavaPlugin)) {
                continue;
            }
            PageScan pageScan = plugin.getClass().getAnnotation(PageScan.class);
            if (pageScan == null) {
                continue;
            }
            for (String packageName : pageScan.value()) {
                try {
                    PageConfigManager.registerPageConfig((JavaPlugin) plugin, packageName);
                } catch (Exception e) {
                    AirGameAPI.getLogUtils().error(e, "从插件 %s 的Java包中扫描界面设定时遇到了一个异常: ", plugin.getName());
                }
            }
        }
    }

    public static void registerPageConfig(JavaPlugin plugin, String packageName) throws IOException, InvocationTargetException, IllegalAccessException {
        AirGameAPI.getLogUtils().info("开始扫描插件 %s 中的包 %s", plugin.getName(), packageName);
        Enumeration<JarEntry> entries = new JarFile((File) getFileMethod.invoke(plugin)).entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();

            if (!entryName.endsWith(".class")) {
                continue;
            }

            // 类所在的路径
            String classPath = entryName.replace("/", ".");
            // 去掉 .class 后缀变成全限定类名
            String className = classPath.substring(0, classPath.length() - 6);

            // 舍弃不是以 packageName 开头的类
            if (!className.startsWith(packageName)) {
                continue;
            }

            // 跳过匿名内部类
            if (className.contains("$")) {
                continue;
            }

            YamlConfiguration config = getPageConfig(plugin, packageName, className);
            if (config == null) {
                continue;
            }

            registerPageConfig(className, new PageConfig(config));
        }
    }

    @SuppressWarnings("unused")
    public static void registerPageConfig(@NotNull Class<? extends PageHandler> clazz, @NotNull PageConfig config) {
        registerPageConfig(clazz.getName(), config);
    }

    public static void registerPageConfig(@NotNull String className, @NotNull PageConfig config) {
        pageConfigs.put(className, config);
        AirGameAPI.getLogUtils().info("已注册 %s 的界面设置.", className);
    }

    public static PageConfig getPageConfig(@NotNull Class<? extends PageHandler> clazz) {
        return pageConfigs.get(clazz.getName());
    }

    /**
     * 从插件中获取 Page 类的配置文件
     *
     * @param plugin      插件对象
     * @param packageName 插件所在的包名
     * @param className   Page 类的全限定类名
     * @return Page 的配置文件
     */
    private static YamlConfiguration getPageConfig(JavaPlugin plugin, String packageName, String className) {
        File pluginDataFolder = plugin.getDataFolder();

        // 配置文件的名称以类的 SimpleName + ".yml" 构成
        String yamlFileName = className.substring(className.lastIndexOf('.') + 1) + ".yml";

        File yamlFile = new File(pluginDataFolder, yamlFileName);

        // 尝试搜索插件存档文件夹根目录
        if (yamlFile.exists()) {
            return YamlConfiguration.loadConfiguration(yamlFile);
        }

        // 尝试搜索插件 jar 文件内的根目录
        if (plugin.getResource(yamlFileName) != null) {
            plugin.saveResource(yamlFileName, true);
            return YamlConfiguration.loadConfiguration(yamlFile);
        }

        // yaml文件所在的子目录，获取较为复杂
        // 当 packageName 为 "net.airgame.bukkit.alliance.page" 时
        //   若 className 为 "net.airgame.bukkit.alliance.page.PageAlliance"
        //   则 yamlFileFolderName 为 ""
        //   若 className 为 "net.airgame.bukkit.alliance.page.permission.PageAlliance"
        //   则 yamlFileFolderName 为 "permission/"
        String yamlFileFolderName = className
                .substring(0, className.lastIndexOf('.') + 1)
                .replace(packageName + ".", "")
                .replace(".", "/");

        File yamlFileFolder = new File(pluginDataFolder, yamlFileFolderName);

        yamlFile = new File(yamlFileFolder, yamlFileName);

        // 尝试搜索插件存档文件夹的 page 对应搜索包名的子目录
        if (yamlFile.exists()) {
            return YamlConfiguration.loadConfiguration(yamlFile);
        }

        // 尝试搜索插件 jar 文件内 page 对应搜索包名的子目录
        InputStream inputStream = plugin.getResource(yamlFileFolderName + yamlFileName);
        if (inputStream != null) {
            if (yamlFileFolder.mkdirs()) {
                AirGameAPI.getLogUtils().info("为插件 %s 创建 Page 文件夹 %s.", plugin.getName(), yamlFileFolder.getName());
            }
            try {
                Files.copy(
                        inputStream,
                        yamlFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 尝试搜索配置文件是否放置在 Page 类的相同 Java 包下
        String packageYamlFilePath = className.replace("*", "/") + ".yml";
        inputStream = plugin.getResource(packageYamlFilePath);
        if (inputStream != null) {
            if (yamlFileFolder.mkdirs()) {
                AirGameAPI.getLogUtils().info("为插件 %s 创建 Page 文件夹 %s.", plugin.getName(), yamlFileFolder.getName());
            }
            try {
                Files.copy(
                        inputStream,
                        yamlFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
