package com.zhaw.frontier.configs;

import com.zhaw.frontier.enums.AppEnvironment;

/**
 * Holds configuration settings for the application, such as the current environment
 * and logging verbosity level.
 */
public class AppConfig {

    private AppEnvironment environment;
    private int logLevel;

    /**
     * Default constructor for {@code AppConfig}.
     * Is needed for LibGDX JSON serialization
     */
    public AppConfig() {}

    /**
     * Gets the current application environment.
     *
     * @return the {@link AppEnvironment} the application is running in
     */
    public AppEnvironment getEnvironment() {
        return environment;
    }

    /**
     * Gets the configured logging level.
     *
     * @return the log level as an integer
     */
    public int getLogLevel() {
        return logLevel;
    }
}
