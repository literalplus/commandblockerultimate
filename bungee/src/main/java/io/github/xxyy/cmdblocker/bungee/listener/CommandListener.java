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
