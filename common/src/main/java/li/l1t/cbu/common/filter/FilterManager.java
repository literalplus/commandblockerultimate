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
import li.l1t.cbu.common.config.AliasResolver;
import li.l1t.cbu.common.filter.result.FilterOpinion;
import li.l1t.cbu.common.platform.SenderAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of multiple filters and dispatches calls to them. This implementation consults filters in registration
 * order and returns first non-{@link FilterOpinion#NONE neutral} opinion.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-24
 */
public class FilterManager {
    private final List<Filter> filters = new ArrayList<>();

    /**
     * Forwards a command execution to filters in order, until the first has a non-{@link FilterOpinion#NONE neutral}
     * opinion, or all filters have been invoked.
     *
     * @param sender      the sender who caused the execution
     * @param commandLine the command line that caused the execution
     * @return the first non-neutral opinion, or {@link FilterOpinion#NONE} if no filter had an opinion
     */
    public FilterOpinion processExecution(SenderAdapter sender, CommandLine commandLine) {
        for (Filter filter : filters) {
            FilterOpinion opinion = filter.processExecution(commandLine, sender);
            if (opinion != FilterOpinion.NONE) {
                return opinion;
            }
        }
        return FilterOpinion.NONE;
    }

    /**
     * Removes all filters, leaving this manager in its initial state.
     */
    public void clearFilters() {
        filters.clear();
    }

    /**
     * Adds a filter to the order-sensitive list of filters this manager calls.
     * <p><b>Note:</b> This does not resolve aliases for that filter!</p>
     *
     * @param filter the filter to add, may not be null
     * @see #resolveAliases(AliasResolver)
     */
    public void addFilter(Filter filter) {
        Preconditions.checkNotNull(filter, "filter");
        filters.add(filter);
    }

    /**
     * Removes a filter from this manager. Aliases are removed as well since those are kept by the filter itself.
     *
     * @param filter the filter to remove
     * @return whether such a filter was present
     */
    public boolean removeFilter(Filter filter) {
        return filters.remove(filter);
    }

    public List<Filter> getFilters() {
        return filters;
    }

    /**
     * Asks all managed filters to resolve their aliases using given resolver.
     * <p><b>Note:</b> This only affects current filters and has no effect on future registrations.</p>
     *
     * @param resolver the resolver to use, may not be null
     */
    public void resolveAliases(AliasResolver resolver) {
        Preconditions.checkNotNull(resolver, "resolver");
        filters.forEach(f -> f.resolveAliases(resolver));
    }
}
