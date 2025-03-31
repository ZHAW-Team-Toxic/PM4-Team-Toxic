package com.zhaw.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import lombok.Setter;

public class ResourceUI {

    private Label woodlabel;
    private Label stoneLabel;
    private Label goldLabel;

    @Setter
    private int wood;

    @Setter
    private int woodIncome;

    @Setter
    private int stone;

    @Setter
    private int stoneIncome;

    @Setter
    private int gold;

    @Setter
    private int goldIncome;

    public ResourceUI(Skin skin, Stage stage) {
        // Stage für die UI
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Ressourcenanzeige oben rechts
        woodlabel = new Label("Holz: " + wood + " + " + woodIncome, skin);
        stoneLabel = new Label("Stein: " + stone + " + " + stoneIncome, skin);
        goldLabel = new Label("Gold: " + gold + " + " + goldIncome, skin);

        root.top().right().pad(10);
        root.add(woodlabel).padBottom(10);
        root.row();
        root.add(stoneLabel).padBottom(10);
        root.row();
        root.add(goldLabel).padBottom(10);
    }

    /**
     * Update the resource labels with new values.
     *
     * @param wood        The current amount of wood.
     * @param woodIncome  The income of wood.
     * @param stone       The current amount of stone.
     * @param stoneIncome The income of stone.
     * @param gold        The current amount of gold.
     * @param goldIncome  The income of gold.
     */
    public void updateResources(
        int wood,
        int woodIncome,
        int stone,
        int stoneIncome,
        int gold,
        int goldIncome
    ) {
        this.wood = wood;
        this.woodIncome = woodIncome;
        this.stone = stone;
        this.stoneIncome = stoneIncome;
        this.gold = gold;
        this.goldIncome = goldIncome;

        // Update the labels with the new values
        woodlabel.setText("Holz: " + wood + " + " + woodIncome);
        stoneLabel.setText("Stein: " + stone + " + " + stoneIncome);
        goldLabel.setText("Gold: " + gold + " + " + goldIncome);
    }

    /**********************************************************************/
    // Getter methods for the labels for testing purposes
    public Label getWoodLabel() {
        return woodlabel;
    }

    public Label getStoneLabel() {
        return stoneLabel;
    }

    public Label getGoldLabel() {
        return goldLabel;
    }
    /**********************************************************************/
}
