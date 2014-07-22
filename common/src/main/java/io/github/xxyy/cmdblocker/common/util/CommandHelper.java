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
        int spaceIndex = chatMessage.indexOf(" ");
        return spaceIndex == -1 ? chatMessage.substring(1) : chatMessage.substring(1, spaceIndex - 1);
    }
}
