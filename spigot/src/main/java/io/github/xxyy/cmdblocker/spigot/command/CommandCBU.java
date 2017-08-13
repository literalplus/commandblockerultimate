/*
 * Command Blocker Ultimate
 * Copyright (C) 2014-2015 Philipp Nowak / Literallie (xxyy.github.io)
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

package io.github.xxyy.cmdblocker.spigot.command;

import io.github.xxyy.cmdblocker.common.command.ManagementCommandStrategy;
import io.github.xxyy.cmdblocker.spigot.CommandBlockerPlugin;
import li.l1t.common.command.BukkitExecution;
import li.l1t.common.command.BukkitExecutionExecutor;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;

/**
 * Represents the /cbu command which is an utility command for CBU.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2014-01-05 // 1.01
 */
public class CommandCBU extends BukkitExecutionExecutor {
    private final ManagementCommandStrategy strategy;

    public CommandCBU(CommandBlockerPlugin plugin) {
        this.strategy = new ManagementCommandStrategy(plugin);
    }

    @Override
    public void execute(BukkitExecution exec) throws UserException, InternalException {
        strategy.execute(exec);
    }
}
