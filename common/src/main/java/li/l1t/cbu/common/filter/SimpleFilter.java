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

import com.google.common.base.Preconditions;
import li.l1t.cbu.common.filter.config.FilterConfiguration;
import li.l1t.cbu.common.filter.criterion.CriteriaList;
import li.l1t.cbu.common.filter.result.FilterOpinion;
import li.l1t.cbu.common.platform.SenderAdapter;

/**
 * A command filter with individual configuration that has a defined set of criteria.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-22
 */
public class SimpleFilter extends CriteriaList implements Filter {
    private final FilterConfiguration configuration;

    public SimpleFilter(FilterConfiguration configuration) {
        super(Preconditions.checkNotNull(configuration, "configuration").getDefaultOpinion());
        this.configuration = configuration;
    }

    @Override
    public FilterConfiguration config() {
        return configuration;
    }

    @Override
    public FilterOpinion processExecution(CommandLine commandLine, SenderAdapter sender) {
        Preconditions.checkNotNull(sender, "sender");
        Preconditions.checkNotNull(commandLine, "commandLine");
        if (!config().doesPreventExecution()) {
            return FilterOpinion.NONE;
        }
        FilterOpinion result = process(commandLine);
        if (sender.hasPermission(config().getBypassPermission())) {
            config().getExecutionAction().onBypass(commandLine, sender);
            return FilterOpinion.NONE;
        } else if (result == FilterOpinion.DENY) {
            configuration.getExecutionAction().onDenial(commandLine, sender);
        }
        return result;
    }
}
