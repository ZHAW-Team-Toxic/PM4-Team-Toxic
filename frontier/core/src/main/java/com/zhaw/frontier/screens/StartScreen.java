package com.zhaw.frontier.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
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
    private final Texture towerBackground;
    private final Animation<Texture> knightAnimation;
    private final Animation<Texture> fireballAnimation;
    private final Texture enemyTexture;
    private final Texture logoTexture;

    private float scrollX = 0f;
    private final float scrollSpeed = 1f;
    private float stateTimeKnights = 0f;
    private float stateTimeFireball = 0f;
    private float logoScaleTime = 0f;

    private final Array<Fireball> flyingFireballs = new Array<>();

    Timer timer = new Timer();

    private static class Fireball {

        float x, y;
        float speedX, speedY;

        public Fireball(float x, float y, float speedX, float speedY) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
        }
    }

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

        startButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    frontierGame.switchScreen(new GameScreen(frontierGame));
                }
            }
        );

        loadButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Loading...");
                }
            }
        );

        exitButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.exit();
                }
            }
        );

        table.add(startButton).pad(10).row();
        table.add(loadButton).pad(10).row();
        table.add(exitButton).pad(10).row();

        this.skyeBackground =
        frontierGame
            .getAssetManager()
            .get("unpacked/titlescreen/Frontier_Sky_Background.png", Texture.class);
        this.groundBackground =
        frontierGame
            .getAssetManager()
            .get("unpacked/titlescreen/Frontier_Ground_Background.png", Texture.class);
        this.towerBackground =
        frontierGame
            .getAssetManager()
            .get("unpacked/titlescreen/Frontier_Tower.png", Texture.class);

        this.knightAnimation =
        new Animation<>(
            0.2f,
            frontierGame
                .getAssetManager()
                .get("unpacked/titlescreen/Frontier_Knights_1.png", Texture.class),
            frontierGame
                .getAssetManager()
                .get("unpacked/titlescreen/Frontier_Knights_2.png", Texture.class),
            frontierGame
                .getAssetManager()
                .get("unpacked/titlescreen/Frontier_Knights_3.png", Texture.class),
            frontierGame
                .getAssetManager()
                .get("unpacked/titlescreen/Frontier_Knights_4.png", Texture.class),
            frontierGame
                .getAssetManager()
                .get("unpacked/titlescreen/Frontier_Knights_5.png", Texture.class),
            frontierGame
                .getAssetManager()
                .get("unpacked/titlescreen/Frontier_Knights_6.png", Texture.class)
        );

        this.knightAnimation.setPlayMode(Animation.PlayMode.LOOP);

        this.fireballAnimation =
        new Animation<>(
            0.2f,
            frontierGame.getAssetManager().get("unpacked/titlescreen/fireball_1.png"),
            frontierGame.getAssetManager().get("unpacked/titlescreen/fireball_2.png"),
            frontierGame.getAssetManager().get("unpacked/titlescreen/fireball_3.png"),
            frontierGame
                .getAssetManager()
                .get("unpacked/titlescreen/fireball_4.png", Texture.class),
            frontierGame
                .getAssetManager()
                .get("unpacked/titlescreen/fireball_5.png", Texture.class),
            frontierGame
                .getAssetManager()
                .get("unpacked/titlescreen/fireball_6.png", Texture.class),
            frontierGame
                .getAssetManager()
                .get("unpacked/titlescreen/fireball_7.png", Texture.class),
            frontierGame.getAssetManager().get("unpacked/titlescreen/fireball_8.png", Texture.class)
        );
        this.fireballAnimation.setPlayMode(Animation.PlayMode.LOOP);

        this.enemyTexture =
        frontierGame
            .getAssetManager()
            .get("unpacked/titlescreen/Frontier_Enemies.png", Texture.class);

        this.logoTexture =
        frontierGame.getAssetManager().get("unpacked/titlescreen/Frontier_Logo.png", Texture.class);

        // Spawn 1–3 fireballs
        int numFireballs = MathUtils.random(2, 4);
        for (int i = 0; i < numFireballs; i++) {
            float startX = -MathUtils.random(1f, 2.5f);
            float startY = background.getWorldHeight() + MathUtils.random(1f, 3f);
            float speedX = MathUtils.random(3f, 5f);
            float speedY = -MathUtils.random(1f, 2f);
            flyingFireballs.add(new Fireball(startX, startY, speedX, speedY));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatchWrapper.begin();

        renderScrollingBackground(delta, spriteBatchWrapper.getBatch());

        spriteBatchWrapper.draw(
            groundBackground,
            0,
            0,
            background.getWorldWidth(),
            background.getWorldHeight()
        );

        renderTowerAndKnights(delta);

        renderFlyingFireball(delta, spriteBatchWrapper.getBatch());

        renderEnemies();

        renderLogo(delta);

        spriteBatchWrapper.end();

        stage.act(delta);
        stage.draw();
    }

    private void renderTowerAndKnights(float delta) {
        float towerAspect = (float) towerBackground.getWidth() / towerBackground.getHeight();
        float towerWidth = background.getWorldHeight() * towerAspect;
        spriteBatchWrapper.draw(towerBackground, 3, 0, towerWidth, background.getWorldHeight());

        spriteBatchWrapper.draw(
            knightAnimation.getKeyFrame(stateTimeKnights),
            0,
            -2,
            towerWidth,
            background.getWorldHeight()
        );
        stateTimeKnights += delta;
    }

    private void renderEnemies() {
        float enemyAspect = (float) enemyTexture.getWidth() / enemyTexture.getHeight();
        float scale = 0.55f; // 25% of full height
        float desiredHeight = background.getWorldHeight() * scale;
        float enemyWidth = desiredHeight * enemyAspect;

        spriteBatchWrapper.draw(enemyTexture, 8f, (-0.5f), enemyWidth, desiredHeight);
    }

    private void renderLogo(float delta) {
        float logoAspect = (float) logoTexture.getWidth() / logoTexture.getHeight();
        float scaleLogo = 0.2f;
        float scalePulse = 1f + 0.05f * MathUtils.sin(logoScaleTime * 2f);
        float desiredHeightLogo = background.getWorldHeight() * scaleLogo;
        float logoWidth = desiredHeightLogo * logoAspect;
        float logoHeight = desiredHeightLogo;

        // Logo position (bottom-left of unscaled image)
        float logoX = 3.5f;
        float logoY = 6f;

        SpriteBatch batch = spriteBatchWrapper.getBatch();
        TextureRegion logoRegion = new TextureRegion(logoTexture);

        batch.draw(
            logoRegion,
            logoX,
            logoY,
            logoWidth / 2f,
            logoHeight / 2f, // originX/Y → center of logo
            logoWidth,
            logoHeight, // size
            scalePulse,
            scalePulse, // scaleX/Y
            0f // no rotation
        );

        logoScaleTime += delta;
    }

    private void renderScrollingBackground(float delta, SpriteBatch renderer) {
        scrollX += scrollSpeed * delta;

        float textureAspect = (float) skyeBackground.getWidth() / skyeBackground.getHeight();
        float textureWidth = background.getWorldHeight() * textureAspect;

        if (scrollX >= textureWidth) {
            scrollX -= textureWidth;
        } else if (scrollX < 0) {
            scrollX += textureWidth;
        }

        background.apply();
        renderer.setProjectionMatrix(background.getCamera().combined);

        float worldWidth = background.getWorldWidth();

        for (float x = -scrollX; x < worldWidth; x += textureWidth) {
            renderer.draw(skyeBackground, x, 0, textureWidth, background.getWorldHeight());
        }
    }

    private void renderFlyingFireball(float delta, SpriteBatch renderer) {
        stateTimeFireball += delta;

        Texture currentFrame = fireballAnimation.getKeyFrame(stateTimeFireball);
        float fireballAspect = (float) currentFrame.getWidth() / currentFrame.getHeight();
        float fireballHeight = 1.2f;
        float fireballWidth = fireballHeight * fireballAspect;

        for (Fireball f : flyingFireballs) {
            f.x += f.speedX * delta;
            f.y += f.speedY * delta;

            if (f.x > background.getWorldWidth() || f.y < -fireballHeight) {
                f.x = -MathUtils.random(1f, 3f);
                f.y = background.getWorldHeight() + MathUtils.random(1f, 3f);
            }

            renderer.draw(currentFrame, f.x, f.y, fireballWidth, fireballHeight);
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
