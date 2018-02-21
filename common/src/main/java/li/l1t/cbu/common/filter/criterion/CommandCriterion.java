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
import li.l1t.cbu.common.filter.FilterOpinion;

import javax.annotation.Nonnull;

/**
 * Inspects command lines and blocks some, based on pre-defined criteria.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-08-19
 */
public interface CommandCriterion {
    /**
     * @return a concise description of this filter, never null
     */
    @Nonnull
    String getDescription();

    /**
     * Checks the opinion of this filter regarding given command line.
     *
     * @param commandLine the command line to check
     * @return what this filter thinks about that line, never null
     */
    @Nonnull
    FilterOpinion checkExecution(CommandLine commandLine);

    /**
     * Resolves aliases of the filtered commands to be blocked too, if supported by this filter.
     *
     * @param resolver the resolver providing alias information
     */
    void resolveAliases(AliasResolver resolver);
}
