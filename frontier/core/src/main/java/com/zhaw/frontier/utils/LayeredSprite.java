package com.zhaw.frontier.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A renderable sprite with an associated z-index for layered rendering.
 */
public class LayeredSprite {

    /** The texture region to render. */
    public TextureRegion region;

    /** The z-index used for render order sorting (e.g. base = 0, overlay = 10). */
    public final int zIndex;

    /**
     * Constructs a new layered sprite.
     *
     * @param region the texture region to render
     * @param zIndex the rendering layer index
     */
    public LayeredSprite(TextureRegion region, int zIndex) {
        this.region = region;
        this.zIndex = zIndex;
    }
}
