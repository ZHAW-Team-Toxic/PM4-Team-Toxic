package com.zhaw.frontier.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

public class BuildingMenuUi implements Disposable  {
    
    private Skin skin;
    private TextureAtlas atlas;
    private Table rootTable;
    private boolean visible = false;

    public BuildingMenuUi(Stage stage, AssetManager assetManager) {
        skin = assetManager.get("skins/skin.json", Skin.class);
        atlas = assetManager.get("packed/textures.atlas", TextureAtlas.class);
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.bottom();
        stage.addActor(rootTable);

        createMenu();
        stage.setKeyboardFocus(rootTable);
        rootTable.setVisible(visible);
    }

     private void createMenu() {
        Table menuTable = new Table();
        menuTable.setBackground(skin.getDrawable("build_menu"));
        menuTable.defaults().pad(5);
        menuTable.defaults().fillY().uniformY();

        // --- Defense Group ---
        //menuTable.add(new Image(atlas.findRegion("building-menu/def_icon")));
        menuTable.add(new ImageButton(new TextureRegionDrawable(atlas.findRegion("demo/donkey"))));
        menuTable.add(new ImageButton(new TextureRegionDrawable(atlas.findRegion("demo/donkey"))));

        // --- Tower Group: Icon + 2 buttons side-by-side
        menuTable.add(new ImageButton(new TextureRegionDrawable(atlas.findRegion("demo/donkey"))));
        menuTable.add(new ImageButton(new TextureRegionDrawable(atlas.findRegion("demo/donkey"))));

        // --- Ressource Group: Icon + 2 buttons side-by-side
        menuTable.add(new ImageButton(new TextureRegionDrawable(atlas.findRegion("demo/donkey"))));
        menuTable.add(new ImageButton(new TextureRegionDrawable(atlas.findRegion("demo/donkey"))));

        // --- Empty Slots ---
        for (int i = 0; i < 3; i++) {
            menuTable.add(new TextButton("Slot", skin)).padRight(10);
        }

        // --- Close Button ---
        menuTable.add(new TextButton("X", skin));

        rootTable.add(menuTable);
    }

    public void toggleVisible() {
        visible = !visible;
        rootTable.setVisible(visible);
    }

    public void hide() {
        visible = false;
        rootTable.setVisible(false);
    }

    public void show() {
        visible = true;
        rootTable.setVisible(true);
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public void dispose() {
        skin.dispose();
    }

    public InputProcessor getInputProcessor() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.V) {
                    toggleVisible();
                    return true;
                }
                return false;
            }
        };
    }
}
