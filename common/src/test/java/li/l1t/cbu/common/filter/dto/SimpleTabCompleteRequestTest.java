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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

class SimpleTabCompleteRequestTest {
    @Test
    void getCursor__no_assume_no_command() {
        // given
        String rawCursor = "henlo";
        SimpleTabCompleteRequest request = givenARequest(rawCursor, false);
        // when
        String result = request.getCursor();
        // then
        assertThat(result, is(rawCursor));
        assertThat(request.getRawCursor(), is(rawCursor));
    }

    private SimpleTabCompleteRequest givenARequest(String cursor, boolean assumeCommand) {
        return new SimpleTabCompleteRequest(new FakeSender(), cursor, assumeCommand);
    }

    @Test
    void getCursor__no_assume_but_command() {
        // given
        String rawCursor = "/henlo";
        SimpleTabCompleteRequest request = givenARequest(rawCursor, false);
        // when
        String result = request.getCursor();
        // then
        assertThat(result, is(rawCursor));
        assertThat(request.getRawCursor(), is(rawCursor));
    }

    @Test
    void getCursor__assume_no_command() {
        // given
        String rawCursor = "henlo";
        SimpleTabCompleteRequest request = givenARequest(rawCursor, true);
        // when
        String result = request.getCursor();
        // then
        assertThat(result, is('/' + rawCursor));
        assertThat(request.getRawCursor(), is(rawCursor));
    }

    @Test
    void getCursor__assume_and_command() {
        // given
        String rawCursor = "/henlo";
        SimpleTabCompleteRequest request = givenARequest(rawCursor, true);
        // when
        String result = request.getCursor();
        // then
        assertThat(result, is(rawCursor));
        assertThat(request.getRawCursor(), is(rawCursor));
    }

    @Test
    void toCommandLine__raw_command() {
        // given
        String rawCommand = "henlo";
        String rawCursor = "/" + rawCommand + " aa bb";
        SimpleTabCompleteRequest request = givenARequest(rawCursor, false);
        // when
        CommandLine line = request.toCommandLine();
        // then
        thenTheCommandLineMatches(line, rawCommand, rawCursor);
    }

    private void thenTheCommandLineMatches(CommandLine line, String rawCommand, String fullMessage) {
        assertThat(line.getFullMessage(), is(fullMessage));
        assertThat(line.getRootCommand(), is(rawCommand));
        assertThat(line.getRawCommand(), is(rawCommand));
    }

    @Test
    void toCommandLine__assumed_command() {
        // given
        String rawCommand = "henlo";
        String rawCursor = rawCommand + " aa bb";
        SimpleTabCompleteRequest request = givenARequest(rawCursor, true);
        // when
        CommandLine line = request.toCommandLine();
        // then
        thenTheCommandLineMatches(line, rawCommand, '/' + rawCursor);
    }

    @Test
    void toCommandLine__caching() {
        // given
        String rawCursor = "/henlo aa bb";
        SimpleTabCompleteRequest request = givenARequest(rawCursor, false);
        // when
        CommandLine line1 = request.toCommandLine();
        CommandLine line2 = request.toCommandLine();
        // then
        assertThat(line2, is(sameInstance(line1)));
    }

    @Test
    void toCommandLine__not_command() {
        // given
        String rawCommand = "henlo";
        String rawCursor = rawCommand + " aa bb";
        SimpleTabCompleteRequest request = givenARequest(rawCursor, false);
        // when
        Assertions.assertThrows(IllegalArgumentException.class, request::toCommandLine);
    }

    @Test
    void findMergedCommand__assumed_command() {
        // given
        String rawCommand = "henlo";
        String rawCursor = rawCommand + " aa bb";
        SimpleTabCompleteRequest request = givenARequest(rawCursor, true);
        // when
        Optional<CommandLine> line = request.findMergedCommand();
        // then
        assertThat(line.isPresent(), is(true));
        thenTheCommandLineMatches(line.orElseThrow(AssertionError::new), rawCommand, '/' + rawCursor);
    }

    @Test
    void findMergedCommand__not_command() {
        // given
        String rawCommand = "henlo";
        String rawCursor = rawCommand + " aa bb";
        SimpleTabCompleteRequest request = givenARequest(rawCursor, false);
        // when
        Optional<CommandLine> line = request.findMergedCommand();
        // then
        assertThat(line.isPresent(), is(false));
    }
}