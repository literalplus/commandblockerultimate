package io.github.xxyy.cmdblocker.spigot.listener;

import io.github.xxyy.cmdblocker.spigot.CommandBlockerPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Listens for {@link org.bukkit.event.player.PlayerCommandPreprocessEvent}s and takes action if required.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 25.3.14 // 1.3
 */
public class CommandListener implements Listener {
    private final CommandBlockerPlugin plugin;

    public CommandListener(final CommandBlockerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(final PlayerCommandPreprocessEvent evt) {
        plugin.handleEvent(evt, evt.getPlayer(), evt.getMessage());
    }
}
