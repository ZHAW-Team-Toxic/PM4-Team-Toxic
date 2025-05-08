package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Engine;
import com.zhaw.frontier.enums.GamePhase;
import org.junit.jupiter.api.*;

class TurnSystemTest {

    private TurnSystem turnSystem;
    private Engine engine;

    @BeforeEach
    void setUp() {
        turnSystem = TurnSystem.getInstance();
        turnSystem.setGamePhase(GamePhase.BUILD_AND_PLAN);
        turnSystem.resetTurnCounter();

        engine = new Engine();
        ResourceProductionSystem.init(engine);
        engine.addSystem(ResourceProductionSystem.getInstance());
    }

    @Test
    void testAdvanceTurn_incrementsTurnCounterAndSetsGamePhase() {
        int initialCounter = turnSystem.getTurnCounter();

        turnSystem.advanceTurn();

        assertEquals(initialCounter + 1, turnSystem.getTurnCounter());
        assertEquals(GamePhase.BUILD_AND_PLAN, turnSystem.getGamePhase());
    }


    @Test
    void testIsEnemyTurn_returnsTrueEveryFifthTurn_upTo50() {
        for (int turn = 1; turn <= 50; turn++) {
            boolean expected = turn % 5 == 0;
            assertEquals(expected, turnSystem.isEnemyTurn(), "Turn: " + turn);

            turnSystem.setTurnCounter(turnSystem.getTurnCounter() + 1);
        }
    }
}
