package edu.sdccd.cisc191.template;

import javafx.scene.control.Button;

/**
 * Adapted from Week 3 JavaFX Lab
 * Extends the basic JavaFX Button with game functionality
 */
public class GameBoardButton extends Button {
    private int row;
    private int col;
    private boolean hasShip;

    public GameBoardButton(int row, int col, boolean hasShip)
    {
        this.row = row;
        this.col = col;
        this.hasShip = hasShip;

        setMinWidth(50);
        setMinHeight(50);
        setPrefWidth(50);
        setPrefHeight(50);
        //setText("?");

    }

    public int[] getLocation(){
        int[] location = {row, col};
        return location;
    }
    public void handleClick() {
        if(hasShip) {
            setText("[X]");
        } else {
            setText("[O]");
        }
        setDisabled(true);
    }
}
