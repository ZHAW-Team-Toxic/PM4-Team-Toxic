package com.zhaw.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PauseScreenUI {

    public PauseScreenUI(
        Stage stage,
        Skin skin,
        Runnable onResume,
        Runnable onSave,
        Runnable onSaveExit
    ) {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton resumeButton = new TextButton("Resume", skin);
        TextButton saveButton = new TextButton("Save", skin);
        TextButton saveExitButton = new TextButton("Save & Exit", skin);

        resumeButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(
                    com.badlogic.gdx.scenes.scene2d.InputEvent event,
                    float x,
                    float y
                ) {
                    onResume.run();
                }
            }
        );

        saveButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(
                    com.badlogic.gdx.scenes.scene2d.InputEvent event,
                    float x,
                    float y
                ) {
                    onSave.run();
                }
            }
        );

        saveExitButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(
                    com.badlogic.gdx.scenes.scene2d.InputEvent event,
                    float x,
                    float y
                ) {
                    onSave.run();
                    onSaveExit.run();
                }
            }
        );

        table.add(resumeButton).fillX().pad(10);
        table.row();
        table.add(saveButton).fillX().pad(10);
        table.row();
        table.add(saveExitButton).fillX().pad(10);
    }
}
