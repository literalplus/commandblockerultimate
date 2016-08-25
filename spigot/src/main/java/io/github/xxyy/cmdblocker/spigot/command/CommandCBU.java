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
            return sendBannerMessageTo(sender);
        }

        if (args[0].equalsIgnoreCase("reloadcfg")) {
            return handleReloadConfig(sender);
        } else if (args[0].equalsIgnoreCase("block")) {
            return handleBlock(sender, args);
        } else if (args[0].equalsIgnoreCase("free")) {
            return handleUnblock(sender, args);
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown action: /" + label + " " + args[0]);
            sendUsageMessageTo(sender);
        }

        return true;
    }

    private boolean sendBannerMessageTo(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_AQUA + "CommandBlockerUltimate " + CommandBlockerPlugin.PLUGIN_VERSION_STRING);
        sender.sendMessage(ChatColor.DARK_AQUA + " Licensed under GNU GPL v2. (Source: https://git.io/fesVwQ)");
        sendUsageMessageTo(sender);
        return true;
    }

    private void sendUsageMessageTo(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Usage: /cbu reloadcfg         - Reloads config file");
        sender.sendMessage(ChatColor.YELLOW + "Usage: /cbu block [command]  - Blocks a command on the fly");
        sender.sendMessage(ChatColor.YELLOW + "Usage: /cbu free [command]  - Unblocks a command on the fly");
    }

    private boolean handleReloadConfig(CommandSender sender) {
        try {
            plugin.replaceConfigAdapter();
        } catch (Exception e) { //Apparently, Yamler throws all kinds of exceptions
            return handleConfigLoadError(sender, e);
        }
        sender.sendMessage(ChatColor.GREEN + "The configuration file has been reloaded successfully.");
        return true;
    }

    private boolean handleConfigLoadError(CommandSender sender, Exception e) {
        e.printStackTrace(); //Oops, the sender did something wrong...Send them a huge block of text to make sure they notice
        sender.sendMessage(ChatColor.RED + "Your configuration file is invalid! See the server log for more details.");
        sender.sendMessage(ChatColor.RED + "Maybe http://yaml-online-parser.appspot.com/ can help you diagnose your issue.");
        sender.sendMessage(ChatColor.RED + "If not, you can get help at https://github.com/xxyy/commandblockerultimate/issues");
        sender.sendMessage(ChatColor.YELLOW + "To protect your server from breaking, we have restored your previous configuration " +
                "for the time being - It will be lost if you restart or reload your server. Execute this command again if you " +
                "think you've fixed your config file.");
        sender.sendMessage(ChatColor.YELLOW + "Check the FAQ at " +
                "https://github.com/xxyy/commandblockerultimate/wiki/Frequently-Asked-Questions for some " +
                "common problems.");
        return true;
    }

    private boolean handleBlock(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return sendTooFewArgumentsMessageTo(sender);
        }
        plugin.getConfigAdapter().addBlockedCommand(args[1]);
        sender.sendMessage(ChatColor.GREEN + "Added /" + args[1] + " to blocked commands.");
        attemptSaveAndNotifyOnFailure(sender);
        return true;
    }

    private boolean sendTooFewArgumentsMessageTo(CommandSender sender) {
        sender.sendMessage("Too few arguments!");
        sendUsageMessageTo(sender);
        return true;
    }

    private boolean handleUnblock(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return sendTooFewArgumentsMessageTo(sender);
        }
        boolean wasBlocked = plugin.getConfigAdapter().removeBlockedCommand(args[1]);
        if (wasBlocked) {
            sender.sendMessage(ChatColor.GREEN + "Removed /" + args[1] + " from blocked commands.");
        } else {
            sender.sendMessage(ChatColor.RED + "/" + args[1] + " is not curently blocked.");
        }
        attemptSaveAndNotifyOnFailure(sender);
        return true;
    }

    private void attemptSaveAndNotifyOnFailure(CommandSender sender) {
        if (!plugin.getConfigAdapter().trySave()) {
            sender.sendMessage(ChatColor.RED + "However, the change could not be saved because of " +
                    "an error. See the server log for details.");
        }
    }
}
