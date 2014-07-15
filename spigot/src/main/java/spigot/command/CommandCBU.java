package spigot.command;

import spigot.CommandBlockerPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Represents the /cbu command which is an utility command for CBU.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 05.01.14 // 1.01
 */
public class CommandCBU implements CommandExecutor {
    private final CommandBlockerPlugin plugin;

    public CommandCBU(final CommandBlockerPlugin instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }

        if (args[0].equalsIgnoreCase("reloadcfg")) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "The configuration file has been reloaded successfully.");

            return true;
        }

        return false;
    }
}
