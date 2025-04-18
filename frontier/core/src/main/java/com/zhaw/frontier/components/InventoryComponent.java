package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import java.util.HashMap;
import java.util.Map;

/**
 * Component for the inventory of an entity.
 * This component acts as the stock for the resources of the player.
 */
public class InventoryComponent implements Component {

    /**
     * The inventory of the entity.
     * This is a map of resource types to their quantities.
     */
    public Map<ResourceTypeEnum, Integer> resources = new HashMap<>();

    /**
     * Constructor for the InventoryComponent.
     * Initializes the inventory with zero quantities for each resource type.
     */
    public InventoryComponent() {
        resources.put(ResourceTypeEnum.RESOURCE_TYPE_WOOD, 0);
        resources.put(ResourceTypeEnum.RESOURCE_TYPE_STONE, 0);
        resources.put(ResourceTypeEnum.RESOURCE_TYPE_IRON, 0);
    }
}
