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

package li.l1t.cbu.spigot.listener.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.reflect.StructureModifier;
import li.l1t.cbu.common.util.CommandExtractor;
import li.l1t.cbu.common.config.ConfigAdapter;
import li.l1t.cbu.spigot.CommandBlockerPlugin;
import org.bukkit.plugin.Plugin;


import java.util.*;

/**
 * Listens for Tab complete packets and intercepts them if necessary.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 03.01.14
 */
@SuppressWarnings("UnusedDeclaration") //Reflection!
public final class TabCompletePacketListener implements PacketListener {
    private static final int SUGGESTIONS_INDEX = 0;
    private static final int TEXT_INDEX = 0;
    private static final ListeningWhitelist SENDING_WHITELIST = ListeningWhitelist.newBuilder()
            .types(PacketType.Play.Server.TAB_COMPLETE)
            .gamePhase(GamePhase.PLAYING).normal().build();

    private static final ListeningWhitelist RECEIVING_WHITELIST = ListeningWhitelist.newBuilder()
            .types(PacketType.Play.Client.TAB_COMPLETE)
            .gamePhase(GamePhase.PLAYING).normal().build();

    private final CommandBlockerPlugin plugin;

    public TabCompletePacketListener(final CommandBlockerPlugin instance) {
        this.plugin = instance;
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        //Packet: {Chat message} http://wiki.vg/Protocol#Tab-Complete_2
        //We need to check the incoming chat message, since tab completions themselves don't
        //include the command they're for by default
        if (event.isCancelled()) {
            return;
        }
        StructureModifier<String> textModifier = event.getPacket().getSpecificModifier(String.class);
        String message = textModifier.read(TEXT_INDEX);
        if(isBlockedCommand(message) && !hasBypassPermission(event)) {
            rejectTabComplete(event);
        }
    }

    private void rejectTabComplete(PacketEvent event) {
        this.plugin.sendTabErrorMessageIfEnabled(event.getPlayer());
        event.setCancelled(true);
    }

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return SENDING_WHITELIST;
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return RECEIVING_WHITELIST;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        //Packet: {(VarInt)Count, Matched command} http://wiki.vg/Protocol#Tab-Complete
        List<String> suggestions = getSuggestionsFrom(event.getPacket());
        if (event.isCancelled() || suggestions.isEmpty() || hasBypassPermission(event)) {
            return;
        }
        Collection<String> blockedSuggestions = findBlockedSuggestionsIn(suggestions);
        suggestions.removeAll(blockedSuggestions);
        if(suggestions.isEmpty() || restrictiveModeApplies(blockedSuggestions)) {
            rejectTabComplete(event);
        } else if(!blockedSuggestions.isEmpty()) {
            writeSuggestionsTo(event.getPacket(), suggestions);
        }
    }

    private boolean restrictiveModeApplies(Collection<String> blockedSuggestions) {
        return !blockedSuggestions.isEmpty() && config().isTabRestrictiveMode();
    }

    private boolean hasBypassPermission(PacketEvent event) {
        return event.getPlayer().hasPermission(config().getBypassPermission());
    }

    private List<String> getSuggestionsFrom(PacketContainer packet) {
        StructureModifier<String[]> modifier = packet.getSpecificModifier(String[].class);
        return new ArrayList<>(Arrays.asList(modifier.read(SUGGESTIONS_INDEX)));
    }

    private Collection<String> findBlockedSuggestionsIn(Collection<String> suggestions) {
        List<String> blocked = new LinkedList<>();
        for(String suggestion : suggestions) {
            if(isBlockedCommand(suggestion)) {
                blocked.add(suggestion);
            }
        }
        return blocked;
    }

    private boolean isBlockedCommand(String text) {
        return text.startsWith("/") && config().isBlocked(CommandExtractor.getRawCommand(text));
    }

    private void writeSuggestionsTo(PacketContainer packet, List<String> suggestions) {
        StructureModifier<String[]> modifier = packet.getSpecificModifier(String[].class);
        String[] suggestionsArray = suggestions.toArray(new String[suggestions.size()]);
        modifier.write(SUGGESTIONS_INDEX, suggestionsArray);
    }

    private ConfigAdapter config() {
        return this.plugin.getConfigAdapter();
    }
}
