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
import li.l1t.cbu.common.filter.result.FilterOpinion;
import li.l1t.cbu.common.platform.FakeSender;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SimpleFilterTest {
    private static final FilterAction DEFAULT_ACTION = new MessageAction();
    private static final String COMMAND_NAME_1 = "henlo";
    private static final String COMMAND_NAME_2 = "test";
    private static final String OTHER_COMMAND_NAME = "gunther";

    @Test
    void processExecution__none_by_default() {
        // given
        SimpleFilter filter = givenTheDefaultFilter();
        FakeSender sender = new FakeSender();
        assumePreventsExecution(filter);
        // when
        FilterOpinion opinion = filter.processExecution(commandLineWith(COMMAND_NAME_1), sender);
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

    private CommandLine commandLineWith(String commandName) {
        return new SimpleCommandLine("/" + commandName + " text xyz");
    }

    @Test
    void processExecution__default_opinion() {
        // given
        SimpleFilter filter = givenAFilter(givenAFilterConfig().defaultOpinion(FilterOpinion.ALLOW));
        FakeSender sender = new FakeSender();
        assumePreventsExecution(filter);
        // when
        FilterOpinion opinion = filter.processExecution(commandLineWith(COMMAND_NAME_1), sender);
        // then
        assertThat(opinion, is(FilterOpinion.ALLOW));
    }

    @Test
    void processExecution__blocking_single() {
        // given
        SimpleFilter filter = givenAFilterBlocking(COMMAND_NAME_1);
        FakeSender sender = new FakeSender();
        assumePreventsExecution(filter);
        // when
        FilterOpinion opinion = filter.processExecution(commandLineWith(COMMAND_NAME_1), sender);
        // then
        assertThat(opinion, is(FilterOpinion.DENY));
    }

    private SimpleFilter givenAFilterBlocking(String... commandName) {
        SimpleFilter filter = givenTheDefaultFilter();
        assumePreventsExecution(filter);
        Arrays.stream(commandName)
                .map(ImmutableSet::of)
                .map(SetCriterion::new)
                .forEach(filter::addCriterion);
        return filter;
    }

    private void assumePreventsExecution(SimpleFilter filter) {
        Assumptions.assumeTrue(filter.config().doesPreventExecution());
    }

    @Test
    void processExecution__blocking_other() {
        // given
        SimpleFilter filter = givenAFilterBlocking(COMMAND_NAME_1);
        FakeSender sender = new FakeSender();
        assumeExecutionDenied(filter, sender, COMMAND_NAME_1);
        // when
        FilterOpinion opinion = filter.processExecution(commandLineWith(OTHER_COMMAND_NAME), sender);
        // then
        assertThat(opinion, is(FilterOpinion.NONE));
    }

    private void assumeExecutionDenied(SimpleFilter filter, FakeSender sender, String commandName) {
        Assumptions.assumeTrue(filter.processExecution(commandLineWith(commandName), sender) == FilterOpinion.DENY);
    }

    @Test
    void processExecution__blocking_multiple() {
        // given
        SimpleFilter filter = givenAFilterBlocking(COMMAND_NAME_1, COMMAND_NAME_2);
        FakeSender sender = new FakeSender();
        // when
        FilterOpinion opinion1 = filter.processExecution(commandLineWith(COMMAND_NAME_1), sender);
        FilterOpinion opinion2 = filter.processExecution(commandLineWith(COMMAND_NAME_2), sender);
        FilterOpinion opinionOther = filter.processExecution(commandLineWith(OTHER_COMMAND_NAME), sender);
        // then
        assertThat(opinion1, is(FilterOpinion.DENY));
        assertThat(opinion2, is(FilterOpinion.DENY));
        assertThat(opinionOther, is(FilterOpinion.NONE));
    }

    @Test
    void processExecution__blocking_disabled() {
        // given
        SimpleFilter filter = givenAFilterBlocking(COMMAND_NAME_1);
        FakeSender sender = new FakeSender();
        assumeExecutionDenied(filter, sender, COMMAND_NAME_1);
        // when
        config(filter).preventExecution(false);
        // then
        thenExecutionIsAllowed(filter, sender, COMMAND_NAME_1);
    }

    private void thenExecutionIsAllowed(SimpleFilter filter, FakeSender sender, String commandName) {
        FilterOpinion opinion = filter.processExecution(commandLineWith(commandName), sender);
        assertThat(opinion, is(FilterOpinion.NONE));
    }

    private MutableFilterConfiguration config(SimpleFilter filter) {
        return (MutableFilterConfiguration) filter.config();
    }

    @Test
    void processExecution__bypass() {
        // given
        SimpleFilter filter = givenAFilterBlocking(COMMAND_NAME_1);
        FakeSender sender = new FakeSender();
        assumeExecutionDenied(filter, sender, COMMAND_NAME_1);
        // when
        sender.grantPermission(filter.config().getBypassPermission());
        // then
        thenExecutionIsAllowed(filter, sender, COMMAND_NAME_1);
    }

    @Test
    void processExecution__bypass_callback() {
        // given
        SimpleFilter filter = givenAFilterBlocking(COMMAND_NAME_1);
        FakeSender sender = givenASenderWithBypassPermission(filter);
        assumeExecutionAllowed(filter, sender, COMMAND_NAME_1);
        SpyAction spy = givenExecutionsAreSpiedUpon(filter);
        // when
        filter.processExecution(commandLineWith(COMMAND_NAME_1), sender);
        // then
        assertThat(spy.getBypassCount(), is(1));
    }

    private FakeSender givenASenderWithBypassPermission(SimpleFilter filter) {
        FakeSender sender = new FakeSender();
        sender.grantPermission(filter.config().getBypassPermission());
        return sender;
    }

    private SpyAction givenExecutionsAreSpiedUpon(SimpleFilter filter) {
        SpyAction spyAction = new SpyAction();
        config(filter).executionAction(spyAction);
        return spyAction;
    }

    private void assumeExecutionAllowed(SimpleFilter filter, FakeSender sender, String commandName) {
        Assumptions.assumeTrue(filter.processExecution(commandLineWith(commandName), sender) != FilterOpinion.DENY);
    }

    @Test
    void processExecution__bypass_no_callback() {
        // given
        SimpleFilter filter = givenAFilterBlocking(COMMAND_NAME_1);
        FakeSender sender = new FakeSender();
        assumeExecutionDenied(filter, sender, COMMAND_NAME_1);
        SpyAction spy = givenExecutionsAreSpiedUpon(filter);
        // when
        filter.processExecution(commandLineWith(COMMAND_NAME_1), sender);
        // then
        assertThat(spy.getBypassCount(), is(0));
    }
}