/*
 * Command Blocker Ultimate
 * Copyright (C) 2014-2017 Philipp Nowak / Literallie (xxyy.github.io)
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
import li.l1t.cbu.common.config.AliasResolver;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A command filter that consults a list of other filters to find its opinion.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-08-19
 */
public class FilterList implements CommandFilter {
    private FilterOpinion defaultOpinion;
    private List<CommandFilter> filters = new ArrayList<>();

    public FilterList(FilterOpinion defaultOpinion) {
        setDefaultOpinion(defaultOpinion);
    }

    public void setDefaultOpinion(FilterOpinion defaultOpinion) {
        Preconditions.checkNotNull(defaultOpinion, "defaultOpinion");
        this.defaultOpinion = defaultOpinion;
    }

    public void addFilter(CommandFilter filter) {
        Preconditions.checkNotNull(filter, "filter");
        filters.add(filter);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "filter list over " + filters.toString();
    }

    @Nonnull
    @Override
    public FilterOpinion checkExecution(CommandLine commandLine) {
        FilterOpinion collectiveOpinion = filters.stream()
                .map(filter -> filter.checkExecution(commandLine))
                .reduce(this::combineOpinions)
                .orElse(FilterOpinion.NONE);
        if (collectiveOpinion == FilterOpinion.NONE) {
            collectiveOpinion = defaultOpinion;
        }
        return collectiveOpinion;
    }

    private FilterOpinion combineOpinions(FilterOpinion first, FilterOpinion second) {
        if (first != FilterOpinion.NONE) {
            return first;
        } else {
            return second;
        }
    }

    @Override
    public void resolveAliases(AliasResolver resolver) {
        filters.forEach(filter -> filter.resolveAliases(resolver));
    }
}
