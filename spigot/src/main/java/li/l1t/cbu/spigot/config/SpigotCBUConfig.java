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

package li.l1t.cbu.spigot.config;

import li.l1t.cbu.common.config.AliasResolver;
import li.l1t.cbu.common.config.CBUConfig;
import li.l1t.cbu.common.config.ConfigAdapter;
import li.l1t.cbu.common.config.InvalidConfigException;
import li.l1t.cbu.common.util.CommandExtractor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This is an alternative implementation of {@link CBUConfig} using the default Spigot YamlConfiguration API. This class
 * is used as a fallback if Yamler is not available. <p>Some comfort features, such as not reloading the file on a
 * syntax error and automatic updating of the file are not implemented because that would add unnecessary extra
 * complexity and those are available out-of-the-box with Yamler.</p>
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2014-07-22
 */
public class SpigotCBUConfig implements ConfigAdapter {

    private final JavaPlugin plugin;
    private List<String> rawTargetCommands;
    private Set<String> blockedCommands;
    private String bypassPermission;
    private boolean showErrorMessage;
    private boolean showTabErrorMessage;
    private String errorMessage;
    private boolean preventTab;
    private boolean tabRestrictiveMode;
    private boolean notifyBypass;
    private String bypassMessage;
    private String tabErrorMessage;

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
        plugin.saveDefaultConfig();
        plugin.getLogger().warning("Using the simplified configuration adapter! When CommandBlockerUltimate updates, new " +
                "options won't be in your configuration file!");
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        //Load options to cache
        blockedCommands = new HashSet<>();
        config.addDefault(TARGET_COMMANDS_PATH, Arrays.asList("?", "help", "plugins", "pl", "version", "ver", "about"));
        rawTargetCommands = config.getStringList(TARGET_COMMANDS_PATH);
        blockedCommands.addAll(rawTargetCommands);

        bypassPermission = config.getString(BYPASS_PERMISSION_PATH, "cmdblock.bypass");
        showErrorMessage = config.getBoolean(SHOW_ERROR_MESSAGE_PATH, true);
        showTabErrorMessage = config.getBoolean(SHOW_TAB_ERROR_MESSAGE_PATH, true);
        errorMessage = config.getString(ERROR_MESSAGE_PATH,
                "&cI am sorry, but you are not permitted to execute this command.");
        preventTab = config.getBoolean(PREVENT_TAB_PATH, true);
        tabRestrictiveMode = config.getBoolean(TAB_RESTRICTIVE_MODE_PATH, false);

        notifyBypass = config.getBoolean(NOTIFY_BYPASS_PATH, false);
        bypassMessage = config.getString(BYPASS_MESSAGE_PATH,
                "&c[CBU] This command is blocked. Executing anyways since you have permission.");
        tabErrorMessage = config.getString(TAB_ERROR_MESSAGE_PATH,
                "&cI am sorry, but I cannot let you do this, Dave.");
    }

    @Override
    public boolean trySave() {
        save();
        return true;
    }

    @Override
    public void save() {
        FileConfiguration config = plugin.getConfig();
        config.set(TARGET_COMMANDS_PATH, rawTargetCommands);
        config.set(BYPASS_PERMISSION_PATH, bypassPermission);
        config.set(SHOW_ERROR_MESSAGE_PATH, showErrorMessage);
        config.set(SHOW_TAB_ERROR_MESSAGE_PATH, showTabErrorMessage);
        config.set(ERROR_MESSAGE_PATH, errorMessage);
        config.set(PREVENT_TAB_PATH, preventTab);
        config.set(TAB_RESTRICTIVE_MODE_PATH, tabRestrictiveMode);
        config.set(NOTIFY_BYPASS_PATH, notifyBypass);
        config.set(BYPASS_MESSAGE_PATH, bypassMessage);
        config.set(TAB_ERROR_MESSAGE_PATH, tabErrorMessage);
        plugin.saveConfig();
    }

    @Override
    public void resolveAliases(AliasResolver aliasResolver) {
        aliasResolver.refreshMap();
        blockedCommands.clear();

        for (String requestedCommand : new ArrayList<>(rawTargetCommands)) {
            resolveAliasesOf(requestedCommand, aliasResolver);
        }
    }

    private void resolveAliasesOf(String requestedCommand, AliasResolver aliasResolver) {
        blockedCommands.add(requestedCommand);
        blockedCommands.addAll(aliasResolver.resolve(requestedCommand));
    }

    @Override
    public boolean isBlocked(String commandName) {
        return blockedCommands.contains(commandName) ||
                blockedCommands.contains(CommandExtractor.removeModPrefix(commandName));
    }

    @Override
    public Collection<String> getRawBlockedCommands() {
        return rawTargetCommands;
    }

    @Override
    public Collection<String> getBlockedCommands() {
        return blockedCommands;
    }

    @Override
    public void addBlockedCommand(String command) {
        if (rawTargetCommands.contains(command)) {
            return;
        }
        blockedCommands.add(command);
        rawTargetCommands.add(command);
    }

    @Override
    public boolean removeBlockedCommand(String command) {
        rawTargetCommands.remove(command);
        return blockedCommands.remove(command);
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
    public boolean isShowTabErrorMessage() {
        return showTabErrorMessage;
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

    @Override
    public boolean isNotifyBypass() {
        return notifyBypass;
    }

    @Override
    public String getBypassMessage() {
        return bypassMessage;
    }

    @Override
    public String getTabErrorMessage() {
        return tabErrorMessage;
    }
}
