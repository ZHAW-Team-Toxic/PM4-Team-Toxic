package com.zhaw.frontier.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.zhaw.frontier.entityFactories.BuildableFactory;
import com.zhaw.frontier.entityFactories.ResourceBuildingFactory;
import com.zhaw.frontier.entityFactories.TowerFactory;
import com.zhaw.frontier.entityFactories.WallFactory;
import com.zhaw.frontier.util.ButtonClickObserver;
import com.zhaw.frontier.util.GameMode;

public class BuildingMenuUi implements Disposable, ButtonClickObserver {
    
    private Skin skin;
    private TextureAtlas atlas;
    private Table rootTable;
    private boolean visible = false;
    private Engine engine; 

    public BuildingMenuUi(Engine engine, Stage stage, AssetManager assetManager) {
        this.engine = engine;
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
        menuTable.setHeight(100);
        //menuTable.setBackground(skin.getDrawable("build_menu"));
        menuTable.defaults().pad(5);
        menuTable.defaults().fillY().uniformY();

        createButtonGroups(menuTable);

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

    @Override
    public void buttonClicked(GameMode gameMode) {
        if (GameMode.BUILDING == gameMode) {
            toggleVisible();
        } else {
            hide();
        }
    }

    private void createButtonGroups(Table menuTable) {
        ButtonGroup<ImageButton> allButtons = new ButtonGroup<ImageButton>();
        allButtons.setMaxCheckCount(1);
        allButtons.setMinCheckCount(0);
        allButtons.setUncheckLast(true);
      
        createTowerButtons(allButtons);
        createRessourceButtons(allButtons);
        createWallButtons(allButtons);

        allButtons.getButtons().forEach(button -> {
            menuTable.add(button).fillY().pad(5);
        });
    }

    private void createTowerButtons(ButtonGroup<ImageButton> allButtons) {
        allButtons.add(createImageButton(TowerFactory::createDefaultTower));
        allButtons.add(createImageButton(TowerFactory::createDefaultTower));
    }

    private void createRessourceButtons(ButtonGroup<ImageButton> allButtons) {
        allButtons.add(createImageButton(ResourceBuildingFactory::createDefaultResourceBuilding));
        allButtons.add(createImageButton(ResourceBuildingFactory::createDefaultResourceBuilding));
    }

    private void createWallButtons(ButtonGroup<ImageButton> allButtons) {
        allButtons.add(createImageButton(WallFactory::createDefaultWall));
        allButtons.add(createImageButton(WallFactory::createDefaultWall));
    }

    private ImageButton createImageButton(BuildableFactory buildableFactory) {
        ImageButton button = new ImageButton(new TextureRegionDrawable(atlas.findRegion("demo/donkey")));
        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Create the building entity using the factory method
                engine.addEntity(buildableFactory.create(engine));
            }
        });
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
                if (visible) {
                    
                }
                return true;
            }
        };
    }
}
