package io.github.xxyy.cmdblocker;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

/**
 * Listens for Tab complete packets and intercepts them if necessary.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 03.01.14
 */
final class TabCompletePacketListener extends PacketAdapter {
    TabCompletePacketListener(final CommandBlockerPlugin instance){
        super(instance, PacketType.Play.Client.TAB_COMPLETE);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if(!event.isCancelled() && event.getPacketType().equals(PacketType.Play.Client.TAB_COMPLETE)){
            PacketContainer packet = event.getPacket();

            StructureModifier<String> textModifier = packet.getSpecificModifier(String.class);

            String chatMessage = textModifier.read(0);
            CommandBlockerPlugin plugin = (CommandBlockerPlugin) getPlugin();

            if(!plugin.canExecute(event.getPlayer(), plugin.getRawCommand(chatMessage))){
                event.setCancelled(true);

                plugin.sendErrorMessageIfEnabled(event.getPlayer());
            }
        }
    }
}
