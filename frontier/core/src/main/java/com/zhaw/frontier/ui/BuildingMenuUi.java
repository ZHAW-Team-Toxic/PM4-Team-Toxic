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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.configs.AppProperties;
import com.zhaw.frontier.entityFactories.BuildableFactory;
import com.zhaw.frontier.entityFactories.ResourceBuildingFactory;
import com.zhaw.frontier.entityFactories.TowerFactory;
import com.zhaw.frontier.entityFactories.WallFactory;
import com.zhaw.frontier.enums.GameMode;
import com.zhaw.frontier.systems.building.BuildingManagerSystem;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.ButtonClickObserver;
import com.zhaw.frontier.utils.EngineHelper;
import java.util.HashMap;
import java.util.Map;

public class BuildingMenuUi implements Disposable, ButtonClickObserver {

    private Skin skin;
    private TextureAtlas atlas;
    private Table rootTable;
    private boolean visible = false;
    private Engine engine;
    private ButtonGroup<ImageButton> buttons = new ButtonGroup<ImageButton>();
    private Map<ImageButton, BuildableFactory> buttonFactoryMap = new HashMap<
        ImageButton,
        BuildableFactory
    >();
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
        menuTable.pad(20);
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
            }
        );
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
        rootTable.add(menuTable).width(viewport.getWorldWidth() * 0.4f).bottom().padBottom(10);
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
        if (GameMode.BUILDING == gameMode && !visible) {
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
        ImageButton btn1 = createImageButton(
            TowerFactory::createDefaultTower,
            atlas.findRegion("Wood_Tower1")
        );
        allButtons.add(btn1);
        groupTable
            .add(toSizedContainer(btn1, 64, 128, AppProperties.WOOD_TOWER_PRICE + " W"))
            .pad(2);
    }

    private void createRessourceButtons(ButtonGroup<ImageButton> allButtons, Table groupTable) {
        ImageButton wood = createImageButton(
            ResourceBuildingFactory::woodResourceBuilding,
            atlas.findRegion("wood_resource_building_ui")
        );
        ImageButton stone = createImageButton(
            ResourceBuildingFactory::stoneResourceBuilding,
            atlas.findRegion("stone_resource_building_ui")
        );
        ImageButton iron = createImageButton(
            ResourceBuildingFactory::ironResourceBuilding,
            atlas.findRegion("iron_resource_building_ui")
        );

        allButtons.add(wood, stone, iron);
        groupTable.add(toContainer(wood, AppProperties.WOOD_RESOURCE_BUILDING_PRICE + " W")).pad(2);
        groupTable
            .add(toContainer(stone, AppProperties.STONE_RESOURCE_BUILDING_PRICE + " S"))
            .pad(2);
        groupTable.add(toContainer(iron, AppProperties.IRON_RESOURCE_BUILDING_PRICE + " I")).pad(2);
    }

    private void createWallButtons(ButtonGroup<ImageButton> allButtons, Table groupTable) {
        ImageButton woodWall = createImageButton(
            WallFactory::createWoodWall,
            atlas.findRegion("wall_wood_single")
        );
        ImageButton stoneWall = createImageButton(
            WallFactory::createStoneWall,
            atlas.findRegion("wall_stone_single")
        );
        ImageButton ironWall = createImageButton(
            WallFactory::createIronWall,
            atlas.findRegion("wall_iron_single")
        );

        allButtons.add(woodWall, stoneWall, ironWall);
        groupTable.add(toContainer(woodWall, AppProperties.WOOD_WALL_PRICE + " W")).pad(2);
        groupTable.add(toContainer(stoneWall, AppProperties.STONE_WALL_PRICE + " S")).pad(2);
        groupTable.add(toContainer(ironWall, AppProperties.IRON_WALL_PRICE + " I")).pad(2);
    }

    private ImageButton createImageButton(
        BuildableFactory buildableFactory,
        TextureRegion buttonImage
    ) {
        ImageButtonStyle style = new ImageButtonStyle();
        TextureRegionDrawable init = new TextureRegionDrawable(buttonImage);
        style.up = init;
        ImageButton button = new ImageButton(style);
        buttonFactoryMap.putIfAbsent(button, buildableFactory);
        return button;
    }

    private Container<Table> toContainer(ImageButton imageButton, String priceText) {
        return toSizedContainer(imageButton, 64, 64, priceText);
    }

    private Container<Table> toSizedContainer(
        ImageButton imageButton,
        int width,
        int height,
        String priceText
    ) {
        Table containerTable = new Table();
        containerTable.top();

        Container<ImageButton> imageContainer = new Container<>(imageButton);
        imageContainer.size(width, height).pad(5);

        imageButton.addListener(
            new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (imageButton.isChecked()) {
                        imageContainer.setBackground(skin.getDrawable("selected"));
                    } else {
                        imageContainer.setBackground((Drawable) null); // remove border
                    }
                }
            }
        );

        containerTable.add(imageContainer).row();
        containerTable.add(new Label(priceText, skin)).padTop(2).center();

        return new Container<>(containerTable);
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
                            .placeBuilding(
                                selectedFactory.create(engine, screenX, screenY),
                                EngineHelper.getInventoryComponent(engine)
                            );
                        return true;
                    }
                    return false;
                }
                return false;
            }
        };
    }
}
