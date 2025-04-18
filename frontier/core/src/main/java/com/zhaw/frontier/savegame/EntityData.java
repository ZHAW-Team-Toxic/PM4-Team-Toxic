package com.zhaw.frontier.savegame;

import java.util.HashMap;

public class EntityData {

    public String entityType;
    public Float x, y;
    public Integer health;
    public Float damage, range, speed;
    public HashMap<String, Integer> inventory = new HashMap<>();
    public String resourceType;
    public Integer countOfAdjacentResources;
}
