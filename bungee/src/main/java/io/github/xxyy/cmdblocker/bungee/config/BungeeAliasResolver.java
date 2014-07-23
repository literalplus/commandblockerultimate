package io.github.xxyy.cmdblocker.bungee.config;

import com.google.common.collect.ImmutableList;
import io.github.xxyy.cmdblocker.common.config.AliasResolver;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Resolves aliases on BungeeCord proxy servers.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 23.7.14
 */
public class BungeeAliasResolver implements AliasResolver {

    private Map<String, Command> commandMap;
    private final Plugin plugin;

    public BungeeAliasResolver(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void refreshMap() {
        this.commandMap = stealCommandMap(plugin);
    }

    @Override
    public List<String> resolve(String commandName) {
        String rawName = commandName.toLowerCase();
        Command foundCommand = null;
        if(commandMap != null) { //If we have the internal command map, we might as well use it
            foundCommand = commandMap.get(commandName); //Versions with prefixes and aliases are stored too
        }

        if(foundCommand == null) { //This handles if the command is not found AND if we don't have the map
            return ImmutableList.of();
        }

        List<String> rtrn = Arrays.asList(foundCommand.getAliases()); //Aliases are stored in lower case

        rtrn.add(foundCommand.getName()); //Also stored in lower case
        rtrn.remove(rawName); //Note that the argument must not always be the real name - users tend to specify aliases

        return rtrn;
    }

    private Map<String, Command> stealCommandMap(Plugin plugin) {
        try {
            Field commandMapField = plugin.getProxy().getPluginManager().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Command> commandMap = (Map<String, Command>) commandMapField.get(plugin.getProxy().getPluginManager());

            return commandMap;
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
            plugin.getLogger().warning("Could not get BungeeCord command map! That probably means that they changed their internals." +
                    "Please contact the plugin author at devnull@nowak-at.net or http://irc.spi.gt/iris/?channels=lit ! Thank you!");
            plugin.getLogger().warning("This means that we can't get aliases for commands. Sorry for that!");
        }

        return null;
    }
}
