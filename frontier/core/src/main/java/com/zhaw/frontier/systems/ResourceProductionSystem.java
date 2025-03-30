package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zhaw.frontier.components.InventoryComponent;
import com.zhaw.frontier.components.ResourceProductionComponent;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import com.zhaw.frontier.components.map.TiledPropertiesEnum;
import java.util.Map;

/**
 * The ResourceProductionSystem aggregates the resource production from production buildings
 * and updates the central inventory. Each production building with a {@link ResourceProductionComponent}
 * and the number of resource tiles within its range.
 *
 * <p>Currently, the system updates the inventory on every frame by calling {@link #endTurn()},
 * but this may later be adjusted to update at specific intervals or turn-based events.</p>
 *
 * @see InventoryComponent
 * @see ResourceProductionComponent
 * @see TiledPropertiesEnum
 */
public class ResourceProductionSystem extends EntitySystem {

    private final Engine engine;
    private ImmutableArray<Entity> productionBuildings;

    /**
     * Constructs a new ResourceProductionSystem.
     *
     * @param engine the Ashley engine that contains the entities to be processed
     */
    public ResourceProductionSystem(Engine engine) {
        super();
        this.engine = engine;
    }

    @Override
    public void addedToEngine(Engine engine) {
        productionBuildings = engine.getEntitiesFor(
            Family
                .all(ResourceProductionComponent.class)
                .exclude(InventoryComponent.class)
                .get()
        );
    }

    /**
     * Called every frame to update the system. For now, it delegates to {@link #endTurn()}
     * to update the inventory based on production from the buildings.
     *
     * @param deltaTime the time in seconds since the last update
     */
    @Override
    public void update(float deltaTime) {
        // TODO: Update inventory at specific intervals instead of every frame.
        endTurn();
    }

    /**
     * Aggregates resource production from each building entity and updates the central inventory.
     * <p>
     * This method retrieves the inventory entity (which is expected to be the only entity containing
     * an {@link InventoryComponent}) and then iterates over all building entities that have both a
     * it multiplies its production rate (per resource type) by the number of resource tiles in range
     * and adds the result to the corresponding resource count in the inventory.
     * </p>
     */
    public void endTurn() {
        ImmutableArray<Entity> inventoryEntities = engine.getEntitiesFor(
            Family.all(InventoryComponent.class).get()
        );
        if (inventoryEntities.size() == 0) {
            // No inventory entity found, so nothing to update.
            return;
        }
        InventoryComponent inventoryEntity = inventoryEntities
            .first()
            .getComponent(InventoryComponent.class);

        // Process each building that produces resources and collects them.
        for (Entity building : productionBuildings) {
            ResourceProductionComponent production = building.getComponent(
                ResourceProductionComponent.class
            );

            // For each resource type produced by the building, update the inventory.
            for (Map.Entry<
                ResourceTypeEnum,
                Integer
            > entry : production.productionRate.entrySet()) {
                ResourceTypeEnum resourceType = entry.getKey();
                int productionRate = entry.getValue();
                inventoryEntity.resources.put(
                    resourceType,
                    inventoryEntity.resources.getOrDefault(resourceType, 0) +
                    (productionRate * production.countOfAdjacentResources)
                );
            }
        }
    }
}
