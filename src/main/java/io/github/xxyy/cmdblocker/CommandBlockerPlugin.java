/*
Command Blocker Ultimate
Copyright (C) 2014 Philipp Nowak / xxyy (xxyy.github.io)

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package io.github.xxyy.cmdblocker;

import com.comphenix.protocol.ProtocolLibrary;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CommandBlocker Ultimate.
 * Blocks certain commands.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 02.01.14
 */
public class CommandBlockerPlugin extends JavaPlugin implements Listener {
    private static Pattern COMMAND_PATTERN = Pattern.compile("^/((\\S)+)");

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.getServer().getPluginManager().registerEvents(this, this);

        ProtocolLibrary.getProtocolManager().removePacketListener(new TabCompletePacketListener(this));

        getServer().getPluginManager().addPermission(
                new Permission(
                        getConfig().getString("bypass-permission"),
                        "Allows to bypass Command Blocker Ultimate",
                        PermissionDefault.OP)
        );
    }

    boolean canExecute(final CommandSender sender, final String command) {
        return !getConfig().getStringList("target-commands").contains(command)
                || sender.hasPermission(getConfig().getString("bypass-permission"));
    }

    void sendErrorMessageIfEnabled(final CommandSender target) {
        if (getConfig().getBoolean("show-error-message", true)) {
            target.sendMessage(
                    StringEscapeUtils.unescapeHtml(
                            ChatColor.translateAlternateColorCodes('&', getConfig().getString("error-message"))
                    )
            );
        }
    }

    String getRawCommand(final String chatMessage) {
        Matcher matcher = COMMAND_PATTERN.matcher(chatMessage);

        if (!matcher.find() || matcher.groupCount() == 0) {
            return "";
        }
        return matcher.group(1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(final PlayerCommandPreprocessEvent evt) {
        if (!canExecute(evt.getPlayer(), getRawCommand(evt.getMessage()))) {
            evt.setCancelled(true);

            sendErrorMessageIfEnabled(evt.getPlayer());
        }
    }
}
