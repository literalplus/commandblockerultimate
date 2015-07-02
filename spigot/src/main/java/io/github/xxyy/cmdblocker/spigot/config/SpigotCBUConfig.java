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

package io.github.xxyy.cmdblocker.spigot.config;

import com.google.common.collect.ImmutableList;
import io.github.xxyy.cmdblocker.common.config.AliasResolver;
import io.github.xxyy.cmdblocker.common.config.ConfigAdapter;
import io.github.xxyy.cmdblocker.common.config.InvalidConfigException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * This is an alternative implementation of {@link io.github.xxyy.cmdblocker.common.config.CBUConfig} using the default
 * Spigot YamlConfiguration API. This class is used as a fallback if Yamler is not available.
 *
 * Some comfort features, such as not reloading the file on a syntax error and automatic updating of the file are not
 * implemented because that would add unnecessary extra complexity and those are available out-of-the-box with Yamler.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.7.14
 */
public class SpigotCBUConfig implements ConfigAdapter {

    private final JavaPlugin plugin;

    private List<String> blockedCommands;

    private String bypassPermission;

    private boolean showErrorMessage;
    private String errorMessage;

    private boolean preventTab;
    private boolean tabRestrictiveMode;

    public SpigotCBUConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean tryInitialize(Logger logger) {
        try {
            initialize();
        } catch (InvalidConfigException ignore) {
            //Can never happen
        }

        return true;
    }

    @Override
    public void initialize() throws InvalidConfigException {
        FileConfiguration config = plugin.getConfig();

        plugin.saveDefaultConfig();
        plugin.getLogger().warning("Using the simplified configuration adapter! When CommandBlockerUltimate updates, new " +
                "options won't be in your configuration file!");

        //Load options to cache
        if(config.contains(ConfigAdapter.TARGET_COMMANDS_PATH)) {
            blockedCommands = config.getStringList(ConfigAdapter.TARGET_COMMANDS_PATH);
        } else {
            blockedCommands = Arrays.asList("?", "help", "plugins", "pl", "version", "ver", "about"); //Y u no default, getStringList()
        }

        bypassPermission = config.getString(ConfigAdapter.BYPASS_PERMISSION_PATH, "cmdblock.bypass");
        showErrorMessage = config.getBoolean(ConfigAdapter.SHOW_ERROR_MESSAGE_PATH, true);
        errorMessage = config.getString(ConfigAdapter.ERROR_MESSAGE_PATH, "&cI am sorry, but you are not permitted to execute this command.");
        preventTab = config.getBoolean(ConfigAdapter.PREVENT_TAB_PATH, true);
        tabRestrictiveMode = config.getBoolean(ConfigAdapter.TAB_RESTRICTIVE_MODE_PATH, false);
    }

    @Override
    public void resolveAliases(AliasResolver aliasResolver) {
        aliasResolver.refreshMap();

        for(String requestedCommand : ImmutableList.copyOf(blockedCommands)) {
            blockedCommands.addAll(aliasResolver.resolve(requestedCommand)); //resolve() doesn't include the argument
        }
    }

    @Override
    public boolean isBlocked(String commandName) {
        return blockedCommands.contains(commandName);
    }

    @Override
    public Collection<String> getBlockedCommands() {
        return blockedCommands;
    }

    @Override
    public String getBypassPermission() {
        return bypassPermission;
    }

    @Override
    public boolean isShowErrorMessage() {
        return showErrorMessage;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean isPreventTab() {
        return preventTab;
    }

    @Override
    public boolean isTabRestrictiveMode() {
        return tabRestrictiveMode;
    }
}
