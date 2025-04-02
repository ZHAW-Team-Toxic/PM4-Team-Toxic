package com.zhaw.frontier.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.entityFactories.WallFactory;
import com.zhaw.frontier.screens.GameScreen;
import com.zhaw.frontier.screens.PauseScreen;
import com.zhaw.frontier.systems.BuildingManagerSystem;
import com.zhaw.frontier.util.ButtonClickObserver;
import com.zhaw.frontier.util.GameMode;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import lombok.Getter;
import lombok.Setter;

/**
 * BaseUIScreen provides a basic interface for the game that allows you to switch between different game modes.
 * The game modes are represented by buttons that are displayed on the screen.
 * The buttons include: demolish, build, fireplace, and pause.
 */
public class BaseUI {

    private Array<ButtonClickObserver> observers = new Array<>();
    private Stage uiStage;
    private Viewport uiViewport;
    private Skin skin;
    private TextureAtlas atlas;

    @Setter
    @Getter
    GameMode gameMode = GameMode.NORMAL;

    /**
     * Constructor for BaseUIScreen.
     *
     * @param frontierGame The game instance
     * @param spriteBatch  The sprite batch
     * @param gameScreen   The game screen
     */
    public BaseUI(
        FrontierGame frontierGame,
        SpriteBatchInterface spriteBatch,
        GameScreen gameScreen
    ) {
        uiViewport = new ScreenViewport(new OrthographicCamera());
        uiStage = new Stage(uiViewport, spriteBatch.getBatch());

        skin = frontierGame.getAssetManager().get("skins/skin.json", Skin.class);
        atlas = new TextureAtlas(Gdx.files.internal("skins/skin.atlas"));

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

        createButton(
            "BrokenPickaxe",
            demolishButtonX,
            demolishButtonY,
            GameMode.DEMOLISH,
            "demolishButton",
            () -> System.out.println("Demolish button was clicked!")
        );

        createButton(
            "Pickaxe",
            buildButtonX,
            buildButtonY,
            GameMode.BUILDING,
            "buildButton",
            () -> System.out.println("Build button was clicked!")
        );

        createButton(
            "Campfire",
            fireplaceButtonX,
            fireplaceButtonY,
            null,
            "fireplaceButton",
            () -> System.out.println("Skipping time")
        );

        createButton(
            "Gears",
            pauseButtonX,
            pauseButtonY,
            null,
            "pauseButton",
            () -> {
                frontierGame.switchScreen(new PauseScreen(frontierGame, gameScreen));
                System.out.println("Opening pause menu...");
            }
        );
    }

    /**
     * Add an observer to the list of observers.
     *
     * @param observer The observer to add
     */
    public void addObserver(ButtonClickObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(GameMode gameMode) {
        for (ButtonClickObserver observer : observers) {
            observer.buttonClicked(gameMode);
        }
    }

    /**
     * Render the screen.
     *
     * @param delta The time in seconds since the last render
     */
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

    /**
     * Resize the screen.
     *
     * @param width  The width
     * @param height The height
     */
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    /**
     * Get the stage.
     *
     * @return The stage
     */
    public Stage getStage() {
        return uiStage;
    }

    /**
     * Dispose of the screen.
     */
    public void dispose() {
        uiStage.dispose();
        skin.dispose();
    }

    private TextButton createButton(
        String spriteName,
        float x,
        float y,
        GameMode gameMode,
        String name,
        Runnable onClick
    ) {
        SpriteDrawable drawable = new SpriteDrawable(atlas.createSprite(spriteName));

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = drawable;
        style.down = drawable;
        style.font = skin.getFont("ArchivoBlack");

        TextButton button = new TextButton("", style);
        button.setSize(uiViewport.getWorldWidth() * 0.03f, uiViewport.getWorldHeight() * 0.04f);
        button.setPosition(x, y);
        button.setName(name);

        button.addListener(
            new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (onClick != null) onClick.run();
                    if (gameMode != null) notifyObservers(gameMode);
                }
            }
        );

        uiStage.addActor(button);
        return button;
    }

    /**
     * Creates an input adapter that switches between building and demolishing mode.
     * @param engine    The engine instance to use for building and demolishing
     * @return          The input adapter that switches between building and demolishing mode
     */
    public InputAdapter createInputAdapter(Engine engine) {
        return new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (getGameMode() == GameMode.DEMOLISH) {
                    engine.getSystem(BuildingManagerSystem.class).removeBuilding(screenX, screenY);
                } else if (getGameMode() == GameMode.BUILDING) {
                    Entity entity = WallFactory.createDefaultWall(engine);
                    entity.getComponent(PositionComponent.class).position =
                    new Vector2(screenX, screenY);
                    engine.getSystem(BuildingManagerSystem.class).placeBuilding(entity);
                }
                return true;
            }
        };
    }
}
