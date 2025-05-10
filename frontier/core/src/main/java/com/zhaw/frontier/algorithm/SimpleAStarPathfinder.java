package com.zhaw.frontier.algorithm;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zhaw.frontier.components.map.TiledPropertiesEnum;
import java.util.*;

/**
 * A simple implementation of the A* pathfinding algorithm for use with a TiledMapTileLayer.
 * It searches for a walkable path from a start point to a goal point, using Manhattan distance as a heuristic.
 * If no path to the goal is found, it returns the path to the closest reachable tile.
 */
public class SimpleAStarPathfinder {

    private final List<TiledMapTileLayer> layers;
    private final Engine engine;

    /**
     * Creates a new A* pathfinder instance using the given tile layer and engine.
     *
     * @param layer  the tile layer representing the map
     * @param engine the Ashley engine instance (currently unused but kept for future extensibility)
     */
    public SimpleAStarPathfinder(List<TiledMapTileLayer> layers, Engine engine) {
        this.layers = layers;
        this.engine = engine;
    }

    /**
     * Finds a path from the start position to the goal position using A*.
     * If the goal is unreachable, returns a path to the closest reachable node to the goal.
     *
     * @param start the starting position
     * @param goal  the goal position
     * @return an array of {@link Vector2} representing the path (excluding the start), or an empty array if no path is found
     */
    public Array<Vector2> findPath(Vector2 start, Vector2 goal) {
        int width = layers.get(0).getWidth();
        int height = layers.get(0).getHeight();

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

    /**
     * Reconstructs the path from the end node back to the start using the cameFrom map.
     *
     * @param cameFrom a map tracking the parent of each visited node
     * @param current  the node to reconstruct the path from
     * @return a reversed array representing the path from start to the given node
     */
    private Array<Vector2> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        Array<Vector2> path = new Array<>();
        while (cameFrom.containsKey(current)) {
            path.add(new Vector2(current.x, current.y));
            current = cameFrom.get(current);
        }
        path.reverse();
        return path;
    }

    /**
     * Computes the Manhattan distance between two nodes.
     *
     * @param a the first node
     * @param b the second node
     * @return the heuristic cost
     */
    private int heuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    /**
     * Determines whether a tile at the specified coordinates is walkable.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if the tile is traversable, false otherwise
     */
    private boolean isWalkable(int x, int y) {
        for (TiledMapTileLayer layer : layers) {
            TiledMapTileLayer.Cell cell = layer.getCell(x, y);
            if (cell != null && cell.getTile() != null) {
                Boolean traversable = cell
                    .getTile()
                    .getProperties()
                    .get(TiledPropertiesEnum.IS_TRAVERSABLE.toString(), Boolean.class);

                // If any layer says "not traversable", we consider it blocked
                if (!Boolean.TRUE.equals(traversable)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Retrieves the valid neighboring nodes (up, down, left, right) of the current node within map bounds.
     *
     * @param node   the current node
     * @param width  the map width
     * @param height the map height
     * @return a list of neighboring nodes
     */
    private List<Node> getNeighbors(Node node, int width, int height) {
        List<Node> neighbors = new ArrayList<>();

        int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

        for (int[] dir : directions) {
            int nx = node.x + dir[0];
            int ny = node.y + dir[1];

            if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
                neighbors.add(new Node(nx, ny));
            }
        }

        return neighbors;
    }

    /**
     * A helper class representing a node in the A* pathfinding graph.
     * Includes cost values for A* and coordinates.
     */
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
