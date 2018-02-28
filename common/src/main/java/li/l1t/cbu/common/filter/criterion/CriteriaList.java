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

import com.google.common.base.Preconditions;
import li.l1t.cbu.common.config.AliasResolver;
import li.l1t.cbu.common.filter.dto.CommandLine;
import li.l1t.cbu.common.filter.result.FilterOpinion;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A compound criterion that consults a list of other criteria, in registration order, to form a collective opinion.
 * The first criterion that returns a non-{@link FilterOpinion#NONE neutral} opinion defines the collective opinion.
 * If no filter has a non-neutral opinion, the {@link #setDefaultOpinion(FilterOpinion) default opinion} is used.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-08-19
 */
public class CriteriaList implements CompoundCriterion {
    private FilterOpinion defaultOpinion;
    private List<CommandCriterion> criteria = new ArrayList<>();

    // TODO: is there a cleaner way to do this in SimpleFilter than ignoring the field here?
    public CriteriaList(FilterOpinion defaultOpinion) {
        // can't call setter because of SimpleFilter override
        this.defaultOpinion = Preconditions.checkNotNull(defaultOpinion, "defaultOpinion");
    }

    @Override
    public void setDefaultOpinion(FilterOpinion defaultOpinion) {
        Preconditions.checkNotNull(defaultOpinion, "defaultOpinion");
        this.defaultOpinion = defaultOpinion;
    }

    @Override
    public FilterOpinion getDefaultOpinion() {
        return defaultOpinion;
    }

    @Override
    public void addCriterion(CommandCriterion criterion) {
        Preconditions.checkNotNull(criterion, "criterion");
        criteria.add(criterion);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "filter list over " + criteria.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @see CriteriaList for details on the decision-making process
     */
    @Nonnull
    @Override
    public FilterOpinion process(CommandLine commandLine) {
        for (CommandCriterion criterion : criteria) {
            FilterOpinion opinion = criterion.process(commandLine);
            if (opinion != FilterOpinion.NONE) {
                return opinion;
            }
        }
        return getDefaultOpinion();
    }

    @Override
    public void resolveAliases(AliasResolver resolver) {
        criteria.forEach(filter -> filter.resolveAliases(resolver));
    }
}
