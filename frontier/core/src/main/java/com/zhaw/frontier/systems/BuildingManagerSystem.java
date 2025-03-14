package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zhaw.frontier.components.DamageComponent;
import com.zhaw.frontier.components.FirerateComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.ShootingRangeComponent;

public class BuildingManagerSystem {

    private ImmutableArray<Entity> buildingEntities;

    public BuildingManagerSystem() {}

    public void update(float deltaTime) {


    }
}
