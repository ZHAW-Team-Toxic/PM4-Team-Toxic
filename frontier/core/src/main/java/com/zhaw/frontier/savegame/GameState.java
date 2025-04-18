package com.zhaw.frontier.savegame;

import java.util.ArrayList;
import java.util.HashMap;

public class GameState {

    // Entity data for ashley engine
    public ArrayList<EntityData> entities = new ArrayList<>();

    // Additional data
    public HashMap<String, Object> metadata = new HashMap<>();
}
