package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import java.util.HashMap;
import java.util.Map;

/**
 * Component for the resource production of an entity.
 * This component contains the production rate of each resource type
 * and the count of adjacent resources.
 */
public class ResourceProductionComponent implements Component {

    /**
     * The production rate of each resource type.
     */
    public Map<ResourceTypeEnum, Integer> productionRate = new HashMap<>();

    /**
     * The count of adjacent resources of the same type.
     */
    public int countOfAdjacentResources = 0;
}
