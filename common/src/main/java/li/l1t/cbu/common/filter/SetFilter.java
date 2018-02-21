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

import li.l1t.cbu.common.config.AliasResolver;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A command filter that keeps a set of root commands that are denied only by their name.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-08-19
 */
public class SetFilter implements CommandFilter {
    private final Set<String> rawCommandNames;
    private final Set<String> resolvedCommandNames = new HashSet<>();
    private final FilterOpinion matchOpinion;

    public SetFilter(Set<String> rawCommandNames) {
        this(rawCommandNames, FilterOpinion.DENY);
    }

    public SetFilter(Set<String> rawCommandNames, FilterOpinion matchOpinion) {
        this.rawCommandNames = rawCommandNames;
        this.matchOpinion = matchOpinion;
        this.resolvedCommandNames.addAll(this.rawCommandNames);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "set filter on " + rawCommandNames.toString();
    }

    @Nonnull
    @Override
    public FilterOpinion checkExecution(CommandLine commandLine) {
        boolean rootCommandBlocked = resolvedCommandNames.contains(commandLine.getRootCommand());
        // v if minecraft:me is blocked, the root command check alone wouldn't find it (blocked "minecraft:me" != root "me")
        boolean rawCommandBlocked = resolvedCommandNames.contains(commandLine.getRawCommand());
        if (rootCommandBlocked || rawCommandBlocked) {
            return matchOpinion;
        } else {
            return FilterOpinion.NONE;
        }
    }

    @Override
    public void resolveAliases(AliasResolver resolver) {
        resolvedCommandNames.clear();
        resolvedCommandNames.addAll(rawCommandNames);
        Set<String> aliasesExcludingNames = rawCommandNames.stream()
                .map(resolver::resolve)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        resolvedCommandNames.addAll(aliasesExcludingNames);
    }

    public Set<String> getRawCommandNames() {
        return rawCommandNames;
    }
}
