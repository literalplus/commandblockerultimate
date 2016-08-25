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

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.event.TabCompleteResponseEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import io.github.xxyy.cmdblocker.bungee.CommandBlockerPlugin;
import io.github.xxyy.cmdblocker.common.config.CBUConfig;
import io.github.xxyy.cmdblocker.common.util.CommandHelper;

import java.util.Iterator;
import java.util.List;

/**
 * Listens for tab-complete events and removes BungeeCord (and Bukkit!) replies.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 16.7.14
 */
public class TabCompleteListener implements Listener {
    private final CommandBlockerPlugin plugin;

    public TabCompleteListener(CommandBlockerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabComplete(TabCompleteEvent evt) {
        if (evt.isCancelled()) {
            return;
        }

        CommandSender sender = null;
        if (evt.getSender() instanceof CommandSender) {
            sender = (CommandSender) evt.getSender();
        }

        if(!plugin.canAccessWithMessage(evt.getCursor(), sender)) {
            return;
        }

        evt.setCancelled(removeBlocked(evt.getSuggestions(), sender));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabCompleteResponse(TabCompleteResponseEvent evt) {
        if (evt.isCancelled()) {
            return;
        }

        CommandSender messageRecipient = null;
        if (evt.getReceiver() instanceof CommandSender) {
            messageRecipient = (CommandSender) evt.getReceiver();
        }

        evt.setCancelled(removeBlocked(evt.getSuggestions(), messageRecipient));
    }

    //Returns whether the event is to be cancelled
    private boolean removeBlocked(List<String> suggestions, CommandSender messageRecipient) {
        if (messageRecipient != null &&
                messageRecipient.hasPermission(config().getBypassPermission())) {
            return false; //Don't need to check if sender has bypass permission
        }

        Iterator<String> it = suggestions.iterator();
        while (it.hasNext()) {
            String suggestion = it.next();

            if (isBlockedCommand(suggestion)) {
                if (config().isTabRestrictiveMode()) {
                    plugin.sendTabErrorMessageIfEnabled(messageRecipient);
                    return true;
                } else {
                    it.remove(); //Remove suggestion from mutable list
                }
            }
        }

        return false;
    }

    private boolean isBlockedCommand(String suggestion) {
        return plugin.isCommand(suggestion) &&
                config().isBlocked(CommandHelper.getRawCommand(suggestion));
    }

    private CBUConfig config() {
        return plugin.getConfigAdapter();
    }
}
