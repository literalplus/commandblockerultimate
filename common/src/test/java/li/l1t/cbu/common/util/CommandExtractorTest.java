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

package li.l1t.cbu.common.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the CommandExtractor class for compliance with the JavaDoc declarations
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2014-09-20
 */
class CommandExtractorTest {
    @Test
    void testGetRawCommand() {
        assertThat(CommandExtractor.getRawCommand("/help"), is("help"));
        assertThat(CommandExtractor.getRawCommand("/help test"), is("help"));
        assertThat(CommandExtractor.getRawCommand("/minecraft:help"), is("minecraft:help"));
        assertThat(CommandExtractor.getRawCommand("/minecraft:help test"), is("minecraft:help"));
        assertThat(CommandExtractor.getRawCommand("/bukkit:help"), is("bukkit:help"));
        assertThat(CommandExtractor.getRawCommand("/bukkit:help something multiple arguments wow"), is("bukkit:help"));
    }

    @Test
    void getRawCommand__startsWithSlash() {
        //given
        String chatMessage = " some mesage starting with a space and not a slash";
        //then an IAE is thrown
        assertThrows(
                IllegalArgumentException.class,
                //when
                () -> CommandExtractor.getRawCommand(chatMessage)
        );
    }

    @Test
    void testRemoveModPrefix() {
        assertThat(CommandExtractor.removeModPrefix("help"), is("help"));
        assertThat(CommandExtractor.removeModPrefix("minecraft:help"), is("help"));
        assertThat(CommandExtractor.removeModPrefix("bukkit:help"), is("help"));
        assertThat(CommandExtractor.removeModPrefix("hey look i have spaces:help"), is("help"));
    }

    @Test
    void testIsCommand__slashCheck() {
        assertThat(CommandExtractor.isCommand("/henlo"), is(true));
        assertThat(CommandExtractor.isCommand("/plug:henlo some params"), is(true));
        assertThat(CommandExtractor.isCommand("hen/lo"), is(false));
        assertThat(CommandExtractor.isCommand("henlo this is message/"), is(false));
    }
}
