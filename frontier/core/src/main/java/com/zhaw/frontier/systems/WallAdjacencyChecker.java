package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.WallPieceComponent;
import com.zhaw.frontier.utils.TileOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class responsible for analyzing wall adjacency.
 * <p>
 * Determines the correct {@link WallPieceComponent.WallPiece} type for a wall entity
 * based on its immediate neighbors in the four cardinal directions (north, south, east, west).
 * Diagonal connections are ignored.
 * </p>
 */
public class WallAdjacencyChecker {

    /**
     * Updates the {@link WallPieceComponent} and {@link RenderComponent} of the given wall entity
     * based on its adjacency to other wall entities.
     *
     * @param entity    the wall entity to update
     * @param allWalls  a list of all current wall entities in the world
     */
    public static void pickWallPiece(Entity entity, List<Entity> allWalls) {
        if (entity == null || entity.getComponent(WallPieceComponent.class) == null) {
            return;
        }

        WallPieceComponent wallPiece = entity.getComponent(WallPieceComponent.class);
        wallPiece.currentWallPiece = determineWallPiece(entity, allWalls);

        RenderComponent renderComponent = entity.getComponent(RenderComponent.class);
        HashMap<TileOffset, TextureRegion> original = wallPiece.wallPieceTextures.get(
            wallPiece.currentWallPiece
        );

        // Deep copy each TextureRegion to prevent shared sprite references
        HashMap<TileOffset, TextureRegion> deepCopy = new HashMap<>();
        for (Map.Entry<TileOffset, TextureRegion> entry : original.entrySet()) {
            deepCopy.put(entry.getKey(), new TextureRegion(entry.getValue())); // clone region
        }

        renderComponent.sprites = deepCopy;
    }

    private static WallPieceComponent.WallPiece determineWallPiece(
        Entity entity,
        List<Entity> allWalls
    ) {
        PositionComponent pos = entity.getComponent(PositionComponent.class);
        if (pos == null) return WallPieceComponent.WallPiece.SINGLE;

        boolean hasLeft = hasWallOnLeft(pos, allWalls, entity);
        boolean hasRight = hasWallOnRight(pos, allWalls, entity);
        boolean hasUp = hasWallOnTop(pos, allWalls, entity);
        boolean hasDown = hasWallOnBottom(pos, allWalls, entity);

        if (hasLeft && hasRight && hasUp && hasDown) return WallPieceComponent.WallPiece.CROSS;
        if (hasLeft && hasRight && hasUp) return WallPieceComponent.WallPiece.T_BOTTOM;
        if (hasLeft && hasRight && hasDown) return WallPieceComponent.WallPiece.T_TOP;
        if (hasUp && hasDown && hasLeft) return WallPieceComponent.WallPiece.T_RIGHT;
        if (hasUp && hasDown && hasRight) return WallPieceComponent.WallPiece.T_LEFT;

        if (hasLeft && hasRight) return WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_MIDDLE;
        if (hasUp && hasDown) return WallPieceComponent.WallPiece.STRAIGHT_VERTICAL_MIDDLE;

        if (hasLeft && hasUp) return WallPieceComponent.WallPiece.CORNER_BOTTOM_RIGHT;
        if (hasRight && hasUp) return WallPieceComponent.WallPiece.CORNER_BOTTOM_LEFT;
        if (hasLeft && hasDown) return WallPieceComponent.WallPiece.CORNER_TOP_RIGHT;
        if (hasRight && hasDown) return WallPieceComponent.WallPiece.CORNER_TOP_LEFT;

        if (hasLeft) return WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_RIGHT;
        if (hasRight) return WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_LEFT;
        if (hasUp) return WallPieceComponent.WallPiece.STRAIGHT_VERTICAL_DOWN;
        if (hasDown) return WallPieceComponent.WallPiece.STRAIGHT_VERTICAL_UP;

        return WallPieceComponent.WallPiece.SINGLE;
    }

    private static boolean hasWallOnLeft(
        PositionComponent self,
        List<Entity> allWalls,
        Entity selfEntity
    ) {
        Vector2 check = new Vector2(self.basePosition.x - self.widthInTiles, self.basePosition.y);
        if (hasWallAt(check, allWalls, selfEntity)) return true;

        return false;
    }

    private static boolean hasWallOnRight(
        PositionComponent self,
        List<Entity> allWalls,
        Entity selfEntity
    ) {
        Vector2 check = new Vector2(self.basePosition.x + self.widthInTiles, self.basePosition.y);
        if (hasWallAt(check, allWalls, selfEntity)) return true;

        return false;
    }

    private static boolean hasWallOnTop(
        PositionComponent self,
        List<Entity> allWalls,
        Entity selfEntity
    ) {
        Vector2 check = new Vector2(self.basePosition.x, self.basePosition.y + self.heightInTiles);
        if (hasWallAt(check, allWalls, selfEntity)) return true;

        return false;
    }

    private static boolean hasWallOnBottom(
        PositionComponent self,
        List<Entity> allWalls,
        Entity selfEntity
    ) {
        Vector2 check = new Vector2(self.basePosition.x, self.basePosition.y - self.heightInTiles);
        if (hasWallAt(check, allWalls, selfEntity)) return true;

        return false;
    }

    private static boolean hasWallAt(Vector2 pos, List<Entity> allWalls, Entity self) {
        for (Entity wall : allWalls) {
            if (wall == self) continue;
            PositionComponent wallPos = wall.getComponent(PositionComponent.class);
            if (wallPos == null) continue;
            if (wallPos.basePosition.epsilonEquals(pos, 0.01f)) {
                return true;
            }
        }
        return false;
    }
}
