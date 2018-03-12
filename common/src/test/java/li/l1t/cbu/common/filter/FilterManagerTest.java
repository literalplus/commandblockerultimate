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
import li.l1t.cbu.common.filter.dto.CommandLine;
import li.l1t.cbu.common.filter.dto.SimpleCommandLine;
import li.l1t.cbu.common.filter.dto.SimpleTabCompleteRequest;
import li.l1t.cbu.common.filter.result.FilterOpinion;
import li.l1t.cbu.common.platform.FakeSender;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class FilterManagerTest {
    private static final String CURSOR_COMMAND = "/ignoredroot";

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
        assertThat(filter.getTabCount(), is(1));
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
        assertThat(filter1.getTabCount(), is(1));
        assertThat(filter2.getTabCount(), is(1));
        assertThat(filter3.getTabCount(), is(0));
    }

    @Test
    void processTabSuggestions__single() {
        // given
        FilterManager manager = new FilterManager();
        givenAllSuggestionsAreKept(manager);
        FakeFilter filter = givenAFilterIsAddedTo(manager, FilterOpinion.DENY);
        String[] suggestions = {"/topkek", "/lol"};
        // when
        List<String> kept = whenSuggestionsAreProcessed(manager, suggestions);
        // then
        assertThat(kept, is(empty()));
        assertThat(filter.getTabCount(), is(2));
    }

    private void givenAllSuggestionsAreKept(FilterManager manager) {
        String[] rawSuggestions = new String[]{"/whatevs", "/something"};
        List<String> kept = whenSuggestionsAreProcessed(manager, rawSuggestions);
        Assumptions.assumeTrue(Arrays.asList(rawSuggestions).equals(kept));
    }

    private List<String> whenSuggestionsAreProcessed(FilterManager manager, String... rawSuggestions) {
        return manager.processTabSuggestions(new FakeSender(), Arrays.asList(rawSuggestions));
    }

    @Test
    void processTabSuggestions__multiple_combined() {
        // given
        FilterManager manager = new FilterManager();
        FakeFilter filter1 = givenAFilterIsAddedTo(manager, FilterOpinion.NONE);
        FakeFilter filter2 = givenAFilterIsAddedTo(manager, FilterOpinion.ALLOW);
        FakeFilter filter3 = givenAFilterIsAddedTo(manager, FilterOpinion.DENY);
        String[] suggestions = {"/henlo", "/wowowow"};
        // when
        List<String> kept = whenSuggestionsAreProcessed(manager, suggestions);
        // then
        assertThat(kept, contains(suggestions));
        assertThat(filter1.getTabCount(), is(2));
        assertThat(filter2.getTabCount(), is(2));
        assertThat(filter3.getTabCount(), is(0));
    }

    @Test
    void processTabSuggestions__multiple_complex_filters() {
        // given
        FilterManager manager = new FilterManager();
        manager.addFilter(givenAPredicateFilter(cl -> cl.getRootCommand().equals("henlo")));
        String[] suggestions = {"/henlo aa", "/wowowow", "this is not a command"};
        // when
        List<String> kept = whenSuggestionsAreProcessed(manager, suggestions);
        // then
        assertThat(kept, contains("/wowowow", "this is not a command"));
    }

    private FakeFilter givenAPredicateFilter(Predicate<CommandLine> predicate) {
        return new FakeFilter() {
            @Nonnull
            @Override
            public FilterOpinion process(CommandLine commandLine) {
                return predicate.test(commandLine) ? FilterOpinion.DENY : FilterOpinion.NONE;
            }
        };
    }

    @Test
    void processTabComplete__single() {
        // given
        FilterManager manager = new FilterManager();
        givenAllCompletionsAreKept(manager);
        FakeFilter filter = givenTheCursorCommandIsNotBlocked(manager);
        String[] suggestions = {"/topkek", "/lol"};
        // when
        List<String> kept = whenCompletionsAreProcessed(manager, CURSOR_COMMAND, suggestions);
        // then
        assertThat(kept, is(empty()));
        assertThat(filter.getTabCount(), is(2));
    }

    private void givenAllCompletionsAreKept(FilterManager manager) {
        String[] rawSuggestions = new String[]{"/whatevs", "/something"};
        List<String> kept = whenCompletionsAreProcessed(manager, "/bb", rawSuggestions);
        Assumptions.assumeTrue(Arrays.asList(rawSuggestions).equals(kept));
    }

    private FakeFilter givenTheCursorCommandIsNotBlocked(FilterManager manager) {
        FakeFilter filter = givenAPredicateFilter(cl -> !cl.getRootCommand().equals(CURSOR_COMMAND.substring(1)));
        manager.addFilter(filter);
        return filter;
    }

    private List<String> whenCompletionsAreProcessed(FilterManager manager, String cursor, String... rawSuggestions) {
        return manager.processTabCompletion(
                new SimpleTabCompleteRequest(new FakeSender(), cursor, false),
                Arrays.asList(rawSuggestions)
        );
    }

    @Test
    void processTabComplete__cursor_blocked_suggestions_allowed() {
        // given
        FilterManager manager = new FilterManager();
        givenAllCompletionsAreKept(manager);
        FakeFilter filter = givenOnlyTheCursorCommandIsBlocked(manager);
        String[] suggestions = {"/topkek", "/lol"};
        // when
        List<String> kept = whenCompletionsAreProcessed(manager, CURSOR_COMMAND, suggestions);
        // then
        assertThat(kept, contains(suggestions));
        assertThat(filter.getTabCount(), is(2));
    }

    private FakeFilter givenOnlyTheCursorCommandIsBlocked(FilterManager manager) {
        FakeFilter filter = givenAPredicateFilter(cl -> cl.getRootCommand().equals(CURSOR_COMMAND.substring(1)));
        manager.addFilter(filter);
        return filter;
    }

    @Test
    void processTabCompletions__multiple_combined() {
        // given
        FilterManager manager = new FilterManager();
        FakeFilter filter1 = givenAFilterIsAddedTo(manager, FilterOpinion.NONE);
        FakeFilter filter2 = givenAFilterIsAddedTo(manager, FilterOpinion.ALLOW);
        FakeFilter filter3 = givenAFilterIsAddedTo(manager, FilterOpinion.DENY);
        String[] suggestions = {"/henlo", "/wowowow"};
        // when
        List<String> kept = whenCompletionsAreProcessed(manager, "/aa", suggestions);
        // then
        assertThat(kept, contains(suggestions));
        assertThat(filter1.getTabCount(), is(2));
        assertThat(filter2.getTabCount(), is(2));
        assertThat(filter3.getTabCount(), is(0));
    }

    @Test
    void processTabCompletions__merging_allowed_root() {
        // given
        FilterManager manager = new FilterManager();
        manager.addFilter(givenAPredicateFilter(cl -> cl.getRootCommand().equals("henlo")));
        String[] suggestions = {"/henlo", "/allowed"};
        // when
        List<String> kept = whenCompletionsAreProcessed(manager, "/aa", suggestions);
        // then
        assertThat(kept, contains("/allowed"));
    }

    @Test
    void processTabCompletions__merging_blocked_root_replacing() {
        // given
        FilterManager manager = new FilterManager();
        manager.addFilter(givenAPredicateFilter(cl -> cl.getRootCommand().equals("aa")));
        String[] suggestions = {"/henlo", "/allowed"};
        // when
        List<String> kept = whenCompletionsAreProcessed(manager, "/aa", suggestions);
        // then
        assertThat(kept, contains(suggestions));
    }

    @Test
    void processTabCompletions__merging_blocked_root_sub() {
        // given
        FilterManager manager = new FilterManager();
        manager.addFilter(givenAPredicateFilter(cl -> cl.getRootCommand().equals("aa")));
        String[] suggestions = {"henlo", "allowed"};
        // when
        List<String> kept = whenCompletionsAreProcessed(manager, "/aa ", suggestions);
        // then
        assertThat(kept, is(empty()));
    }

    @Test
    void processTabCompletions__merging_blocked_sub_suggestion() {
        // given
        FilterManager manager = new FilterManager();
        manager.addFilter(givenAPredicateFilter(cl -> cl.getFullMessage().startsWith("/aa ILLIEGAL")));
        String[] suggestions = {"ILLIEGAL", "allowed"};
        // when
        List<String> kept = whenCompletionsAreProcessed(manager, "/aa ", suggestions);
        // then
        assertThat(kept, contains("allowed"));
    }
}