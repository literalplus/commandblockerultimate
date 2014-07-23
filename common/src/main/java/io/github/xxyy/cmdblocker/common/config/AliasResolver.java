package io.github.xxyy.cmdblocker.common.config;

import java.util.List;

/**
 * Resolves all aliases of a given command
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.7.14
 */
public interface AliasResolver {
    /**
     * Resolves the aliases of the given command
     * @param commandName Name of the command or any alias of the command
     * @return A List of all aliases and the name of requested command, <b>excluding {@code commandName}</b> or an empty List if none were found
     */
    List<String> resolve(String commandName);

    /**
     * Refreshes this resolver's internal data. (Data is normally acquired through reflecting the server's command map)
     */
    void refreshMap();
}
