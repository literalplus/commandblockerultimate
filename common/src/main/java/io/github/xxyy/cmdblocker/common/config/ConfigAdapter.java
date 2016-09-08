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

import java.util.Collection;
import java.util.logging.Logger;

/**
 * Represents a configuration adapter (in most cases a YAML file) that is used to fetch
 * CommandBlockerUltimate configuration options and messages.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 16.7.14
 */
public interface ConfigAdapter {

    //Path definitions
    String SHOW_ERROR_MESSAGE_PATH = "show-error-message";
    String SHOW_TAB_ERROR_MESSAGE_PATH = "show-tab-error-message";
    String TARGET_COMMANDS_PATH = "target-commands";
    String BYPASS_PERMISSION_PATH = "bypass-permission";
    String ERROR_MESSAGE_PATH = "error-message";
    String PREVENT_TAB_PATH = "prevent-tab";
    String TAB_RESTRICTIVE_MODE_PATH = "tab-restrictive-mode";
    String NOTIFY_BYPASS_PATH = "notify-bypass";
    String BYPASS_MESSAGE_PATH = "bypass-message";
    String TAB_ERROR_MESSAGE_PATH = "tab-error-message";

    /**
     * Tries to initialize this config adapter. If an exception occurs, it is logged to {@code
     * logger} with details on how to get help from this plugin's author.
     *
     * @param logger Logger to print to
     * @return whether the configuration was initialized successfully.
     */
    boolean tryInitialize(Logger logger);

    /**
     * Initializes this config adapter, loading all values into cache.
     *
     * @throws InvalidConfigException if the underlying configuration is invalid for some reason
     *                                (e.g. syntax error)
     */
    void initialize() throws InvalidConfigException;

    /**
     * Attempts to write the current state of the values defined in the adapter back to disk.
     *
     * @return whether the write succeeded
     */
    boolean trySave();

    /**
     * Writes the current state of the values defined in the adapted back to disk.
     *
     * @throws Exception if an error occurs while writing
     */
    void save() throws Exception;

    /**
     * Resolves aliases in the block list of this adapter. This is done beforehand to avoid having
     * to do an expensive search every time {@link #isBlocked(String)} is called. This method should
     * normally be called shortly after the server has enabled all plugins to allow it to register
     * all plugin's aliases. This method will {@link AliasResolver#refreshMap() refresh the
     * resolver's map automatically}.
     *
     * @param aliasResolver Resolver to use in order to resolve aliases
     */
    void resolveAliases(AliasResolver aliasResolver);

    /**
     * Checks whether a given command, or derivatives of given command (e.g. {@code me} for {@code
     * minecraft:me}) are blocked.
     *
     * @param commandName Command name to check
     * @return whether the given command is blocked.
     */
    boolean isBlocked(String commandName);

    /**
     * Gets a Collection of blocked commands, including resolved aliases. All values are in lower
     * case.
     *
     * @return A collection of blocked commands
     */
    Collection<String> getBlockedCommands();

    /**
     * Adds a blocked command to this adapter's set of blocked commands. If the command is already
     * in the set, this method does nothing.
     *
     * @param command the command to add
     */
    void addBlockedCommand(String command);

    /**
     * Removes a blocked command from this adapter's set of blocked commands.
     *
     * @param command the command to remove
     * @return whether the set contained given command
     */
    boolean removeBlockedCommand(String command);

    /**
     * @return the permission a player must have to execute blocked commands
     */
    String getBypassPermission();

    /**
     * @return whether the plugin will show error messages when somebody is not permitted to execute
     * a blocked command
     */
    boolean isShowErrorMessage();

    /**
     * @return whether the plugin will show error messages when somebody is not permitted to
     * tab-complete a blocked command
     */
    boolean isShowTabErrorMessage();

    /**
     * @return the error message displayed when somebody tries to execute a blocked command
     */
    String getErrorMessage();

    /**
     * @return whether to block tab-completion for blocked commands
     */
    boolean isPreventTab();

    /**
     * <p>What strategy to use when blocking tab-complete replies from the server.</p> <p> true:
     * block all completions returning a targeted command (for example, if /p is typed and /pl is
     * blocked, print error message) </p> <p> false: just remove blocked commands from list (in the
     * above example, other commands starting with p would still be shown without notice) </p>
     *
     * @return whether tab restrictive mode is enabled
     */
    boolean isTabRestrictiveMode();

    /**
     * @return whether to display a message to the command sender when bypassing CBU
     */
    boolean isNotifyBypass();

    /**
     * @return the message displayed when bypassing CBU
     * @see #isNotifyBypass()
     */
    String getBypassMessage();

    /**
     * @return the message displayed when somebody tries to tab-complete a blocked command in
     * restrictive mode
     */
    String getTabErrorMessage();
}
