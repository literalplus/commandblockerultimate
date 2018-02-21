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

package li.l1t.cbu.common.filter.result;

import li.l1t.cbu.common.filter.CommandLine;
import li.l1t.cbu.common.filter.action.FilterAction;
import li.l1t.cbu.common.platform.SenderAdapter;

import javax.annotation.Nullable;

/**
 * Represents the result of a filter processing a command line.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-21
 */
public interface FilterResult {
    CommandLine getCommandLine();

    FilterOpinion getOpinion();

    SenderAdapter getSender();

    /**
     * @return the execution or completion action, depending on the query type, of the filter whose opinion is
     * reflected in this result, or null if no filter had a non-{@link FilterOpinion#NONE neutral} opinion
     */
    @Nullable
    FilterAction getAction();
}
