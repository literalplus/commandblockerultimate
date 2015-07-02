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

package io.github.xxyy.cmdblocker.common.util;

/**
 * Static utility class which helps dealing with commands and related.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.7.14
 */
public final class CommandHelper {
    private CommandHelper() {

    }

    /**
     * Turns a chat message, as entered by a player in Minecraft (this is obviously an unintended coincidence), into
     * a raw command name, as used by the CommandBlockerUltimate config file.
     * @param chatMessage The chat message to parse
     * @return raw command of that
     */
    public static String getRawCommand(String chatMessage) {
        chatMessage = chatMessage.toLowerCase(); //Bukkit saves all command in lower case

        int spaceIndex = chatMessage.indexOf(" "); //For finding the executed command's name
        if (spaceIndex == -1) { //If no space found
            chatMessage = chatMessage.substring(1); //Just remove slash
        } else { //If we have a space
            chatMessage = chatMessage.substring(1, spaceIndex); //Get the first word of the message and remove slash
        }

        return removeModPrefix(chatMessage); //Return the raw command name!
    }

    /**
     * Removes the Minecraft "mod prefix" from a command name.
     * For example, when passed "bukkit:kill", this method returns "kill".
     * @param commandName Raw command name, without slash or arguments. See {@link #getRawCommand(String)}.
     * @return The raw raw command name, without prefix.
     */
    public static String removeModPrefix(String commandName) {
        int colonIndex = commandName.indexOf(":"); //For removing those minecraft plugin/mod prefixes, e.g. bukkit:kill -> kill
        if(colonIndex != -1) { //If we have a colon
            commandName = commandName.substring(colonIndex + 1); //Remove prefix including the colon
        }
        return commandName;
    }
}
