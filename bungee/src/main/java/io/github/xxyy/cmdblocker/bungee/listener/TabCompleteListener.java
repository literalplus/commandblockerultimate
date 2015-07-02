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
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Iterator;

/**
 * Listens for tab-complete events and removes BungeeCord commands.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 16.7.14
 */
public class TabCompleteListener implements Listener {
    private final CommandBlockerPlugin plugin;

    public TabCompleteListener(CommandBlockerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent evt) {
        CommandSender sender = null;

        if (evt.getSender() instanceof CommandSender) {
            sender = (CommandSender) evt.getSender();

            if(sender.hasPermission(plugin.getConfigAdapter().getBypassPermission())) {
                return; //Don't need to check if sender has bypass
            }
        }

        Iterator<String> it = evt.getSuggestions().iterator();
        while(it.hasNext()) {
            String suggestion = it.next();

            if(plugin.getConfigAdapter().isBlocked(CommandHelper.getRawCommand(suggestion))) {
                if(plugin.getConfigAdapter().isTabRestrictiveMode()) {
                    evt.setCancelled(true);
                    plugin.sendErrorMessageIfEnabled(sender);
                }
            } else {
                it.remove(); //Remove suggestion from mutable list
            }
        }
    }
}
