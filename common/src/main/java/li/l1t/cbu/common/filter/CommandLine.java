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

package li.l1t.cbu.common.filter;

import li.l1t.common.string.Args;

/**
 * Stores information about a single command execution for computation of filter opinions.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-08-19
 */
public interface CommandLine {
    /**
     * @return the complete messages of this line, as entered by the player
     */
    String getFullMessage();

    /**
     * @return the resolved {@link #getRawCommand() command} of this line, excluding plugin prefixes
     */
    String getRootCommand();

    /**
     * @return the literal root command of this line, meaning everything between the initial slash (/) and the first
     * space, or the the end of the message, if there is no space, as entered by the player, but converted to lower
     * case
     */
    String getRawCommand();

    /**
     * @return an {@link Args} object representing the actual arguments of this line
     */
    Args args();
}
