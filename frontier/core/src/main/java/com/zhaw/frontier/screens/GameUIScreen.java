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

        float button1X = uiViewport.getWorldWidth() - buttonWidth - marginX;
        float button1Y = marginY;

        float button2X = button1X - buttonWidth - 10;
        float button2Y = button1Y;

        float button3X = button1X;
        float button3Y = button1Y + buttonHeight + 10;

        float button4X = button3X;
        float button4Y = button3Y + buttonHeight + 10;

        TextButton.TextButtonStyle button1Style = new TextButton.TextButtonStyle();
        button1Style.up = copperDrawable;
        button1Style.down = copperDrawable;
        button1Style.font = font;
        TextButton button1 = new TextButton("Demolish", button1Style);
        button1.setSize(buttonWidth, buttonHeight);
        button1.setPosition(button1X, button1Y);
        uiStage.addActor(button1);
        button1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Demolish button was clicked!");
                notifyObservers(GameMode.DEMOLISH);
            }
        });

        TextButton.TextButtonStyle button2Style = new TextButton.TextButtonStyle();
        button2Style.up = diamondDrawable;
        button2Style.down = diamondDrawable;
        button2Style.font = font;
        TextButton button2 = new TextButton("Build", button2Style);
        button2.setSize(buttonWidth, buttonHeight);
        button2.setPosition(button2X, button2Y);
        uiStage.addActor(button2);
        button2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Build button was clicked!");
                notifyObservers(GameMode.BUILDING);
            }
        });

        TextButton.TextButtonStyle button3Style = new TextButton.TextButtonStyle();
        button3Style.up = bronzeDrawable;
        button3Style.down = bronzeDrawable;
        button3Style.font = font;
        TextButton button3 = new TextButton("Fire Place", button3Style);
        button3.setSize(buttonWidth, buttonHeight);
        button3.setPosition(button3X, button3Y);
        uiStage.addActor(button3);
        button3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("skipping time");
            }
        });

        TextButton.TextButtonStyle button4Style = new TextButton.TextButtonStyle();
        button4Style.up = emeraldDrawable;
        button4Style.down = emeraldDrawable;
        button4Style.font = font;
        TextButton button4 = new TextButton("Pause", button4Style);
        button4.setSize(buttonWidth, buttonHeight);
        button4.setPosition(button4X, button4Y);
        uiStage.addActor(button4);
        button4.addListener(new ChangeListener() {
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
