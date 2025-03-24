package com.zhaw.frontier.entities;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.zhaw.frontier.components.BuildingPositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class HQ {

    /**
     *
     */
    public static Entity createDefaultHQ(Engine engine) {
        List<Component> components = new ArrayList<>();
        components.add(new BuildingPositionComponent());
        components.add(new RenderComponent());

        return EntityFactory.buildEntity(engine, components);
    }
}
