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

package li.l1t.cbu.common.filter;

import li.l1t.cbu.common.filter.action.FilterAction;
import li.l1t.cbu.common.platform.SenderAdapter;

import javax.annotation.Nullable;

/**
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-21
 */
public class ImmutableFilterResult implements FilterResult {
    private final CommandLine commandLine;
    private final FilterOpinion opinion;
    private final SenderAdapter sender;
    private final FilterAction executionAction;
    private final FilterAction tabCompleteAction;

    //TODO: constructor with Filter argument for actions

    public ImmutableFilterResult(CommandLine commandLine, FilterOpinion opinion, SenderAdapter sender,
                                 FilterAction executionAction, FilterAction tabCompleteAction) {
        this.commandLine = commandLine;
        this.opinion = opinion;
        this.sender = sender;
        this.executionAction = executionAction;
        this.tabCompleteAction = tabCompleteAction;
    }

    @Override
    public CommandLine getCommandLine() {
        return commandLine;
    }

    @Override
    public FilterOpinion getOpinion() {
        return opinion;
    }

    @Override
    public SenderAdapter getSender() {
        return sender;
    }

    @Nullable
    @Override
    public FilterAction getExecutionAction() {
        return executionAction;
    }

    @Nullable
    @Override
    public FilterAction getTabCompleteAction() {
        return tabCompleteAction;
    }
}
