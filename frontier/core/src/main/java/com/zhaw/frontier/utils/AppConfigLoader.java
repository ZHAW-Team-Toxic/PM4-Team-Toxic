package com.zhaw.frontier.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.zhaw.frontier.configs.AppConfig;

/**
 * Utility class for loading application configuration from a JSON file.
 */
public class AppConfigLoader {

    /**
     * Reads and parses the {@code config.json} file located in the internal assets directory.
     *
     * @return an {@link AppConfig} object populated with values from the JSON file
     */
    public static AppConfig ReadAppConfig() {
        FileHandle file = Gdx.files.internal("config.json");
        Json json = new Json();
        AppConfig config = json.fromJson(AppConfig.class, file);
        return config;
    }
}
