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

package io.github.xxyy.cmdblocker.common.config;

import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Comments;
import net.cubespace.Yamler.Config.Config;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.Yamler.Config.Path;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents the CommandBlockerUltimate configuration file.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 16.7.14
 */
public class CBUConfig extends Config implements ConfigAdapter {

    @Path(ConfigAdapter.TARGET_COMMANDS_PATH)
    @Comments({"Define what commands should be blocked in the following property: (without leading slash)",
            "With Spigot/Bukkit, if you specify a command, its aliases will be blocked also. (Example: 'tell' will also block 'msg', 'bukkit:tell', etc.)",
            "On BungeeCord, only BungeeCord command aliases can be blocked - If you want to block Spigot/Bukkit, you'll have to write all aliases down."})
    private List<String> blockedCommands = Arrays.asList("help", "plugins", "version");

    @Path(ConfigAdapter.BYPASS_PERMISSION_PATH)
    @Comment("Define the permission that a player needs to bypass the protection: (Default: cmdblock.bypass)")
    private String bypassPermission = "cmdblock.bypass";

    @Path(ConfigAdapter.SHOW_ERROR_MESSAGE_PATH)
    @Comment("Should the plugin send an error message if one is not allowed to execute/tab-complete a command? (Default: true)")
    private boolean showErrorMessage = true;

    @Path(ConfigAdapter.ERROR_MESSAGE_PATH)
    @Comments({"What should that message be? (Use & for color codes, HTML escape codes accepted)",
            "Example: &c&lError message &euro;&auml;&#00A7;"})
    private String errorMessage = "&cI am sorry, but you are not permitted to execute this command.";

    @Path(ConfigAdapter.PREVENT_TAB_PATH)
    @Comments({"@since 1.02",
            "Whether to prevent tab-completion for blocked commands.",
            "Note: Requires ProtocolLib on Spigot!",
            "Default value: true"})
    private boolean preventTab = true;

    @Path(TAB_RESTRICTIVE_MODE_PATH)
    @Comments({"What strategy to use when blocking tab-complete replies from the server.",
            "true: block all completions returning a targeted command (for example, if /p is typed and /pl is blocked, print error message)",
            "false: just remove blocked commands from list (in the above example, other commands starting with p would still be shown without notice)",
            "Default value: false"})
    private boolean tabRestrictiveMode = false;

    public CBUConfig(File configFile) {
        CONFIG_HEADER = new String[]{
                "Configuration file for CommandBlockerUltimate. CommandBlockerUltimate is licensed under " +
                        "a GNU GPL v2 license (see the LICENSE file in the plugin jar for details)." +
                        "Find its source at https://github.com/xxyy/commandblockerultimate." +
                        "If you need help configuring, drop by #lit on irc.spi.gt (http://irc.spi.gt/iris/?channels=lit)."
        };
        CONFIG_FILE = configFile;
    }

    @Override
    public boolean tryInitialize(Logger logger) {
        try {
            this.initialize(); //This does not call #initialize() to avoid another try-catch block
            this.save(); //Save, just in case any new options were added
        } catch (InvalidConfigException | InvalidConfigurationException e) {
            logger.log(Level.WARNING, "Encountered exception!", e);
            logger.warning("Could not load configuration file. Please double-check your YAML syntax with http://yaml-online-parser.appspot.com/.");
            logger.warning("The plugin might (will) not function in the way you want it to (since it doesn't know what you want)");
            logger.warning("If you don't understand this error, try asking in #lit on irc.spi.gt. (Please don't leave a second after asking)");
            logger.warning("WebChat: http://irc.spi.gt/iris/?channels=lit");
            return false;
        }

        return true;
    }

    @Override
    public void initialize() throws InvalidConfigException {
        try {
            this.init();
        } catch (InvalidConfigurationException e) {
            throw new InvalidConfigException(e);
        }
    }

    @Override
    public void resolveAliases(AliasResolver aliasResolver) {
        aliasResolver.refreshMap();

        for (String requestedCommand : Collections.unmodifiableList(blockedCommands)) {
            blockedCommands.addAll(aliasResolver.resolve(requestedCommand));
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
