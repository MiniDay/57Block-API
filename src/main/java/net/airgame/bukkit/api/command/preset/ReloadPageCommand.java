package net.airgame.bukkit.api.command.preset;

import net.airgame.bukkit.api.annotation.Command;
import net.airgame.bukkit.api.annotation.CommandExecutor;
import net.airgame.bukkit.api.annotation.Sender;
import net.airgame.bukkit.api.manager.PageConfigManager;
import org.bukkit.command.CommandSender;

@CommandExecutor(
        name = "reloadPageSetting",
        aliases = {"reloadPage"},
        permission = "airgame.admin"
)
@SuppressWarnings("unused")
public class ReloadPageCommand {
    @Command
    public void reloadPage(@Sender CommandSender sender) {
        PageConfigManager.reload();
        sender.sendMessage("§a已重载所有插件的GUI配置.");
    }
}
