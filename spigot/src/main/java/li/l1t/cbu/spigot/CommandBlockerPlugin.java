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

package li.l1t.cbu.spigot;

import li.l1t.cbu.common.config.AliasResolver;
import li.l1t.cbu.common.config.CBUConfig;
import li.l1t.cbu.common.config.ConfigAdapter;
import li.l1t.cbu.common.config.InvalidConfigException;
import li.l1t.cbu.common.platform.PlatformAdapter;
import li.l1t.cbu.common.util.CBUVersion;
import li.l1t.cbu.common.util.CommandHelper;
import li.l1t.cbu.spigot.command.CommandCBU;
import li.l1t.cbu.spigot.config.SpigotAliasResolver;
import li.l1t.cbu.spigot.config.SpigotCBUConfig;
import li.l1t.cbu.spigot.listener.CommandListener;
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
 * Entry point for the CommandBlockUltimate plugin on the Spigot platform.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2014-01-02 // 1.0
 */
public class CommandBlockerPlugin extends JavaPlugin implements PlatformAdapter, Listener {
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

        getLogger().info("CommandBlockerUltimate " + CBUVersion.PLUGIN_VERSION_STRING + " is licensed under the GNU General Public License " +
                "Version 2. See the LICENSE file included in its .jar archive for details.");
    }

    private ConfigAdapter createConfig() {
        if (getServer().getPluginManager().getPlugin("Yamler") != null) {
            return new CBUConfig(new File(getDataFolder(), "config.yml"));
        } else {
            getLogger().warning("It is recommended that you install Yamler, because that allows the config to be a lot " +
                    "more flexible and straightforward to use.");
            return new SpigotCBUConfig(this);
        }
    }

    /**
     * Checks whether a given {@link org.bukkit.command.CommandSender} can execute a given command and sends an error
     * message if they cannot.
     *
     * @param sender  the sender to check for
     * @param command the raw name of the command to check
     * @return Whether {@code sender} can execute {@code command}, not taking aliases into account.
     */
    private boolean canExecute(final CommandSender sender, final String command) {
        if (configAdapter.isBlocked(command)) {
            if (!sender.hasPermission(getConfigAdapter().getBypassPermission())) {
                sendErrorMessageIfEnabled(sender, command);
                return false;
            } else {
                sendBypassMessageIfEnabled(sender, command);
                return true;
            }
        }
        return true;
    }

    public void sendTabErrorMessageIfEnabled(final CommandSender target) {
        if (getConfigAdapter().isShowTabErrorMessage()) {
            target.sendMessage(
                    unescapeCommandMessage(getConfigAdapter().getTabErrorMessage(), target, "<command>")
            );
        }
    }

    private void sendBypassMessageIfEnabled(final CommandSender target, final String command) {
        if (getConfigAdapter().isNotifyBypass()) {
            target.sendMessage(
                    unescapeCommandMessage(getConfigAdapter().getBypassMessage(), target, command)
            );
        }
    }

    private void sendErrorMessageIfEnabled(final CommandSender target, final String command) {
        if (getConfigAdapter().isShowErrorMessage()) {
            target.sendMessage(
                    unescapeCommandMessage(getConfigAdapter().getErrorMessage(), target, command)
            );
        }
    }

    private String unescapeCommandMessage(final String message, final CommandSender target, final String command) {
        return StringEscapeUtils.unescapeHtml(
                ChatColor.translateAlternateColorCodes('&',
                        message.replace("<command>", command).replace("<name>", target.getName())
                )
        );
    }

    /**
     * Handles a cancellable event and decides if that chat message contains a blocked command. If it does, the event is
     * cancelled and an error message is printed.
     *
     * @param evt         What to cancel
     * @param sender      Who wrote the message
     * @param chatMessage The message written, including the slash.
     */
    public void handleEvent(Cancellable evt, CommandSender sender, String chatMessage) {
        boolean executionCancelled = chatMessage.startsWith("/") &&
                !canExecute(sender, CommandHelper.getRawCommand(chatMessage));
        if (executionCancelled) {
            evt.setCancelled(true);
        }
    }

    @Override
    public ConfigAdapter getConfigAdapter() {
        return configAdapter;
    }

    @Override
    public void replaceConfigAdapter() throws InvalidConfigException {
        ConfigAdapter newAdapter = createConfig();
        newAdapter.initialize();
        this.configAdapter = newAdapter;
        configAdapter.resolveAliases(aliasResolver);
    }

    private void tryHookProtocolLib() {
        if (!getConfigAdapter().isPreventTab()) {
            return;
        }

        if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            try {
                //The ClassLoader seems to load the class even when it's not imported - Feel free to provide a better implementation.
                Class.forName("li.l1t.cbu.spigot.listener.protocol.TabCompletePacketListener")
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

    @Override
    public AliasResolver getAliasResolver() {
        return aliasResolver;
    }
}
