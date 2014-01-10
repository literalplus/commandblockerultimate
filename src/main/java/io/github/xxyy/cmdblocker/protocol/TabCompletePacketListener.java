package io.github.xxyy.cmdblocker.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import io.github.xxyy.cmdblocker.CommandBlockerPlugin;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Listens for Tab complete packets and intercepts them if necessary.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 03.01.14
 */
public final class TabCompletePacketListener extends PacketAdapter {
    public TabCompletePacketListener(final CommandBlockerPlugin instance) {
        super(instance, PacketType.Play.Client.TAB_COMPLETE, PacketType.Play.Server.TAB_COMPLETE);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (!event.isCancelled()/* && event.getPacketType().equals(PacketType.Play.Client.TAB_COMPLETE)*/) {
            //Packet: {Chat message} http://wiki.vg/Protocol#Tab-Complete_2
            PacketContainer packet = event.getPacket();

            StructureModifier<String> textModifier = packet.getSpecificModifier(String.class);

            String chatMessage = textModifier.read(0);
            CommandBlockerPlugin plugin = (CommandBlockerPlugin) getPlugin();

            if (!plugin.canExecute(event.getPlayer(), plugin.getRawCommand(chatMessage))) {
                event.setCancelled(true);

                plugin.sendErrorMessageIfEnabled(event.getPlayer());
            }
        }
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (!event.isCancelled()/* && event.getPacketType().equals(PacketType.Play.Client.TAB_COMPLETE)*/) {
            //Nothing else than server TAB_COMPLETE should come our way
            //Packet: {(VarInt)Count, Matched command} http://wiki.vg/Protocol#Tab-Complete
            PacketContainer packetContainer = event.getPacket();

            StructureModifier<String[]> matchModifier = packetContainer.getSpecificModifier(String[].class);

            String[] matchedCommands = matchModifier.read(0);
            List<String> allowedCommands = null;

            CommandBlockerPlugin plugin1 = (CommandBlockerPlugin) getPlugin();

            for (String matchedCommand : matchedCommands) {
                if (!plugin1.canExecute(event.getPlayer(), plugin1.getRawCommand(matchedCommand))) {


                    if (plugin1.getConfig().getBoolean("tab-restrictive-mode")) {
                        plugin1.sendErrorMessageIfEnabled(event.getPlayer());
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

            if (allowedCommands != null) {
                if (allowedCommands.size() == 0) {
                    plugin1.sendErrorMessageIfEnabled(event.getPlayer());
                    event.setCancelled(true);
                } else {
                    matchModifier.write(0, allowedCommands.toArray(new String[allowedCommands.size()]));
                }
            }
        }
    }
}
