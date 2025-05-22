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
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code ResourceProductionSystem} is responsible for collecting resources produced by
 * buildings and updating the central inventory accordingly.
 *
 * <p>Each entity with a {@link ResourceProductionComponent} contributes to the global inventory
 * based on its production rate and the number of adjacent resource tiles.
 * The number of adjacent tiles is expected to be calculated beforehand and stored in
 * {@link ResourceProductionComponent#countOfAdjacentResources}.</p>
 *
 * <p>This system updates the inventory every frame by default (via {@link #update(float)}),
 * but the actual resource collection logic is encapsulated in {@link #endTurn()},
 * which can later be triggered manually for turn-based gameplay.</p>
 *
 * <p>Only one entity in the engine should contain an {@link InventoryComponent},
 * which serves as the global resource store.</p>
 *
 * @see InventoryComponent
 * @see ResourceProductionComponent
 * @see ResourceAdjacencyChecker
 * @see TiledPropertiesEnum
 */
public class ResourceProductionSystem extends EntitySystem {

    private static ResourceProductionSystem instance;
    private final Engine engine;
    private ImmutableArray<Entity> productionBuildings;

    private ResourceProductionSystem(Engine engine) {
        super();
        this.engine = engine;
        instance = this;
    }

    /**
     * Initializes the singleton instance of {@code ResourceProductionSystem}.
     *
     * @param engine the engine to be used by the system
     */
    public static void init(Engine engine) {
        instance = new ResourceProductionSystem(engine);
    }

    /**
     * Returns the singleton instance of {@code ResourceProductionSystem}.
     *
     * @return the singleton instance of ResourceProductionSystem
     */
    public static ResourceProductionSystem getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ResourceProductionSystem not initialized");
        }
        return instance;
    }

    /**
     * Called when the system is added to the engine. Retrieves all entities
     * that produce resources (i.e., have a {@link ResourceProductionComponent})
     * but are not inventories themselves.
     *
     * @param engine the engine that the system is added to
     */
    @Override
    public void addedToEngine(Engine engine) {
        productionBuildings =
        engine.getEntitiesFor(
            Family.all(ResourceProductionComponent.class).exclude(InventoryComponent.class).get()
        );
    }

    /**
     * Called every frame to update the system.
     * Currently delegates to {@link #endTurn()} to simulate turn-based collection.
     *
     * @param deltaTime the time in seconds since the last update
     */
    @Override
    public void update(float deltaTime) {
        // TODO: Replace with time-based or turn-based execution if needed.
    }

    /**
     * Aggregates resource production from each building and updates the central inventory.
     * <p>
     * For each resource-producing building, this method multiplies the configured
     * production rate per resource type by the number of adjacent matching resource tiles,
     * then adds the total to the inventory.
     * </p>
     * <p>
     * Example: A building producing wood at rate 2 and with 3 adjacent wood tiles
     * will contribute 6 wood to the inventory.
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

        for (Entity building : productionBuildings) {
            ResourceProductionComponent production = building.getComponent(
                ResourceProductionComponent.class
            );

            for (Map.Entry<
                ResourceTypeEnum,
                Integer
            > entry : production.productionRate.entrySet()) {
                ResourceTypeEnum resourceType = entry.getKey();
                int productionRate = entry.getValue();
                int totalProduction = productionRate * production.countOfAdjacentResources;

                inventoryEntity.resources.put(
                    resourceType,
                    inventoryEntity.resources.getOrDefault(resourceType, 0) + totalProduction
                );
            }
        }
    }

    /**
     * Returns the projected income from all production buildings.
     * <p>
     * This method calculates the total production rate for each resource type
     * based on the number of adjacent resource tiles and returns a map of
     * resource types to their projected income.
     * </p>
     *
     * @return a map of resource types to their projected income
     */
    public Map<ResourceTypeEnum, Integer> getProjectedIncome() {
        Map<ResourceTypeEnum, Integer> income = new HashMap<>();

        for (Entity building : productionBuildings) {
            ResourceProductionComponent production = building.getComponent(
                ResourceProductionComponent.class
            );

            for (Map.Entry<
                ResourceTypeEnum,
                Integer
            > entry : production.productionRate.entrySet()) {
                int total = entry.getValue() * production.countOfAdjacentResources;
                income.merge(entry.getKey(), total, Integer::sum);
            }
        }

        return income;
    }
}
