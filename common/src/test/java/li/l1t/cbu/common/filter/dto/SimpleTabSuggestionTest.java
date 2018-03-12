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

import li.l1t.cbu.common.platform.FakeSender;
import li.l1t.cbu.common.util.CommandExtractor;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class SimpleTabSuggestionTest {
    private static final String NOT_A_COMMAND = "this is definitely not a command";
    private static final String A_COMMAND = "/greetings sub";

    @Test
    void findMergedCommand__not_a_command() {
        // given
        SimpleTabSuggestion suggestion = new SimpleTabSuggestion(new FakeSender(), NOT_A_COMMAND);
        assumeTrue(!CommandExtractor.isCommand(NOT_A_COMMAND));
        // when
        Optional<CommandLine> result = suggestion.findMergedCommand();
        // then
        assertThat(result.isPresent(), is(false));
    }

    @Test
    void findMergedCommand__suggested_command() {
        // given
        SimpleTabSuggestion suggestion = new SimpleTabSuggestion(new FakeSender(), A_COMMAND);
        assumeTrue(CommandExtractor.isCommand(A_COMMAND));
        // when
        Optional<CommandLine> result = suggestion.findMergedCommand();
        // then
        assertThat(result.isPresent(), is(true));
        assertThat(result.orElseThrow(AssertionError::new).getFullMessage(), is(A_COMMAND));
    }

    @Test
    void findMergedCommand__cursor_with_sub_command() {
        // given
        SimpleTabSuggestion suggestion = new SimpleTabSuggestion(new FakeSender(), "my-sub", "/henlo ");
        // when
        Optional<CommandLine> result = suggestion.findMergedCommand();
        // then
        assertThat(result.isPresent(), is(true));
        assertThat(result.orElseThrow(AssertionError::new).getFullMessage(), is("/henlo my-sub"));
    }

    @Test
    void findMergedCommand__cursor_with_root_command() {
        // given
        SimpleTabSuggestion suggestion = new SimpleTabSuggestion(new FakeSender(), "/my-sub", "/henlo");
        // when
        Optional<CommandLine> result = suggestion.findMergedCommand();
        // then
        assertThat(result.isPresent(), is(true));
        assertThat(result.orElseThrow(AssertionError::new).getFullMessage(), is("/my-sub"));
    }

    @Test
    void findMergedCommand__text_change() {
        // given
        SimpleTabSuggestion suggestion = new SimpleTabSuggestion(new FakeSender(), "my-sub", "/henlo ");
        // when
        suggestion.setText("it me");
        // then
        Optional<CommandLine> result = suggestion.findMergedCommand();
        assertThat(result.isPresent(), is(true));
        assertThat(result.orElseThrow(AssertionError::new).getFullMessage(), is("/henlo it me"));
    }

    @Test
    void getCursor__absent() {
        // given
        SimpleTabSuggestion suggestion = new SimpleTabSuggestion(new FakeSender(), "some text");
        // when
        Optional<String> cursor = suggestion.getCursor();
        // then
        assertThat(cursor.isPresent(), is(false));
    }

    @Test
    void getCursor__present() {
        // given
        String rawCursor = "it me the cursor";
        SimpleTabSuggestion suggestion = new SimpleTabSuggestion(new FakeSender(), "some text", rawCursor);
        // when
        Optional<String> cursor = suggestion.getCursor();
        // then
        assertThat(cursor.isPresent(), is(true));
        assertThat(cursor.orElseThrow(AssertionError::new), is(rawCursor));
    }
}