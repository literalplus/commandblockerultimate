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

import li.l1t.cbu.common.filter.FilterResult;

/**
 * A filter action that sends chat messages on denial and bypass.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-21
 */
public class MessageAction implements FilterAction {
    private boolean showDenialMessage = true;
    private boolean showBypassMessage = false;
    private String errorMessage = "&cYou are not permitted to execute this command.";
    private String bypassMessage = "&c[CBU] /<command> is blocked. Executing anyways since you have permission.";

    @Override
    public void onDenial(FilterResult result) {
        if (showDenialMessage) {
            parseAndSendMessage(errorMessage, result);
        }
    }

    private void parseAndSendMessage(String rawMessage, FilterResult result) {
        String parsedMessage = rawMessage
                .replace("<command>", result.getCommandLine().getRawCommand());
        result.getSender().sendMessage(parsedMessage);
    }

    @Override
    public void onBypass(FilterResult result) {
        if (showBypassMessage) {
            parseAndSendMessage(bypassMessage, result);
        }
    }
}
