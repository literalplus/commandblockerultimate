package io.github.xxyy.cmdblocker.config;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Implementations update the configuration file to a specific specification version.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 06.01.14 // 1.02
 */
//TODO: Is this needed? Relying on ConfigUpdaters implementation detail .values() anyway
public interface ConfigUpdater {
    boolean needsUpdating(final FileConfiguration configToCheck);
    String getAdditionalLines();
    String getVersionNumber();
}
