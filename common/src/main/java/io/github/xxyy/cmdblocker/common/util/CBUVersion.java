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

package io.github.xxyy.cmdblocker.common.util;

import li.l1t.common.version.PluginVersion;

/**
 * Stores the version of CommandBlockerUltimate that is currently running.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-08-13
 */
public class CBUVersion {
    //Create plugin version from manifest (see cbu-bootstrap/pom.xml -> maven-jar-plugin,buildnumber-maven-plugin for details)
    public static final PluginVersion PLUGIN_VERSION = PluginVersion.ofClass(CBUVersion.class);
    public static final String PLUGIN_VERSION_STRING = PLUGIN_VERSION.toString();

    private CBUVersion() {

    }
}
