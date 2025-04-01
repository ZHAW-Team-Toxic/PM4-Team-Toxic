package com.zhaw.frontier.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.util.ButtonClickObserver;
import com.zhaw.frontier.util.GameMode;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;

public class BaseUIScreen {

    private Array<ButtonClickObserver> observers = new Array<>();
    private Stage uiStage;
    private Viewport uiViewport;
    private Skin skin;
    private TextureAtlas atlas;

    public BaseUIScreen(
        FrontierGame frontierGame,
        SpriteBatchInterface spriteBatch,
        GameScreen gameScreen
    ) {
        uiViewport = new ScreenViewport(new OrthographicCamera());
        uiStage = new Stage(uiViewport, spriteBatch.getBatch());

        skin = frontierGame.getAssetManager().get("skins/skin.json", Skin.class);
        atlas = new TextureAtlas(Gdx.files.internal("skins/skin.atlas"));

        BitmapFont font = skin.getFont("ArchivoBlack");

        Sprite demolishSprite = atlas.createSprite("BrokenPickaxe");
        SpriteDrawable demolishDrawable = new SpriteDrawable(demolishSprite);

        Sprite buildSprite = atlas.createSprite("Pickaxe");
        SpriteDrawable buildDrawable = new SpriteDrawable(buildSprite);

        Sprite fireplaceSprite = atlas.createSprite("Campfire");
        SpriteDrawable fireplaceDrawable = new SpriteDrawable(fireplaceSprite);

        Sprite pauseSprite = atlas.createSprite("Gears");
        SpriteDrawable pauseDrawable = new SpriteDrawable(pauseSprite);

        float buttonWidth = uiViewport.getWorldWidth() * 0.03f;
        float buttonHeight = uiViewport.getWorldHeight() * 0.04f;

        float marginX = 20f;
        float marginY = 20f;

        float demolishButtonX = uiViewport.getWorldWidth() - buttonWidth - marginX;
        float demolishButtonY = marginY;

        float buildButtonX = demolishButtonX - buttonWidth - 10;
        float buildButtonY = demolishButtonY;

        float fireplaceButtonX = demolishButtonX;
        float fireplaceButtonY = demolishButtonY + buttonHeight + 10;

        float pauseButtonX = fireplaceButtonX;
        float pauseButtonY = fireplaceButtonY + buttonHeight + 10;

        TextButton.TextButtonStyle demolishButtonStyle = new TextButton.TextButtonStyle();
        demolishButtonStyle.up = demolishDrawable;
        demolishButtonStyle.down = demolishDrawable;
        demolishButtonStyle.font = font;
        TextButton demolishButton = new TextButton("", demolishButtonStyle);
        demolishButton.setSize(buttonWidth, buttonHeight);
        demolishButton.setPosition(demolishButtonX, demolishButtonY);
        uiStage.addActor(demolishButton);
        demolishButton.addListener(
            new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    System.out.println("Demolish button was clicked!");
                    notifyObservers(GameMode.DEMOLISH);
                }
            }
        );

        TextButton.TextButtonStyle buildButtonStyle = new TextButton.TextButtonStyle();
        buildButtonStyle.up = buildDrawable;
        buildButtonStyle.down = buildDrawable;
        buildButtonStyle.font = font;
        TextButton buildButton = new TextButton("", buildButtonStyle);
        buildButton.setSize(buttonWidth, buttonHeight);
        buildButton.setPosition(buildButtonX, buildButtonY);
        uiStage.addActor(buildButton);
        buildButton.addListener(
            new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    System.out.println("Build button was clicked!");
                    notifyObservers(GameMode.BUILDING);
                }
            }
        );

        TextButton.TextButtonStyle fireplaceButtonStyle = new TextButton.TextButtonStyle();
        fireplaceButtonStyle.up = fireplaceDrawable;
        fireplaceButtonStyle.down = fireplaceDrawable;
        fireplaceButtonStyle.font = font;
        TextButton fireplaceButton = new TextButton("", fireplaceButtonStyle);
        fireplaceButton.setSize(buttonWidth, buttonHeight);
        fireplaceButton.setPosition(fireplaceButtonX, fireplaceButtonY);
        uiStage.addActor(fireplaceButton);
        fireplaceButton.addListener(
            new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    System.out.println("skipping time");
                }
            }
        );

        TextButton.TextButtonStyle pauseButtonStyle = new TextButton.TextButtonStyle();
        pauseButtonStyle.up = pauseDrawable;
        pauseButtonStyle.down = pauseDrawable;
        pauseButtonStyle.font = font;
        TextButton pauseButton = new TextButton("", pauseButtonStyle);
        pauseButton.setName("pauseButton");
        pauseButton.setSize(buttonWidth, buttonHeight);
        pauseButton.setPosition(pauseButtonX, pauseButtonY);
        uiStage.addActor(pauseButton);
        pauseButton.addListener(
            new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    frontierGame.switchScreen(new PauseScreen(frontierGame, gameScreen));
                    System.out.println("opening pause menu...");
                }
            }
        );

        Gdx.input.setInputProcessor(uiStage);
    }

    public void addObserver(ButtonClickObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(GameMode gameMode) {
        for (ButtonClickObserver observer : observers) {
            observer.buttonClicked(gameMode);
        }
    }

    public void render(float delta) {
        uiViewport.apply();

        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        int scissorX = screenWidth - 560;
        int scissorY = 0;
        int scissorWidth = 560;
        int scissorHeight = screenHeight / 3;

        Gdx.gl.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);

        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

        uiStage.act(delta);
        uiStage.draw();
    }

    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    public Stage getStage() {
        return uiStage;
    }

    public void dispose() {
        uiStage.dispose();
        skin.dispose();
    }

    Table getTable() {
        return (Table) getStage().getActors().first();
    }
}
