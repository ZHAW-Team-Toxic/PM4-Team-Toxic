package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.TestMapEnvironment;
import com.zhaw.frontier.enums.GamePhase;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.TurnChangeListener;
import java.lang.reflect.Field;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxExtension.class)
public class TurnSystemTest {

    private Engine testEngine;
    private TestMapEnvironment testMapEnvironment;
    private TurnSystem turnSystem;

    @BeforeEach
    void setUp() throws Exception {
        // === Set up TestMapEnvironment with proper map and engine ===
        testMapEnvironment = new TestMapEnvironment();
        testEngine = testMapEnvironment.getTestEngine();

        // === Ensure texture atlas (required by many factories) is loaded ===
        AssetManagerInstance.getManager().load("packed/textures.atlas", TextureAtlas.class);
        AssetManagerInstance.getManager().finishLoading();

        // === Reset singleton TurnSystem instance via reflection ===
        Field instanceField = TurnSystem.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        // === Initialize required systems ===
        ResourceProductionSystem.init(testEngine);
        testEngine.addSystem(ResourceProductionSystem.getInstance());

        EnemySpawnSystem.create(testEngine);

        // === Get fresh TurnSystem instance ===
        turnSystem = TurnSystem.getInstance();
        turnSystem.setGamePhase(GamePhase.BUILD_AND_PLAN);
        turnSystem.resetTurnCounter();
    }

    @Test
    void testAdvanceTurn_incrementsTurnCounterAndSetsGamePhase() {
        int initialCounter = turnSystem.getTurnCounter();

        turnSystem.advanceTurn();

        assertEquals(initialCounter + 1, turnSystem.getTurnCounter());
        assertEquals(GamePhase.ENEMY_TURN, turnSystem.getGamePhase());
    }

    @Test
    void testExecuteTurn_setsCorrectGamePhase() {
        turnSystem.executeTurn(GamePhase.COLLECTION);
        assertEquals(GamePhase.COLLECTION, turnSystem.getGamePhase());

        turnSystem.executeTurn(GamePhase.BUILD_PROGRESS);
        assertEquals(GamePhase.BUILD_PROGRESS, turnSystem.getGamePhase());
    }

    @Test
    void testListenersAreNotifiedOnAdd() {
        var notified = new boolean[] { false };
        TurnChangeListener listener = (turn, phase) -> {
            notified[0] = true;
            assertEquals(1, turn);
            assertEquals(GamePhase.BUILD_AND_PLAN, phase);
        };

        turnSystem.addListener(listener);
        assertTrue(notified[0], "Listener should be notified immediately upon registration.");
    }

    @Test
    void testListenersAreNotifiedOnTurnAdvance() {
        final int[] callCount = { 0 };

        TurnChangeListener listener = (turn, phase) -> {
            callCount[0]++;
        };

        turnSystem.addListener(listener);
        int expectedCalls = 4; // 1 from addListener, 3 phases in advanceTurn

        turnSystem.advanceTurn();

        assertEquals(
            expectedCalls,
            callCount[0],
            "Listener should be called once for each phase and once on add."
        );
    }
}
