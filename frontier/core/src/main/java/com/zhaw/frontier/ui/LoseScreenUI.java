package com.zhaw.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LoseScreenUI {

    public LoseScreenUI(Stage stage, Skin skin, int enemiesKilled, Runnable openStartScreen) {
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label loseLabel = new Label("Game Over", skin, "default");
        Label killStats = new Label("Enemies defeated: " + enemiesKilled, skin);
        TextButton backToMenu = new TextButton("Back to Menu", skin);

        backToMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openStartScreen.run();
            }
        });

        table.add(loseLabel).padBottom(30).row();
        table.add(killStats).padBottom(30).row();
        table.add(backToMenu).width(300).height(60);

        stage.addActor(table);
    }
}
