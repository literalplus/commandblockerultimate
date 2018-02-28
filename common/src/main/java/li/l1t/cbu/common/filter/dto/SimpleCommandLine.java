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

package li.l1t.cbu.common.filter.dto;

import li.l1t.cbu.common.util.CommandExtractor;
import li.l1t.common.string.Args;

/**
 * A simple implementation of a command line that eagerly computes all fields excluding {@link #args()} using {@link
 * CommandExtractor}.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-08-19
 */
public class SimpleCommandLine implements CommandLine {
    private final String fullMessage;
    private final String rootCommand;
    private final String rawCommand;
    private Args args = null;

    /**
     * Creates a command line.
     *
     * @param fullMessage the full chat message, must be {@link CommandExtractor#isCommand(String) a command}
     * @throws IllegalArgumentException if given message is not a command
     */
    public SimpleCommandLine(String fullMessage) {
        this.fullMessage = fullMessage;
        this.rawCommand = CommandExtractor.getRawCommand(fullMessage);
        this.rootCommand = CommandExtractor.removeModPrefix(this.rawCommand);
    }

    @Override
    public String getFullMessage() {
        return fullMessage;
    }

    @Override
    public String getRootCommand() {
        return rootCommand;
    }

    @Override
    public String getRawCommand() {
        return rawCommand;
    }

    @Override
    public Args args() {
        if (args == null) {
            args = Args.fromCommandLine(getFullMessage());
        }
        return args;
    }
}
