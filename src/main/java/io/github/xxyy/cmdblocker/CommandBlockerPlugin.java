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

import io.github.xxyy.cmdblocker.config.ConfigUpdateHelper;
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
import org.mcstats.Metrics;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CommandBlocker Ultimate.
 * Blocks certain commands.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 02.01.14 // 1.0
 */
public class CommandBlockerPlugin extends JavaPlugin implements Listener {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^/((\\S)+)");

    @Override
    public void onEnable() {
        //Do config stuffs
        saveDefaultConfig();
        if(ConfigUpdateHelper.updateConfig(this, new File(getDataFolder(), "config.yml"))){
            getLogger().info("Your configuration file has been updated! Check out the new options :)");
        }

        getServer().getPluginManager().addPermission(
                new Permission(
                        getConfig().getString("bypass-permission"),
                        "Allows to bypass Command Blocker Ultimate (Recommended access level: Staff)",
                        PermissionDefault.OP)
        );

        //Register command listener
        this.getServer().getPluginManager().registerEvents(this, this);

        //Hook into ProtocolLib, if enabled and loaded
        tryHookProtocolLib();

        //Register commands
        getCommand("cbu").setExecutor(new CommandCBU(this));

        //Hey, how about metrics?
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            getLogger().info("Could not start Metrics. This error is non-crucial. Just ignore it.");
        }
    }

    public boolean canExecute(final CommandSender sender, final String command) {
        return !isBlocked(command) || sender.hasPermission(getConfig().getString("bypass-permission"));
    }

    public boolean isBlocked(String command) {
        return getConfig().getStringList("target-commands").contains(command);
    }


    public void sendErrorMessageIfEnabled(final CommandSender target) {
        if (getConfig().getBoolean("show-error-message", true)) {
            target.sendMessage(
                    StringEscapeUtils.unescapeHtml(
                            ChatColor.translateAlternateColorCodes('&', getConfig().getString("error-message"))
                    )
            );
        }
    }

    public String getRawCommand(final String chatMessage) {
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

    private void tryHookProtocolLib(){
        if(!getConfig().getBoolean("prevent-tab", true)){
            return;
        }

        if(getServer().getPluginManager().getPlugin("ProtocolLib") != null){
            try{
                //The ClassLoader seems to load the class even when it's not imported - Feel free to provide a better implementation.
                com.comphenix.protocol.ProtocolLibrary.getProtocolManager().addPacketListener(
                        (com.comphenix.protocol.events.PacketListener) Class
                                .forName("io.github.xxyy.cmdblocker.protocol.TabCompletePacketListener")
                                .getConstructor(CommandBlockerPlugin.class)
                                .newInstance(this));
                return;
            }catch(Throwable throwable){
                getLogger().log(Level.WARNING, "Problem when trying to hook ProtcolLib!", throwable);
            }
        }

        getLogger().warning("Could not hook ProtocolLib! " +
                "Please check that you installed it correctly. " +
                "If you want this message to be omitted, set 'prevent-tab' to false in the plugin's config file. " +
                "Get ProtocolLib here: http://dev.bukkit.org/bukkit-plugins/protocollib/");
        getLogger().warning("Tab-completion will NOT be prevented!");
    }
}
