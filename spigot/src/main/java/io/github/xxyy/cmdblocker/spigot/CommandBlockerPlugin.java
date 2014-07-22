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

package io.github.xxyy.cmdblocker.spigot;

import io.github.xxyy.cmdblocker.common.config.CBUConfig;
import io.github.xxyy.cmdblocker.common.config.ConfigAdapter;
import io.github.xxyy.cmdblocker.common.config.InvalidConfigException;
import io.github.xxyy.cmdblocker.common.util.CommandHelper;
import io.github.xxyy.cmdblocker.lib.io.github.xxyy.common.version.PluginVersion;
import io.github.xxyy.cmdblocker.spigot.command.CommandCBU;
import io.github.xxyy.cmdblocker.spigot.config.SpigotAliasResolver;
import io.github.xxyy.cmdblocker.spigot.config.SpigotCBUConfig;
import io.github.xxyy.cmdblocker.spigot.listener.CommandListener;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * CommandBlocker Ultimate.
 * Blocks certain commands.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 02.01.14 // 1.0
 */
public class CommandBlockerPlugin extends JavaPlugin implements Listener {

    //Create plugin version from manifest (see cbu-bootstrap/pom.xml -> maven-jar-plugin,buildnumber-maven-plugin for details)
    //Don't need to read this every time since we don't need the individual properties anyway --> performance
    public static String PLUGIN_VERSION_STRING = PluginVersion.ofClass(CommandBlockerPlugin.class).toString();

    private ConfigAdapter configAdapter;
    private SpigotAliasResolver aliasResolver = new SpigotAliasResolver(this);

    @Override
    public void onEnable() {
        //Do config stuffs
        this.configAdapter = createConfig();
        this.configAdapter.tryInitialize(getLogger()); //Prints error if loading failed

        getServer().getScheduler().runTaskLater(this, new Runnable() { //When will Java 8 finally be standard? So looking forward to that day
            @Override
            public void run() {
                aliasResolver.refreshMap();
                configAdapter.resolveAliases(aliasResolver); //This needs to be done after all plugins have loaded to catch all aliases
            }
        }, 0L); //Scheduler only begins work after all plugins are loaded

        //Register permission (Not sure if this is used anywhere though)
        getServer().getPluginManager().addPermission(
                new Permission(
                        getConfigAdapter().getBypassPermission(),
                        "Allows to bypass Command Blocker Ultimate (Recommended access level: Staff)",
                        PermissionDefault.OP)
        );

        //Register command listener
        this.getServer().getPluginManager().registerEvents(new CommandListener(this), this);

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

        getLogger().info("CommandBlockerUltimate " + PLUGIN_VERSION_STRING + " is licensed under the GNU General Public License " +
                "Version 2. See the LICENSE file included in its .jar archive for details.");
    }

    private ConfigAdapter createConfig() {
        if(getServer().getPluginManager().getPlugin("Yamler") != null) {
            return new CBUConfig(new File(getDataFolder(), "config.yml"));
        } else {
            getLogger().warning("Using default config adapter!");
            getLogger().warning("It is recommended that you install Yamler, because that allows the config to be a lot " +
                    "more flexible and straightforward to use.");
            return new SpigotCBUConfig(this);
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
        if (configAdapter.isBlocked(command) && !sender.hasPermission(getConfigAdapter().getBypassPermission())) {
            sendErrorMessageIfEnabled(sender);
            return false;
        }
        return true;
    }

    public void sendErrorMessageIfEnabled(final CommandSender target) {
        if (getConfigAdapter().isShowErrorMessage()) {
            target.sendMessage(
                    StringEscapeUtils.unescapeHtml(
                            ChatColor.translateAlternateColorCodes('&', getConfigAdapter().getErrorMessage())
                    )
            );
        }
    }

    /**
     * Handles a cancellable event and decides if that chat message contains a blocked command.
     * If it does, the event is cancelled and an error message is printed.
     *
     * @param evt         What to cancel
     * @param sender      Who wrote the message
     * @param chatMessage The message written, including the slash.
     */
    public void handleEvent(Cancellable evt, CommandSender sender, String chatMessage) {
        if (!canExecute(sender, CommandHelper.getRawCommand(chatMessage))) {
            evt.setCancelled(true);
        }
    }

    /**
     * Returns the current config adapter used by the plugin.
     * <b>Warning:</b> The adapter might be replaced at any time, so make sure to always get the latest one!
     * @return the plugin's current config adapter
     */
    public ConfigAdapter getConfigAdapter() {
        return configAdapter;
    }

    /**
     * Replaces the current config adapter by a fresh one with current values from the configuration file.
     * This is used instead of {@link CBUConfig#reload()} to allow server owners to react and fix their configuration file
     * instead of breaking the plugin by assuming the default values.
     * If the current config file is invalid, an exception is thrown and the adapter is not replaced.
     * Note that this method doesn't work with {@link SpigotCBUConfig} since Bukkit's configuration API doesn't allow
     * handling of syntax errors.
     * @throws InvalidConfigException Propagated from {@link CBUConfig#initialize()} - If you get this, you can
     *                                          safely assume that thew adapter has not been replaced.
     */
    public void replaceConfigAdapter() throws InvalidConfigException {
        ConfigAdapter newAdapter = createConfig();
        newAdapter.initialize();
        this.configAdapter = newAdapter;
    }

    private void tryHookProtocolLib() {
        if (!getConfigAdapter().isPreventTab()) {
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
}
