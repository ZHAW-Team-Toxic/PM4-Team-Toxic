package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.zhaw.frontier.utils.LayeredSprite;
import com.zhaw.frontier.utils.MapLayerRenderEntry;
import com.zhaw.frontier.utils.TileOffset;
import com.zhaw.frontier.utils.WorldCoordinateUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * System responsible for rendering the map and game entities.
 * <p>
 * This system renders the tiled map layers (bottom, decoration, and resource layers)
 * using an {@link OrthogonalTiledMapRenderer} and draws game entities (currently buildings)
 * with a {@link SpriteBatch}. Building entities are rendered based on their {@link PositionComponent}
 * and {@link RenderComponent} data.
 * </p>
 */
public class RenderSystem extends EntitySystem {

    private final Viewport viewport;
    private final Engine engine;
    private final OrthogonalTiledMapRenderer renderer;

    private ImmutableArray<Entity> buildings;
    private ImmutableArray<Entity> enemies;
    private Entity mapEntity;

    private final MapLayerMapper mapLayerMapper = new MapLayerMapper();

    /**
     * Constructs a new RenderSystem.
     *
     * @param viewport the {@link Viewport} used for rendering.
     * @param engine   the {@link Engine} that manages game entities.
     * @param renderer the {@link OrthogonalTiledMapRenderer} used for rendering the tiled map.
     */
    public RenderSystem(Viewport viewport, Engine engine, OrthogonalTiledMapRenderer renderer) {
        super(1);
        this.viewport = viewport;
        this.engine = engine;
        this.renderer = renderer;
    }

    /**
     * Called when the system is added to an engine.
     * <p>
     *     This method retrieves the map entity and all building entities from the engine.
     *     It initializes the map layers and buildings so they can be rendered in the update method.
     * </p>
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
        renderMapLayers();

        // Render all building entities.
        renderMultiTiledBuildings((SpriteBatch) renderer.getBatch());

        //Render all enemies
        renderEnemies((SpriteBatch) renderer.getBatch());

        // End the sprite batch.
        renderer.getBatch().end();
    }

    private void renderMapLayers() {
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
                // TODO später hier intercepten
                renderer.renderTileLayer(layer.layer);
            } else {
                Gdx.app.debug("RenderSystem", "Skipping null layer: " + layer.name);
            }
        }
    }

    /**
     * Renders building entities using the provided sprite batch.
     * <p>
     * This method iterates over all entities that have both a {@link PositionComponent} and a
     * {@link RenderComponent}. Only entities whose {@code renderType} is set to
     * {@link RenderComponent.RenderType#BUILDING} are rendered. The method calculates pixel coordinates
     * for each building and draws its sprite at that position.
     * </p>
     *
     * @param renderer the {@link SpriteBatch} used for drawing sprites.
     */
    private void renderMultiTiledBuildings(SpriteBatch renderer) {
        //for now we use the layer 10 as the animation layer
        int ANIMATION_LAYER = 10;
        for (Entity building : buildings) {
            if (
                building.getComponent(RenderComponent.class).renderType ==
                RenderComponent.RenderType.BUILDING
            ) {
                PositionComponent positionComponent = building.getComponent(
                    PositionComponent.class
                );
                RenderComponent renderComponent = building.getComponent(RenderComponent.class);
                RoundAnimationComponent roundAnimComponent = building.getComponent(
                    RoundAnimationComponent.class
                );

                Vector2 pixelCoordinate = WorldCoordinateUtils.calculatePixelCoordinateForBuildings(
                    positionComponent.basePosition.x,
                    positionComponent.basePosition.y,
                    mapEntity.getComponent(BottomLayerComponent.class).bottomLayer
                );

                for (int i = 0; i < renderComponent.widthInTiles; i++) {
                    for (int j = 0; j < renderComponent.heightInTiles; j++) {
                        TileOffset offset = new TileOffset(i, j);
                        List<LayeredSprite> layers = renderComponent.sprites.get(offset);

                        if (layers == null) continue;

                        // sort layers by z-index
                        layers.sort(Comparator.comparingInt(ls -> ls.zIndex));

                        for (LayeredSprite layer : layers) {
                            TextureRegion region = layer.region;

                            // If this is the animated layer, override with current animation frame
                            if (roundAnimComponent != null && layer.zIndex == ANIMATION_LAYER) {
                                Array<TextureRegion> animFrames = roundAnimComponent.frames.get(
                                    offset
                                );
                                if (animFrames != null && !animFrames.isEmpty()) {
                                    int clampedIndex = Math.min(
                                        roundAnimComponent.currentFrameIndex,
                                        animFrames.size - 1
                                    );
                                    region = animFrames.get(clampedIndex);
                                }
                            }

                            renderer.draw(
                                region,
                                pixelCoordinate.x + i * 16,
                                pixelCoordinate.y + j * 16,
                                region.getRegionWidth(),
                                region.getRegionHeight()
                            );
                        }
                    }
                }
            }
        }
    }

    private void renderEnemies(SpriteBatch spriteBatch) {
        for (Entity enemy : enemies) {
            if (
                enemy.getComponent(RenderComponent.class).renderType ==
                RenderComponent.RenderType.ENEMY
            ) {
                PositionComponent positionComponent = enemy.getComponent(PositionComponent.class);
                RenderComponent renderComponent = enemy.getComponent(RenderComponent.class);
                Vector2 pixelCoordinate = new Vector2(
                    positionComponent.basePosition.x,
                    positionComponent.basePosition.y
                );

                for (int i = 0; i < renderComponent.widthInTiles; i++) {
                    for (int j = 0; j < renderComponent.heightInTiles; j++) {
                        TileOffset offset = new TileOffset(i, j);
                        List<LayeredSprite> layers = renderComponent.sprites.get(offset);

                        if (layers == null) continue;

                        // sort layers by z-index
                        layers.sort(Comparator.comparingInt(ls -> ls.zIndex));

                        for (LayeredSprite layer : layers) {
                            TextureRegion region = layer.region;
                            //place sprite on tile map and also if its multi-tiled place it i or j further on the tile
                            spriteBatch.draw(
                                region,
                                (pixelCoordinate.x * 16) + i * 16,
                                (pixelCoordinate.y * 16) + j * 16,
                                region.getRegionWidth(),
                                region.getRegionHeight()
                            );
                        }
                    }
                }
            }
        }
    }
}
