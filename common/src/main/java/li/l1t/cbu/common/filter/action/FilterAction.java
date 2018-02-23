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
import li.l1t.cbu.common.platform.SenderAdapter;

/**
 * An action that is executed when a command execution or tab completion is denied by a filter.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-21
 */
public interface FilterAction {
    /**
     * Notifies this action that an execution or completion was denied.
     *
     * @param commandLine the command line that caused the denial
     * @param sender      the sender that that caused the execution
     */
    void onDenial(CommandLine commandLine, SenderAdapter sender);

    /**
     * Notifies this action that an execution or completion was denied, but the command sender has the necessary
     * permission to bypass the result.
     *
     * @param commandLine the command line that caused the denial
     * @param sender      the sender that has the required permission
     */
    void onBypass(CommandLine commandLine, SenderAdapter sender);
}
