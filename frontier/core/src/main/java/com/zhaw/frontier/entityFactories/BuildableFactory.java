package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

@FunctionalInterface
public interface BuildableFactory {
    Entity create(Engine engine, float x, float y);
}
