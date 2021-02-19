package net.airgame.bukkit.api.conversation;

import net.airgame.bukkit.api.AirGameAPI;
import net.airgame.bukkit.api.listener.ConversationListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("unused")
public abstract class Conversation {
    private final Player player;

    public Conversation(Player player) {
        this.player = player;
    }

    public String getPlayerInput() throws ExecutionException, InterruptedException {
        return ConversationListener.getPlayerInput(player).get();
    }

    public String getPlayerInput(long time) throws InterruptedException, ExecutionException, TimeoutException {
        return ConversationListener.getPlayerInput(player).get(time, TimeUnit.SECONDS);
    }

    public String getPlayerInput(long time, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return ConversationListener.getPlayerInput(player).get(time, unit);
    }

    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    public void sendMessage(String message, Object... args) {
        sendMessage(String.format(message, args));
    }

    public abstract void conversation() throws InterruptedException, ExecutionException, TimeoutException;

    public BukkitTask start(Plugin plugin) {
        AirGameAPI.sync(player::closeInventory);
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                conversation();
            } catch (InterruptedException e) {
                if (getInterruptMessage() != null) {
                    player.sendMessage(getInterruptMessage());
                }
                AirGameAPI.getLogUtils().debug(e, "执行会话时遇到了一个中断异常: ");
            } catch (ExecutionException e) {
                if (getExecutionMessage() != null) {
                    player.sendMessage(getExecutionMessage());
                }
                AirGameAPI.getLogUtils().debug(e, "执行会话时遇到了一个执行异常: ");
            } catch (TimeoutException e) {
                if (getTimeoutMessage() != null) {
                    player.sendMessage(getTimeoutMessage());
                }
                AirGameAPI.getLogUtils().debug(e, "执行会话时遇到了一个超时异常: ");
            } catch (Exception e) {
                if (getExceptionMessage() != null) {
                    player.sendMessage(getExceptionMessage());
                }
                AirGameAPI.getLogUtils().debug(e);
            }
        });
    }

    public BukkitTask startIgnoreException(Plugin plugin) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                conversation();
            } catch (Exception e) {
                AirGameAPI.getLogUtils().debug(e, "执行会话时遇到了一个异常: ");
            }
        });
    }

    public Player getPlayer() {
        return player;
    }

    public String getInterruptMessage() {
        return null;
    }

    public String getExecutionMessage() {
        return null;
    }

    public String getTimeoutMessage() {
        return null;
    }

    public String getExceptionMessage() {
        return null;
    }
}
