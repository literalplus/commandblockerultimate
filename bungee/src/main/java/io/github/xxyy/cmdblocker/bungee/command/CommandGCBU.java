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

package io.github.xxyy.cmdblocker.bungee.command;

import io.github.xxyy.cmdblocker.bungee.CommandBlockerPlugin;
import io.github.xxyy.cmdblocker.common.command.ManagementCommandStrategy;
import li.l1t.common.bungee.command.BungeeExecution;
import li.l1t.common.bungee.command.BungeeExecutionExecutor;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;

/**
 * Command for managing BungeeCord CommandBlockerUltimate.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2014-07-16
 */
public class CommandGCBU extends BungeeExecutionExecutor {
    private final ManagementCommandStrategy strategy;

    public CommandGCBU(CommandBlockerPlugin plugin) {
        super("gcbu", "cmdblock.admin");
        strategy = new ManagementCommandStrategy(plugin);
    }

    @Override
    public void execute(BungeeExecution exec) throws UserException, InternalException {
        strategy.execute(exec);
    }
}
