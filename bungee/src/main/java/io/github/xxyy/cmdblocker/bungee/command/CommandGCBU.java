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

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

import io.github.xxyy.cmdblocker.bungee.CommandBlockerPlugin;

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
            sender.sendMessage(new ComponentBuilder(" Licensed under GNU GPL v2.").color(ChatColor.DARK_AQUA).create());
            sender.sendMessage(new ComponentBuilder(" Get the source at https://github.com/xxyy/commandblockerultimate").color(ChatColor.DARK_AQUA).create());
            sender.sendMessage(new ComponentBuilder("/gcbu <reloadcfg>").create());
            return;
        }

        if (args[0].equalsIgnoreCase("reloadcfg")) {
            try {
                plugin.replaceConfigAdapter(); //Replace config adapter with newly-read file
            } catch (Exception e) { //Apparently, Yamler throws all kind sof exceptions
                e.printStackTrace(); //Oops, the sender did something wrong...Send them a huge block of text to make sure they notice
                sender.sendMessage(new ComponentBuilder("Your configuration file is invalid! See the server log for more details." +
                "Maybe http://yaml-online-parser.appspot.com/ can help you diagnose your issue." +
                "If not, you can get help at https://github.com/xxyy/commandblockerultimate/issues").color(ChatColor.RED).create());
                sender.sendMessage(new ComponentBuilder("To protect your server from breaking, we have restored your previous configuration " +
                        "for the time being - It will be lost if you restart or reload your server. Execute this command again if you " +
                        "think you've fixed your config file.").color(ChatColor.YELLOW).create());
                return;
            }
            //Phew, no exception. Inform the sender that everything went well
            sender.sendMessage(new ComponentBuilder("The configuration file has been reloaded successfully.").color(ChatColor.GREEN).create());
        } else {
            sender.sendMessage(new ComponentBuilder("Unknown action: /gcbu "+args[0]).color(ChatColor.RED).create());
            sender.sendMessage(new ComponentBuilder("Usage: /gcbu <reloadcfg>").color(ChatColor.YELLOW).create());
        }
    }
}
