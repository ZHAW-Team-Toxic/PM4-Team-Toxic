package com.zhaw.frontier.systems;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;

/**
 * Error System responsible for notifications on the game when the player performs an invalid action.
 * Such an invalid action may be: already occupied tile, wrong position, not enough resources, etc.
 */
public class ErrorSystem {

    private static ErrorSystem instance;

    private final Skin skin;
    private final Stage stage;
    private Dialog activeDialog;

    private ErrorSystem(Stage stage, Skin skin) {
        if (instance != null) {
            throw new IllegalStateException("ErrorSystem already initialized");
        }
        this.stage = stage;
        this.skin = skin;
    }

    /**
     * Initializes the singleton instance.
     */
    public static void init(Stage stage, Skin skin) {
        if (instance == null) {
            instance = new ErrorSystem(stage, skin);
        }
    }

    /**
     * Returns the singleton instance. Call init() first!
     */
    public static ErrorSystem getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ErrorPopup not initialized");
        }
        return instance;
    }

    private void show(String message) {
        /*
        Current workaround to prevent the resource and placement tests from failing (ErrorSystem not initialized)
         */
        if (stage != null) {
            if (activeDialog != null && activeDialog.hasParent()) {
                activeDialog.hide();
            }

            activeDialog =
            new Dialog("", skin) {
                @Override
                protected void result(Object object) {}
            };

            Table contentTable = new Table(skin);
            contentTable.setBackground(skin.getDrawable("build_menu_96_32"));

            Label messageLabel = new Label(message, skin);
            contentTable.add(messageLabel).pad(10).row();

            TextButton okButton = new TextButton("OK", skin);
            contentTable.add(okButton).pad(10);

            okButton.addListener(event -> {
                activeDialog.hide();
                return true;
            });

            activeDialog.getContentTable().add(contentTable);
            activeDialog.show(stage);
        }
    }

    /**
     * Shows error message because the player does not have enough resources to build
     */
    public void showNotEnoughResources() {
        show("Not enough resources to build");
    }

    /**
     * Shows error message because the player cannot build here
     */
    public void showCannotBuildHere() {
        show("Cannot build resource here");
    }

    /**
     * Shows error message because the player is trying to build on an already occupied tile
     */
    public void showIsOccupied() {
        show("Tile is already occupied");
    }
}
