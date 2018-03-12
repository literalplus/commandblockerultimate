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
import li.l1t.cbu.common.filter.action.SpyAction;
import li.l1t.cbu.common.filter.config.FilterConfiguration;
import li.l1t.cbu.common.filter.config.MutableFilterConfiguration;
import li.l1t.cbu.common.filter.criterion.CommandCriterion;
import li.l1t.cbu.common.filter.dto.CommandLine;
import li.l1t.cbu.common.filter.dto.Completable;
import li.l1t.cbu.common.filter.result.FilterOpinion;
import li.l1t.cbu.common.platform.SenderAdapter;

import javax.annotation.Nonnull;

/**
 * A mock implementation of a filter that counts how often some methods are called and always returns its default opinion.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-28
 */
public class FakeFilter implements Filter {
    private MutableFilterConfiguration config = new MutableFilterConfiguration(new SpyAction(), new SpyAction());
    private FilterOpinion defaultOpinion = FilterOpinion.NONE;
    private int executionCount = 0;
    private int tabCount = 0;
    private int aliasResolutionCount = 0;

    @Nonnull
    @Override
    public FilterConfiguration config() {
        return config;
    }

    @Nonnull
    @Override
    public FilterOpinion processExecution(CommandLine commandLine, SenderAdapter sender) {
        executionCount++;
        return process(commandLine);
    }

    @Nonnull
    @Override
    public FilterOpinion processTabComplete(Completable request) {
        tabCount++;
        return request.findMergedCommand()
                .map(this::process)
                .orElse(FilterOpinion.NONE);
    }

    public int getTabCount() {
        return tabCount;
    }

    @Override
    public void setDefaultOpinion(FilterOpinion defaultOpinion) {
        this.defaultOpinion = Preconditions.checkNotNull(defaultOpinion, "defaultOpinion");
    }

    @Override
    public FilterOpinion getDefaultOpinion() {
        return defaultOpinion;
    }

    public int getExecutionCount() {
        return executionCount;
    }

    @Override
    public void addCriterion(CommandCriterion criterion) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "fake filter with " + getDefaultOpinion();
    }

    @Nonnull
    @Override
    public FilterOpinion process(CommandLine commandLine) {
        return getDefaultOpinion();
    }

    @Override
    public void resolveAliases(AliasResolver resolver) {
        aliasResolutionCount++;
    }

    public int getAliasResolutionCount() {
        return aliasResolutionCount;
    }
}
