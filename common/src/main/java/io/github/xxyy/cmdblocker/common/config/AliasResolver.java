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
