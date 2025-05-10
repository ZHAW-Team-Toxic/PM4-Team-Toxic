package com.zhaw.frontier.savegame;

import java.util.ArrayList;

public class GameState {

    // Entity data for ashley engine
    public ArrayList<EntityData> entities = new ArrayList<>();

    // Additional data
    public MetaData metadata = new MetaData();

    MetaData getMetadata() {
        return metadata;
    }
}
