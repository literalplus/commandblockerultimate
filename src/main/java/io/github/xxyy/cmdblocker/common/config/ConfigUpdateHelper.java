package io.github.xxyy.cmdblocker.common.config;

import io.github.xxyy.cmdblocker.common.ConfigAdapter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Manages updating of the main configuration file.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 06.01.14 // 1.02
 */
public final class ConfigUpdateHelper {
    private ConfigUpdateHelper() {

    }

    public static boolean updateConfig(final ConfigAdapter adapter) {
        boolean changed = false;
        for (ConfigUpdater configUpdater : ConfigUpdaters.values()) {

            if (configUpdater.needsUpdating(adapter)) {

                try (FileWriter fileWriter = new FileWriter(adapter.getFile(), true)) {

                    adapter.getLogger().info("Now updating your config file to version " + configUpdater.getVersionNumber() + "!");
                    fileWriter.write("\n# @since " + configUpdater.getVersionNumber() + "\n");
                    fileWriter.write(configUpdater.getAdditionalLines() + "\n");

                } catch (IOException ioe) {
                    adapter.getLogger().log(Level.WARNING, "An unexpected Exception occurred while trying to update " +
                            "your config file to version " + configUpdater.getVersionNumber(), ioe);
                }

                changed = true;
            }
        }

        if(changed) {
            adapter.reload();
        }

        return changed;
    }
}
