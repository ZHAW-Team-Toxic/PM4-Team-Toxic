package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.zhaw.frontier.enums.Team;

public class TeamComponent implements Component {

    public Team team;

    public TeamComponent(Team team) {
        this.team = team;
    }
}
