package com.zhaw.frontier.entities;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import java.util.List;

/**
 *
 */
public class EntityFactory {

    /**
     *
     */
    public static Entity buildEntity(Engine engine, List<Component> components) {
        Entity entity = engine.createEntity();
        for (Component component : components) {
            entity.add(component);
        }
        return entity;
    }

    /**
     *
     */
    public static Entity buildEntity(Engine engine, Component component) {
        Entity entity = engine.createEntity();
        entity.add(component);
        return entity;
    }
}
