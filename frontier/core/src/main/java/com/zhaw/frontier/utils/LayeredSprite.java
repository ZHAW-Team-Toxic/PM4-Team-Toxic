package com.zhaw.frontier.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LayeredSprite {

    public TextureRegion region;
    public final int zIndex; // sort by this (e.g. base=0, overlay=10)

    public LayeredSprite(TextureRegion region, int zIndex) {
        this.region = region;
        this.zIndex = zIndex;
    }
}
