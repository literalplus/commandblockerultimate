package io.github.xxyy.cmdblocker.spigot.command;

import io.github.xxyy.cmdblocker.common.config.InvalidConfigException;
import io.github.xxyy.cmdblocker.spigot.CommandBlockerPlugin;
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
            sender.sendMessage(ChatColor.DARK_AQUA + "CommandBlockerUltimate "+CommandBlockerPlugin.PLUGIN_VERSION_STRING);
            sender.sendMessage(ChatColor.DARK_AQUA + " Licensed under GNU GPL v3.");
            sender.sendMessage(ChatColor.DARK_AQUA + " Get the source at https://github.com/xxyy/commandblockerultimate");
            return false;
        }

        if (args[0].equalsIgnoreCase("reloadcfg")) {
            try {
                plugin.replaceConfigAdapter(); //Replace config adapter with newly-read file
            } catch (InvalidConfigException e) {
                e.printStackTrace(); //Oops, the sender did something wrong...Send them a huge block of text to make sure they notice
                sender.sendMessage(ChatColor.RED + "Your configuration file is invalid! See the server log for more details.");
                sender.sendMessage(ChatColor.RED + "Maybe http://yaml-online-parser.appspot.com/ can help you diagnose your issue.");
                sender.sendMessage(ChatColor.RED + "If not, you can get help at http://irc.spi.gt/iris/channels=lit");
                sender.sendMessage(ChatColor.YELLOW + "To protect your server from breaking, we have restored your previous configuration" +
                        "for the time being - It will be lost if you restart or reload your server. Execute this command again if you" +
                        "think you've fixed your config file.");
                return true;
            }
            //Phew, no exception. Inform the sender that everything went well
            sender.sendMessage(ChatColor.GREEN + "The configuration file has been reloaded successfully.");

            return true;
        }

        return false;
    }
}
