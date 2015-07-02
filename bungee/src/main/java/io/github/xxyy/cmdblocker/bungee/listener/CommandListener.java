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

package io.github.xxyy.cmdblocker.bungee.listener;

import io.github.xxyy.cmdblocker.bungee.CommandBlockerPlugin;
import io.github.xxyy.cmdblocker.common.util.CommandHelper;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;


/**
 * Listens for bungee chat, filters commands and blocks blocked ones. Makes sense, eh?
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 15.7.14
 */
public class CommandListener implements Listener {
    private final CommandBlockerPlugin plugin;

    public CommandListener(CommandBlockerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(ChatEvent evt) {
        if (!evt.isCommand()) {
            return;
        }

        checkCommand(evt, evt.getSender(), evt.getMessage());
    }

    private void checkCommand(Cancellable evt, Connection connection, String command) {
        CommandSender sender = null;
        boolean hasPermission = true;

        if (connection instanceof CommandSender) {
            sender = (CommandSender) connection;
            hasPermission = sender.hasPermission(plugin.getConfigAdapter().getBypassPermission());
        }

        if(!hasPermission) { //Has bypass permission, actually. Too long var name though
            checkCommand(sender, command, evt);
        }
    }

    private void checkCommand(CommandSender sender, String command, Cancellable evt) {
        if (plugin.getConfigAdapter().isBlocked(CommandHelper.getRawCommand(command))) {
            evt.setCancelled(true);

            plugin.sendErrorMessageIfEnabled(sender);
        }
    }
}
