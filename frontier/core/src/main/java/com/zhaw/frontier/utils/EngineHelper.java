package com.zhaw.frontier.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.zhaw.frontier.components.InventoryComponent;

/**
 * EngineHelper
 */
public class EngineHelper {

    public static InventoryComponent getInventoryComponent(Engine engine) {
        Entity stock = engine.getEntitiesFor(Family.all(InventoryComponent.class).get()).first();
        return stock.getComponent(InventoryComponent.class);
    }
}
