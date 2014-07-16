package io.github.xxyy.cmdblocker.bungee.command;

import io.github.xxyy.cmdblocker.bungee.CommandBlockerPlugin;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

/**
 * Command for managing BungeeCord CommandBlockerUltimate.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 16.7.14
 */
public class CommandGCBU extends Command {
    private final CommandBlockerPlugin plugin;

    public CommandGCBU(CommandBlockerPlugin plugin) {
        super("gcbu", "cmdblock.admin");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("CommandBlockerUltimate ").color(ChatColor.DARK_AQUA)
                    .append(CommandBlockerPlugin.PLUGIN_VERSION_STRING).color(ChatColor.AQUA).create());
            sender.sendMessage(new ComponentBuilder(" Licensed under GNU GPL v3.").color(ChatColor.DARK_AQUA).create());
            sender.sendMessage(new ComponentBuilder(" Get the source at https://github.com/xxyy/commandblockerultimate").color(ChatColor.DARK_AQUA).create());
            return;
        }

        if (args[0].equalsIgnoreCase("reloadcfg")) {
            try {
                plugin.replaceConfigAdapter(); //Replace config adapter with newly-read file
            } catch (InvalidConfigurationException e) {
                e.printStackTrace(); //Oops, the sender did something wrong...Send them a huge block of text to make sure they notice
                sender.sendMessage(new ComponentBuilder("Your configuration file is invalid! See the server log for more details." +
                "Maybe http://yaml-online-parser.appspot.com/ can help you diagnose your issue." +
                "If not, you can get help at http://irc.spi.gt/iris/channels=lit").color(ChatColor.RED).create());
                sender.sendMessage(new ComponentBuilder("To protect your server from breaking, we have restored your previous configuration" +
                        "for the time being - It will be lost if you restart or reload your server. Execute this command again if you" +
                        "think you've fixed your config file.").color(ChatColor.YELLOW).create());
                return;
            }
            //Phew, no exception. Inform the sender that everything went well
            sender.sendMessage(new ComponentBuilder("The configuration file has been reloaded successfully.").color(ChatColor.GREEN).create());
        }
    }
}
