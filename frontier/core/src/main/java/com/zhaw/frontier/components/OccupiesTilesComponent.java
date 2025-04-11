package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 * Component that represents the tiles occupied by an entity.
 * This component is used to track the tiles that an entity occupies
 */
public class OccupiesTilesComponent implements Component {

    /**
     * The list of tiles occupied by the entity.
     * Each tile is represented by its coordinates (x, y).
     */
    public List<Vector2> occupiedTiles = new ArrayList<>();
}
