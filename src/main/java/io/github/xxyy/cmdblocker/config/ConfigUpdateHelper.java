package io.github.xxyy.cmdblocker.config;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages updating of the main configuration file.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 06.01.14 // 1.02
 */
public final class ConfigUpdateHelper {
    private static final Logger LOGGER = PluginLogger.getLogger(ConfigUpdateHelper.class.getName());

    private ConfigUpdateHelper() {

    }

    public boolean updateConfig(final Plugin plugin, final File configFile) {
        return updateConfig(plugin, configFile, false);
    }

    private boolean updateConfig(final Plugin plugin, final File configFile, final boolean alreadyChanged) {
        for (ConfigUpdater configUpdater : ConfigUpdaters.values()) {

            if (configUpdater.needsUpdating(plugin.getConfig())) {

                try (FileWriter fileWriter = new FileWriter(configFile, true)) {

                    fileWriter.write("\n# @since " + configUpdater.getVersionNumber() + "\n");
                    fileWriter.write(configUpdater.getAdditionalLines() + "\n");

                } catch (IOException ioe) {
                    LOGGER.log(Level.WARNING, "An unexpected Exception occurred while trying to update " +
                            "your config file to version " + configUpdater.getVersionNumber(), ioe);
                }

                plugin.reloadConfig();
            }
        }

        return alreadyChanged;
    }
}
