/*
 * Command Blocker Ultimate
 * Copyright (C) 2014-2017 Philipp Nowak / Literallie (xxyy.github.io)
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

package li.l1t.cbu.bungee;

import com.google.common.base.Preconditions;
import li.l1t.cbu.bungee.command.CommandGCBU;
import li.l1t.cbu.bungee.config.BungeeAliasResolver;
import li.l1t.cbu.bungee.listener.CommandListener;
import li.l1t.cbu.bungee.listener.TabCompleteListener;
import li.l1t.cbu.common.config.AliasResolver;
import li.l1t.cbu.common.config.CBUConfig;
import li.l1t.cbu.common.config.InvalidConfigException;
import li.l1t.cbu.common.platform.PlatformAdapter;
import li.l1t.cbu.common.util.CBUVersion;
import li.l1t.cbu.common.util.CommandExtractor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Bungee plugin class for CommandBlockerUltimate.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2014-07-15
 */
public class CommandBlockerPlugin extends Plugin implements PlatformAdapter {
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
                configAdapter.resolveAliases(aliasResolver);
            }
        }, 5, TimeUnit.SECONDS); //If any plugins takes longer than this to load, the author is doing something severely wrong

        //Register listeners
        getProxy().getPluginManager().registerListener(this, new CommandListener(this));
        getProxy().getPluginManager().registerListener(this, new TabCompleteListener(this));

        //Register command
        getProxy().getPluginManager().registerCommand(this, new CommandGCBU(this));

        getLogger().info("CommandBlockerUltimate " + CBUVersion.PLUGIN_VERSION_STRING + " is licensed under the GNU General Public License " +
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
        return !(connection instanceof CommandSender) ||
                !canAccessWithMessage(command, (CommandSender) connection);
    }

    public boolean canAccessWithMessage(String command, CommandSender commandSender) {
        Preconditions.checkNotNull(command, "command");
        Preconditions.checkNotNull(commandSender, "commandSender");
        if (!isCommand(command)) {
            return true;
        }
        String rawCommand = CommandExtractor.getRawCommand(command);
        if (getConfigAdapter().isBlocked(rawCommand)) {
            if (commandSender.hasPermission(getConfigAdapter().getBypassPermission())) {
                sendBypassMessageIfEnabled(commandSender, rawCommand);
            } else { //if they don't, cancel it
                sendErrorMessageIfEnabled(commandSender, rawCommand);
                return false;
            }
        }
        return true;
    }

    public boolean isCommand(String command) {
        return command.startsWith("/");
    }

    public void sendTabErrorMessageIfEnabled(CommandSender sender) {
        if (getConfigAdapter().isShowTabErrorMessage() && sender != null) {
            sender.sendMessage( //Send message
                    unescapeCommandMessage(getConfigAdapter().getTabErrorMessage(), sender, "<command>")
            );
        }
    }

    private void sendErrorMessageIfEnabled(CommandSender sender, String command) {
        if (getConfigAdapter().isShowErrorMessage() && sender != null) {
            sender.sendMessage( //Send message
                    unescapeCommandMessage(getConfigAdapter().getErrorMessage(), sender, command)
            );
        }
    }

    private void sendBypassMessageIfEnabled(CommandSender sender, String command) {
        if (getConfigAdapter().isNotifyBypass() && sender != null) {
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

    @Override
    public CBUConfig getConfigAdapter() {
        return configAdapter;
    }

    @Override
    public void replaceConfigAdapter() throws InvalidConfigException {
        CBUConfig newAdapter = createConfig();
        newAdapter.initialize();
        this.configAdapter = newAdapter;
        configAdapter.resolveAliases(aliasResolver);
    }

    @Override
    public AliasResolver getAliasResolver() {
        return aliasResolver;
    }
}
