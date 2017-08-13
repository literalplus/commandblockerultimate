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

package io.github.xxyy.cmdblocker.common.platform;

import io.github.xxyy.cmdblocker.common.config.AliasResolver;
import io.github.xxyy.cmdblocker.common.config.CBUConfig;
import io.github.xxyy.cmdblocker.common.config.ConfigAdapter;
import io.github.xxyy.cmdblocker.common.config.InvalidConfigException;

/**
 * Provides an adapter for platform-specific operations related to the CommandBlockerUltimate plugin.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-08-13
 */
public interface PlatformAdapter {

    /**
     * Returns the current config adapter used by this platform. <p><b>Note:</b> The adapter might be replaced at any
     * time, so make sure to always get the latest one!</p>
     *
     * @return this platform's current config adapter
     */
    ConfigAdapter getConfigAdapter();

    /**
     * Replaces the current config adapter by a fresh one with current values from the configuration file. This is used
     * instead of {@link CBUConfig#reload()} to allow server owners to react and fix their configuration file instead of
     * breaking the plugin by assuming the default values. If the newly loaded config file is invalid, an exception is
     * thrown and the adapter is not replaced. Note that this method may not have any effect with platform-specific
     * config adapters.
     *
     * @throws InvalidConfigException Propagated from {@link CBUConfig#initialize()} - If you get this, you can safely
     *                                assume that thew adapter has not been replaced.
     */
    void replaceConfigAdapter() throws InvalidConfigException;

    /**
     * @return the platform-specific alias resolver
     */
    AliasResolver getAliasResolver();
}
