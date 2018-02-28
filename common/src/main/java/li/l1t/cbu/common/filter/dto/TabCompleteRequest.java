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

/**
 * Provides access to the event data of a tab-completion of a chat command, with utility methods for easy processing.
 * <p><b>Note:</b> This represents an incoming event, which has only the command line (as far as the player has typed,
 * "cursor") and an optional flag to assume it is a command even when not starting with a slash.</p>
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-28
 */
public interface TabCompleteRequest {
    /**
     * @return who issued this completion request
     */
    SenderAdapter getSender();

    /**
     * @return what the player has typed in the chat box so far, with a slash prepended if the text does not
     * {@link CommandExtractor#isCommand(String) indicate a command} but a command is
     * {@link #isAssumeCommand() to be assumed}
     */
    String getCursor();

    /**
     * @return the raw text the player has typed in the chat box so far, ignoring {@link #isAssumeCommand()}
     */
    String getRawCursor();

    /**
     * @return whether this request should be treated as a command even if it doesn't start with a slash
     * (used in command blocks)
     */
    boolean isAssumeCommand();

    /**
     * @return this request's {@link #getCursor()} coverted to a command line object
     * @throws IllegalArgumentException if this request does not represent a command
     */
    CommandLine toCommandLine();
}
