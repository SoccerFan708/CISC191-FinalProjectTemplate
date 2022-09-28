package edu.sdccd.cisc191.template;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Adapted from Week 3 JavaFX Lab
 * This program is a turn-based game of Battleship against the computer.
 * The user selects buttons which correspond to the computer's hidden board. A hit is recorded when the clicked button
 * corresponds to an area where the computer has a ship. There are 3 types of ships, differentiated by how many tiles they each
 * occupy.
 */
public class Server extends Application {

    private Scene scene;
    private BorderPane gameCanvasBorderPane;
    private Canvas tileGrid;
    private ControllerGameBoard controller;
    private GameBoardLabel enemyShipsSunk;
    private GameBoardLabel shipsSunk;
    private GameBoardLabel shipCount;
    private GameBoardLabel message;

    /**
     * This updates the HUD messages and displays them. It also draws everything else that is on the screen.
     */
    public void update() {
        enemyShipsSunk.setText("Enemy Ships Sunk: "+controller.modelGameBoard.computerShipsSunk);
        shipsSunk.setText("Ships Lost: "+controller.modelGameBoard.playerShipsSunk);
        shipCount.setText("Ships Remaining: "+controller.modelGameBoard.playerShipCount);
        message.setText(controller.modelGameBoard.currentMessage);
        if(controller.isGameOver()) {
            message = new GameBoardLabel(2);
            if (controller.computerWins()) message.setText("You Lost!");
            else message.setText("You Won!");
        }
        draw();
    }

    /**
     * This draws the HUD and player board. The grid that represents the player's board will display red for ship
     * tiles hit and gray for misses, otherwise the tiles will be white. Black represents the location of a ship.
     */
    private void draw(){
        //Create a 30 X 30 grid of tiles to represent the players game board. This grid will display the computer's shots.
        int TILE_W = 30;
        int TILE_H = 30;
        tileGrid = new Canvas(TILE_W*ModelGameBoard.DIMENSION, TILE_H*ModelGameBoard.DIMENSION);
        GraphicsContext g2 = tileGrid.getGraphicsContext2D();
        for(int row=0;row<ModelGameBoard.DIMENSION;row++)
            for (int col = 0; col < ModelGameBoard.DIMENSION; col++) {
                switch (controller.modelGameBoard.playerBoard[row][col]) {
                    case 0:
                        g2.setFill(Color.WHITE);
                        break;
                    case 1:
                    case 2:
                    case 3:
                        g2.setFill(Color.BLACK);
                        break;
                    case 4:g2.setFill(Color.RED);
                        break;
                    case 5:
                        g2.setFill(Color.GRAY);
                        break;
                }
                g2.fillRect(row * TILE_W, col * TILE_H, TILE_W, TILE_H);
                g2.setStroke(Color.BLACK);
                g2.strokeRect(row * TILE_W, col * TILE_H, TILE_W, TILE_H);
            }
        VBox gameHud = new VBox(enemyShipsSunk, shipsSunk, shipCount, message);
        gameCanvasBorderPane.setTop(gameHud);
        gameCanvasBorderPane.setBottom(tileGrid);

    }
    @Override
    public void start(Stage stage) throws Exception {
        controller = new ControllerGameBoard();
        tileGrid = new Canvas();
        enemyShipsSunk = new GameBoardLabel(1);
        message = new GameBoardLabel(1);
        shipsSunk = new GameBoardLabel(1);
        shipCount = new GameBoardLabel(1);
        gameCanvasBorderPane = new BorderPane();
        update();
        BorderPane root = new BorderPane();
        GridPane buttonGrid = new GridPane();
        for (int row=0; row < ModelGameBoard.DIMENSION; row++)
            for (int col = 0; col < ModelGameBoard.DIMENSION; col++) {
                GameBoardButton button = new GameBoardButton(row, col, controller.modelGameBoard.shipAt(controller.modelGameBoard.computerBoard, row, col));
                int finalRow = row;
                int finalCol = col;
                button.setOnAction(e -> {
                    if (!controller.isGameOver() && controller.currentTurn.equals(ModelGameBoard.PLAYER_NAME)) {
                        button.handleClick();
                        controller.shoot(ModelGameBoard.PLAYER_NAME, finalRow, finalCol);
                        controller.enemyThink();
                        update();
                    }
                });
                buttonGrid.add(button, row, col);
            }
        root.setLeft(buttonGrid);
        root.setCenter(gameCanvasBorderPane);


        scene = new Scene(root, 830, 500, Color.WHITE);
        stage.setTitle("BattleShip!");
        stage.setScene(scene);
        stage.show();
    }
}
