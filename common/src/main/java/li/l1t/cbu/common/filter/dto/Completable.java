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

import java.util.Optional;

/**
 * Represents something that may offer tab-completion to full command lines for a specific sender.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-03-06
 */
public interface Completable {
    /**
     * @return the sender that completions will be sent to
     */
    SenderAdapter getSender();

    /**
     * Attempts to compute the full command line that will be in the chat box when the suggestion
     * is accepted, using {@link li.l1t.cbu.common.util.CommandExtractor#mergeTabSuggestion(String, String)}.
     *
     * @return an optional containing the command line constructed from text and cursor, if available, or an empty
     * optional otherwise
     */
    Optional<CommandLine> findMergedCommand();
}
