package com.zhaw.frontier.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;

/**
 * Tower entity class. This class is used to create a tower entity.
 * Current components:
 * - PositionComponent
 * - RenderComponent
 */
public class Tower extends Entity {

    /**
     * Constructor for the Tower entity.
     * This constructor creates a tower entity with a position component and a render component.
     */
    public Tower() {
        add(new PositionComponent());

        RenderComponent renderComponent = new RenderComponent();

        //TODO change placeholder for the actual tower texture
        Texture texture = createPlaceHolder();
        renderComponent.sprite = new Sprite(texture);

        add(renderComponent);
    }

    /**
     * Create a default tower.
     * @return The default tower.
     */
    public static Tower createDefaultTower() {
        return new Tower();
    }

    private Texture createPlaceHolder() {
        // Create a Pixmap with dimensions 12x12 using RGBA8888 format.
        Pixmap pixmap = new Pixmap(12, 12, Format.RGBA8888);
        // Set the drawing color to black.
        pixmap.setColor(0, 0, 0, 1);
        // Fill the entire pixmap with the black color.
        pixmap.fill();
        // Create a texture from the pixmap.
        Texture texture = new Texture(pixmap);
        // Dispose of the pixmap since it's no longer needed.
        pixmap.dispose();
        return texture;
    }
}
