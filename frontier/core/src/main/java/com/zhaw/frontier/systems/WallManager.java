package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.WallPieceComponent;
import java.util.ArrayList;
import java.util.List;

/**
 * System responsible for managing and updating wall entities based on adjacency logic.
 * <p>
 * This system evaluates each wall entity and determines the correct visual representation
 * (WallPiece) depending on the presence of neighboring wall entities (north, south, east, west).
 * It delegates the actual pattern detection to the {@link WallAdjacencyChecker}.
 * </p>
 */
public class WallManager {

    /**
     * Updates all wall entities in the engine by re-evaluating their correct wall piece type.
     * <p>
     * The method collects all wall entities and applies adjacency logic to determine
     * whether each wall should be rendered as a single piece, straight, corner, T-junction, or cross.
     * </p>
     *
     * @param engine the {@link Engine} containing all wall entities to evaluate
     */
    public static void update(Engine engine) {
        Family wallFamily = Family
            .all(PositionComponent.class, WallPieceComponent.class, RenderComponent.class)
            .get();

        ImmutableArray<Entity> wallEntities = engine.getEntitiesFor(wallFamily);

        List<Entity> allWallsList = new ArrayList<>();
        for (int i = 0; i < wallEntities.size(); i++) {
            allWallsList.add(wallEntities.get(i));
        }

        for (Entity entity : allWallsList) {
            WallAdjacencyChecker.pickWallPiece(entity, allWallsList);
            Gdx.app.debug(
                "WallManager",
                "Wall updated: " + entity.getComponent(WallPieceComponent.class).currentWallPiece
            );
        }
    }
}
