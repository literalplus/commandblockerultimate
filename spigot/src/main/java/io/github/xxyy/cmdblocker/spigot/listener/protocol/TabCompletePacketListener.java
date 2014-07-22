package io.github.xxyy.cmdblocker.spigot.listener.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.reflect.StructureModifier;
import io.github.xxyy.cmdblocker.common.util.CommandHelper;
import io.github.xxyy.cmdblocker.spigot.CommandBlockerPlugin;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Listens for Tab complete packets and intercepts them if necessary.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 03.01.14
 */
@SuppressWarnings("UnusedDeclaration") //Reflection!
public final class TabCompletePacketListener implements PacketListener {
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
        if (!event.isCancelled()) {
            //Nothing else than server TAB_COMPLETE should come our way
            //Packet: {Chat message} http://wiki.vg/Protocol#Tab-Complete_2
            PacketContainer packet = event.getPacket();
            StructureModifier<String> textModifier = packet.getSpecificModifier(String.class);

            this.plugin.handleEvent(event, event.getPlayer(), /* chatMessage */ textModifier.read(0));
        }
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
    public void onPacketSending(final PacketEvent event) {
        if (!event.isCancelled()) {
            //Nothing else than server TAB_COMPLETE should come our way
            //Packet: {(VarInt)Count, Matched command} http://wiki.vg/Protocol#Tab-Complete

            if (event.getPlayer().hasPermission(this.plugin.getConfigAdapter().getBypassPermission())) {
                return;
            }

            PacketContainer packetContainer = event.getPacket();

            StructureModifier<String[]> matchModifier = packetContainer.getSpecificModifier(String[].class);

            String[] matchedCommands = matchModifier.read(0); //Commands suggested by the server
            List<String> allowedCommands = null;


            for (String matchedCommand : matchedCommands) {
                if (this.plugin.getConfigAdapter().isBlocked(CommandHelper.getRawCommand(matchedCommand))) { //Not using canExecute to save some permission checks (Single one done above)
                    if (this.plugin.getConfigAdapter().isTabRestrictiveMode()) { //Hides all replies if anything is matched
                        this.plugin.sendErrorMessageIfEnabled(event.getPlayer());
                        event.setCancelled(true);
                        return;
                    } else {
                        if (allowedCommands == null) { //Create list only when needed
                            allowedCommands = new LinkedList<>(Arrays.asList(matchedCommands));
                        }
                        allowedCommands.remove(matchedCommand);
                    }
                }
            }

            if (allowedCommands != null) { // null means that nothing was found
                if (allowedCommands.size() == 0) { //Nothing is allowed
                    this.plugin.sendErrorMessageIfEnabled(event.getPlayer());
                    event.setCancelled(true);
                } else { //Write allowed commands
                    matchModifier.write(0, allowedCommands.toArray(new String[allowedCommands.size()]));
                }
            }
        }
    }
}
