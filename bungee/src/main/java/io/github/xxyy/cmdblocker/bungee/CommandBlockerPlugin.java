/*
 * Command Blocker Ultimate
 * Copyright (C) 2014-2015 Philipp Nowak / Literallie (xxyy.github.io)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package io.github.xxyy.cmdblocker.bungee;

import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.plugin.Plugin;

import io.github.xxyy.cmdblocker.bungee.command.CommandGCBU;
import io.github.xxyy.cmdblocker.bungee.config.BungeeAliasResolver;
import io.github.xxyy.cmdblocker.bungee.listener.CommandListener;
import io.github.xxyy.cmdblocker.bungee.listener.TabCompleteListener;
import io.github.xxyy.cmdblocker.common.config.CBUConfig;
import io.github.xxyy.cmdblocker.common.util.CommandHelper;
import io.github.xxyy.cmdblocker.lib.io.github.xxyy.common.version.PluginVersion;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Bungee plugin class for CommandBlockerUltimate.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 15.7.14
 */
public class CommandBlockerPlugin extends Plugin {
    //Create plugin version from manifest (see cbu-bootstrap/pom.xml -> maven-jar-plugin,buildnumber-maven-plugin for details)
    //Don't need to read this every time since we don't need the individual properties anyway --> performance
    public static String PLUGIN_VERSION_STRING = PluginVersion.ofClass(CommandBlockerPlugin.class).toString();

    private CBUConfig configAdapter;
    private BungeeAliasResolver aliasResolver = new BungeeAliasResolver(this);

    @Override
    public void onEnable() {
        //Do config stuffs
        this.configAdapter = createConfig();
        this.configAdapter.tryInitialize(getLogger()); //Prints error if loading failed

        getProxy().getScheduler().schedule(this, new Runnable() { //Hacky way to execute code after all plugins have been loaded
            @Override
            public void run() {
                aliasResolver.refreshMap();
                configAdapter.resolveAliases(aliasResolver);
            }
        }, 5, TimeUnit.SECONDS); //If any plugins takes longer than this to load, the author is doing something severely wrong

        //Register listeners
        getProxy().getPluginManager().registerListener(this, new CommandListener(this));
        getProxy().getPluginManager().registerListener(this, new TabCompleteListener(this));

        //Register command
        getProxy().getPluginManager().registerCommand(this, new CommandGCBU(this));

        getLogger().info("CommandBlockerUltimate " + PLUGIN_VERSION_STRING + " is licensed under the GNU General Public License " +
                "Version 2. See the LICENSE file included in its .jar archive for details.");
    }

    private CBUConfig createConfig() {
        return new CBUConfig(new File(getDataFolder(), "config.yml"));
    }

    /**
     * Checks whether a command sender is permitted to execute a command and sends notification messages to them if
     * those are enabled.
     *
     * @param command    the command to check
     * @param connection the connection attempting to execute that command
     * @return whether the execution should be cancelled
     */
    public boolean handleCommandExecution(String command, Connection connection) {
        CommandSender commandSender = (connection instanceof CommandSender) ? (CommandSender) connection : null;

        String rawCommand = CommandHelper.getRawCommand(command);
        if (getConfigAdapter().isBlocked(rawCommand)){
            if (commandSender != null && //if they have the bypass permission, allow execution
                    commandSender.hasPermission(getConfigAdapter().getBypassPermission())){
                sendBypassMessageIfEnabled(commandSender, rawCommand);
            } else { //if they don't, cancel it
                sendErrorMessageIfEnabled(commandSender, rawCommand);
                return true;
            }
        }
        return false;
    }

    public void sendTabErrorMessageIfEnabled(CommandSender sender) {
        if (getConfigAdapter().isShowErrorMessage() && sender != null){
            sender.sendMessage( //Send message
                    unescapeCommandMessage(getConfigAdapter().getTabErrorMessage(), sender, "<command>")
            );
        }
    }

    private void sendErrorMessageIfEnabled(CommandSender sender, String command) {
        if (getConfigAdapter().isShowErrorMessage() && sender != null){
            sender.sendMessage( //Send message
                    unescapeCommandMessage(getConfigAdapter().getErrorMessage(), sender, command)
            );
        }
    }

    private void sendBypassMessageIfEnabled(CommandSender sender, String command) {
        if (getConfigAdapter().isShowErrorMessage() && sender != null){
            sender.sendMessage( //Send message
                    unescapeCommandMessage(getConfigAdapter().getBypassMessage(), sender, command)
            );
        }
    }

    private BaseComponent[] unescapeCommandMessage(String message, CommandSender sender, String command) {
        return TextComponent.fromLegacyText( //Make JSON thing from text
                ChatColor.translateAlternateColorCodes('&', //translate colors
                        message.replace("<command>", command).replace("<name>", sender.getName())
                )
        );
    }

    /**
     * Returns the current config adapter used by the plugin.
     * <b>Warning:</b> The adapter might be replaced at any time, so make sure to always get the latest one!
     *
     * @return the plugin's current config adapter
     */
    public CBUConfig getConfigAdapter() {
        return configAdapter;
    }

    /**
     * Replaces the current config adapter by a fresh one with current values from the configuration file.
     * This is used instead of {@link CBUConfig#reload()} to allow server owners to react and fix their configuration file
     * instead of breaking the plugin by assuming the default values.
     * If the current config file is invalid, an exception is thrown and the adapter is not replaced.
     *
     * @throws net.cubespace.Yamler.Config.InvalidConfigurationException Propagated from {@link CBUConfig#init()} - If you get this, you can
     *                                                                   safely assume that thew adapter has not been replaced.
     */
    public void replaceConfigAdapter() throws InvalidConfigurationException {
        CBUConfig newAdapter = createConfig();
        newAdapter.init();
        this.configAdapter = newAdapter;
        configAdapter.resolveAliases(aliasResolver);
    }
}
