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
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class CriteriaListTest {
    @Test
    void checkExecution__none() {
        //given
        CriteriaList list = givenACriteriaList();
        //when
        FilterOpinion res = whenChecked(list, "/wow args");
        //then
        assertThat(res, CoreMatchers.is(FilterOpinion.NONE));
    }

    private CriteriaList givenACriteriaList() {
        return new CriteriaList(FilterOpinion.NONE);
    }

    private FilterOpinion whenChecked(CriteriaList list, String fullMessage) {
        return list.process(new SimpleCommandLine(fullMessage));
    }

    @Test
    void checkExecution__one() {
        //given
        CriteriaList list = givenACriteriaList();
        list.addCriterion(givenASetCriterion("blocked"));
        //when
        FilterOpinion res = whenChecked(list, "/blocked");
        //then
        assertThat(res, CoreMatchers.is(FilterOpinion.DENY));
    }

    private SetCriterion givenASetCriterion(String... blocked) {
        return new SetCriterion(ImmutableSet.copyOf(blocked));
    }

    @Test
    void checkExecution__multi_pos() {
        //given
        CriteriaList list = givenACriteriaList();
        list.addCriterion(givenASetCriterion("blocked"));
        list.addCriterion(givenASetCriterion("other"));
        //when
        FilterOpinion res1 = whenChecked(list, "/blocked");
        FilterOpinion res2 = whenChecked(list, "/other");
        //then
        assertThat(res1, CoreMatchers.is(FilterOpinion.DENY));
        assertThat(res2, CoreMatchers.is(FilterOpinion.DENY));
    }

    @Test
    void checkExecution__multi_neg() {
        //given
        CriteriaList list = givenACriteriaList();
        list.addCriterion(givenASetCriterion("blocked"));
        list.addCriterion(givenASetCriterion("other"));
        //when
        FilterOpinion res = whenChecked(list, "/wow");
        //then
        assertThat(res, CoreMatchers.is(FilterOpinion.NONE));
    }

    @Test
    void checkExecution__multi_same() {
        //given
        CriteriaList list = givenACriteriaList();
        list.addCriterion(givenASetCriterion("blocked"));
        list.addCriterion(givenASetCriterion("blocked"));
        //when
        FilterOpinion res = whenChecked(list, "/blocked");
        //then
        assertThat(res, CoreMatchers.is(FilterOpinion.DENY));
    }

    @Test
    void checkExecution__multi_aliases() {
        //given
        CriteriaList list = givenACriteriaList();
        list.addCriterion(givenASetCriterion("mycommand"));
        list.addCriterion(givenASetCriterion("othercommand"));
        DummyResolver resolver = givenAnAliasResolver();
        //when
        list.resolveAliases(resolver);
        //then
        thenAliasResolutionYielded(FilterOpinion.DENY, list);
    }

    private DummyResolver givenAnAliasResolver() {
        return new DummyResolver(
                ImmutableMap.<String, List<String>>builder()
                        .put("mycommand", ImmutableList.of("alias3", "alias4"))
                        .put("othercommand", ImmutableList.of())
                        .build()
        );
    }

    private void thenAliasResolutionYielded(FilterOpinion aliasOpinion, CriteriaList list) {
        assertThat(whenChecked(list, "/mycommand wow"), is(FilterOpinion.DENY));
        assertThat(whenChecked(list, "/alias3"), is(aliasOpinion));
        assertThat(whenChecked(list, "/alias4"), is(aliasOpinion));
        assertThat(whenChecked(list, "/othercommand"), is(FilterOpinion.DENY));
        assertThat(whenChecked(list, "/wow"), is(FilterOpinion.NONE));
    }
}
