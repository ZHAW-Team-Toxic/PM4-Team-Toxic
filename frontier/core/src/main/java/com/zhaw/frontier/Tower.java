package com.zhaw.frontier;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tower {
    private float x, y;
    private TextureRegion region;

    public Tower(float x, float y, TextureRegion region) {
        this.x = x;
        this.y = y;
        this.region = region;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public TextureRegion getRegion() {
        return region;
    }

    public void draw(Batch batch) {
        batch.draw(region, x, y);
    }

}
