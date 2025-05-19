package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;
import com.zhaw.frontier.mappers.MapLayerMapper;
import com.zhaw.frontier.utils.MapLayerRenderEntry;
import com.zhaw.frontier.utils.TileOffset;
import com.zhaw.frontier.utils.WorldCoordinateUtils;
import java.util.*;

/**
 * System responsible for rendering the map and game entities.
 * <p>
 * This system renders the tiled map layers (bottom, decoration, and resource
 * layers)
 * using an {@link OrthogonalTiledMapRenderer} and draws game entities
 * (currently buildings)
 * with a {@link SpriteBatch}. Building entities are rendered based on their
 * {@link PositionComponent}
 * and {@link RenderComponent} data.
 * </p>
 */
public class RenderSystem extends EntitySystem {

    private final Viewport viewport;
    private final OrthogonalTiledMapRenderer renderer;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private ImmutableArray<Entity> buildings;
    private ImmutableArray<Entity> enemies;
    private ImmutableArray<Entity> towers;
    private ImmutableArray<Entity> normal;
    private Entity mapEntity;
    private ComponentMapper<TextureRotationComponent> textureRotationComponentMapper =
        ComponentMapper.getFor(TextureRotationComponent.class);
    private ComponentMapper<RangeComponent> rangeComponentMapper = ComponentMapper.getFor(
        RangeComponent.class
    );

    private final MapLayerMapper mapLayerMapper = new MapLayerMapper();

    /**
     * Constructs a new RenderSystem.
     *
     * @param viewport the {@link Viewport} used for rendering.
     * @param engine   the {@link Engine} that manages game entities.
     * @param renderer the {@link OrthogonalTiledMapRenderer} used for rendering the
     *                 tiled map.
     */
    public RenderSystem(Viewport viewport, Engine engine, OrthogonalTiledMapRenderer renderer) {
        super(1);
        this.viewport = viewport;
        this.renderer = renderer;
    }

    /**
     * Called when the system is added to an engine.
     * <p>
     * This method retrieves the map entity and all building entities from the
     * engine.
     * It initializes the map layers and buildings so they can be rendered in the
     * update method.
     * </p>
     *
     * @param engine The {@link Engine} this system was added to.
     */
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        this.mapEntity = engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first();
        this.buildings =
        engine.getEntitiesFor(
            Family
                .all(
                    PositionComponent.class,
                    RenderComponent.class,
                    BuildingAnimationComponent.class
                )
                .get()
        );
        this.enemies =
        engine.getEntitiesFor(
            Family
                .all(PositionComponent.class, RenderComponent.class, EnemyAnimationComponent.class)
                .get()
        );

        this.towers =
        engine.getEntitiesFor(
            Family
                .all(PositionComponent.class, RenderComponent.class, TowerAnimationComponent.class)
                .get()
        );

