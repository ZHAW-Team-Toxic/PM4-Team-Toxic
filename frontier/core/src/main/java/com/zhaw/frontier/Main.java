package com.zhaw.frontier;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.zhaw.frontier.wrappers.SpriteBatchWrapper;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private AssetManager assetManager;
    private SpriteBatchWrapper batch;
    private Texture image;

    @Override
    public void create() {
        assetManager = new AssetManager();
        assetManager.load("libgdx.png", Texture.class);

        batch = batch == null ? new SpriteBatchWrapper() : batch;
    }

    @Override
    public void render() {
        assetManager.update();

        if (image == null && assetManager.isLoaded("libgdx.png")) {
            image = assetManager.get("libgdx.png", Texture.class);
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        if (image != null) {
            batch.draw(image, 140, 210);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
    }

    public void setSpriteBatchWrapper(SpriteBatchWrapper spriteBatchWrapper) {
        this.batch = spriteBatchWrapper;
    }

    public void setImageTexture(Texture texture) {
        this.image = texture;
    }
}
