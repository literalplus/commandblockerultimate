package io.github.xxyy.cmdblocker.common.config;

/**
 * An exception that is thrown when a configuration loaded by a {@link ConfigAdapter} is invalid for some reason.
 * We can't use {@link net.cubespace.Yamler.Config.InvalidConfigurationException} because Yamler might not be available in all cases.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.7.14
 */
public class InvalidConfigException extends Exception {
    public InvalidConfigException() {

    }

    public InvalidConfigException(Throwable cause) {
        super(cause);
    }
}
