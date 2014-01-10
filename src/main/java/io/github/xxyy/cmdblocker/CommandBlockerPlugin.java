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

import java.io.File;
import java.lang.management.ManagementFactory;
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
        //Check for Java 7 (Required)
        String javaVersionString = ManagementFactory.getRuntimeMXBean().getSpecVersion();
        javaVersionString = javaVersionString.substring(2);
        try{
            int javaVersion = Integer.parseInt(javaVersionString);

            if(javaVersion < 7){
                getLogger().severe("This plugin requires at least Java 7. Please update your Java.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }catch(NumberFormatException nfe){
            getLogger().warning("Could not determine your Java Version, assuming you are running Java 7. " +
                    "If you are not, please update to Java 7.");
        }

        //Do config stuffs
        saveDefaultConfig();
        if(ConfigUpdateHelper.updateConfig(this, new File(getDataFolder(), "config.yml"))){
            getLogger().info("Your configuration file has been updated! Check out the new options :)");
        }

        //Register command listener
        this.getServer().getPluginManager().registerEvents(this, this);

        //Hook into ProtocolLib, if enabled and loaded
        tryHookProtocolLib();

        //Register commands
        getCommand("cbu").setExecutor(new CommandCBU(this));
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

    private void tryHookProtocolLib(){
        if(!getConfig().getBoolean("prevent-tab", true)){
            return;
        }

        if(getServer().getPluginManager().getPlugin("ProtocolLib") == null){
            getLogger().warning("Could not hook ProtocolLib - Plugin not loaded! " +
                    "Please check that you installed it correctly. " +
                    "If you want this message to be omitted, set 'prevent-tab' to false in the plugin's config file. " +
                    "Get ProtocolLib here: http://dev.bukkit.org/bukkit-plugins/protocollib/");
            getLogger().warning("Tab-completion will NOT be prevented!");
            return;
        }

        ProtocolLibrary.getProtocolManager().addPacketListener(new TabCompletePacketListener(this));

        getServer().getPluginManager().addPermission(
                new Permission(
                        getConfig().getString("bypass-permission"),
                        "Allows to bypass Command Blocker Ultimate (Recommended access level: Staff)",
                        PermissionDefault.OP)
        );
    }
}
