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

/*
 * Command Blocker Ultimate
 * Copyright (C) 2014-2015 Philipp Nowak / Literallie (xxyy.github.io)
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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests the CommandHelper class for compliance with the JavaDoc declarations
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2014-09-20
 */
public class CommandHelperTest {
    @Test
    public void testGetRawCommand() {
        assertThat(CommandHelper.getRawCommand("/help"), is("help"));
        assertThat(CommandHelper.getRawCommand("/help test"), is("help"));
        assertThat(CommandHelper.getRawCommand("/minecraft:help"), is("minecraft:help"));
        assertThat(CommandHelper.getRawCommand("/minecraft:help test"), is("minecraft:help"));
        assertThat(CommandHelper.getRawCommand("/bukkit:help"), is("bukkit:help"));
        assertThat(CommandHelper.getRawCommand("/bukkit:help something multiple arguments wow"), is("bukkit:help"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getRawCommand__startsWithSlash() {
        //given
        String chatMessage = " some mesage starting with a space and not a slash";
        //when
        CommandHelper.getRawCommand(chatMessage);
        //then an IAE is thrown
    }

    @Test
    public void testRemoveModPrefix() {
        assertThat(CommandHelper.removeModPrefix("help"), is("help"));
        assertThat(CommandHelper.removeModPrefix("minecraft:help"), is("help"));
        assertThat(CommandHelper.removeModPrefix("bukkit:help"), is("help"));
        assertThat(CommandHelper.removeModPrefix("hey look i have spaces:help"), is("help"));
    }
}
