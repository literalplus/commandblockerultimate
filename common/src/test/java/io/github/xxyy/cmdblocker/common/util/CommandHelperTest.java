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

package io.github.xxyy.cmdblocker.common.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests the CommandHelper class for compliance with the JavaDoc declarations
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 20/09/14
 */
public class CommandHelperTest {
    @Test
    public void testGetRawCommand() {
        assertThat(CommandHelper.getRawCommand("/help"), is("help"));
        assertThat(CommandHelper.getRawCommand("/help test"), is("help"));
        assertThat(CommandHelper.getRawCommand("/minecraft:help"), is("help"));
        assertThat(CommandHelper.getRawCommand("/minecraft:help test"), is("help"));
        assertThat(CommandHelper.getRawCommand("/bukkit:help"), is("help"));
        assertThat(CommandHelper.getRawCommand("/bukkit:help something multiple arguments wow"), is("help"));
    }
}
