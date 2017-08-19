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

package li.l1t.cbu.spigot.config;

import com.google.common.collect.ImmutableList;
import li.l1t.cbu.common.config.AliasResolver;
import li.l1t.cbu.common.util.CommandExtractor;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A command alias resolver for Spigot server. Also supports Minecraft and Bukkit commands using reflection.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.7.14
 */
public class SpigotAliasResolver implements AliasResolver {
    private static final String SIMPLE_COMMAND_MAP_NAME = "org.bukkit.command.SimpleCommandMap";
    private Map<String, Command> commandMap;
    private final Plugin plugin;

    public SpigotAliasResolver(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void refreshMap() {
        this.commandMap = stealCommandMap(plugin);
    }

    @Override
    public List<String> resolve(String commandName) {
        String rawName = CommandExtractor.removeModPrefix(commandName).toLowerCase();
        Command foundCommand;
        if (commandMap != null) { //If we have the internal command map, we might as well use it
            foundCommand = commandMap.get(commandName); //Versions with prefixes and aliases are stored too
        } else {
            foundCommand = plugin.getServer().getPluginCommand(commandName); //Bukkit is filtering internal commands so we can't get those here
        }

        if (foundCommand == null) {
            return ImmutableList.of();
        }

        List<String> rtrn = new ArrayList<>(foundCommand.getAliases()); //Aliases are stored in lower case

        rtrn.add(foundCommand.getName()); //Also stored in lower case
        rtrn.remove(rawName); //Note that the argument must not always be the real name - users tend to specify aliases

        return rtrn;
    }

    private Map<String, Command> stealCommandMap(Plugin plugin) {
        try {
            Field commandMapField = plugin.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            Object commandMap = commandMapField.get(plugin.getServer());
            Class<?> simpleCommandMapClazz = null;

            /*
                This is for compatibility with PerWorldPlugins: http://dev.bukkit.org/bukkit-plugins/perworldplugins/
                They replace Bukkit's SimpleCommandMap with their own fake version, but still keep it extending the
                one we know. So if we search the class' inheritance hierarchy, we should eventually get to a
                SimpleCommandMap, which has what we need. They also thankfully copy all the fields over to their
                version.
                 */
            Class<?> clazz = commandMap.getClass();
            do {
                if (clazz.getName().equals(SIMPLE_COMMAND_MAP_NAME)) {
                    simpleCommandMapClazz = clazz;
                    break;
                }
            } while ((clazz = clazz.getSuperclass()) != null); //Object.class.getSuperclass() -> null

            if (simpleCommandMapClazz == null) { //This is just error messages and helping with reporting custom command maps
                plugin.getLogger().warning(String.format("Detected non-standard command map - Aliases may fail to " +
                        "resolve. It is possible that it has been replaced by a plugin or you are using an incompatible " +
                        "fork. Found custom command map of class %s.", commandMap.getClass().getName()));
                plugin.getLogger().warning(String.format("Server software: %s %s %s",
                        plugin.getServer().getName(), plugin.getServer().getBukkitVersion(), plugin.getServer().getVersion()));

                StringBuilder pluginListBuilder = new StringBuilder("Installed plugins: ");
                for(Plugin installedPlugin : plugin.getServer().getPluginManager().getPlugins()) {
                    pluginListBuilder.append("{{ ")
                            .append(installedPlugin.isEnabled() ? "+ " : "- ")
                            .append(installedPlugin.getName())
                            .append(" by ").append(installedPlugin.getDescription().getAuthors())
                            .append(" version ").append(installedPlugin.getDescription().getVersion())
                            .append(" -> ").append(installedPlugin.getDescription().getWebsite())
                            .append(" }}, ");
                }
                plugin.getLogger().warning(pluginListBuilder.toString());

                simpleCommandMapClazz = commandMap.getClass(); //We can still try, right?

                try {
                    Plugin enemyPlugin = JavaPlugin.getProvidingPlugin(commandMap.getClass()); //This throws exceptions in various cases
                    plugin.getLogger().warning(String.format("Plugin providing the unexpected class: %s", enemyPlugin.getName()));
                } catch (IllegalStateException | IllegalArgumentException e) {
                    plugin.getLogger().warning("Unexpected class is not provided by a plugin, possibly using a heavily-modified fork!");
                }
            }

            Field actualMapField = simpleCommandMapClazz.getDeclaredField("knownCommands"); //class SimpleCommandMap
            actualMapField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Command> actualMap = (Map<String, Command>) actualMapField.get(commandMap);

            return actualMap;
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
            plugin.getLogger().warning("Could not get CraftBukkit command map! That could mean that they changed their " +
                    "internals. Please file an issue at https://github.com/xxyy/commandblockerultimate/issues with your " +
                    "server log and plugin list! Thank you!");
            plugin.getLogger().warning("This means that we can't get aliases for Bukkit and Minecraft built-in commands. Sorry for that!");
        }

        return null;
    }
}
