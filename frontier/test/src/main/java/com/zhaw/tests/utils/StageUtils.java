package com.zhaw.tests.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class StageUtils {
    /**
     * Recursively searches for a TextButton with the given text inside the Stage.
     *
     * @param stage      The Stage containing the UI elements.
     * @param buttonText The text of the button to find.
     * @return The TextButton if found, otherwise null.
     */
    public static TextButton findButtonByText(Stage stage, String buttonText) {
        for (Actor actor : stage.getActors()) {
            TextButton button = findButtonRecursively(actor, buttonText);
            if (button != null) {
                return button;
            }
        }
        return null;
    }

    /**
     * Recursively searches for a TextButton within an actor, handling nested tables.
     *
     * @param actor      The root actor to start the search from.
     * @param buttonText The text of the button to find.
     * @return The found TextButton or null if not found.
     */
    private static TextButton findButtonRecursively(Actor actor, String buttonText) {
        if (actor instanceof TextButton) {
            TextButton button = (TextButton) actor;
            if (button.getText().toString().equals(buttonText)) {
                return button;
            }
        } else if (actor instanceof Table) {
            Table table = (Table) actor;
            for (Actor child : table.getChildren()) {
                TextButton button = findButtonRecursively(child, buttonText);
                if (button != null) {
                    return button;
                }
            }
        }
        return null;
    }
}