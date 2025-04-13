package com.zhaw.frontier.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;

public class StartScreen extends ScreenAdapter {

    private final FrontierGame frontierGame;
    private final SpriteBatchInterface spriteBatchWrapper;
    private final ExtendViewport background;
    private final ScreenViewport menu;
    private final Stage stage;
    private final Skin skin;
    private final Texture skyeBackground;
    private final Texture groundBackground;

    // Scroll data
    private float scrollX = 0f;
    private final float scrollSpeed = 1f;

    public StartScreen(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;
        this.spriteBatchWrapper = frontierGame.getBatch();
        this.background = new ExtendViewport(16, 9);
        this.background.getCamera().position.set(8, 4.5f, 0);

        this.menu = new ScreenViewport();
        this.stage = new Stage(menu, spriteBatchWrapper.getBatch());
        Gdx.input.setInputProcessor(stage);

        this.skin = frontierGame.getAssetManager().get("skins/skin.json", Skin.class);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton startButton = new TextButton("Start", skin);
        TextButton loadButton = new TextButton("Load", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                frontierGame.switchScreen(new GameScreen(frontierGame));
            }
        });

        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Loading...");
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(startButton).pad(10).row();
        table.add(loadButton).pad(10).row();
        table.add(exitButton).pad(10).row();

        this.skyeBackground = frontierGame.getAssetManager().get("unpacked/Frontier Sky Background.png", Texture.class);
        this.groundBackground = frontierGame.getAssetManager().get("unpacked/Frontier Ground Background.png", Texture.class);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatchWrapper.begin();

        renderScrollingBackground(delta, spriteBatchWrapper.getBatch());
        spriteBatchWrapper.draw(groundBackground, 0, 0, background.getWorldWidth(), background.getWorldHeight());



        spriteBatchWrapper.end();

        stage.act(delta);
        stage.draw();
    }


    private void renderScrollingBackground(float delta, SpriteBatch renderer) {
        scrollX += scrollSpeed * delta;

        float textureAspect = (float) skyeBackground.getWidth() / skyeBackground.getHeight();
        float textureWidth = background.getWorldHeight() * textureAspect;

        // Manual float wrap to avoid black gap
        if (scrollX >= textureWidth) {
            scrollX -= textureWidth;
        } else if (scrollX < 0) {
            scrollX += textureWidth;
        }

        background.apply();
        renderer.setProjectionMatrix(background.getCamera().combined);

        float worldWidth = background.getWorldWidth();

        // Always draw enough tiles to fill the screen
        for (float x = -scrollX; x < worldWidth; x += textureWidth) {
            renderer.draw(skyeBackground, x, 0, textureWidth, background.getWorldHeight());
        }

    }

    @Override
    public void resize(int width, int height) {
        background.update(width, height, true);
        menu.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public Stage getStage() {
        return stage;
    }

    public Table getTable() {
        return (Table) stage.getActors().first();
    }
}
