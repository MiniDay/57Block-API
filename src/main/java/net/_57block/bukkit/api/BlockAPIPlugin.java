package net._57block.bukkit.api;

import net._57block.bukkit.api.command.annotation.CommandExecutor;
import net._57block.bukkit.api.command.annotation.CommandScan;
import net._57block.bukkit.api.command.executor.CommandHandler;
import net._57block.bukkit.api.command.parameter.ParameterParserManager;
import net._57block.bukkit.api.command.parameter.parser.*;
import net._57block.bukkit.api.command.parameter.parser.bukkit.*;
import net._57block.bukkit.api.listener.MainListener;
import net._57block.bukkit.api.util.EconomyAPI;
import net._57block.bukkit.api.util.LogUtils;
import net._57block.bukkit.api.util.PointAPI;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class BlockAPIPlugin extends JavaPlugin {
    private static LogUtils logUtils;
    private static BlockAPIPlugin instance;

    public static LogUtils getLogUtils() {
        return logUtils;
    }

    public static BlockAPIPlugin getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        long startTime = System.currentTimeMillis();
        instance = this;

        initLogUtil();
        logUtils.info("==================================================");
        loadLibraries();
        logUtils.info("==================================================");

        logUtils.info("插件载入完成. 总共耗时 %d 毫秒!", System.currentTimeMillis() - startTime);
    }

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        logUtils.info("==================================================");
        EconomyAPI.reloadEconomyHook();
        PointAPI.reloadPlayerPointAPIHook();
        logUtils.info("==================================================");
        initParameterParser();
        logUtils.info("==================================================");
        initCommand();
        logUtils.info("==================================================");
        Bukkit.getPluginManager().registerEvents(new MainListener(), this);

        logUtils.info("插件启动完成. 总共耗时 %d 毫秒!", System.currentTimeMillis() - startTime);
    }

    @Override
    public void onDisable() {
        logUtils.flush();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return super.onCommand(sender, command, label, args);
    }

    /**
     * 初始化日志器的设定
     */
    private void initLogUtil() {
        File defaultLogSettingsFile = new File(getDataFolder(), "defaultLogSettings.yml");
        if (!defaultLogSettingsFile.exists()) {
            saveResource("defaultLogSettings.yml", true);
        }
        LogUtils.DEFAULT_CONFIG = YamlConfiguration.loadConfiguration(defaultLogSettingsFile);

        logUtils = new LogUtils(this);
    }

    /**
     * 加载存放于 plugins/57Block-API/libs 中的第三方库文件
     * 仅 .jar 文件会被加载
     */
    private void loadLibraries() {
        logUtils.info("开始加载第三方库.");
        try {
            ClassLoader loader = getClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            File libFolder = new File(getDataFolder(), "libs");
            if (libFolder.mkdirs()) {
                logUtils.info("创建第三方库存放文件夹...");
            }
            File[] files = libFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    if (!fileName.endsWith(".jar")) {
                        logUtils.warning("跳过加载非 jar 拓展名的第三方库: %s", fileName);
                        continue;
                    }
                    method.invoke(loader, file.toURI().toURL());
                    logUtils.info("已加载第三方库: %s", fileName);
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | MalformedURLException e) {
            logUtils.error(e, "加载第三方库时遇到了一个错误: ");
        }
        logUtils.info("第三方库加载完成.");
    }

    /**
     * 初始化本 API 自带的一些命令参数解析器
     */
    private void initParameterParser() {
        logUtils.info("开始注册默认命令参数解析器.");
        ParameterParserManager.registerParser(boolean.class, BooleanParser.class);
        ParameterParserManager.registerParser(Boolean.class, BooleanParser.class);

        ParameterParserManager.registerParser(byte.class, ByteParser.class);
        ParameterParserManager.registerParser(Byte.class, ByteParser.class);

        ParameterParserManager.registerParser(int.class, IntegerParser.class);
        ParameterParserManager.registerParser(Integer.class, IntegerParser.class);

        ParameterParserManager.registerParser(long.class, LongParser.class);
        ParameterParserManager.registerParser(Long.class, LongParser.class);

        ParameterParserManager.registerParser(float.class, FloatParser.class);
        ParameterParserManager.registerParser(Float.class, FloatParser.class);

        ParameterParserManager.registerParser(double.class, DoubleParser.class);
        ParameterParserManager.registerParser(Double.class, DoubleParser.class);

        ParameterParserManager.registerParser(String.class, StringParser.class);
        ParameterParserManager.registerParser(String[].class, StringArrayParser.class);

        ParameterParserManager.registerParser(BlockFace.class, BlockFaceParser.class);
        ParameterParserManager.registerParser(GameMode.class, GameModeParser.class);
        ParameterParserManager.registerParser(Location.class, LocationParser.class);
        ParameterParserManager.registerParser(OfflinePlayer.class, OfflinePlayerParser.class);
        ParameterParserManager.registerParser(Player.class, PlayerParser.class);
        ParameterParserManager.registerParser(CommandSender.class, CommandSenderParser.class);
        ParameterParserManager.registerParser(World.class, WorldParser.class);
        logUtils.info("默认命令参数解析器注册完成.");
    }

    /**
     * 初始化命令
     */
    private void initCommand() {
        logUtils.info("开始初始化命令管理器.");

        ClassLoader classLoader = getClassLoader();
        SimpleCommandMap commandMap;
        Method findClassMethod;

        try {
            SimplePluginManager manager = (SimplePluginManager) Bukkit.getPluginManager();
            Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (SimpleCommandMap) commandMapField.get(manager);
            findClassMethod = classLoader.getClass().getDeclaredMethod("findClass", String.class);
            findClassMethod.setAccessible(true);
        } catch (Exception e) {
            logUtils.error(e, "初始化命令管理器时遇到了一个错误: ");
            return;
        }

        ArrayList<String> scanPackages = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (!(plugin instanceof JavaPlugin)) {
                continue;
            }
            CommandScan commandScan = plugin.getClass().getAnnotation(CommandScan.class);
            if (commandScan == null) {
                continue;
            }
            scanPackages.addAll(Arrays.asList(commandScan.value()));
            logUtils.info("已添加插件 %s 需要扫描的包: ", plugin.getName());
            for (String s : commandScan.value()) {
                logUtils.info(s);
            }
        }
        logUtils.info("==================================================");

        HashMap<Plugin, ArrayList<String>> scanPlugin = new HashMap<>();
        try {
            Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                if (!(plugin instanceof JavaPlugin)) {
                    continue;
                }
                Enumeration<JarEntry> entries = new JarFile((File) getFileMethod.invoke(plugin)).entries();

                ArrayList<String> classNames = new ArrayList<>();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (!entryName.endsWith(".class")) {
                        continue;
                    }
                    String s = entryName.replace("/", ".");
                    s = s.substring(0, s.length() - 6);
                    for (String packageName : scanPackages) {
                        if (s.startsWith(packageName)) {
                            classNames.add(s);
                        }
                    }
                }

                if (classNames.isEmpty()) {
                    continue;
                }
                scanPlugin.put(plugin, classNames);
            }
        } catch (Exception e) {
            logUtils.error(e, "从插件的Java包中扫描命令执行器时遇到了一个异常: ");
        }

        for (Map.Entry<Plugin, ArrayList<String>> entry : scanPlugin.entrySet()) {
            Plugin plugin = entry.getKey();
            ArrayList<String> classNames = entry.getValue();
            logUtils.info("开始扫描插件 %s", plugin.getName());
            for (String className : classNames) {
                try {
                    Class<?> clazz = (Class<?>) findClassMethod.invoke(classLoader, className);
                    CommandExecutor annotation = clazz.getAnnotation(CommandExecutor.class);
                    if (annotation == null) {
                        continue;
                    }
                    CommandHandler handler = CommandHandler.generatorCommandHandler(clazz);
                    commandMap.register(plugin.getName(), handler);
//                    handler.register(commandMap);
                } catch (Exception | Error e) {
                    logUtils.error(e, "初始化插件 %s 的命令管理器 %s 时遇到了一个错误: ", plugin.getName(), className);
                }

            }
        }
        logUtils.info("命令管理器初始化完成.");
    }

}
