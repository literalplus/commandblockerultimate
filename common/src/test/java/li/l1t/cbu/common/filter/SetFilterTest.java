/*
 * Command Blocker Ultimate
 * Copyright (C) 2014-2017 Philipp Nowak / Literallie (xxyy.github.io)
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import li.l1t.cbu.common.config.DummyResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class SetFilterTest {
    @Test
    void checkExecution__pos_single() {
        //given
        SetFilter filter = new SetFilter(ImmutableSet.of("blocked"));
        //when
        FilterOpinion opinion = whenChecked(filter, "/blocked args wow");
        //then
        assertThat(opinion, is(FilterOpinion.DENY));
    }

    private FilterOpinion whenChecked(SetFilter filter, String fullMessage) {
        return filter.checkExecution(new SimpleCommandLine(fullMessage));
    }

    @Test
    void checkExecution__pos_multiple() {
        //given
        SetFilter filter = new SetFilter(ImmutableSet.of("blocked", "more"));
        //when
        FilterOpinion opinion1 = whenChecked(filter, "/blocked args wow");
        FilterOpinion opinion2 = whenChecked(filter, "/more args wow");
        //then
        assertThat(opinion1, is(FilterOpinion.DENY));
        assertThat(opinion2, is(FilterOpinion.DENY));
    }

    @Test
    void checkExecution__neg_multiple() {
        //given
        SetFilter filter = new SetFilter(ImmutableSet.of("blocked", "more"));
        //when
        FilterOpinion opinion = whenChecked(filter, "/other args wow");
        //then
        assertThat(opinion, is(FilterOpinion.NONE));
    }

    @Test
    void checkExecution__pos_customOpinion() {
        //given
        SetFilter filter = new SetFilter(ImmutableSet.of("allowed"), FilterOpinion.ALLOW);
        //when
        FilterOpinion opinion = whenChecked(filter, "/allowed args wow");
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
        SetFilter filter = new SetFilter(ImmutableSet.of("command", "othercommand"));
        //when
        filter.resolveAliases(resolver);
        //then
        assertThat(whenChecked(filter, "/command wow"), is(FilterOpinion.DENY));
        assertThat(whenChecked(filter, "/alias1"), is(FilterOpinion.DENY));
        assertThat(whenChecked(filter, "/alias2"), is(FilterOpinion.DENY));
        assertThat(whenChecked(filter, "/othercommand"), is(FilterOpinion.DENY));
        assertThat(whenChecked(filter, "/wow"), is(FilterOpinion.NONE));
    }

}