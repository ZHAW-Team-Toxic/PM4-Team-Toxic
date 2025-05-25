package com.zhaw.frontier.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.enums.GameMode;
import com.zhaw.frontier.enums.GamePhase;
import com.zhaw.frontier.screens.GameScreen;
import com.zhaw.frontier.screens.PauseScreen;
import com.zhaw.frontier.systems.*;
import com.zhaw.frontier.systems.building.BuildingManagerSystem;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.ButtonClickObserver;
import com.zhaw.frontier.utils.EngineHelper;
import com.zhaw.frontier.utils.TurnChangeListener;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import lombok.Getter;
import lombok.Setter;

/**
 * BaseUIScreen provides a basic interface for the game that allows you to
 * switch between different game modes.
 * The game modes are represented by buttons that are displayed on the screen.
 * The buttons include: demolish, build, fireplace, and pause.
 */
public class BaseUI implements ButtonClickObserver, TurnChangeListener {

    private Array<ButtonClickObserver> observers = new Array<>();
    private Stage uiStage;
    private Viewport uiViewport;
    private Skin skin;
    private TextureAtlas atlas;

    @Setter
    @Getter
    private GameMode gameMode = GameMode.NORMAL;

    private TextButton buildButton;
    private TextButton demolishButton;
    private TextButton fireplaceButton;

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

        skin = AssetManagerInstance.getManager().get("skins/skin.json", Skin.class);
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

        demolishButton =
        createButton(
            "BrokenPickaxe",
            demolishButtonX,
            demolishButtonY,
            GameMode.DEMOLISH,
            "demolishButton",
            () -> System.out.println("Demolish button was clicked!"),
            "demolish"
        );

        buildButton =
        createButton(
            "Pickaxe",
            buildButtonX,
            buildButtonY,
            GameMode.BUILDING,
            "buildButton",
            () -> System.out.println("Build button was clicked!"),
            "build"
        );

        ButtonGroup<TextButton> modeGroup = new ButtonGroup<>(buildButton, demolishButton);
        modeGroup.setMaxCheckCount(1);
        modeGroup.setMinCheckCount(0);
        modeGroup.setUncheckLast(true);

        fireplaceButton =
        createButton(
            "Campfire",
            fireplaceButtonX,
            fireplaceButtonY,
            GameMode.NORMAL,
            "fireplaceButton",
            () -> {
                TurnSystem.getInstance().advanceTurn();
                demolishButton.setChecked(false);
                buildButton.setChecked(false);
                buildButton.setDisabled(true);
                demolishButton.setDisabled(true);
                fireplaceButton.setDisabled(true);
            },
            "campfire"
        );

        TextButton pauseButton = createButton(
            "Gears",
            pauseButtonX,
            pauseButtonY,
            null,
            "pauseButton",
            () -> {
                buildButton.setChecked(false);
                demolishButton.setChecked(false);
                frontierGame.switchScreen(new PauseScreen(frontierGame, gameScreen));
                System.out.println("Opening pause menu...");
            },
            null
        );

        TurnSystem.getInstance().addListener(this);
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
    }

    private TextButton createButton(
        String spriteName,
        float x,
        float y,
        GameMode gameMode,
        String name,
        Runnable onClick,
        String styleName // Optional: Name of style in skin (can be null)
    ) {
        TextButton button;

        if (styleName != null && skin.has(styleName, TextButton.TextButtonStyle.class)) {
            button = new TextButton("", skin, styleName);
        } else {
            // Fallback: use drawable from sprite
            SpriteDrawable drawable = new SpriteDrawable(atlas.createSprite(spriteName));
            TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
            style.up = drawable;
            style.down = drawable;
            style.font = skin.getFont("ArchivoBlack");
            button = new TextButton("", style);
        }

        button.setSize(uiViewport.getWorldWidth() * 0.03f, uiViewport.getWorldHeight() * 0.04f);
        button.setPosition(x, y);
        button.setName(name);

        button.addListener(
            new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (onClick != null) onClick.run();

                    if (gameMode != null) {
                        notifyObservers(gameMode);
                        setGameMode(gameMode);
                    }
                }
            }
        );

        uiStage.addActor(button);
        return button;
    }

    /**
     * Creates an input adapter that switches between building and demolishing mode.
     *
     * @param engine The engine instance to use for building and demolishing
     * @return The input adapter that switches between building and demolishing mode
     */
    public InputAdapter createInputAdapter(Engine engine) {
        return new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (getGameMode() == GameMode.DEMOLISH) {
                    engine
                        .getSystem(BuildingManagerSystem.class)
                        .removeBuilding(
                            screenX,
                            screenY,
                            EngineHelper.getInventoryComponent(engine)
                        );
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * Unchecks the buildButton when the BuildingMenuUi is closed using the "X" at
     * the corner
     *
     * @param newMode New mode after the BuildingMenuUi is closed
     */
    public void buttonClicked(GameMode newMode) {
        if (newMode == GameMode.NORMAL && gameMode == GameMode.BUILDING) {
            buildButton.setChecked(false);
        }
        gameMode = newMode;
    }

    @Override
    public void onTurnChanged(int turn, GamePhase phase) {
        if (phase == GamePhase.BUILD_AND_PLAN) {
            buildButton.setDisabled(false);
            demolishButton.setDisabled(false);
            fireplaceButton.setDisabled(false);
        }
    }
}
