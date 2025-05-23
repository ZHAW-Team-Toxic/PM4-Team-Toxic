package com.zhaw.frontier.systems;

import com.zhaw.frontier.enums.GamePhase;
import com.zhaw.frontier.utils.TurnChangeListener;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * TurnSystem manages the game phases and turn counter.
 * It handles the execution of different game phases
 * and the transition between them.
 * It also provides a singleton instance for easy access.
 */
public class TurnSystem {

    @Getter
    @Setter
    private GamePhase gamePhase;

    @Setter
    @Getter
    private int turnCounter;

    private final List<TurnChangeListener> listeners = new ArrayList<>();
    private static TurnSystem instance;

    private TurnSystem() {
        this.gamePhase = GamePhase.BUILD_AND_PLAN;
        this.turnCounter = 1;
    }

    /**
     * Singleton instance getter.
     *
     * @return the single instance of TurnSystem
     */
    public static TurnSystem getInstance() {
        if (instance == null) {
            instance = new TurnSystem();
        }
        return instance;
    }

    public void addListener(TurnChangeListener listener) {
        listeners.add(listener);
        // Notify the initial state immediately
        listener.onTurnChanged(turnCounter, gamePhase);
    }

    public void removeListener(TurnChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (TurnChangeListener listener : listeners) {
            listener.onTurnChanged(turnCounter, gamePhase);
        }
    }

    /**
     * Executes the turn based on the current game phase.
     *
     * @param gamePhase the current game phase
     */
    public void executeTurn(GamePhase gamePhase) {
        setGamePhase(gamePhase);
        notifyListeners();

        switch (gamePhase) {
            case BUILD_AND_PLAN:
                System.out.println("Start of build and plan phase.");
                //TODO: Show the amount of resources buildings will cost (buildings are free at the moment)
                //TODO: If within scope also show new buildings as "in progress"
                break;
            case COLLECTION:
                System.out.println("Start of collection phase.");
                ResourceProductionSystem.getInstance().endTurn();
                break;
            case BUILD_PROGRESS:
                System.out.println("Start of build progress phase.");
                //TODO: If within scope building build progress is updated
                //TODO: Deduct resources from inventory for buildings that are in progress (buildings are free at the moment)
                //TODO: If within scope show the newly built buildings instantly or with a timer
                break;
            case ENEMY_TURN:
                System.out.println("Start of enemy turn phase.");
                EnemySpawnSystem.getInstance().spawnEnemies(turnCounter);
                break;
            default:
                throw new IllegalArgumentException("Invalid game phase: " + gamePhase);
        }
    }

    public void advanceTurn() {
        executeTurn(GamePhase.COLLECTION);
        executeTurn(GamePhase.BUILD_AND_PLAN);
        executeTurn(GamePhase.ENEMY_TURN);
        turnCounter++;
    }

    /**
     * Checks if it's the enemy's turn.
     *
     * @return true if it's the enemy's turn, false otherwise
     */
    public boolean isEnemyTurn() {
        return turnCounter % 5 == 0;
    }

    /**
     * Resets the turn counter to 1.
     */
    public void resetTurnCounter() {
        this.turnCounter = 1;
    }
}
