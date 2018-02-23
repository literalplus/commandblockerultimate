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

import com.google.common.base.Preconditions;
import li.l1t.cbu.common.filter.action.FilterAction;
import li.l1t.cbu.common.filter.result.FilterOpinion;

import javax.annotation.Nonnull;

/**
 * A mutable implementation of a filter configuration which provides builder-like setter methods and
 * includes defaults for all values.
 * Note however that the defaults are not considered part of the API and should not be relied upon.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-23
 */
public class MutableFilterConfiguration implements FilterConfiguration {
    private FilterOpinion defaultOpinion = FilterOpinion.NONE;
    private String bypassPermission = "cmdblock.bypass";
    private boolean preventExecution = true;
    private FilterAction executionAction;
    private boolean preventTabComplete = true;
    private boolean tabRestrictiveMode = false;
    private FilterAction tabCompleteAction;

    public MutableFilterConfiguration(FilterAction executionAction, FilterAction tabCompleteAction) {
        this.executionAction = Preconditions.checkNotNull(executionAction, "executionAction");
        this.tabCompleteAction = Preconditions.checkNotNull(tabCompleteAction, "tabCompleteAction");
    }

    @Nonnull
    @Override
    public FilterOpinion getDefaultOpinion() {
        return defaultOpinion;
    }

    @Nonnull
    @Override
    public String getBypassPermission() {
        return bypassPermission;
    }

    @Override
    public boolean doesPreventExecution() {
        return preventExecution;
    }

    @Override
    public FilterAction getExecutionAction() {
        return executionAction;
    }

    @Override
    public boolean doesPreventTabComplete() {
        return preventTabComplete;
    }

    @Override
    public boolean usesTabRestrictiveMode() {
        return tabRestrictiveMode;
    }

    @Override
    public FilterAction getTabCompleteAction() {
        return tabCompleteAction;
    }

    public MutableFilterConfiguration defaultOpinion(FilterOpinion defaultOpinion) {
        Preconditions.checkNotNull(defaultOpinion, "defaultOpinion");
        this.defaultOpinion = defaultOpinion;
        return this;
    }

    public MutableFilterConfiguration bypassPermission(String bypassPermission) {
        Preconditions.checkNotNull(bypassPermission, "bypassPermission");
        this.bypassPermission = bypassPermission;
        return this;
    }

    public MutableFilterConfiguration preventExecution(boolean preventExecution) {
        this.preventExecution = preventExecution;
        return this;
    }

    public MutableFilterConfiguration executionAction(FilterAction executionAction) {
        Preconditions.checkNotNull(executionAction, "executionAction");
        this.executionAction = executionAction;
        return this;
    }

    public MutableFilterConfiguration preventTabComplete(boolean preventTabComplete) {
        this.preventTabComplete = preventTabComplete;
        return this;
    }

    public MutableFilterConfiguration tabRestrictiveMode(boolean tabRestrictiveMode) {
        this.tabRestrictiveMode = tabRestrictiveMode;
        return this;
    }

    public MutableFilterConfiguration tabCompleteAction(FilterAction tabCompleteAction) {
        Preconditions.checkNotNull(tabCompleteAction, "tabCompleteAction");
        this.tabCompleteAction = tabCompleteAction;
        return this;
    }
}
