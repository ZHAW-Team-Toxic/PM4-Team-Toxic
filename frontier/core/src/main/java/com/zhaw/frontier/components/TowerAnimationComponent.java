package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;

/**
 * keeps track of the diffrerent direction textures
 */
public class TowerAnimationComponent implements Component {

    public Map<Integer, TextureRegion> animationTextures = new HashMap<>();
    public int degrees = 0;
}
