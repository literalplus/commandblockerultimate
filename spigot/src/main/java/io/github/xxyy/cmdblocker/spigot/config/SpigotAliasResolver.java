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
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;

import io.github.xxyy.cmdblocker.common.config.AliasResolver;
import io.github.xxyy.cmdblocker.common.util.CommandHelper;

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
        String rawName = CommandHelper.removeModPrefix(commandName).toLowerCase();
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

            if (!commandMap.getClass().getSimpleName().equals("SimpleCommandMap")) { //This is dirty
                plugin.getLogger().warning(String.format("Detected non-standard command map - Aliases may fail to " +
                        "resolve. It is possible that it has been replaced by a plugin or you are using an incompatible " +
                        "fork. Found this command map: %s", commandMap.getClass().getName()));
                plugin.getLogger().warning(String.format("Server software: %s %s",
                        plugin.getServer().getName(), plugin.getServer().getBukkitVersion()));
            }

            Field actualMapField = commandMap.getClass().getDeclaredField("knownCommands"); //class SimpleCommandMap
            actualMapField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Command> actualMap = (Map<String, Command>) actualMapField.get(commandMap);

            return actualMap;
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
            plugin.getLogger().warning("Could not get CraftBukkit command map! That probably means that they changed their internals." +
                    "Please contact the plugin author at devnull@nowak-at.net or http://irc.spi.gt/iris/?channels=lit ! Thank you!");
            plugin.getLogger().warning("This means that we can't get aliases for Bukkit and Minecraft built-in commands. Sorry for that!");
        }

        return null;
    }
}
