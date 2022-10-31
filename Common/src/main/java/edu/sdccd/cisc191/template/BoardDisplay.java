package edu.sdccd.cisc191.template;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BoardDisplay {
    /**
     * This class draws a player's own board on the screen. This was created with an eye on possibly having the server display
     * the boards of all players playing.
    */

    private Canvas displayBoard;
    private int[][] boardGrid;
    private Color specialColor;
    int tileW, tileH;

    protected BoardDisplay(){}

    public BoardDisplay( int[][] board, int tileW, int tileH, Color spColor){
        boardGrid = board;
        displayBoard = new Canvas(board[0].length*tileW, board.length*tileH);
        specialColor = spColor;
        this.tileH = tileH;
        this.tileW = tileW;
    }

public Canvas getDisplayBoard(){
    GraphicsContext g2 = displayBoard.getGraphicsContext2D();
    for(int row=0;row<boardGrid.length;row++)
        for (int col = 0; col < boardGrid[0].length; col++) {
            switch (boardGrid[row][col]) {
                case 0:
                    g2.setFill(Color.WHITE);
                    break;
                case 1:
                case 2:
                case 3:
                    g2.setFill(Color.BLACK);
                    break;
                case 4:g2.setFill(specialColor);
                    break;
                case 5:
                    g2.setFill(Color.GRAY);
                    break;
            }
            g2.fillRect(col * tileW, row * tileH, tileW, tileH);
            g2.setStroke(Color.BLACK);
            g2.strokeRect(col * tileW, row * tileH, tileW, tileH);
        }
        return displayBoard;
}

    /**
     * This prints the board to screen, mainly here for debugging purposes
     */
    public void displayBoard(){
        for(int i=0;i<boardGrid.length;i++) {
            for (int j=0;j<boardGrid[i].length;j++) {
                System.out.print(boardGrid[j][i]);
            }
            System.out.println();
        }
    }


}
