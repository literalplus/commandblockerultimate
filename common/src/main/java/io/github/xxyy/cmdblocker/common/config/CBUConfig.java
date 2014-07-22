package io.github.xxyy.cmdblocker.common.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.cubespace.Yamler.Config.*;

import java.io.File;
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
            "If you specify a command, its aliases will be blocked also. (Example: 'tell' will also block 'msg', 'bukkit:tell', etc.)"})
    private List<String> targetCommands = Lists.newArrayList("help", "plugins", "version");

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
            super.init();
        } catch (InvalidConfigurationException e) {
            throw new InvalidConfigException(e);
        }
    }

    @Override
    public void resolveAliases(AliasResolver aliasResolver) {
        for (String requestedCommand : ImmutableList.copyOf(targetCommands)) {
            targetCommands.addAll(aliasResolver.resolve(requestedCommand));
        }
    }

    @Override
    public boolean isBlocked(String commandName) {
        return targetCommands.contains(commandName);
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
