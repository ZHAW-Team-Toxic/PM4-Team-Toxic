package com.zhaw.frontier;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.zhaw.frontier.wrappers.SpriteBatchWrapper;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatchWrapper batch;
    private Texture image;

    @Override
    public void create() {
        batch = batch == null ? new SpriteBatchWrapper() : batch;
        image = new Texture("libgdx.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }

    public void setSpriteBatchWrapper(SpriteBatchWrapper spriteBatchWrapper) {
        this.batch = spriteBatchWrapper;
    }

    public void setImageTexture(Texture texture) {
        this.image = texture;
    }
}
