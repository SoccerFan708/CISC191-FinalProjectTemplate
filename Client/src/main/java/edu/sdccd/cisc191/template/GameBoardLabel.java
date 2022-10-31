package edu.sdccd.cisc191.template;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Adapted from Week 3 JavaFX Lab
 * Extends the basic JavaFX Label with game functionality
 */
public class GameBoardLabel extends Label {
    public static Insets LABEL_PADDING = new Insets(5);

    public GameBoardLabel(int type) {
        setPadding(LABEL_PADDING);
        switch(type) {
            case 1:
                setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
                break;
            case 2:
                setFont(Font.font("Tahoma", FontWeight.BOLD, 40));
                break;
        }
    }
}