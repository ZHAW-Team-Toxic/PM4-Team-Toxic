package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class OccupiesTilesComponent implements Component {

    public List<Vector2> occupiedTiles = new ArrayList<>();
}
