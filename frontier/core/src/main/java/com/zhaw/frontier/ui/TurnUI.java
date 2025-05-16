package com.zhaw.frontier.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.zhaw.frontier.enums.GamePhase;
import com.zhaw.frontier.systems.TurnSystem;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.TurnChangeListener;

public class TurnUI implements TurnChangeListener, Disposable {

    private final Table rootTable;
    private final Skin skin;

    private final Label turnLabel;
    private final Label phaseLabel;
    private final Texture backgroundTexture; // for disposing

    public TurnUI(Stage stage) {
        this.skin = AssetManagerInstance.getManager().get("skins/skin.json", Skin.class);

        // Labels
        turnLabel = new Label("Turn: 1", skin);
        phaseLabel = new Label("Phase: Build and Plan", skin);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1, 1, 1, 0.7f)); // white with 70% opacity
        pixmap.fill();
        backgroundTexture = new Texture(pixmap);
        pixmap.dispose(); // pixmap can be disposed after creating texture

        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(backgroundTexture);

        // Centered bar with background
        Table barTable = new Table();
        barTable.setBackground(backgroundDrawable);
        barTable.pad(6);
        barTable.add(turnLabel).padRight(15);
        barTable.add(phaseLabel);

        // Root layout to position it at top center
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top();
        rootTable.add(barTable).center().padTop(10); // center horizontally

        stage.addActor(rootTable);

        // Register for turn change updates
        TurnSystem.getInstance().addListener(this);
    }

    @Override
    public void dispose() {
        TurnSystem.getInstance().removeListener(this);
        backgroundTexture.dispose();
    }

    @Override
    public void onTurnChanged(int turn, GamePhase phase) {
        turnLabel.setText("Turn: " + turn);
        phaseLabel.setText("Phase: " + phase.getDisplayText());
    }
}
