package com.zhaw.frontier.entities;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.zhaw.frontier.components.DamageComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.ShootingRangeComponent;

public class Tower extends Entity {

    public Tower() {}

    public Tower(PositionComponent position, DamageComponent damage, ShootingRangeComponent shootingRange) {
        add(position).add(damage).add(shootingRange);
    }

    public Tower addComponent(Component component) {
        add(component);
        return this;
    }

}
