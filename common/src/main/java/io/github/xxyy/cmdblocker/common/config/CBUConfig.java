package io.github.xxyy.cmdblocker.common.config;

import net.cubespace.Yamler.Config.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents the CommandBlockerUltimate configuration file.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 16.7.14
 */
public class CBUConfig extends Config {
    @Path("target-commands")
    @Comment("Define what commands should be blocked in the following property: (without leading slash)")
    private List<String> targetCommands = Arrays.asList("?", "help", "plugins", "pl", "version", "ver", "about");

    @Path("bypass-permission")
    @Comment("Define the permission that a player needs to bypass the protection: (Default: cmdblock.bypass)")
    private String bypassPermission = "cmdblock.bypass";

    @Path("show-error-message")
    @Comment("Should the plugin send an error message if one is not allowed to execute/tab-complete a command? (Default: true)")
    private boolean showErrorMessage = true;

    @Path("error-message")
    @Comments({"What should that message be? (Use & for color codes, HTML escape codes accepted)",
            "Example: &c&lError message &euro;&auml;&#00A7;"})
    private String errorMessage = "&cI am sorry, but you are not permitted to execute this command.";

    @Path("prevent-tab")
    @Comments({"@since 1.02",
            "Whether to prevent tab-completion for blocked commands.",
            "Note: Requires ProtocolLib on Spigot!",
            "Default value: true"})
    private boolean preventTab = true;

    @Path("tab-restrictive-mode")
    @Comments({"What strategy to use when blocking tab-complete replies from the server.",
            "true: block all completions returning a targeted command (for example, if /p is typed and /pl is blocked, print error message)",
            "false: just remove blocked commands from list (in the above example, other commands starting with p would still be shown without notice)\n",
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

    /**
     * Tries to initialize this config by calling {@link #init()}.
     * If an exception occurs, it is logged to {@code logger} with details on how to get help from this plugin's author.
     * @param logger Logger to print to
     * @return whether the configuration was initialized successfully.
     */
    public boolean tryInit(Logger logger) {
        try {
            this.init();
        } catch (InvalidConfigurationException e) {
            logger.log(Level.WARNING, "Encountered exception!", e);
            logger.warning("Could not not configuration file. Please double-check your YAML syntax with http://yaml-online-parser.appspot.com/.");
            logger.warning("The plugin might (will) not function in the way you want it to (since it doesn't know what you want)");
            logger.warning("If you don't understand this error, try asking in #lit on irc.spi.gt. (Please don't leave a second after asking)");
            logger.warning("WebChat: http://irc.spi.gt/iris/?channels=lit");
            return false;
        }

        return true;
    }

    public boolean isBlocked(String commandName) {
        return targetCommands.contains(commandName);
    }

    public String getRawCommand(String chatMessage) {
        int spaceIndex = chatMessage.indexOf(" ");
        return spaceIndex == -1 ? chatMessage : chatMessage.substring(0, spaceIndex - 1);
    }

    public String getBypassPermission() {
        return bypassPermission;
    }

    public boolean isShowErrorMessage() {
        return showErrorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isPreventTab() {
        return preventTab;
    }

    public boolean isTabRestrictiveMode() {
        return tabRestrictiveMode;
    }
}
