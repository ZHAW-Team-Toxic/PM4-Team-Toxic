package com.zhaw.frontier.savegame;

import com.zhaw.frontier.enums.GamePhase;

import java.util.ArrayList;
import java.util.HashMap;

public class GameState {

    // Entity data for ashley engine
    public ArrayList<EntityData> entities = new ArrayList<>();

    // Additional data
    public MetaData metadata = new MetaData();

    MetaData getMetadata() {
        return metadata;
    }
}
