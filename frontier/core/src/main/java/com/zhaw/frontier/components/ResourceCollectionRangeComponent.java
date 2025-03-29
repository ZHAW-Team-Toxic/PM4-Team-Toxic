package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * Contains the range of the resource collection of an {@link com.badlogic.ashley.core.Entity}
 * and the number of tiles in range. The range is the number of tiles around the entity that are
 * considered in the resource collection. The number of tiles in range is the number of tiles
 * that match the resource type of the entity's {@link ResourceProductionComponent}.
 */
public class ResourceCollectionRangeComponent implements Component {

    /**
     * The range of the resource collection.
     */
    public int range = 1;
    /**
     * The number of tiles in range that match the resource type of the entity's {@link ResourceProductionComponent}.
     */
    public int tilesInRange = 0;
    /**
     * Flag indicating whether the number of tiles in range has been calculated.
     */
    public boolean isCalculated = false;
}
