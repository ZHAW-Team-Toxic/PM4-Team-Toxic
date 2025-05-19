package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RangeComponent;
import com.zhaw.frontier.components.TowerComponent;
import com.zhaw.frontier.entityFactories.TowerFactory;
import com.zhaw.frontier.utils.WorldCoordinateUtils;

/**
 * SelectSystem
 */
public class RangeVisualSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private TiledMapTileLayer layer;

    public RangeVisualSystem(TiledMapTileLayer layer) {
        this.layer = layer;
    }

    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(
        PositionComponent.class
    );

    private final ComponentMapper<RangeComponent> rm = ComponentMapper.getFor(RangeComponent.class);

    public void addedToEngine(Engine engine) {
        entities =
        engine.getEntitiesFor(Family.all(PositionComponent.class, TowerComponent.class).get());
    }

    public InputAdapter clickListener(Viewport viewport) {
        return new InputAdapter() {
            /**
             * Called when a touch/click is first detected.
             * <p>
             * If the left mouse button is pressed, the screen coordinates are converted
             * to world coordinates and stored as the last touch position.
             * </p>
             *
             * @param screenX the x-coordinate on the screen.
             * @param screenY the y-coordinate on the screen.
             * @param pointer the pointer for the event.
             * @param button  the mouse button that was pressed.
             * @return true if the event was handled, false otherwise.
             */
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.RIGHT) {
                    var clickPos = WorldCoordinateUtils.calculateWorldCoordinate(
                        viewport,
                        layer,
                        (float) screenX,
                        (float) screenY
                    );
                    for (Entity entity : entities) {
                        var pos = pm.get(entity);
                        Rectangle rec = new Rectangle(
                            pos.basePosition.x,
                            pos.basePosition.y,
                            pos.widthInTiles,
                            pos.heightInTiles
                        );
                        if (rec.contains(clickPos)) {
                            toggleRange(entity);
                            return true;
                        }
                    }
                }
                return false;
            }
        };
    }

    private void toggleRange(Entity entity) {
        var rc = rm.get(entity);
        if (rc == null) {
            var newRc = TowerFactory.createRangeComponent();
            entity.add(newRc);
        } else {
            entity.remove(RangeComponent.class);
        }
    }
}
