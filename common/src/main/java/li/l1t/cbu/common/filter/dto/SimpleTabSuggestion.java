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

import com.google.common.base.Preconditions;
import li.l1t.cbu.common.platform.SenderAdapter;
import li.l1t.cbu.common.util.CommandExtractor;

import java.util.Optional;

/**
 * Represents a single tab suggestion for the purpose of being processed by filters.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-03-06
 */
public class SimpleTabSuggestion implements Completable {
    private final SenderAdapter sender;
    private final String cursor;
    private String text;

    public SimpleTabSuggestion(SenderAdapter sender, String text) {
        this(sender, text, "");
    }

    public SimpleTabSuggestion(SenderAdapter sender, String text, String cursor) {
        this.sender = Preconditions.checkNotNull(sender, "sender");
        this.text = Preconditions.checkNotNull(text, "text");
        this.cursor = Preconditions.checkNotNull(cursor, "cursor");
    }

    @Override
    public SenderAdapter getSender() {
        return sender;
    }

    @Override
    public Optional<CommandLine> findMergedCommand() {
        String merged = CommandExtractor.mergeTabSuggestion(cursor, text);
        if (!CommandExtractor.isCommand(merged)) {
            return Optional.empty();
        } else {
            return Optional.of(new SimpleCommandLine(merged));
        }
    }

    public Optional<String> getCursor() {
        return cursor.isEmpty() ? Optional.empty() : Optional.of(cursor);
    }

    public void setText(String text) {
        this.text = Preconditions.checkNotNull(text, "text");
    }
}
