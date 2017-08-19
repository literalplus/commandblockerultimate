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

package li.l1t.cbu.common.command;

import li.l1t.cbu.common.config.ConfigAdapter;
import li.l1t.cbu.common.platform.PlatformAdapter;
import li.l1t.cbu.common.util.CBUVersion;
import li.l1t.common.command.CommandExecution;
import li.l1t.common.command.ExecutionExecutor;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.common.i18n.DefaultFormat;
import li.l1t.common.i18n.Message;
import net.md_5.bungee.api.ChatColor;

import java.util.Collection;

/**
 * Handles platform-independent execution of the CommandBlockerUltimate management command.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-08-12
 */
public class ManagementCommandStrategy implements ExecutionExecutor<CommandExecution> {
    private final PlatformAdapter platform;

    public ManagementCommandStrategy(PlatformAdapter platform) {
        this.platform = platform;
    }

    @Override
    public void execute(CommandExecution exec) throws UserException, InternalException {
        if (exec.hasNoArgs()) {
            sendBannerMessageTo(exec);
            return;
        }
        switch (exec.arg(0).toLowerCase()) {
            case "reloadcfg":
                handleReloadConfig(exec);
                return;
            case "block":
                handleBlock(exec);
                return;
            case "free":
                handleUnblock(exec);
                return;
            case "list":
                handleList(exec);
                return;
            default:
                exec.respond(DefaultFormat.userError(Message.ofText("Unknown action: /%s %s", exec.label(), exec.arg(0))));
                sendUsageMessageTo(exec);
        }
    }

    private void sendBannerMessageTo(CommandExecution exec) {
        exec.respond(ChatColor.DARK_AQUA + "CommandBlockerUltimate " + CBUVersion.PLUGIN_VERSION_STRING);
        exec.respond(ChatColor.DARK_AQUA + " Licensed under GNU GPLv2. (Source: https://git.io/fesVwQ)");
        sendUsageMessageTo(exec);
    }

    private void sendUsageMessageTo(CommandExecution exec) {
        exec.respondUsage("reloadcfg", "", "Reloads config file");
        exec.respondUsage("block", "<command>", "Blocks a command on the fly");
        exec.respondUsage("free", "<command>", "Unblocks a command on the fly");
        exec.respondUsage("list", "", "Lists blocked commands");
    }

    private void handleReloadConfig(CommandExecution exec) {
        try {
            platform.replaceConfigAdapter();
        } catch (Exception e) { //Apparently, Yamler throws all kinds of exceptions
            handleConfigLoadError(exec, e);
            return;
        }
        exec.respond(DefaultFormat.success(Message.ofText("The configuration file has been reloaded successfully.")));
    }

    private void handleConfigLoadError(CommandExecution exec, Exception e) {
        e.printStackTrace();
        //Oops, the sender did something wrong...Send them a huge block of text to make sure they notice
        exec.respond(DefaultFormat.userError("Your configuration file is invalid! See the server log for details."));
        exec.respond(ChatColor.RED + "Your configuration file is invalid! See the server log for more details.");
        exec.respond(ChatColor.RED + "Maybe http://yaml-online-parser.appspot.com/ can help you diagnose your issue.");
        exec.respond(ChatColor.RED + "If not, you can get help at https://github.com/xxyy/commandblockerultimate/issues");
        exec.respond(ChatColor.YELLOW + "To protect your server from breaking, we have restored your previous configuration " +
                "for the time being - It will be lost if you restart or reload your server. Execute this command again if you " +
                "think you've fixed your config file.");
        exec.respond(ChatColor.YELLOW + "Check the FAQ at " +
                "https://github.com/xxyy/commandblockerultimate/wiki/Frequently-Asked-Questions for some " +
                "common problems.");
    }

    private void handleBlock(CommandExecution exec) {
        config().addBlockedCommand(exec.arg(1));
        config().resolveAliases(platform.getAliasResolver());
        exec.respond(DefaultFormat.success(Message.ofText("Added /%s to blocked commands.", exec.arg(1))));
        attemptSaveAndNotifyOnFailure(exec);
    }

    private ConfigAdapter config() {
        return platform.getConfigAdapter();
    }

    private void handleUnblock(CommandExecution exec) {
        boolean wasBlocked = config().removeBlockedCommand(exec.arg(1));
        config().resolveAliases(platform.getAliasResolver());
        notifyUnblockResult(exec, exec.arg(1), wasBlocked);
        attemptSaveAndNotifyOnFailure(exec);
    }

    private void notifyUnblockResult(CommandExecution exec, String commandName, boolean wasBlocked) {
        if (wasBlocked) {
            exec.respond(DefaultFormat.success(Message.ofText("Removed /%s from blocked commands.", commandName)));
        } else {
            exec.respond(DefaultFormat.userError(Message.ofText("/%s is not currently blocked.", commandName)));
        }
    }

    private void attemptSaveAndNotifyOnFailure(CommandExecution exec) {
        if (!config().trySave()) {
            exec.respond(DefaultFormat.userError(Message.ofText("However, the change could not be saved because" +
                    "of an error. See the server log for details.")));
        }
    }

    private void handleList(CommandExecution exec) {
        Collection<String> blockedCommands = config().getRawBlockedCommands();
        if (blockedCommands.isEmpty()) {
            exec.respond(DefaultFormat.userError("No commands are blocked."));
            exec.respond(ChatColor.YELLOW + "You may block commands with /%s block <command>, or in the " +
                    "configuration file at plugins/CommandBlockerUltimate/config.yml .", exec.label());
            return;
        }
        exec.respond(DefaultFormat.listHeader(Message.ofText("%d commands blocked", blockedCommands.size())));
        for (String blockedCommand : blockedCommands) {
            exec.respond(DefaultFormat.listItem(Message.ofText("/%s", blockedCommand)));
        }
    }
}
