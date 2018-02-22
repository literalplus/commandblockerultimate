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

package li.l1t.cbu.common.filter.criterion;

import li.l1t.cbu.common.config.AliasResolver;
import li.l1t.cbu.common.filter.CommandLine;
import li.l1t.cbu.common.filter.result.FilterOpinion;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Matches command lines only by their base command. Is aware of plugin prefixes, so blocking "me" covers "me" as well
 * as "minecraft:me" etc.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-08-19
 */
public class SetCriterion implements CommandCriterion {
    private final Set<String> rawCommandNames;
    private final Set<String> resolvedCommandNames = new HashSet<>();
    private FilterOpinion matchOpinion;
    private boolean resolveAliases;

    public SetCriterion(Set<String> rawCommandNames) {
        this(rawCommandNames, FilterOpinion.DENY, true);
    }

    public SetCriterion(Set<String> rawCommandNames, FilterOpinion matchOpinion, boolean resolveAliases) {
        this.rawCommandNames = rawCommandNames;
        this.matchOpinion = matchOpinion;
        this.resolveAliases = resolveAliases;
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
        if (!resolveAliases) {
            return;
        }
        Set<String> aliasesExcludingNames = rawCommandNames.stream()
                .map(resolver::resolve)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        resolvedCommandNames.addAll(aliasesExcludingNames);
    }

    public void setResolveAliases(boolean resolveAliases) {
        this.resolveAliases = resolveAliases;
    }
}
