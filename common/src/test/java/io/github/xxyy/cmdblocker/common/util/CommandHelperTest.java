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
