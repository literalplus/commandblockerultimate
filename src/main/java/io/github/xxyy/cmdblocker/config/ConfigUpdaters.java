package io.github.xxyy.cmdblocker.config;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Provides implementations for updating config files between versions.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 06.01.14
 */
public enum ConfigUpdaters implements ConfigUpdater{

    TO_102("1.02", "# Whether to prevent tab-completion for blocked commands.\n" +
            "# Note: Requires ProtocolLib!\n" +
            "# Default value: true\n" +
            "prevent-tab: true"){
        @Override
        public boolean needsUpdating(final FileConfiguration configToCheck) {
            return !configToCheck.contains("prevent-tab");
        }
    };

    private ConfigUpdaters(final String version, final String lines){
        this.versionNumber = version;
        this.additionalLines = lines;
    }

    private final String additionalLines;
    private final String versionNumber;

    public abstract boolean needsUpdating(final FileConfiguration configToCheck);

    @Override
    public String getAdditionalLines() {
        return additionalLines;
    }

    @Override
    public String getVersionNumber() {
        return versionNumber;
    }
}