        this.normal =
        engine.getEntitiesFor(
            Family
                .all(PositionComponent.class, RenderComponent.class)
                .exclude(
                    TowerAnimationComponent.class,
                    EnemyAnimationComponent.class,
                    BuildingAnimationComponent.class
                )
                .get()
        );
    }

    /**
     * Updates the render system.
     * <p>
     * This method clears the screen, applies the viewport, updates the camera view,
     * renders the map layers, and then renders all building entities.
     * </p>
     *
     * @param deltaTime the time elapsed since the last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        // Clear the screen with black.
        ScreenUtils.clear(0, 0, 0, 1);

        // Apply viewport and update camera.
        viewport.apply();
        renderer.setView((OrthographicCamera) viewport.getCamera());

        // Begin the sprite batch.
        renderer.getBatch().begin();

        // Render the tiled map layers.
        renderMapLayers((SpriteBatch) renderer.getBatch());

        // Render all building entities.
        renderAllEntities((SpriteBatch) renderer.getBatch());

        // End the sprite batch.
        renderer.getBatch().end();
    }

    private void renderTowerRange(SpriteBatch batch, RangeComponent range, Vector2 pos) {
        // Gdx.app.log("[Rendering]", "drawing range");
        batch.draw(
            range.rangeTexture,
            pos.x - (range.range / 2) + 8,
            pos.y - (range.range / 2) + 8,
            range.range,
            range.range
        );
    }

    private void renderMapLayers(SpriteBatch renderer) {
        int BOTTOM_LAYER = 0;
        int DECORATION_LAYER = 1;
        int RESOURCE_LAYER = 2;

        List<MapLayerRenderEntry> layersToRender = new ArrayList<>();

        layersToRender.add(
            new MapLayerRenderEntry(
                "bottomLayer",
                BOTTOM_LAYER,
                mapEntity.getComponent(BottomLayerComponent.class).bottomLayer
            )
        );
        layersToRender.add(
            new MapLayerRenderEntry(
                "decorationLayer",
                DECORATION_LAYER,
                mapEntity.getComponent(DecorationLayerComponent.class).decorationLayer
            )
        );
        layersToRender.add(
            new MapLayerRenderEntry(
                "resourceLayer",
                RESOURCE_LAYER,
                mapEntity.getComponent(ResourceLayerComponent.class).resourceLayer
            )
        );

        // sort with z-index
        layersToRender.sort(Comparator.comparingInt(l -> l.zIndex));

        // Render alle Layers
        for (MapLayerRenderEntry layer : layersToRender) {
            if (layer.layer != null) {
                for (int i = layer.layer.getWidth(); i >= 0; i--) {
                    for (int j = layer.layer.getHeight(); j >= 0; j--) {
                        // Get the tile at the current position
                        if (layer.layer.getCell(i, j) == null) continue;

                        // Render the tile at the specified position
                        renderer.draw(
                            layer.layer.getCell(i, j).getTile().getTextureRegion(),
                            i * 16,
                            j * 16,
                            layer.layer.getCell(i, j).getTile().getTextureRegion().getRegionWidth(),
                            layer.layer.getCell(i, j).getTile().getTextureRegion().getRegionHeight()
                        );
                    }
                }
            } else {
                Gdx.app.debug("RenderSystem", "Skipping null layer: " + layer.name);
            }
        }
    }

    private void renderAllEntities(SpriteBatch batch) {
        Array<Entity> combined = new Array<>();

        towers.forEach(e -> combined.add(e));
        buildings.forEach(e -> combined.add(e));
        enemies.forEach(e -> combined.add(e));
        normal.forEach(e -> combined.add(e));

        combined.sort(
            Comparator
                .comparingDouble(entity -> {
                    PositionComponent pos = ((Entity) entity).getComponent(PositionComponent.class);
                    float pixelCoord = WorldCoordinateUtils.calculateWorldCoordinate(
                        viewport,
                        mapEntity.getComponent(BottomLayerComponent.class).bottomLayer,
                        pos.basePosition.x,
                        pos.basePosition.y
                    )
                        .y;
                    return pixelCoord + pos.heightInTiles;
                })
                .thenComparingInt(entity -> {
                    RenderComponent render = ((Entity) entity).getComponent(RenderComponent.class);
                    return render.zIndex;
                })
        );

        for (Entity entity : combined) {
            RenderComponent render = entity.getComponent(RenderComponent.class);
            PositionComponent pos = entity.getComponent(PositionComponent.class);
            Vector2 basePixel = new Vector2();
            if (
                render.renderType == RenderComponent.RenderType.BUILDING ||
                render.renderType == RenderComponent.RenderType.TOWER
            ) {
                basePixel =
                WorldCoordinateUtils.calculatePixelCoordinateForBuildings(
                    pos.basePosition.x,
                    pos.basePosition.y,
                    mapEntity.getComponent(BottomLayerComponent.class).bottomLayer
                );
            }

            if (render.renderType == RenderComponent.RenderType.ENEMY) {
                basePixel = new Vector2(pos.basePosition.x * 16, pos.basePosition.y * 16);
            }

            if (render.renderType == RenderComponent.RenderType.NORMAL) {
                basePixel = new Vector2(pos.basePosition.x * 16, pos.basePosition.y * 16);
            }

            float rotation = 0;
            var rotationComponent = textureRotationComponentMapper.get(entity);
            if (rotationComponent != null) {
                rotation = rotationComponent.rotation;
            }

            // draw range for tower
            var range = rangeComponentMapper.get(entity);
            if (range != null) {
                renderTowerRange(batch, range, basePixel);
            }

            for (int i = 0; i < render.widthInTiles; i++) {
                for (int j = render.heightInTiles - 1; j >= 0; j--) {
                    TileOffset offset = new TileOffset(i, j);
                    TextureRegion region = render.sprites.get(offset);

                    float drawX = basePixel.x + i * 16;
                    float drawY = basePixel.y + j * 16;

                    batch.draw(
                        region,
                        drawX,
                        drawY,
                        8,
                        8,
                        region.getRegionWidth(),
                        region.getRegionHeight(),
                        1,
                        1,
                        rotation
                    );
                }
            }

            HealthBarManager.drawHealthBar(batch, entity, getEngine());
        }
    }

    public void drawGridWithTempPixel(
        SpriteBatch batch,
        int mapWidthInTiles,
        int mapHeightInTiles,
        int tileSize,
        Color color
    ) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1); // volle Deckkraft
        pixmap.fill();
        Texture tempPixel = new Texture(pixmap);
        pixmap.dispose(); // Blending aktivieren, falls nicht bereits an
        batch.enableBlending();
        Color oldColor = batch.getColor();
        batch.setColor(color); // z. B. new Color(0, 0, 0, 0.3f)
        float thickness = 1f;
        for (int x = 0; x <= mapWidthInTiles; x++) {
            float drawX = x * tileSize;
            batch.draw(tempPixel, drawX, 0, thickness, mapHeightInTiles * tileSize);
        }
        for (int y = 0; y <= mapHeightInTiles; y++) {
            float drawY = y * tileSize;
            batch.draw(tempPixel, 0, drawY, mapWidthInTiles * tileSize, thickness);
        }
        batch.setColor(oldColor);
        tempPixel.dispose();
    }
}
