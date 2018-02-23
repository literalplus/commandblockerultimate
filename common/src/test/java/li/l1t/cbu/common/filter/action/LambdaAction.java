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

package li.l1t.cbu.common.filter.action;

import li.l1t.cbu.common.filter.CommandLine;
import li.l1t.cbu.common.filter.result.FilterResult;
import li.l1t.cbu.common.platform.SenderAdapter;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A simple filter action that forwards handling to lambda expressions.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-23
 */
public class LambdaAction implements FilterAction {
    private Consumer<FilterResult> denialConsumer;
    private BiConsumer<CommandLine, SenderAdapter> bypassConsumer;

    public LambdaAction denial(Consumer<FilterResult> denialConsumer) {
        this.denialConsumer = denialConsumer;
        return this;
    }

    public LambdaAction bypass(BiConsumer<CommandLine, SenderAdapter> bypassConsumer) {
        this.bypassConsumer = bypassConsumer;
        return this;
    }

    @Override
    public void onDenial(FilterResult result) {
        if (denialConsumer != null) {
            denialConsumer.accept(result);
        }
    }

    @Override
    public void onBypass(CommandLine commandLine, SenderAdapter sender) {
        if (bypassConsumer != null) {
            bypassConsumer.accept(commandLine, sender);
        }
    }
}
