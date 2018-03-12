/*
 * Command Blocker Ultimate
 * Copyright (C) 2014-2018 Philipp Nowak / Literallie (l1t.li)
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

package li.l1t.cbu.common.util;

import com.google.common.base.Preconditions;

/**
 * Static utility class which helps dealing with commands and related.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2014-07-22
 */
public final class CommandExtractor {
    private CommandExtractor() {

    }

    /**
     * Turns a chat message, as entered by a player in Minecraft into a raw command name, as used
     * by the CommandBlockerUltimate config file.
     *
     * @param chatMessage the chat message to parse, starting with a slash ('/')
     * @return raw command of the message, in lower case
     * @throws IllegalArgumentException if the command does not start with a slash
     */
    public static String getRawCommand(String chatMessage) {
        Preconditions.checkArgument(isCommand(chatMessage),
                "getRawCommand('%s') requires a command, starting with a slash, to be passed!",
                chatMessage);
        String rawCommand;

        int spaceIndex = chatMessage.indexOf(' ');
        if (spaceIndex == -1) {
            rawCommand = chatMessage.substring(1);
        } else {
            rawCommand = chatMessage.substring(1, spaceIndex); //first word of the message
        }

        return rawCommand.toLowerCase(); //Bukkit saves commands in lower case
    }

    /**
     * @param chatMessage the chat message to inspect
     * @return whether given message is a command, judging from its text only
     */
    public static boolean isCommand(String chatMessage) {
        return !chatMessage.isEmpty() && chatMessage.charAt(0) == '/';
    }

    /**
     * Removes the Minecraft "mod prefix" from a command name. For example, when passed
     * "bukkit:kill", this method returns "kill".
     *
     * @param commandName Raw command name, without slash or arguments. See {@link
     *                    #getRawCommand(String)}.
     * @return The raw raw command name, without prefix.
     */
    public static String removeModPrefix(String commandName) {
        int colonIndex = commandName.indexOf(':');
        if (colonIndex != -1) {
            commandName = commandName.substring(colonIndex + 1); //Remove prefix including the colon
        }
        return commandName;
    }

    /**
     * Attempts to merge a command cursor (the text the player had already typed in the chat box at the moment the
     * tab-complete request was issued) with a raw tab suggestion.
     * <p>
     * If the cursor is an empty string, the raw suggestion is returned unmodified. If the cursor ends with a space,
     * the suggestion is returned concatenated to it. If the cursor does not end with a space, the last word
     * (i.e. whatever is after the last space) is replaced by the suggestion and the result is returned.
     * </p>
     *
     * @param cursor        the cursor text that the player had already entered into the chat box at the moment the tab-complete
     *                      request was issued, empty string for none or unknown, must not be null
     * @param rawSuggestion the suggestion to merge with, must not be null
     * @return the merged command line, never null, but no guarantees are made as to whether it is a valid command line
     */
    public static String mergeTabSuggestion(String cursor, String rawSuggestion) {
        Preconditions.checkNotNull(cursor, "cursor");
        Preconditions.checkNotNull(rawSuggestion, "rawSuggestion");
        int lastSpaceIndex = cursor.lastIndexOf(' ');
        if (lastSpaceIndex == -1) {
            return rawSuggestion;
        } else {
            return cursor.substring(0, lastSpaceIndex + 1) + rawSuggestion;
        }
    }
}
