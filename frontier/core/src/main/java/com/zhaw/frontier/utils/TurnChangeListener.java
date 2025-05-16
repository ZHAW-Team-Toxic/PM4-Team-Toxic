package com.zhaw.frontier.utils;

import com.zhaw.frontier.enums.GamePhase;

public interface TurnChangeListener {
    void onTurnChanged(int turn, GamePhase phase);
}
