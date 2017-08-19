/*
 * Command Blocker Ultimate
 * Copyright (C) 2014-2017 Philipp Nowak / Literallie (xxyy.github.io)
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

package li.l1t.cbu.bungee.listener;

import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import li.l1t.cbu.bungee.CommandBlockerPlugin;


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
        boolean executionCancelled = plugin.handleCommandExecution(evt.getMessage(), evt.getSender());
        if (executionCancelled) {
            evt.setCancelled(true);
        }
    }
}
