package net.airgame.bukkit.api.thread;

import net.airgame.bukkit.api.PluginMain;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * 倒计时执行器
 * <p>
 * 当 timer 为零时执行 onFinish 方法
 * <p>
 * 新建该对象后，使用 start 方法启动
 *
 * @see CountdownRunnable#onTick(int) 每过一个启动间隔执行该方法
 * @see CountdownRunnable#onFinish() 倒计时结束时的执行该方法
 * @see CountdownRunnable#start(Plugin, boolean) 以每秒一次的间隔来启动
 * @see CountdownRunnable#start(Plugin, long, boolean) 以设定间隔来启动
 */
@SuppressWarnings("unused")
public abstract class CountdownRunnable implements Runnable {
    /**
     * 计时器
     */
    private int timer;
    /**
     * 这个倒计时是否正在运行
     */
    private BukkitTask task;

    public CountdownRunnable(int timer) {
        this.timer = timer;
    }

    @Override
    public void run() {
        try {
            onTick(timer);
        } catch (Exception e) {
            PluginMain.getLogUtils().error(e, "插件 %s 执行倒计时线程时在 %d tick 发生异常:", task.getOwner().getName(), timer);
        }
        if (timer == 0) {
            try {
                onFinish();
            } catch (Exception e) {
                PluginMain.getLogUtils().error(e, "插件 %s 执行倒计时线程时在 onFinish 发生异常:", task.getOwner().getName());
            }
            cancel();
            return;
        }
        timer--;
    }

    public synchronized void cancel() {
        if (task == null) {
            return;
        }
        task.cancel();
        task = null;
    }

    /**
     * 倒计时每过一秒执行一次这个方法
     *
     * @param tick 倒计时剩余时间
     */
    public abstract void onTick(int tick);

    /**
     * 倒计时结束后执行的方法
     */
    public abstract void onFinish();

    /**
     * 启动这个倒计时线程
     * <p>
     * 以每 1 秒作一次倒计时衰减
     *
     * @param plugin 插件对象
     * @param async  是否异步运行
     */
    public void start(Plugin plugin, boolean async) {
        start(plugin, 20, false);
    }

    /**
     * 启动这个倒计时线程
     *
     * @param plugin 插件对象
     * @param period 每多少 ticks 作一次倒计时衰减
     * @param async  是否异步运行
     */
    public void start(Plugin plugin, long period, boolean async) {
        if (isRunning()) {
            throw new IllegalStateException("不允许启动一个已经在运行中的倒计时线程!");
        }
        if (async) {
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, period, period);
        } else {
            task = Bukkit.getScheduler().runTaskTimer(plugin, this, period, period);
        }
    }

    /**
     * @return 当前倒计时剩余时间
     */
    public int getTimer() {
        return timer;
    }

    /**
     * 设计倒计时剩余时间
     *
     * @param timer 剩余时间
     */
    public void setTimer(int timer) {
        this.timer = timer;
    }

    /**
     * 获取该倒计时是否正在运行
     * <p>
     * 未被调用start方法之前、手动调用 cancel 方法 或 onFinish 方法完成后 会返回 false
     * <p>
     * 其余的运行期间会返回true
     *
     * @return 该倒计时是否正在运行
     */
    public boolean isRunning() {
        return task != null;
    }

}
