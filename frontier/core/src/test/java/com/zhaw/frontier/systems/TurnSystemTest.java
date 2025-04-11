package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.ashley.core.EntitySystem;
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

        turnSystem.executeTurn(GamePhase.BUILD_AND_PLAN, mock(EntitySystem.class));

        assertEquals(GamePhase.BUILD_AND_PLAN, turnSystem.getGamePhase());
        assertEquals(initialCounter + 1, turnSystem.getTurnCounter());
    }

    @Test
    void testExecuteTurn_collectionPhase_callsEndTurn() {
        ResourceProductionSystem resourceSystem = mock(ResourceProductionSystem.class);

        turnSystem.executeTurn(GamePhase.COLLECTION, resourceSystem);

        verify(resourceSystem).endTurn();
    }

    @Test
    void testExecuteTurn_collectionPhase_withWrongSystem_throwsException() {
        EntitySystem wrongSystem = mock(EntitySystem.class);

        Exception exception = assertThrows(
            IllegalArgumentException.class,
            () -> turnSystem.executeTurn(GamePhase.COLLECTION, wrongSystem)
        );

        assertTrue(exception.getMessage().contains("Invalid system for collection phase"));
    }

    @Test
    void testExecuteTurn_enemyTurn_withWrongSystem_throwsException() {
        EntitySystem wrongSystem = mock(EntitySystem.class);

        Exception exception = assertThrows(
            IllegalArgumentException.class,
            () -> turnSystem.executeTurn(GamePhase.ENEMY_TURN, wrongSystem)
        );

        assertTrue(exception.getMessage().contains("Invalid system for enemy turn phase"));
    }

    @Test
    void testIsEnemyTurn_returnsTrueEveryFifthTurn_upTo50() {
        for (int turn = 1; turn <= 50; turn++) {
            turnSystem.executeTurn(GamePhase.BUILD_AND_PLAN, mock(EntitySystem.class));
            boolean expected = turn % 5 == 0;
            boolean actual = turnSystem.isEnemyTurn();

            assertEquals(expected, actual);
        }
    }
}
