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

import li.l1t.cbu.common.filter.dto.CommandLine;
import li.l1t.cbu.common.platform.SenderAdapter;

/**
 * A filter action that keeps track of how often its event methods have been called.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-02-23
 */
public class SpyAction implements FilterAction {
    private int denialCount;
    private int bypassCount;

    @Override
    public void onDenial(CommandLine commandLine, SenderAdapter sender) {
        denialCount++;
    }

    @Override
    public void onBypass(CommandLine commandLine, SenderAdapter sender) {
        bypassCount++;
    }

    public int getDenialCount() {
        return denialCount;
    }

    public int getBypassCount() {
        return bypassCount;
    }
}
