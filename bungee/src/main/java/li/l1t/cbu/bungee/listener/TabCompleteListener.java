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

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.event.TabCompleteResponseEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import li.l1t.cbu.bungee.CommandBlockerPlugin;
import li.l1t.cbu.common.config.CBUConfig;
import li.l1t.cbu.common.util.CommandExtractor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Listens for tab-complete events and removes BungeeCord (and Bukkit!) replies.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2014-07-16
 */
public class TabCompleteListener implements Listener {
    private final CommandBlockerPlugin plugin;

    public TabCompleteListener(CommandBlockerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabComplete(TabCompleteEvent event) {
        CommandSender sender = getSenderOrNull(event);
        if (shouldIgnoreTabComplete(event, sender)) {
            return;
        }
        if (isBlockedCommand(event.getCursor())) {
            rejectCompletion(event, sender);
        } else {
            removeBlockedSuggestions(event.getSuggestions(), event, sender);
        }
    }

    private boolean shouldIgnoreTabComplete(Cancellable event, CommandSender sender) {
        return event.isCancelled() || sender == null || hasBypassPermission(sender) || !config().isPreventTab();
    }

    private boolean hasBypassPermission(CommandSender sender) {
        return sender.hasPermission(config().getBypassPermission());
    }

    private CommandSender getSenderOrNull(TabCompleteEvent event) {
        if (event.getSender() instanceof CommandSender) {
            return (CommandSender) event.getSender();
        } else {
            return null;
        }
    }

    private boolean isBlockedCommand(String suggestion) {
        return plugin.isCommand(suggestion) &&
                config().isBlocked(CommandExtractor.getRawCommand(suggestion));
    }

    private void removeBlockedSuggestions(Collection<String> suggestions, Cancellable event, CommandSender sender) {
        Collection<String> blocked = findBlockedSuggestionsIn(suggestions);
        if (!blocked.isEmpty() && config().isTabRestrictiveMode()) {
            rejectCompletion(event, sender);
        } else {
            suggestions.removeAll(blocked);
        }
    }

    private void rejectCompletion(Cancellable event, CommandSender sender) {
        plugin.sendTabErrorMessageIfEnabled(sender);
        event.setCancelled(true);
    }

    private Collection<String> findBlockedSuggestionsIn(Collection<String> suggestions) {
        List<String> blocked = new LinkedList<>();
        for (String suggestion : suggestions) {
            if (isBlockedCommand(suggestion)) {
                blocked.add(suggestion);
            }
        }
        return blocked;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabCompleteResponse(TabCompleteResponseEvent event) {
        CommandSender receiver = getReceiverOrNull(event);
        if (shouldIgnoreTabComplete(event, receiver)) {
            return;
        }
        removeBlockedSuggestions(event.getSuggestions(), event, receiver);
    }

    private CommandSender getReceiverOrNull(TabCompleteResponseEvent event) {
        if (event.getReceiver() instanceof CommandSender) {
            return (CommandSender) event.getReceiver();
        } else {
            return null;
        }
    }

    private CBUConfig config() {
        return plugin.getConfigAdapter();
    }
}
