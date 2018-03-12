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

import li.l1t.cbu.common.platform.SenderAdapter;
import li.l1t.cbu.common.util.CommandExtractor;

import java.util.Optional;

/**
 * A simple tab complete request that lazily computes its command line object, but caches the result.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-28
 */
public class SimpleTabCompleteRequest implements TabCompleteRequest {
    private final SenderAdapter sender;
    private final String rawCursor;
    private final boolean assumeCommand;
    private CommandLine commandLine;

    public SimpleTabCompleteRequest(SenderAdapter sender, String rawCursor, boolean assumeCommand) {
        this.sender = sender;
        this.rawCursor = rawCursor;
        this.assumeCommand = assumeCommand;
    }

    @Override
    public SenderAdapter getSender() {
        return sender;
    }

    @Override
    public String getCursor() {
        String tmpCursor = getRawCursor();
        if (isAssumeCommand() && !CommandExtractor.isCommand(tmpCursor)) {
            return '/' + tmpCursor;
        } else {
            return tmpCursor;
        }
    }

    @Override
    public String getRawCursor() {
        return rawCursor;
    }

    @Override
    public boolean isAssumeCommand() {
        return assumeCommand;
    }

    @Override
    public CommandLine toCommandLine() {
        if (commandLine == null) {
            commandLine = new SimpleCommandLine(getCursor());
        }
        return commandLine;
    }

    @Override
    public Optional<CommandLine> findMergedCommand() {
        if (CommandExtractor.isCommand(getCursor())) {
            return Optional.of(toCommandLine());
        } else {
            return Optional.empty();
        }
    }
}
