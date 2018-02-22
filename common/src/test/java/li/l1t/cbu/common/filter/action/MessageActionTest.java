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

package li.l1t.cbu.common.filter.action;

import li.l1t.cbu.common.filter.CommandLine;
import li.l1t.cbu.common.filter.SimpleCommandLine;
import li.l1t.cbu.common.filter.result.FilterOpinion;
import li.l1t.cbu.common.filter.result.FilterResult;
import li.l1t.cbu.common.filter.result.ImmutableFilterResult;
import li.l1t.cbu.common.platform.SenderAdapter;
import li.l1t.cbu.common.platform.TestSender;
import org.junit.jupiter.api.Test;


class MessageActionTest {
    private static final String RAW_MESSAGE = "&chenlo <name>, you failed with <command>.";
    private static final String COMMAND_LINE = "/hey:some weird command";

    @Test
    void onDenial__message_by_default() {
        // given
        MessageAction action = new MessageAction();
        TestSender sender = new TestSender();
        ImmutableFilterResult result = givenAFilterResult(sender);
        // when
        action.onDenial(result);
        // then
        sender.assertReceivedMessages(1);
    }

    private ImmutableFilterResult givenAFilterResult(TestSender sender) {
        return new ImmutableFilterResult(
                givenACommandLine(), FilterOpinion.NONE, sender, null
        );
    }

    private SimpleCommandLine givenACommandLine() {
        return new SimpleCommandLine(COMMAND_LINE);
    }

    @Test
    void onDenial__custom_message() {
        // given
        MessageAction action = new MessageAction();
        action.setErrorMessage(RAW_MESSAGE);
        TestSender sender = new TestSender();
        ImmutableFilterResult result = givenAFilterResult(sender);
        // when
        action.onDenial(result);
        // then
        sender.assertReceivedMessages(1);
        sender.assertLastReceivedMessageIs(computeExpectedMessage(result));
    }

    private String computeExpectedMessage(FilterResult result) {
        return computeExpectedMessage(result.getCommandLine(), result.getSender());
    }

    private String computeExpectedMessage(CommandLine commandLine, SenderAdapter sender) {
        return "§chenlo " + sender.getName() + ", you failed with " + commandLine.getRawCommand() + ".";
    }

    @Test
    void onDenial__no_message() {
        // given
        MessageAction action = new MessageAction();
        action.setShowDenialMessage(false);
        TestSender sender = new TestSender();
        ImmutableFilterResult result = givenAFilterResult(sender);
        // when
        action.onDenial(result);
        // then
        sender.assertDidNotReceiveAnyMessages();
    }

    @Test
    void onBypass__custom_message() {
        // given
        MessageAction action = new MessageAction();
        action.setShowBypassMessage(true);
        action.setBypassMessage(RAW_MESSAGE);
        TestSender sender = new TestSender();
        SimpleCommandLine commandLine = givenACommandLine();
        // when
        action.onBypass(commandLine, sender);
        // then
        sender.assertReceivedMessages(1);
        sender.assertLastReceivedMessageIs(computeExpectedMessage(commandLine, sender));
    }

    @Test
    void onBypass__no_message_by_default() {
        // given
        MessageAction action = new MessageAction();
        TestSender sender = new TestSender();
        // when
        action.onBypass(givenACommandLine(), sender);
        // then
        sender.assertDidNotReceiveAnyMessages();
    }
}