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

package li.l1t.cbu.common.platform;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ChatColor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * An sender adapter that remembers which messages it was sent and keeps a list of permissions it has. Note that messages
 * are remembered in parsed form, i.e. with colour codes already replaced. (This does not respect HTML entities because
 * we can't depend on commons-lang in cbu-common: Spigot and Bungee use different versions)
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-21
 */
public class FakeSender implements SenderAdapter {
    private final List<String> grantedPermissions = new ArrayList<>();
    private final List<String> receivedMessages = new ArrayList<>();

    @Nonnull
    @Override
    public String getName() {
        return "Dummy";
    }

    @Override
    public boolean hasPermission(String permission) {
        Preconditions.checkNotNull(permission, "permission");
        return grantedPermissions.contains(permission);
    }

    public void grantPermission(String permission) {
        grantedPermissions.add(permission);
    }

    public void revokePermission(String permission) {
        grantedPermissions.remove(permission);
    }

    /**
     * {@inheritDoc}
     * <p><b>Note:</b> This does not respect HTML escapes for technical reasons.</p>
     *
     * @param message the raw message to send
     */
    @Override
    public void sendMessage(String message) {
        Preconditions.checkNotNull(message, "message");
        String finalMessage = ChatColor.translateAlternateColorCodes('&', message);
        receivedMessages.add(finalMessage);
    }

    public void forgetMessages() {
        receivedMessages.clear();
    }

    public Optional<String> popLatestMessage() {
        if (receivedMessages.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(receivedMessages.remove(receivedMessages.size() - 1));
        }
    }

    public List<String> getReceivedMessages() {
        return receivedMessages;
    }

    public void assertDidNotReceiveAnyMessages() {
        assertThat(
                "test sender received messages when it should not have",
                receivedMessages, is(empty())
        );
    }

    public void assertReceivedMessages(int expectedCount) {
        assertThat(
                "test sender received incorrect number of messages: " + receivedMessages,
                receivedMessages, hasSize(expectedCount)
        );
    }

    public void assertLastReceivedMessageIs(String expected) {
        assertThat("test sender should have received '" + expected + "' but did not", !receivedMessages.isEmpty());
        assertThat("wrong last received message", popLatestMessage().orElseThrow(AssertionError::new), is(expected));
    }
}
