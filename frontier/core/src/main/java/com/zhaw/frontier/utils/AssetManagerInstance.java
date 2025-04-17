package com.zhaw.frontier.utils;

import com.badlogic.gdx.assets.AssetManager;

public class AssetManagerInstance {

    private static final AssetManager assetManager = new AssetManager();

    private AssetManagerInstance() {}

    public static AssetManager getManager() {
        return assetManager;
    }
}
