package io.github.xxyy.cmdblocker.bungee.listener;

import io.github.xxyy.cmdblocker.bungee.CommandBlockerPlugin;
import io.github.xxyy.cmdblocker.common.util.CommandHelper;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Iterator;

/**
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
