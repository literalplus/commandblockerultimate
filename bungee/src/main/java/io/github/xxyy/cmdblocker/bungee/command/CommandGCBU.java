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

package io.github.xxyy.cmdblocker.bungee.command;

import io.github.xxyy.cmdblocker.common.util.CBUVersion;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

import io.github.xxyy.cmdblocker.bungee.CommandBlockerPlugin;
import io.github.xxyy.cmdblocker.common.config.ConfigAdapter;

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
            sendBannerTo(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("reloadcfg")) {
            handleReloadConfig(sender);
        } else if (args[0].equalsIgnoreCase("block")) {
            handleBlock(sender, args);
        } else if (args[0].equalsIgnoreCase("free")) {
            handleUnblock(sender, args);
        } else {
            sendSimpleMessage(sender, "Unknown action: /gcbu " + args[0], ChatColor.RED);
            sendUsageTo(sender);
        }
    }

    private void handleReloadConfig(CommandSender sender) {
        try {
            plugin.replaceConfigAdapter();
        } catch (Exception e) { //Apparently, Yamler throws all kind sof exceptions
            handleConfigLoadError(sender, e);
            return;
        }
        sendSimpleMessage(sender, "The configuration file has been reloaded successfully.", ChatColor.GREEN);
    }

    private void handleConfigLoadError(CommandSender sender, Exception e) {
        e.printStackTrace(); //Oops, the sender did something wrong...Send them a huge block of text to make sure they notice
        sendSimpleMessage(sender, "Your configuration file is invalid! See the server log for more details." +
                "Maybe http://yaml-online-parser.appspot.com/ can help you diagnose your issue." +
                "If not, you can get help at https://github.com/xxyy/commandblockerultimate/issues", ChatColor.RED);
        sendSimpleMessage(sender, "To protect your server from breaking, we have restored your previous configuration " +
                "for the time being - It will be lost if you restart or reload your server. Execute this command again if you " +
                "think you've fixed your config file.", ChatColor.YELLOW);
        sendSimpleMessage(sender, "Check the FAQ at " +
                "https://github.com/xxyy/commandblockerultimate/wiki/Frequently-Asked-Questions for some " +
                "common problems.", ChatColor.YELLOW);
    }

    private void sendUsageTo(CommandSender sender) {
        sendSimpleMessage(sender, "Usage: /gcbu reloadcfg - Reloads config", ChatColor.YELLOW);
        sendSimpleMessage(sender, "Usage: /gcbu block     - Blocks a command", ChatColor.YELLOW);
        sendSimpleMessage(sender, "Usage: /gcbu free      - Unblocks a command", ChatColor.YELLOW);
    }

    private void sendBannerTo(CommandSender sender) {
        sender.sendMessage(new ComponentBuilder("CommandBlockerUltimate ").color(ChatColor.DARK_AQUA)
                .append(CBUVersion.PLUGIN_VERSION_STRING).color(ChatColor.AQUA).create());
        sendSimpleMessage(sender, " Licensed under GNU GPL v2.", ChatColor.DARK_AQUA);
        sendSimpleMessage(sender, " Get the source at https://github.com/xxyy/commandblockerultimate", ChatColor.DARK_AQUA);
        sendUsageTo(sender);
    }

    private void handleBlock(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendTooFewArgumentsMessageTo(sender);
            return;
        }
        config().addBlockedCommand(args[1]);
        config().resolveAliases(plugin.getAliasResolver());
        sendSimpleMessage(sender, "Added /" + args[1] + " to blocked commands.", ChatColor.GREEN);
        attemptSaveAndNotifyOnFailure(sender);
    }

    private void sendSimpleMessage(CommandSender sender, String message, ChatColor color) {
        sender.sendMessage(new ComponentBuilder(message).color(color).create());
    }

    private void attemptSaveAndNotifyOnFailure(CommandSender sender) {
        if (!config().trySave()) {
            sendSimpleMessage(sender, "However, the change could not be saved because of an error. " +
                    "See the server log for details.", ChatColor.RED);
        }
    }

    private ConfigAdapter config() {
        return plugin.getConfigAdapter();
    }

    private boolean sendTooFewArgumentsMessageTo(CommandSender sender) {
        sendSimpleMessage(sender, "Too few arguments!", ChatColor.RED);
        sendUsageTo(sender);
        return true;
    }

    private void handleUnblock(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendTooFewArgumentsMessageTo(sender);
        }
        boolean wasBlocked = config().removeBlockedCommand(args[1]);
        config().resolveAliases(plugin.getAliasResolver());
        notifyUnblockResult(sender, args[1], wasBlocked);
        attemptSaveAndNotifyOnFailure(sender);
    }

    private void notifyUnblockResult(CommandSender sender, String commandName, boolean wasBlocked) {
        if (wasBlocked) {
            sendSimpleMessage(sender, "Removed /" + commandName + " from blocked commands.", ChatColor.GREEN);
        } else {
            sendSimpleMessage(sender, "/" + commandName + " is not curently blocked.", ChatColor.RED);
        }
    }
}
