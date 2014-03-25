package io.github.xxyy.cmdblocker.common.config;

import io.github.xxyy.cmdblocker.common.ConfigAdapter;

/**
 * Implementations update the configuration file to a specific specification version.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 06.01.14 // 1.02
 */
public interface ConfigUpdater {
    boolean needsUpdating(final ConfigAdapter adapter);
    String getAdditionalLines();
    String getVersionNumber();
}
