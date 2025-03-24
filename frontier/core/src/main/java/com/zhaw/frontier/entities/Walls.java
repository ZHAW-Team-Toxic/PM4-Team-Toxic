package com.zhaw.frontier.entities;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.zhaw.frontier.components.BuildingPositionComponent;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.RenderComponent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Walls {

    /**
     *
     * @param engine
     * @return
     */
    public static Entity createDefaultWall(Engine engine) {
        List<Component> components = new ArrayList<>();
        components.add(new BuildingPositionComponent());
        components.add(new HealthComponent());

        RenderComponent renderComponent = new RenderComponent();

        //TODO change placeholder for the actual wall texture
        Texture texture = createPlaceHolder();
        renderComponent.sprite = new Sprite(texture);

        components.add(renderComponent);
        return EntityFactory.buildEntity(engine, components);
    }

    private static Texture createPlaceHolder() {
        // Create a Pixmap with dimensions 12x12 using RGBA8888 format.
        Pixmap pixmap = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        // Set the drawing color to red.
        pixmap.setColor(64, 0, 0, 1);
        // Fill the entire pixmap with the black color.
        pixmap.fill();
        // Create a texture from the pixmap.
        Texture texture = new Texture(pixmap);
        // Dispose of the pixmap since it's no longer needed.
        pixmap.dispose();
        return texture;
    }
}
