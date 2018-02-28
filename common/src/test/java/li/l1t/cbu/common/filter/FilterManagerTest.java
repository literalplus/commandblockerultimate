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

import li.l1t.cbu.common.config.FakeResolver;
import li.l1t.cbu.common.filter.dto.SimpleCommandLine;
import li.l1t.cbu.common.filter.dto.SimpleTabCompleteRequest;
import li.l1t.cbu.common.filter.result.FilterOpinion;
import li.l1t.cbu.common.platform.FakeSender;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class FilterManagerTest {
    @Test
    void resolveAliases__forwarding_single() {
        // given
        FilterManager manager = new FilterManager();
        FakeFilter filter = givenAFilterIsAddedTo(manager);
        // when
        whenAliasesAreResolved(manager);
        //then
        assertThat(filter.getAliasResolutionCount(), is(1));
    }

    private FakeFilter givenAFilterIsAddedTo(FilterManager manager) {
        FakeFilter filter = new FakeFilter();
        manager.addFilter(filter);
        assumeTrue(manager.getFilters().contains(filter));
        return filter;
    }

    private void whenAliasesAreResolved(FilterManager manager) {
        manager.resolveAliases(new FakeResolver(new HashMap<>()));
    }

    @Test
    void resolveAliases__forwarding_multiple() {
        // given
        FilterManager manager = new FilterManager();
        FakeFilter filter1 = givenAFilterIsAddedTo(manager);
        FakeFilter filter2 = givenAFilterIsAddedTo(manager);
        // when
        whenAliasesAreResolved(manager);
        //then
        assertThat(filter1.getAliasResolutionCount(), is(1));
        assertThat(filter2.getAliasResolutionCount(), is(1));
    }

    @Test
    void processExecution__none() {
        // given
        FilterManager manager = new FilterManager();
        // when
        FilterOpinion opinion = whenAnyExecutionIsProcessed(manager);
        // then
        assertThat(opinion, is(FilterOpinion.NONE));
    }

    private FilterOpinion whenAnyExecutionIsProcessed(FilterManager manager) {
        return manager.processExecution(new FakeSender(), new SimpleCommandLine("/whatever"));
    }

    @Test
    void processExecution__single() {
        // given
        FilterManager manager = new FilterManager();
        assumeTrue(whenAnyExecutionIsProcessed(manager) == FilterOpinion.NONE);
        FakeFilter filter = givenAFilterIsAddedTo(manager, FilterOpinion.ALLOW);
        // when
        FilterOpinion opinion = whenAnyExecutionIsProcessed(manager);
        // then
        assertThat(opinion, is(FilterOpinion.ALLOW));
        assertThat(filter.getExecutionCount(), is(1));
    }

    private FakeFilter givenAFilterIsAddedTo(FilterManager manager, FilterOpinion opinion) {
        FakeFilter filter = givenAFilterIsAddedTo(manager);
        filter.setDefaultOpinion(opinion);
        return filter;
    }

    @Test
    void processExecution__removal() {
        // given
        FilterManager manager = new FilterManager();
        FakeFilter filter = givenAFilterIsAddedTo(manager, FilterOpinion.ALLOW);
        assumeTrue(whenAnyExecutionIsProcessed(manager) == FilterOpinion.ALLOW);
        // when
        boolean removed = manager.removeFilter(filter);
        // then
        assertThat(removed, is(true));
        assertThat("further removal attempts return false", manager.removeFilter(filter), is(false));
        assertThat(whenAnyExecutionIsProcessed(manager), is(FilterOpinion.NONE));
        assertThat(filter.getExecutionCount(), is(1));
    }

    @Test
    void processExecution__clear() {
        // given
        FilterManager manager = new FilterManager();
        FakeFilter filter = givenAFilterIsAddedTo(manager, FilterOpinion.ALLOW);
        assumeTrue(whenAnyExecutionIsProcessed(manager) == FilterOpinion.ALLOW);
        // when
        manager.clearFilters();
        // then
        assertThat(whenAnyExecutionIsProcessed(manager), is(FilterOpinion.NONE));
        assertThat(filter.getExecutionCount(), is(1));
    }

    @Test
    void processExecution__multiple_later_ignored() {
        // given
        FilterManager manager = new FilterManager();
        FakeFilter filter1 = givenAFilterIsAddedTo(manager, FilterOpinion.ALLOW);
        FakeFilter filter2 = givenAFilterIsAddedTo(manager, FilterOpinion.DENY);
        // when
        FilterOpinion opinion = whenAnyExecutionIsProcessed(manager);
        // then
        assertThat(opinion, is(FilterOpinion.ALLOW));
        assertThat(filter1.getExecutionCount(), is(1));
        assertThat(filter2.getExecutionCount(), is(0));
    }

    @Test
    void processExecution__multiple_nones_ignored() {
        // given
        FilterManager manager = new FilterManager();
        FakeFilter filter1 = givenAFilterIsAddedTo(manager, FilterOpinion.NONE);
        FakeFilter filter2 = givenAFilterIsAddedTo(manager, FilterOpinion.DENY);
        // when
        FilterOpinion opinion = whenAnyExecutionIsProcessed(manager);
        // then
        assertThat(opinion, is(FilterOpinion.DENY));
        assertThat(filter1.getExecutionCount(), is(1));
        assertThat(filter2.getExecutionCount(), is(1));
    }

    @Test
    void processTabRequest__single() {
        // given
        FilterManager manager = new FilterManager();
        assumeTrue(whenAnyTabRequestIsProcessed(manager) == FilterOpinion.NONE);
        FakeFilter filter = givenAFilterIsAddedTo(manager, FilterOpinion.ALLOW);
        // when
        FilterOpinion opinion = whenAnyTabRequestIsProcessed(manager);
        // then
        assertThat(opinion, is(FilterOpinion.ALLOW));
        assertThat(filter.getTabRequestCount(), is(1));
    }

    private FilterOpinion whenAnyTabRequestIsProcessed(FilterManager manager) {
        return manager.processTabRequest(new SimpleTabCompleteRequest(new FakeSender(), "/whatevs", false));
    }

    @Test
    void processTabRequest__multiple_combined() {
        // given
        FilterManager manager = new FilterManager();
        FakeFilter filter1 = givenAFilterIsAddedTo(manager, FilterOpinion.NONE);
        FakeFilter filter2 = givenAFilterIsAddedTo(manager, FilterOpinion.ALLOW);
        FakeFilter filter3 = givenAFilterIsAddedTo(manager, FilterOpinion.DENY);
        // when
        FilterOpinion opinion = whenAnyTabRequestIsProcessed(manager);
        // then
        assertThat(opinion, is(FilterOpinion.ALLOW));
        assertThat(filter1.getTabRequestCount(), is(1));
        assertThat(filter2.getTabRequestCount(), is(1));
        assertThat(filter3.getTabRequestCount(), is(0));
    }
}