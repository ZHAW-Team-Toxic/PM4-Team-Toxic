package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.zhaw.frontier.components.RoundAnimationComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.systems.RoundAnimationSystem;
import com.zhaw.frontier.utils.TileOffset;
import java.util.HashMap;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Unit tests for verifying animation frame updates in the {@link RoundAnimationSystem}.
 *
 * <p>
 * The system handles animations based on rounds (not delta time),
 * and cycles through static frame sets stored in {@link RoundAnimationComponent}.
 * </p>
 */
@ExtendWith(GdxExtension.class)
public class RoundAnimationSystemTest {

    private static Engine testEngine;

    /**
     * Initializes the test engine and adds the systems under test.
     */
    @BeforeAll
    public static void setup() {
        testEngine = new Engine();
    }

    /**
     * Adds the system under test: {@link RoundAnimationSystem}.
     */
    @BeforeEach
    public void clearEntities() {
        testEngine.removeAllEntities();
    }

    /**
     * Verifies that frame index cycles correctly through all available frames
     * and loops back to zero after the last one.
     */
    @Test
    public void testAnimationFrameAdvancesEachRound() {
        Entity building = testEngine.createEntity();

        RoundAnimationComponent anim = new RoundAnimationComponent();
        anim.frames = new HashMap<TileOffset, Array<TextureRegion>>(5);
        //assuming that you have 5 frames / states to animate
        anim.frames.put(
            new TileOffset(0, 0),
            new Array<>(
                new TextureRegion[] {
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                }
            )
        );
        anim.frames.put(
            new TileOffset(1, 0),
            new Array<>(
                new TextureRegion[] {
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                }
            )
        );
        anim.frames.put(
            new TileOffset(0, 1),
            new Array<>(
                new TextureRegion[] {
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                }
            )
        );
        anim.frames.put(
            new TileOffset(1, 1),
            new Array<>(
                new TextureRegion[] {
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                }
            )
        );
        anim.frames.put(
            new TileOffset(2, 0),
            new Array<>(
                new TextureRegion[] {
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                    new TextureRegion(),
                }
            )
        );
        anim.currentFrameIndex = 0;

        RenderComponent render = new RenderComponent();
        render.renderType = RenderComponent.RenderType.BUILDING;
        render.widthInTiles = 1;
        render.heightInTiles = 1;
        building.add(render);

        building.add(anim);
        testEngine.addEntity(building);

        assertEquals(0, anim.currentFrameIndex);

        RoundAnimationSystem.updateFrameForRoundComponent(building);
        assertEquals(1, anim.currentFrameIndex);

        RoundAnimationSystem.updateFrameForRoundComponent(building);
        assertEquals(2, anim.currentFrameIndex);

        RoundAnimationSystem.updateFrameForRoundComponent(building);
        assertEquals(3, anim.currentFrameIndex);

        RoundAnimationSystem.updateFrameForRoundComponent(building);
        assertEquals(4, anim.currentFrameIndex);

        RoundAnimationSystem.updateFrameForRoundComponent(building);
        assertEquals(0, anim.currentFrameIndex);
    }

    /**
     * Verifies that calling the update method on an animation component with empty frame sets
     * does not throw exceptions.
     */
    @Test
    public void testEmptyFramesHandledGracefully() {
        Entity building = testEngine.createEntity();
        RoundAnimationComponent anim = new RoundAnimationComponent();
        anim.frames = new HashMap<>(); // leer
        anim.currentFrameIndex = 0;

        building.add(anim);
        building.add(new RenderComponent());
        testEngine.addEntity(building);

        // sollte keine Exception werfen
        assertDoesNotThrow(() -> RoundAnimationSystem.updateFrameForRoundComponent(building));
        assertEquals(0, anim.currentFrameIndex);
    }

    /**
     * Verifies that calling the update method on an entity without an HQRoundAnimationComponent
     * does not result in an exception or crash.
     */
    @Test
    public void testNoAnimationComponentDoesNothing() {
        Entity building = testEngine.createEntity(); // kein anim-Component

        // Sollte keine Exception auslösen
        assertDoesNotThrow(() -> RoundAnimationSystem.updateFrameForRoundComponent(building));
    }

    @AfterAll
    public static void tearDown() {
        testEngine.removeAllEntities();
    }
}
