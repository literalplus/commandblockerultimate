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

import li.l1t.cbu.common.filter.result.FilterOpinion;

/**
 * A command criterion that consults other criteria to form a collective opinion.
 * If no filter has a non-neutral opinion, the {@link #setDefaultOpinion(FilterOpinion) default opinion} is used.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-22
 */
public interface CompoundCriterion extends CommandCriterion {
    void setDefaultOpinion(FilterOpinion defaultOpinion);

    void addCriterion(CommandCriterion criterion);
}
