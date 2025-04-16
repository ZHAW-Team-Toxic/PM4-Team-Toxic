package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

public class TargetTypeComponent implements Component {

    public Class<? extends Component> targetComponentType;

    public TargetTypeComponent(Class<? extends Component> targetType) {
        this.targetComponentType = targetType;
    }
}
