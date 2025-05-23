package com.zhaw.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.zhaw.frontier.configs.AppProperties;
import com.zhaw.frontier.enums.GamePhase;
import com.zhaw.frontier.systems.TurnSystem;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.TurnChangeListener;

public class TurnUI implements TurnChangeListener, Disposable {

    private final Table rootTable;
    private final Skin skin;

    private final Label turnLabel;
    private final Label phaseLabel;
    private static final String divider = "  ";

    public TurnUI(Stage stage) {
        this.skin = AssetManagerInstance.getManager().get(AppProperties.SKIN_PATH, Skin.class);

        // Labels
        turnLabel = new Label("Turn: 1", skin);
        phaseLabel = new Label(divider + "Phase: Build and Plan", skin);

        // Centered bar with background
        Table barTable = new Table();
        barTable.setBackground(skin.getDrawable("white_bg_32_32"));
        barTable.pad(6);
        barTable.add(turnLabel).padRight(15).pad(5);
        barTable.add(phaseLabel).pad(5);

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
    }

    @Override
    public void onTurnChanged(int turn, GamePhase phase) {
        turnLabel.setText("Turn: " + turn);
        phaseLabel.setText(divider + "Phase: " + phase.getDisplayText());
    }
}
