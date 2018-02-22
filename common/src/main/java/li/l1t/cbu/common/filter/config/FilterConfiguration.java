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

package li.l1t.cbu.common.filter.config;

import li.l1t.cbu.common.filter.action.FilterAction;
import li.l1t.cbu.common.filter.result.FilterOpinion;

/**
 * Stores the behaviour configuration of a {@link li.l1t.cbu.common.filter.Filter}.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-22
 */
public interface FilterConfiguration {
    /**
     * @return the opinion that is used if no criteria has a non-{@link FilterOpinion#NONE neutral} opinion
     */
    FilterOpinion getDefaultOpinion();

    /**
     * @return the permission that allows command senders to bypass this filter entirely
     */
    String getBypassPermission();

    /**
     * @return whether this filter processes command executions
     */
    boolean doesPreventExecution();

    /**
     * @return what this filter does when an execution is denied
     */
    FilterAction getExecutionAction();

    /**
     * <p><b>Note:</b> Even when this return true, tab-completion might not actually be blocked on some platforms
     * if required libraries are missing.</p>
     *
     * @return whether this filter processes tab-completion
     */
    boolean doesPreventTabComplete();

    /**
     * @return true if, when this filter matches a tab-completion, it clears all suggestions and not only infringing ones
     */
    boolean usesTabRestrictiveMode();

    /**
     * @return what this filter does when a tab-completion is denied
     */
    FilterAction getTabCompleteAction();
}
