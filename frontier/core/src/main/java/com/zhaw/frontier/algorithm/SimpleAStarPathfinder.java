package com.zhaw.frontier.algorithm;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zhaw.frontier.components.OccupiesTilesComponent;
import com.zhaw.frontier.components.map.TiledPropertiesEnum;
import java.util.*;

public class SimpleAStarPathfinder {

    private final TiledMapTileLayer layer;
    private final Engine engine;

    public SimpleAStarPathfinder(TiledMapTileLayer layer, Engine engine) {
        this.layer = layer;
        this.engine = engine;
    }

    public Array<Vector2> findPath(Vector2 start, Vector2 goal) {
        int width = layer.getWidth();
        int height = layer.getHeight();

        Node startNode = new Node((int) start.x, (int) start.y);
        Node goalNode = new Node((int) goal.x, (int) goal.y);

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost));
        Set<Node> closedSet = new HashSet<>();
        Map<Node, Node> cameFrom = new HashMap<>();

        startNode.gCost = 0;
        startNode.fCost = heuristic(startNode, goalNode);
        openSet.add(startNode);

        Node closestReachable = null;
        int closestDistance = Integer.MAX_VALUE;

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            closedSet.add(current);

            int distanceToGoal = heuristic(current, goalNode);
            if (distanceToGoal < closestDistance && isWalkable(current.x, current.y)) {
                closestReachable = current;
                closestDistance = distanceToGoal;
            }

            if (current.equals(goalNode) && isWalkable(current.x, current.y)) {
                return reconstructPath(cameFrom, current);
            }

            for (Node neighbor : getNeighbors(current, width, height)) {
                if (closedSet.contains(neighbor)) continue;

                boolean walkable = isWalkable(neighbor.x, neighbor.y);
                if (!walkable && !neighbor.equals(goalNode)) continue;

                int tentativeG = current.gCost + 1;

                if (tentativeG < neighbor.gCost || !openSet.contains(neighbor)) {
                    neighbor.gCost = tentativeG;
                    neighbor.fCost = tentativeG + heuristic(neighbor, goalNode);
                    cameFrom.put(neighbor, current);
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        // Could not reach the goal, return path to closest reachable point
        if (closestReachable != null) {
            return reconstructPath(cameFrom, closestReachable);
        }

        return new Array<>(); // No path found at all
    }

    private Array<Vector2> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        Array<Vector2> path = new Array<>();
        while (cameFrom.containsKey(current)) {
            path.add(new Vector2(current.x + 0.5f, current.y + 0.5f));
            current = cameFrom.get(current);
        }
        path.reverse();
        return path;
    }

    private int heuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private boolean isWalkable(int x, int y) {
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell == null || cell.getTile() == null) return false;

        Boolean traversable = cell
            .getTile()
            .getProperties()
            .get(TiledPropertiesEnum.IS_TRAVERSABLE.toString(), Boolean.class);
        return Boolean.TRUE.equals(traversable) && !isTileOccupied(x, y);
    }

    private boolean isTileOccupied(int x, int y) {
        ImmutableArray<Entity> entities = engine.getEntitiesFor(
            Family.all(OccupiesTilesComponent.class).get()
        );

        for (Entity entity : entities) {
            OccupiesTilesComponent occ = entity.getComponent(OccupiesTilesComponent.class);
            for (Vector2 tile : occ.occupiedTiles) {
                if ((int) tile.x == x && (int) tile.y == y) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<Node> getNeighbors(Node node, int width, int height) {
        List<Node> neighbors = new ArrayList<>();

        int[][] directions = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1},
        };

        for (int[] dir : directions) {
            int nx = node.x + dir[0];
            int ny = node.y + dir[1];

            if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
                neighbors.add(new Node(nx, ny));
            }
        }

        return neighbors;
    }

    private static class Node {
        int x, y;
        int gCost = Integer.MAX_VALUE;
        int fCost = Integer.MAX_VALUE;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Node)) return false;
            Node other = (Node) obj;
            return this.x == other.x && this.y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
