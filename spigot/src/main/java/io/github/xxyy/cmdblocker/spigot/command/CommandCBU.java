/*
 * Command Blocker Ultimate
 * Copyright (C) 2014-2015 Philipp Nowak / Literallie (xxyy.github.io)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package io.github.xxyy.cmdblocker.spigot.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.github.xxyy.cmdblocker.common.config.InvalidConfigException;
import io.github.xxyy.cmdblocker.spigot.CommandBlockerPlugin;

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
            sender.sendMessage(ChatColor.DARK_AQUA + " Licensed under GNU GPL v2.");
            sender.sendMessage(ChatColor.DARK_AQUA + " Get the source at https://github.com/xxyy/commandblockerultimate");
            return false;
        }

        if (args[0].equalsIgnoreCase("reloadcfg")) {
            try {
                plugin.replaceConfigAdapter(); //Replace config adapter with newly-read file
            } catch (Exception e) { //Apparently, Yamler throws all kinds of exceptions
                e.printStackTrace(); //Oops, the sender did something wrong...Send them a huge block of text to make sure they notice
                sender.sendMessage(ChatColor.RED + "Your configuration file is invalid! See the server log for more details.");
                sender.sendMessage(ChatColor.RED + "Maybe http://yaml-online-parser.appspot.com/ can help you diagnose your issue.");
                sender.sendMessage(ChatColor.RED + "If not, you can get help at https://github.com/xxyy/commandblockerultimate/issues");
                sender.sendMessage(ChatColor.YELLOW + "To protect your server from breaking, we have restored your previous configuration " +
                        "for the time being - It will be lost if you restart or reload your server. Execute this command again if you " +
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
