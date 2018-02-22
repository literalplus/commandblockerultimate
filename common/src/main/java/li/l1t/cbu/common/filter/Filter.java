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

import li.l1t.cbu.common.filter.config.FilterConfiguration;
import li.l1t.cbu.common.filter.criterion.CommandCriterion;
import li.l1t.cbu.common.filter.criterion.CompoundCriterion;
import li.l1t.cbu.common.filter.result.FilterOpinion;
import li.l1t.cbu.common.platform.SenderAdapter;

/**
 * A named command filter with individual configuration.
 * A command filter processes tab-completions as well as command executions, checks them based on a collection of
 * pre-defined {@link CommandCriterion criteria} and returns the aggregated {@link FilterOpinion opinion}.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-22
 */
public interface Filter extends CompoundCriterion {
    /**
     * @return this filter's configuration which need not be immutable
     */
    FilterConfiguration config();

    /**
     * Processes an execution, respecting this filter's configuration.
     *
     * @param commandLine the command line to process
     * @param sender      the sender that issued given command line
     * @return the collective opinion of this filter's criteria regarding given execution, never null
     */
    FilterOpinion processExecution(SenderAdapter sender, CommandLine commandLine);


}
