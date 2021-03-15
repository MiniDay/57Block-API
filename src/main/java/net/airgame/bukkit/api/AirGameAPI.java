package net.airgame.bukkit.api;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import net.airgame.bukkit.api.annotation.CommandScan;
import net.airgame.bukkit.api.command.parameter.ParameterParserManager;
import net.airgame.bukkit.api.command.parameter.parser.*;
import net.airgame.bukkit.api.command.parameter.parser.bukkit.*;
import net.airgame.bukkit.api.hook.PointAPI;
import net.airgame.bukkit.api.hook.VaultAPI;
import net.airgame.bukkit.api.listener.ConversationListener;
import net.airgame.bukkit.api.listener.PageListener;
import net.airgame.bukkit.api.listener.PluginHookListener;
import net.airgame.bukkit.api.manager.CommandManager;
import net.airgame.bukkit.api.manager.PageConfigManager;
import net.airgame.bukkit.api.manager.PersistenceManager;
import net.airgame.bukkit.api.page.handler.PageHandler;
import net.airgame.bukkit.api.util.AirUtils;
import net.airgame.bukkit.api.util.LogUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

@CommandScan("net.airgame.bukkit.api.command.preset")
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class AirGameAPI extends JavaPlugin {
    private static AirGameAPI instance;
    private static LogUtils logUtils;
    private static String defaultLocale;
    private PersistenceManager persistenceManager;

    public static AirGameAPI getInstance() {
        return instance;
    }

    public static LogUtils getLogUtils() {
        return logUtils;
    }

    /**
     * 在主线程上执行一些代码
     * <p>
     * 没事别调用这个方法，要用你自己在你的插件主类里再写一个
     * <p>
     * 否则服务器的 timings 可能会把你的代码运行时间也算进这个插件中
     *
     * @param runnable 执行代码
     * @return BukkitTask对象
     */
    public static BukkitTask sync(@NotNull Runnable runnable) {
        return Bukkit.getScheduler().runTask(instance, runnable);
    }

    /**
     * 异步执行一些代码
     * <p>
     * 没事别调用这个方法，要用你自己在你的插件主类里再写一个
     * <p>
     * 否则服务器的 timings 可能会把你的代码运行时间也算进这个插件中
     *
     * @param runnable 执行代码
     * @return BukkitTask对象
     */
    public static BukkitTask async(@NotNull Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(instance, runnable);
    }

    @Override
    public void onLoad() {
        long startTime = System.currentTimeMillis();
        instance = this;

        saveDefaultConfig();
        reloadConfig();

        initLogUtil();
        logUtils.info("==================================================");
        loadLibraries();
        logUtils.info("==================================================");
        PageConfigManager.init();
        logUtils.info("==================================================");
        CommandManager.init(getClassLoader());
        logUtils.info("==================================================");
        initParameterParser();
        logUtils.info("==================================================");
        initPersistenceManager();
        logUtils.info("==================================================");

        logUtils.info("插件载入完成. 总共耗时 %d 毫秒!", System.currentTimeMillis() - startTime);
    }

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        logUtils.info("插件正在启动...");
        logUtils.debug("当前服务器 MC 版本: " + AirUtils.getMCVersion());
        logUtils.debug("当前服务器 nms 包名: " + AirUtils.getNMSPackage().getName());

        logUtils.info("==================================================");
        VaultAPI.reloadVaultHook();
        PointAPI.reloadPlayerPointAPIHook();
        logUtils.info("==================================================");
        initCommand();
        logUtils.info("==================================================");
        initPageConfig();
        logUtils.info("==================================================");

        Bukkit.getPluginManager().registerEvents(new PageListener(), this);
        logUtils.info("已注册 GUI 相关监听器.");
        Bukkit.getPluginManager().registerEvents(new PluginHookListener(), this);
        logUtils.info("已注册插件挂接监听器.");
        Bukkit.getPluginManager().registerEvents(new ConversationListener(), this);
        logUtils.info("已注册玩家会话监听器.");

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            // 这里只能用反射的方式实例化 SignEditListener
            // 否则在没有安装 ProtocolLib 的服务器上会由于类加载器异常而导致插件无法启动
            try {
                ProtocolLibrary.getProtocolManager().addPacketListener(
                        (PacketListener) Class.forName("net.airgame.bukkit.api.listener.SignEditListener.SignEditListener").newInstance()
                );
                logUtils.info("已启用 ProtocolLib 支持: SignEditAPI.");
            } catch (Exception e) {
                logUtils.error(e, "启用 ProtocolLib 支持: SignEditAPI 时遇到了一个异常: ");
            }
        }

        logUtils.info("插件启动完成. 总共耗时 %d 毫秒!", System.currentTimeMillis() - startTime);
    }

    @Override
    public void onDisable() {
        for (LogUtils utils : LogUtils.ALL_INSTANCES) {
            PrintWriter writer = utils.getFileWriter();
            if (writer != null) {
                writer.close();
            }
        }
        if (persistenceManager != null) {
            persistenceManager.close();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            InventoryView view = player.getOpenInventory();
            Inventory inventory = view.getTopInventory();
            if (!(inventory.getHolder() instanceof PageHandler)) {
                continue;
            }
            player.closeInventory();
            player.sendMessage("§c由于服务器调整数据, 你打开的界面被强行关闭了.");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return super.onCommand(sender, command, label, args);
    }

    /**
     * 初始化日志器的设定
     */
    private void initLogUtil() {
        File defaultLogSettingsFile = saveDefaultFile("defaultLogSettings.yml");
        LogUtils.DEFAULT_CONFIG = YamlConfiguration.loadConfiguration(defaultLogSettingsFile);

        logUtils = new LogUtils(this);
    }

    /**
     * 加载存放于 plugins/AirGame-API/libs 中的第三方库文件
     * 仅 .jar 文件会被加载
     */
    private void loadLibraries() {
        if (!getConfig().getBoolean("loadLibraries", true)) {
            logUtils.warning("跳过加载第三方库.");
            return;
        }
        logUtils.info("开始加载第三方库.");
        File libFolder = new File(getDataFolder(), "libs");
        if (libFolder.mkdirs()) {
            logUtils.info("创建第三方库存放文件夹...");
        }
        File[] files = libFolder.listFiles();
        if (files == null) {
            return;
        }

        ClassLoader loader = getClassLoader();
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            for (File file : files) {
                String fileName = file.getName();
                if (!fileName.endsWith(".jar")) {
                    logUtils.warning("跳过加载非 jar 拓展名的第三方库: %s", fileName);
                    continue;
                }
                method.invoke(loader, file.toURI().toURL());
                logUtils.info("已加载第三方库: %s", fileName);
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
        ParameterParserManager.registerParser(Sound.class, SoundParser.class);
        ParameterParserManager.registerParser(CommandSender.class, CommandSenderParser.class);
        ParameterParserManager.registerParser(World.class, WorldParser.class);
        logUtils.info("默认命令参数解析器注册完成.");
    }

    private void initPersistenceManager() {
        FileConfiguration config = getConfig();
        if (!config.getBoolean("datasource.enable")) {
            AirGameAPI.getLogUtils().warning("跳过初始化持久化管理器.");
            return;
        }
        AirGameAPI.getLogUtils().info("开始初始化持久化管理器.");
        saveDefaultFile("sql.properties");
        persistenceManager = new PersistenceManager(this);
        AirGameAPI.getLogUtils().info("持久化管理器初始化完成.");
    }

    /**
     * 初始化命令
     */
    private void initCommand() {
        logUtils.info("开始注册命令.");
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (!(plugin instanceof JavaPlugin)) {
                continue;
            }
            CommandScan commandScan = plugin.getClass().getAnnotation(CommandScan.class);
            if (commandScan == null) {
                continue;
            }
            if (commandScan.value().length < 1) {
                continue;
            }
            try {
                CommandManager.registerPluginCommand((JavaPlugin) plugin, commandScan.value()[0]);
            } catch (IOException | InvocationTargetException | IllegalAccessException e) {
                logUtils.error(e, "在为插件 %s 注册命令时遇到了一个异常:", plugin.getName());
            }
        }
        logUtils.info("命令注册完成.");
    }

    /**
     * 初始化界面
     */
    private void initPageConfig() {
        logUtils.info("开始注册界面设定.");

        PageConfigManager.reload();

        logUtils.info("界面设定注册完成.");
    }

    /**
     * 将插件 jar 文件内的文件保存到插件存档目录中
     * <p>
     * 若插件存档目录中已存在该文件则不会保存
     *
     * @param name 文件名称
     */
    private File saveDefaultFile(String name) {
        // 这里不能用日志器
        // 因为日志器的配置文件也是用这个方法生成的
        if (getDataFolder().mkdirs()) {
            getLogger().info("创建插件存档文件夹...");
        }
        File file = new File(getDataFolder(), name);
        if (file.exists()) {
            return file;
        }
        saveResource(name, true);
        getLogger().info("复制 " + name + " 至插件存档文件夹...");
        return file;
    }

}
