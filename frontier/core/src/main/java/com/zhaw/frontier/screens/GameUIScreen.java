package com.zhaw.frontier.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.util.ButtonClickObserver;
import com.zhaw.frontier.util.GameMode;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;

public class GameUIScreen {
    private Array<ButtonClickObserver> observers = new Array<>();
    private Stage uiStage;
    private Viewport uiViewport;
    private Skin skin;
    private TextureAtlas atlas;

    public GameUIScreen(FrontierGame frontierGame, SpriteBatchInterface spriteBatch) {
        uiViewport = new ScreenViewport(new OrthographicCamera());
        uiStage = new Stage(uiViewport, spriteBatch.getBatch());

        skin = frontierGame.getAssetManager().get("skins/skin.json", Skin.class);
        atlas = new TextureAtlas(Gdx.files.internal("skins/skin.atlas"));

        BitmapFont font = skin.getFont("ArchivoBlack");

        //TODO: Change Assets used for the four buttons (placeholders at the moment to be able to distinguish them)

        Sprite copperSprite = atlas.createSprite("Copper");
        SpriteDrawable copperDrawable = new SpriteDrawable(copperSprite);

        Sprite diamondSprite = atlas.createSprite("Diamond");
        SpriteDrawable diamondDrawable = new SpriteDrawable(diamondSprite);

        Sprite bronzeSprite = atlas.createSprite("Bronze");
        SpriteDrawable bronzeDrawable = new SpriteDrawable(bronzeSprite);

        Sprite emeraldSprite = atlas.createSprite("Emerald");
        SpriteDrawable emeraldDrawable = new SpriteDrawable(emeraldSprite);

        float buttonWidth = uiViewport.getWorldWidth() * 0.05f;
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
        demolishButtonStyle.up = copperDrawable;
        demolishButtonStyle.down = copperDrawable;
        demolishButtonStyle.font = font;
        TextButton demolishButton = new TextButton("Demolish", demolishButtonStyle);
        demolishButton.setSize(buttonWidth, buttonHeight);
        demolishButton.setPosition(demolishButtonX, demolishButtonY);
        uiStage.addActor(demolishButton);
        demolishButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Demolish button was clicked!");
                notifyObservers(GameMode.DEMOLISH);
            }
        });

        TextButton.TextButtonStyle buildButtonStyle = new TextButton.TextButtonStyle();
        buildButtonStyle.up = diamondDrawable;
        buildButtonStyle.down = diamondDrawable;
        buildButtonStyle.font = font;
        TextButton buildButton = new TextButton("Build", buildButtonStyle);
        buildButton.setSize(buttonWidth, buttonHeight);
        buildButton.setPosition(buildButtonX, buildButtonY);
        uiStage.addActor(buildButton);
        buildButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Build button was clicked!");
                notifyObservers(GameMode.BUILDING);
            }
        });

        TextButton.TextButtonStyle fireplaceButtonStyle = new TextButton.TextButtonStyle();
        fireplaceButtonStyle.up = bronzeDrawable;
        fireplaceButtonStyle.down = bronzeDrawable;
        fireplaceButtonStyle.font = font;
        TextButton fireplaceButton = new TextButton("Fire Place", fireplaceButtonStyle);
        fireplaceButton.setSize(buttonWidth, buttonHeight);
        fireplaceButton.setPosition(fireplaceButtonX, fireplaceButtonY);
        uiStage.addActor(fireplaceButton);
        fireplaceButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("skipping time");
            }
        });

        TextButton.TextButtonStyle pauseButtonStyle = new TextButton.TextButtonStyle();
        pauseButtonStyle.up = emeraldDrawable;
        pauseButtonStyle.down = emeraldDrawable;
        pauseButtonStyle.font = font;
        TextButton pauseButton = new TextButton("Pause", pauseButtonStyle);
        pauseButton.setSize(buttonWidth, buttonHeight);
        pauseButton.setPosition(pauseButtonX, pauseButtonY);
        uiStage.addActor(pauseButton);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //TODO: Wait for merge of issue #14
                System.out.println("opening pause menu...");
            }
        });

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
}
