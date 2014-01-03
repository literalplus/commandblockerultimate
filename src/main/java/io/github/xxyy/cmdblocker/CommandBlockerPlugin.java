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

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
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
    }

    private boolean canExecute(final CommandSender sender, final String command) {
        if (!getConfig().getStringList("target-commands").contains(command)) {
            return true;
        }

        if (sender instanceof ConsoleCommandSender) {
            return getConfig().getBoolean("console-bypass", true);
        } else {
            return sender.hasPermission(getConfig().getString("bypass-permission"));
        }
    }

    private void sendErrorMessageIfEnabled(final CommandSender target) {
        if (getConfig().getBoolean("show-error-message", true)) {
            target.sendMessage(
                    StringEscapeUtils.unescapeHtml(
                            ChatColor.translateAlternateColorCodes('&', getConfig().getString("error-message"))
                    )
            );
        }
    }

    private String getRawCommand(final String chatMessage) {
        Matcher matcher = COMMAND_PATTERN.matcher(chatMessage);
        matcher.find();
        if(!matcher.find() || matcher.groupCount() == 0){
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabComplete(final PlayerChatTabCompleteEvent evt) {
        if (!canExecute(evt.getPlayer(), getRawCommand(evt.getChatMessage()))) {
            evt.getTabCompletions().clear();

            sendErrorMessageIfEnabled(evt.getPlayer());
        }
    }
}
