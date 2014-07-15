package io.github.xxyy.cmdblocker.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a generic base for {@link io.github.xxyy.cmdblocker.common.ConfigAdapter}s.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 25.3.14 // 1.3
 */
public abstract class GenericConfigAdapter implements ConfigAdapter {
    public static final Pattern COMMAND_PATTERN = Pattern.compile("^/((\\S)+)");

    @Override
    public String getRawCommand(final String chatMessage) { //I'm so looking forward to when everyone has Java 8 and I can use a default method here
        Matcher matcher = COMMAND_PATTERN.matcher(chatMessage); //Remove slash

        if (!matcher.find() || matcher.groupCount() == 0) {
            return "";
        }

        return matcher.group(1);
    }
}
