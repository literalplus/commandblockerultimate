package io.github.xxyy.cmdblocker.common.config;

import io.github.xxyy.cmdblocker.common.ConfigAdapter;

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
            "prevent-tab: true\n" +
            "# What strategy to use when blocking tab-complete replies from the server.\n" +
            "# true: block all completions returning a targeted command (for example, if /p is typed and /pl is blocked, print error message)\n" +
            "# false: just remove blocked commands from list (in the above example, other commands starting with p would still be shown without notice)\n" +
            "# Default value: false\n" +
            "tab-restrictive-mode: false"){
        @Override
        public boolean needsUpdating(final ConfigAdapter adapter) {
            return !adapter.contains("prevent-tab");
        }
    };

    private ConfigUpdaters(final String version, final String lines){
        this.versionNumber = version;
        this.additionalLines = lines;
    }

    private final String additionalLines;
    private final String versionNumber;

    @Override
    public String getAdditionalLines() {
        return additionalLines;
    }

    @Override
    public String getVersionNumber() {
        return versionNumber;
    }
}
