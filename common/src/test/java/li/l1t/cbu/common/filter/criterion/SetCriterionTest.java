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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import li.l1t.cbu.common.config.DummyResolver;
import li.l1t.cbu.common.filter.SimpleCommandLine;
import li.l1t.cbu.common.filter.result.FilterOpinion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class SetCriterionTest {
    @Test
    void checkExecution__pos_single() {
        //given
        SetCriterion criterion = new SetCriterion(ImmutableSet.of("blocked"));
        //when
        FilterOpinion opinion = whenChecked(criterion, "/blocked args wow");
        //then
        assertThat(opinion, is(FilterOpinion.DENY));
    }

    private FilterOpinion whenChecked(SetCriterion criterion, String fullMessage) {
        return criterion.checkExecution(new SimpleCommandLine(fullMessage));
    }

    @Test
    void checkExecution__pos_prefix_blocked() {
        //given
        SetCriterion criterion = new SetCriterion(ImmutableSet.of("bukkit:blocked"));
        //when
        FilterOpinion negative = whenChecked(criterion, "/blocked args wow");
        FilterOpinion positive = whenChecked(criterion, "/bukkit:blocked args wow");
        //then
        assertThat(negative, is(FilterOpinion.NONE));
        assertThat(positive, is(FilterOpinion.DENY));
    }

    @Test
    void checkExecution__pos_prefix_implied() {
        //given
        SetCriterion criterion = new SetCriterion(ImmutableSet.of("blocked"));
        //when
        FilterOpinion opinion1 = whenChecked(criterion, "/blocked args wow");
        FilterOpinion opinion2 = whenChecked(criterion, "/bukkit:blocked args wow");
        //then
        assertThat(opinion1, is(FilterOpinion.DENY));
        assertThat(opinion2, is(FilterOpinion.DENY));
    }

    @Test
    void checkExecution__pos_multiple() {
        //given
        SetCriterion criterion = new SetCriterion(ImmutableSet.of("blocked", "more"));
        //when
        FilterOpinion opinion1 = whenChecked(criterion, "/blocked args wow");
        FilterOpinion opinion2 = whenChecked(criterion, "/more args wow");
        //then
        assertThat(opinion1, is(FilterOpinion.DENY));
        assertThat(opinion2, is(FilterOpinion.DENY));
    }

    @Test
    void checkExecution__neg_multiple() {
        //given
        SetCriterion criterion = new SetCriterion(ImmutableSet.of("blocked", "more"));
        //when
        FilterOpinion opinion = whenChecked(criterion, "/other args wow");
        //then
        assertThat(opinion, is(FilterOpinion.NONE));
    }

    @Test
    void checkExecution__pos_customOpinion() {
        //given
        SetCriterion criterion = new SetCriterion(ImmutableSet.of("allowed"), FilterOpinion.ALLOW);
        //when
        FilterOpinion opinion = whenChecked(criterion, "/allowed args wow");
        //then
        assertThat(opinion, is(FilterOpinion.ALLOW));
    }

    @Test
    void resolveAliases() {
        //given
        DummyResolver resolver = new DummyResolver(
                ImmutableMap.<String, List<String>>builder()
                        .put("command", ImmutableList.of("alias1", "alias2"))
                        .put("othercommand", ImmutableList.of())
                        .build()
        );
        SetCriterion criterion = new SetCriterion(ImmutableSet.of("command", "othercommand"));
        //when
        criterion.resolveAliases(resolver);
        //then
        assertThat(whenChecked(criterion, "/command wow"), is(FilterOpinion.DENY));
        assertThat(whenChecked(criterion, "/alias1"), is(FilterOpinion.DENY));
        assertThat(whenChecked(criterion, "/alias2"), is(FilterOpinion.DENY));
        assertThat(whenChecked(criterion, "/othercommand"), is(FilterOpinion.DENY));
        assertThat(whenChecked(criterion, "/wow"), is(FilterOpinion.NONE));
    }

}