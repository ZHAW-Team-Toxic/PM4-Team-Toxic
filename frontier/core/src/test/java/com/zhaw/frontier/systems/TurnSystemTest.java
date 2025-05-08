package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.ashley.core.Engine;
import com.zhaw.frontier.enums.GamePhase;
import org.junit.jupiter.api.*;

class TurnSystemTest {

    private TurnSystem turnSystem;

    @BeforeEach
    void setUp() {
        turnSystem = TurnSystem.getInstance();
        turnSystem.setGamePhase(GamePhase.BUILD_AND_PLAN);
        turnSystem.resetTurnCounter();
    }

    @Test
    void testExecuteTurn_setsGamePhaseAndIncrementsTurnCounter() {
        int initialCounter = turnSystem.getTurnCounter();

        turnSystem.executeTurn(GamePhase.BUILD_AND_PLAN);

        assertEquals(GamePhase.BUILD_AND_PLAN, turnSystem.getGamePhase());
        assertEquals(initialCounter + 1, turnSystem.getTurnCounter());
    }

    @Test
    void testExecuteTurn_collectionPhase_callsEndTurn() {
        Engine testEngine = new Engine();
        ResourceProductionSystem.init(testEngine);
        testEngine.addSystem(ResourceProductionSystem.getInstance());

        turnSystem.executeTurn(GamePhase.COLLECTION);

        assertEquals(GamePhase.COLLECTION, turnSystem.getGamePhase());
    }

    @Test
    void testIsEnemyTurn_returnsTrueEveryFifthTurn_upTo50() {
        for (int turn = 1; turn <= 50; turn++) {
            turnSystem.executeTurn(GamePhase.BUILD_AND_PLAN);
            boolean expected = (turn + 1) % 5 == 0;
            boolean actual = turnSystem.isEnemyTurn();

            assertEquals(expected, actual);
        }
    }
}
