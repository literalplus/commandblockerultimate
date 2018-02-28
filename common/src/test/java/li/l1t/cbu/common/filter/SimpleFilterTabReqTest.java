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

import com.google.common.collect.ImmutableSet;
import li.l1t.cbu.common.filter.action.FilterAction;
import li.l1t.cbu.common.filter.action.MessageAction;
import li.l1t.cbu.common.filter.action.SpyAction;
import li.l1t.cbu.common.filter.config.MutableFilterConfiguration;
import li.l1t.cbu.common.filter.criterion.SetCriterion;
import li.l1t.cbu.common.filter.dto.CommandLine;
import li.l1t.cbu.common.filter.dto.SimpleCommandLine;
import li.l1t.cbu.common.filter.dto.SimpleTabCompleteRequest;
import li.l1t.cbu.common.filter.result.FilterOpinion;
import li.l1t.cbu.common.platform.FakeSender;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SimpleFilterTabReqTest {
    private static final FilterAction DEFAULT_ACTION = new MessageAction();
    private static final String COMMAND_NAME_1 = "henlo";
    private static final String COMMAND_NAME_2 = "test";
    private static final String OTHER_COMMAND_NAME = "gunther";

    @Test
    void processTabComplete__none_by_default() {
        // given
        SimpleFilter filter = givenTheDefaultFilter();
        assumePreventsTab(filter);
        // when
        FilterOpinion opinion = whenATabRequestIsProcessed(filter, COMMAND_NAME_1);
        // then
        assertThat(opinion, is(FilterOpinion.NONE));
    }

    private SimpleFilter givenTheDefaultFilter() {
        return givenAFilter(givenAFilterConfig());
    }

    private MutableFilterConfiguration givenAFilterConfig() {
        return new MutableFilterConfiguration(DEFAULT_ACTION, DEFAULT_ACTION);
    }

    private SimpleFilter givenAFilter(MutableFilterConfiguration configuration) {
        return new SimpleFilter(configuration);
    }

    private void assumePreventsTab(SimpleFilter filter) {
        Assumptions.assumeTrue(filter.config().doesPreventTabComplete());
    }

    private FilterOpinion whenATabRequestIsProcessed(SimpleFilter filter, String commandName) {
        return whenATabRequestIsProcessed(filter, commandName, new FakeSender());
    }

    private FilterOpinion whenATabRequestIsProcessed(SimpleFilter filter, String commandName, FakeSender sender) {
        return filter.processTabRequest(new SimpleTabCompleteRequest(
                sender, "/" + commandName + " aa", false
        ));
    }

    @Test
    void processTabComplete__default_opinion() {
        // given
        SimpleFilter filter = givenAFilter(givenAFilterConfig().defaultOpinion(FilterOpinion.ALLOW));
        assumePreventsTab(filter);
        // when
        FilterOpinion opinion = whenATabRequestIsProcessed(filter, COMMAND_NAME_1);
        // then
        assertThat(opinion, is(FilterOpinion.ALLOW));
    }

    private CommandLine commandLineWith(String commandName) {
        return new SimpleCommandLine("/" + commandName + " text xyz");
    }

    @Test
    void processTabComplete__blocking_single() {
        // given
        SimpleFilter filter = givenAFilterBlocking(COMMAND_NAME_1);
        // when
        FilterOpinion opinion = whenATabRequestIsProcessed(filter, COMMAND_NAME_1);
        // then
        assertThat(opinion, is(FilterOpinion.DENY));
    }

    private SimpleFilter givenAFilterBlocking(String... commandName) {
        SimpleFilter filter = givenTheDefaultFilter();
        assumePreventsTab(filter);
        Arrays.stream(commandName)
                .map(ImmutableSet::of)
                .map(SetCriterion::new)
                .forEach(filter::addCriterion);
        return filter;
    }

    @Test
    void processTabComplete__blocking_other() {
        // given
        SimpleFilter filter = givenAFilterBlocking(COMMAND_NAME_1);
        assumeTabRequestDenied(filter, COMMAND_NAME_1);
        // when
        FilterOpinion opinion = whenATabRequestIsProcessed(filter, OTHER_COMMAND_NAME);
        // then
        assertThat(opinion, is(FilterOpinion.NONE));
    }

    private void assumeTabRequestDenied(SimpleFilter filter, String commandName) {
        Assumptions.assumeTrue(whenATabRequestIsProcessed(filter, commandName) == FilterOpinion.DENY);
    }

    @Test
    void processTabComplete__blocking_disabled() {
        // given
        SimpleFilter filter = givenAFilterBlocking(COMMAND_NAME_1);
        assumeTabRequestDenied(filter, COMMAND_NAME_1);
        // when
        config(filter).preventTabComplete(false);
        // then
        thenTabRequestIsAllowed(filter, COMMAND_NAME_1);
    }

    private void thenTabRequestIsAllowed(SimpleFilter filter, String commandName) {
        FilterOpinion opinion = whenATabRequestIsProcessed(filter, commandName);
        assertThat(opinion, is(FilterOpinion.NONE));
    }

    private MutableFilterConfiguration config(SimpleFilter filter) {
        return (MutableFilterConfiguration) filter.config();
    }

    @Test
    void processTabComplete__bypass() {
        // given
        SimpleFilter filter = givenAFilterBlocking(COMMAND_NAME_1);
        FakeSender sender = new FakeSender();
        assumeTabRequestDenied(filter, COMMAND_NAME_1, sender);
        SpyAction spy = new SpyAction();
        config(filter).tabCompleteAction(spy);
        // when
        sender.grantPermission(filter.config().getBypassPermission());
        // then
        thenTabRequestIsAllowed(filter, COMMAND_NAME_1, sender);
        assertThat(spy.getBypassCount(), is(1));
        assertThat(spy.getDenialCount(), is(0));
    }

    private void assumeTabRequestDenied(SimpleFilter filter, String commandName, FakeSender sender) {
        Assumptions.assumeTrue(whenATabRequestIsProcessed(filter, commandName, sender) == FilterOpinion.DENY);
    }

    private void thenTabRequestIsAllowed(SimpleFilter filter, String commandName, FakeSender sender) {
        FilterOpinion opinion = whenATabRequestIsProcessed(filter, commandName, sender);
        assertThat(opinion, is(FilterOpinion.NONE));
    }

    @Test
    void processTabComplete__denial_callback() {
        // given
        SimpleFilter filter = givenAFilterBlocking(COMMAND_NAME_1);
        SpyAction spy = new SpyAction();
        config(filter).tabCompleteAction(spy);
        // when
        FilterOpinion opinion = whenATabRequestIsProcessed(filter, COMMAND_NAME_1);
        // then
        assertThat(opinion, is(FilterOpinion.DENY));
        assertThat(spy.getBypassCount(), is(0));
        assertThat(spy.getDenialCount(), is(1));
    }

    @Test
    void processTabComplete__no_callback_when_allowed() {
        // given
        SimpleFilter filter = givenTheDefaultFilter();
        filter.setDefaultOpinion(FilterOpinion.ALLOW);
        SpyAction spy = new SpyAction();
        config(filter).tabCompleteAction(spy);
        // when
        FilterOpinion opinion = whenATabRequestIsProcessed(filter, COMMAND_NAME_1);
        // then
        assertThat(opinion, is(FilterOpinion.ALLOW));
        assertThat(spy.getBypassCount(), is(0));
        assertThat(spy.getDenialCount(), is(0));
    }
}