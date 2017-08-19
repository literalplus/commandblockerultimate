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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class SimpleCommandLineTest {
    @Test
    void getFullMessage() {
        //given
        String fullMessage = "/command test arguments wow";
        //when
        SimpleCommandLine line = givenALineWith(fullMessage);
        //then
        assertThat(line.getFullMessage(), is(fullMessage));
    }

    private SimpleCommandLine givenALineWith(String fullMessage) {
        return new SimpleCommandLine(fullMessage);
    }

    @ParameterizedTest
    @CsvSource({"/command test, command", "/plugin:cmd test, cmd"})
    void getRootCommand__simple(String fullMessage, String rootCommand) {
        //given parameters
        //when
        SimpleCommandLine line = givenALineWith(fullMessage);
        //then
        assertThat(line.getRootCommand(), is(rootCommand));
    }

    @Test
    void getRawCommand() {
        //given
        String rawCommand = "rawCommand";
        String fullMessage = "/" + rawCommand + " with parameters";
        //when
        SimpleCommandLine line = givenALineWith(fullMessage);
        //then
        assertThat(line.getRawCommand(), is(rawCommand.toLowerCase()));
    }

    @Test
    void args__three() {
        //given
        String[] args = new String[]{"some", "arguments", "wow"};
        String fullMessage = givenAFullMessageFromArgs(args);
        //when
        SimpleCommandLine line = givenALineWith(fullMessage);
        //then
        thenTheLinesArgsAre(args, line);
    }

    private String givenAFullMessageFromArgs(String[] args) {
        return "/someCommand " + Arrays.stream(args).collect(Collectors.joining(" "));
    }

    private void thenTheLinesArgsAre(String[] args, SimpleCommandLine line) {
        assertThat(line.args(), is(notNullValue()));
        assertThat(line.args().args(), is(args));
    }

    @Test
    void args__none() {
        //given
        String[] args = new String[]{};
        String fullMessage = "/command";
        //when
        SimpleCommandLine line = givenALineWith(fullMessage);
        //then
        thenTheLinesArgsAre(args, line);
    }
}