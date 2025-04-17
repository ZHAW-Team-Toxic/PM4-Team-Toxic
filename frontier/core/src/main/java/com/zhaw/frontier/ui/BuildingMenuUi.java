package com.zhaw.frontier.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.entityFactories.BuildableFactory;
import com.zhaw.frontier.entityFactories.ResourceBuildingFactory;
import com.zhaw.frontier.entityFactories.TowerFactory;
import com.zhaw.frontier.entityFactories.WallFactory;
import com.zhaw.frontier.enums.GameMode;
import com.zhaw.frontier.systems.BuildingManagerSystem;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.ButtonClickObserver;
import java.util.HashMap;
import java.util.Map;

public class BuildingMenuUi implements Disposable, ButtonClickObserver {

    private Skin skin;
    private TextureAtlas atlas;
    private Table rootTable;
    private boolean visible = false;
    private Engine engine;
    private ButtonGroup<ImageButton> buttons = new ButtonGroup<ImageButton>();
    private Map<ImageButton, BuildableFactory> buttonFactoryMap = new HashMap<ImageButton, BuildableFactory>();
    private Viewport viewport;

    private final Array<ButtonClickObserver> observers = new Array<>();

    public BuildingMenuUi(Engine engine, Stage stage) {
        this.engine = engine;
        this.viewport = stage.getViewport();
        skin = AssetManagerInstance.getManager().get("skins/skin.json", Skin.class);
        atlas = AssetManagerInstance.getManager().get("packed/textures.atlas", TextureAtlas.class);
        rootTable = new Table();
        rootTable.clear();
        rootTable.setFillParent(true);
        rootTable.bottom();
        stage.addActor(rootTable);

        createMenu();
        stage.setKeyboardFocus(rootTable);
        rootTable.setVisible(visible);
    }

    private void createMenu() {
        Table menuTable = new Table();
        menuTable.setBackground(skin.getDrawable("build_menu_96_32"));
        menuTable.setFillParent(false);
        menuTable.pad(10);
        menuTable.bottom();
        menuTable.defaults().pad(5);

        // === Top row: close button aligned right ===
        Table closeRow = new Table();
        closeRow.add().expandX(); // spacer
        TextButton closeButton = new TextButton("X", skin);
        closeButton.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        notifyObservers(GameMode.NORMAL);
                        hide();
                    }
                });
        closeRow.add(closeButton).top().right().padTop(10).padRight(10);

        menuTable.add(closeRow).expandX().fillX().row();

        // === Groups ===
        Table groupRow = new Table();

        Table towerGroup = createGroupTable("Towers");
        createTowerButtons(buttons, towerGroup);
        groupRow.add(towerGroup).pad(10);

        Table resourceGroup = createGroupTable("Resources");
        createRessourceButtons(buttons, resourceGroup);
        groupRow.add(resourceGroup).pad(10);

        Table wallGroup = createGroupTable("Walls");
        createWallButtons(buttons, wallGroup);
        groupRow.add(wallGroup).pad(10);

        menuTable.add(groupRow).expandX().center();

        // === Add menuTable to rootTable at the bottom, and stretch it ===
        rootTable.add(menuTable).width(viewport.getWorldWidth() * 0.3f).bottom().padBottom(10);
    }

    public void show() {
        visible = true;
        rootTable.setVisible(true);
        buttons.uncheckAll();
    }

    public void hide() {
        visible = false;
        rootTable.setVisible(false);
        buttons.uncheckAll();
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public void dispose() {
        skin.dispose();
    }

    public int getTotalButtonCount() {
        return buttons.getButtons().size;
    }

    @Override
    public void buttonClicked(GameMode gameMode) {
        if (GameMode.BUILDING == gameMode) {
            show();
        } else {
            hide();
        }
    }

    public void addObserver(ButtonClickObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(GameMode mode) {
        for (ButtonClickObserver observer : observers) {
            observer.buttonClicked(mode);
        }
    }

    private Table createGroupTable(String title) {
        Table groupTable = new Table();
        groupTable.top();
        groupTable.setSkin(skin);
        groupTable.add(title).colspan(99).padBottom(5).row(); // Title above buttons
        return groupTable;
    }

    private void createTowerButtons(ButtonGroup<ImageButton> allButtons, Table groupTable) {
        ImageButton btn1 = createImageButton(TowerFactory::createDefaultTower,
                atlas.findRegion("buildings/Tower/Wood_Tower1"));
        ImageButton btn2 = createImageButton(TowerFactory::createDefaultTower);
        allButtons.add(btn1, btn2);
        groupTable.add(toSizedContainer(btn1, 64, 128)).pad(2);
        groupTable.add(toContainer(btn2)).pad(2);
    }

    private void createRessourceButtons(ButtonGroup<ImageButton> allButtons, Table groupTable) {
        ImageButton wood = createImageButton(ResourceBuildingFactory::woodResourceBuilding);
        ImageButton stone = createImageButton(ResourceBuildingFactory::stoneResourceBuilding);
        ImageButton iron = createImageButton(ResourceBuildingFactory::ironResourceBuilding);
        allButtons.add(wood, stone, iron);
        groupTable.add(toContainer(wood)).pad(2);
        groupTable.add(toContainer(stone)).pad(2);
        groupTable.add(toContainer(iron)).pad(2);
    }

    private void createWallButtons(ButtonGroup<ImageButton> allButtons, Table groupTable) {
        ImageButton wall1 = createImageButton(WallFactory::createWoodWall);
        ImageButton wall2 = createImageButton(WallFactory::createStoneWall);
        allButtons.add(wall1, wall2);
        groupTable.add(toContainer(wall1)).pad(2);
        groupTable.add(toContainer(wall2)).pad(2);
    }

    private ImageButton createImageButton(BuildableFactory buildableFactory, TextureRegion buttonImage) {
        ImageButtonStyle style = new ImageButtonStyle();
        TextureRegionDrawable init = new TextureRegionDrawable(buttonImage);
        style.up = init;
        ImageButton button = new ImageButton(style);
        buttonFactoryMap.putIfAbsent(button, buildableFactory);
        return button;
    }

    private ImageButton createImageButton(BuildableFactory buildableFactory) {
        ImageButtonStyle style = new ImageButtonStyle();
        TextureRegionDrawable init = new TextureRegionDrawable(atlas.findRegion("demo/donkey"));
        style.up = init;
        ImageButton button = new ImageButton(style);
        buttonFactoryMap.putIfAbsent(button, buildableFactory);
        return button;
    }

    private Container<ImageButton> toContainer(ImageButton imageButton) {
        return toSizedContainer(imageButton, 64, 64);
    }

    private Container<ImageButton> toSizedContainer(ImageButton imageButton, int width, int height) {
        Container<ImageButton> container = new Container<>(imageButton);
        container.size(width, height).pad(5);
        imageButton.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (imageButton.isChecked()) {
                            container.setBackground(skin.getDrawable("selected"));
                        } else {
                            container.setBackground((Drawable) null); // remove border
                        }
                    }
                });
        return container;
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
                if (visible) {
                    ImageButton imageButton = buttons.getChecked();
                    BuildableFactory selectedFactory = buttonFactoryMap.get(imageButton);
                    if (selectedFactory != null) {
                        engine
                                .getSystem(BuildingManagerSystem.class)
                                .placeBuilding(selectedFactory.create(engine, screenX, screenY));
                        return true;
                    }
                    return false;
                }
                return false;
            }
        };
    }
}
