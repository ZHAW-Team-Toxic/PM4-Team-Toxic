
package com.zhaw.frontier.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Disposable;

/**
 * GameUi
 */
public class GameUi implements Disposable {
    private Skin skin;

    public GameUi(Stage stage) {
        skin = new Skin(Gdx.files.internal("ui/Particle Park UI.json"));
        Table root = new Table();
        root.setFillParent(true);
        root.pad(10);
        stage.addActor(root);

        Table table = new Table();
        root.add(table).growX();

        table.defaults().space(5);
        Label label = new Label("Player Health:", skin);
        table.add(label);

        ProgressBar progressBar = new ProgressBar(0, 100, 1, false, skin);
        progressBar.setValue(50);
        table.add(progressBar).expandX().left();

        TextButton textButton = new TextButton("Menu", skin);
        table.add(textButton);

        root.row();
        table = new Table();
        table.setBackground(skin.getDrawable("highlight"));
        root.add(table).growX().expandY().bottom();

        ButtonGroup<CheckBox> buttonGroup = new ButtonGroup<>();
        table.defaults().space(20);
        CheckBox checkBox = new CheckBox("Option1", skin, "radio");
        table.add(checkBox);
        buttonGroup.add(checkBox);

        checkBox = new CheckBox("Option2", skin, "radio");
        table.add(checkBox);
        buttonGroup.add(checkBox);

        checkBox = new CheckBox("Option3", skin, "radio");
        table.add(checkBox);
        buttonGroup.add(checkBox);

        checkBox = new CheckBox("Option4", skin, "radio");
        table.add(checkBox);
        buttonGroup.add(checkBox);

        checkBox = new CheckBox("Option5", skin, "radio");
        table.add(checkBox);
        buttonGroup.add(checkBox);
    }

    @Override
    public void dispose() {
        skin.dispose();
    }

}
