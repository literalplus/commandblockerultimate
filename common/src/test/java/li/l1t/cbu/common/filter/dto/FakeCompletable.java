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

package li.l1t.cbu.common.filter.dto;

import com.google.common.base.Preconditions;
import li.l1t.cbu.common.platform.SenderAdapter;

import java.util.Optional;

/**
 * A mock implementation of a completable.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2018-03-12
 */
public class FakeCompletable implements Completable {
    private final SenderAdapter sender;
    private final CommandLine mergedCommand;

    public FakeCompletable(SenderAdapter sender, CommandLine mergedCommand) {
        this.sender = Preconditions.checkNotNull(sender, "sender");
        this.mergedCommand = mergedCommand;
    }

    @Override
    public SenderAdapter getSender() {
        return sender;
    }

    @Override
    public Optional<CommandLine> findMergedCommand() {
        return Optional.ofNullable(mergedCommand);
    }
}
