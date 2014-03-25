package io.github.xxyy.cmdblocker.common;

import java.io.File;
import java.util.logging.Logger;

/**
 * Allows high-level code to interface with server implementation-specific configuration.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 25.3.14 // 1.3
 */
public interface ConfigAdapter {

    ///////////// Config entry paths /////////////////
    String SHOW_ERROR_MESSAGE = "show-error-message";
    String PREVENT_TAB = "prevent-tab";
    String TARGET_COMMANDS = "target-commands";
    String BYPASS_PERMISSION = "bypass-permission";
    String ACCESS_DENIED_MESSAGE = "error-message";
    String TAB_RESTRICTIVE_MODE = "tab-restrictive-mode";

    /**
     * Checks whether a specific command is blocked in configuration.
     * @param commandName Name of the command to check
     * @return Whether that command is blocked.
     */
    boolean isBlocked(String commandName);

    /**
     * Gets the raw command name of a chat message.
     * Example: {@code /cbu some arg} -> {@code cbu}
     * @param chatMessage Chat message to use
     * @return Raw command name for that message or an empty String if no command could be found.
     */
    String getRawCommand(String chatMessage);

    /**
     * Gets a boolean value from configuration
     * @param path String representation of where to find that boolean in the configuration
     * @param def Default value to return if the value at that path is invalid or that path could not be found.
     * @return A boolean found at {@code path} or {@code def} on error.
     */
    boolean getBoolean(String path, boolean def);

    /**
     * Checks whether the configuration contains a value at a specified path.
     * @param path Path to seek at
     * @return whether the configuration contains a value at the given path.
     */
    boolean contains(String path);

    /**
     * @return a Logger instance associated with this adapter.
     */
    Logger getLogger();

    /**
     * Reloads the configuration managed by this adapter.
     */
    void reload();

    /**
     * @return the {@link java.io.File} this configuration is stored in.
     */
    File getFile();
}
