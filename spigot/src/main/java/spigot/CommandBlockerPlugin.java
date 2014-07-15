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

package spigot;

import io.github.xxyy.cmdblocker.common.ConfigAdapter;
import io.github.xxyy.cmdblocker.common.GenericConfigAdapter;
import io.github.xxyy.cmdblocker.common.config.ConfigUpdateHelper;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import spigot.command.CommandCBU;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CommandBlocker Ultimate.
 * Blocks certain commands.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 02.01.14 // 1.0
 */
public class CommandBlockerPlugin extends JavaPlugin implements Listener {

    private Config configAdapter = new Config();

    @Override
    public void onEnable() {
        //Do config stuffs
        saveDefaultConfig();
        if (ConfigUpdateHelper.updateConfig(this.configAdapter)) {
            getLogger().info("Your configuration file has been updated! Check out the new options :)");
        }

        getServer().getPluginManager().addPermission(
                new Permission(
                        getConfig().getString(ConfigAdapter.BYPASS_PERMISSION),
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

    /**
     * Checks whether a given {@link org.bukkit.command.CommandSender} can execute a given command
     * and sends an error message if they cannot.
     *
     * @param sender  Sender to check
     * @param command Name of the command to check, might have a slash in front.
     * @return Whether {@code sender} can execute {@code command}, not taking aliases into account.
     */
    private boolean canExecute(final CommandSender sender, final String command) {
        if(configAdapter.isBlocked(command) && !sender.hasPermission(getConfig().getString(ConfigAdapter.BYPASS_PERMISSION))){
            sendErrorMessageIfEnabled(sender);
            return false;
        }
        return true;
    }

    public void sendErrorMessageIfEnabled(final CommandSender target) {
        if (getConfig().getBoolean(ConfigAdapter.SHOW_ERROR_MESSAGE, true)) {
            target.sendMessage(
                    StringEscapeUtils.unescapeHtml(
                            ChatColor.translateAlternateColorCodes('&', getConfig().getString(ConfigAdapter.ACCESS_DENIED_MESSAGE))
                    )
            );
        }
    }

    /**
     * Handles a cancellable event and decides if that chat message contains a blocked command.
     * If it does, the event is cancelled and an error message is printed.
     * @param evt What to cancel
     * @param sender Who wrote the message
     * @param chatMessage The message written, including the slash.
     */
    public void handleEvent(Cancellable evt, CommandSender sender, String chatMessage) {
        if (!canExecute(sender, configAdapter.getRawCommand(chatMessage))) {
            evt.setCancelled(true);
        }
    }

    public ConfigAdapter getConfigAdapter(){
        return configAdapter;
    }

    private void tryHookProtocolLib() {
        if (!getConfig().getBoolean(ConfigAdapter.PREVENT_TAB, true)) {
            return;
        }

        if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            try {
                //The ClassLoader seems to load the class even when it's not imported - Feel free to provide a better implementation.
                Class.forName("io.github.xxyy.cmdblocker.spigot.listener.protocol.TabCompletePacketListener")
                        .getConstructor(CommandBlockerPlugin.class)
                        .newInstance(this);
                return;
            } catch (Throwable throwable) {
                getLogger().log(Level.WARNING, "Problem when trying to hook ProtocolLib!", throwable);
            }
        }

        getLogger().warning("Could not hook ProtocolLib! " +
                "Please check that you installed it correctly. " +
                "If you want this message to be omitted, set 'prevent-tab' to false in the plugin's config file. " +
                "Get ProtocolLib here: http://dev.bukkit.org/bukkit-plugins/protocollib/");
        getLogger().warning("Tab-completion will NOT be prevented!");
    }

    public class Config extends GenericConfigAdapter {

        private File configFile;

        protected Config() {
            this.configFile = new File(getDataFolder(), "config.yml");
        }

        @Override
        public File getFile() {
            return configFile;
        }

        @Override
        public boolean isBlocked(String commandName) {
            return getConfig().getStringList(TARGET_COMMANDS).contains(getRawCommand(commandName));
        }

        @Override
        public boolean getBoolean(String path, boolean def) {
            return getConfig().getBoolean(path, def);
        }

        @Override
        public boolean contains(String path) {
            return getConfig().contains(path);
        }

        @Override
        public Logger getLogger() {
            return CommandBlockerPlugin.this.getLogger();
        }

        @Override
        public void reload() {
            reloadConfig();
        }
    }
}
