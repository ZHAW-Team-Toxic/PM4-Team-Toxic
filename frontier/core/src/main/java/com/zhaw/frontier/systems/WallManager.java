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

public class WallManager  {

    public static void update(Engine engine) {
        Family wallFamily = Family.all(PositionComponent.class, WallPieceComponent.class, RenderComponent.class).get();

        ImmutableArray<Entity> wallEntities = engine.getEntitiesFor(wallFamily);

        List<Entity> allWallsList = new ArrayList<>();
        for (int i = 0; i < wallEntities.size(); i++) {
            allWallsList.add(wallEntities.get(i));
        }

        for (Entity entity : allWallsList) {
            WallAdjacencyChecker.pickWallPiece(entity, allWallsList); // <<< nur diese Zeile
            Gdx.app.debug("WallManager", "Wall updated: " + entity.getComponent(WallPieceComponent.class).currentWallPiece);
        }
    }

}
