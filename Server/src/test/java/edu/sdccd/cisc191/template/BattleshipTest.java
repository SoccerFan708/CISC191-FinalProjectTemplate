package edu.sdccd.cisc191.template;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class BattleshipTest {
    private ArrayList<int[]> playerShipLocations, computerShipLocations;
    private int[][] testBoard;
    ModelGameBoard modelGameBoard;
    GameEnemyBrain gameEnemyBrain;
    ControllerGameBoard controllerGameBoard;

    /**
     * Sets up for the tests
     */
    @BeforeAll public void setUp(){
        controllerGameBoard = new ControllerGameBoard();
        modelGameBoard = controllerGameBoard.modelGameBoard;
        gameEnemyBrain = new GameEnemyBrain();
        testBoard = new int[ModelGameBoard.DIMENSION][ModelGameBoard.DIMENSION];
        playerShipLocations = modelGameBoard.playerShipLocations;
        computerShipLocations = modelGameBoard.computerShipLocations;
    }

    /**
     * Test that the modelGameBoard initializes, creates the different game boards, and the ships
     */
    @Test public void testModelGameBoardConstruction(){
        assertEquals(ModelGameBoard.DIMENSION, modelGameBoard.playerBoard.length);
        assertEquals(ModelGameBoard.DIMENSION, modelGameBoard.playerBoard[0].length);
        assertEquals(ModelGameBoard.DIMENSION, modelGameBoard.computerBoard.length);
        assertEquals(ModelGameBoard.DIMENSION, modelGameBoard.computerBoard[0].length);
        //The total tiles occupied by each player's ship must add to 10, the static SHIP_TILE_COUNT;
        assertEquals(ModelGameBoard.SHIP_TILE_COUNT, getModelGameBoardShipCounts(modelGameBoard.computerShipLocations));
        assertEquals(ModelGameBoard.SHIP_TILE_COUNT, getModelGameBoardShipCounts(modelGameBoard.playerShipLocations));
    }

    /**
     * Tests the way ModelGameBoard finds empty space to place ships
     */
    @Test public void testModelGameBoardCheckForSpace(){
        assertFalse(modelGameBoard.checkForSpace(testBoard, 3, 0, 0, 0, -1));
        assertTrue(modelGameBoard.checkForSpace(testBoard, 3, 0, 0, 0, 1));
        assertFalse(modelGameBoard.checkForSpace(testBoard, 3, 0, 0, -1, 0));
        assertTrue(modelGameBoard.checkForSpace(testBoard, 3, 0, 0, 1, 0));
    }

    /**
     * Tests ModelGameBoard's shoot method
     */
    @Test public void testModelGameBoardShoot(){
        //Target an empty location to shoot
       int[] target = getTarget(modelGameBoard.computerBoard, false);
        boolean[] effect = modelGameBoard.shoot(ModelGameBoard.PLAYER_NAME, target[0], target[1]);
        //No Hit
        assertFalse(effect[0]);
        //No Sink
        assertFalse(effect[1]);
        //Target an occupied location to shoot
         target = getTarget(modelGameBoard.computerBoard, true);
         effect = modelGameBoard.shoot(ModelGameBoard.PLAYER_NAME, target[0], target[1]);
        // Hit
        assertTrue(effect[0]);
    }
    public int[] getTarget(int[][] board, boolean occupiedLocation){
        int[] target = new int[2];
        Random random = new Random();
        boolean shipAtLocation;
        do{
            target[0] = random.nextInt(ModelGameBoard.DIMENSION);
            target[1] = random.nextInt(ModelGameBoard.DIMENSION);
            shipAtLocation = modelGameBoard.shipAt(board, target[0], target[1]);
        }while(shipAtLocation != occupiedLocation);
        return target;
    }
    public int getModelGameBoardShipCounts(ArrayList<int[]> shipLocations){
        int sum = 0;
        Iterator<int[]> iter = shipLocations.iterator();
        for(int[] location : shipLocations){
            int typeX = location[2] - location[0];
            int typeY = location[3] - location[1];
            if(typeX > typeY) sum += (typeX+1);
            else sum += typeY+1;
        }
        return sum;
    }

    /**
     * Test that the GameEnemyBrain instance is creating an internal board to track its shots.
     */
    @Test public void testGameEnemyBrain(){
        //Test internal tracking board
    gameEnemyBrain.setInternalGameBoard(20);
    assertEquals(20, gameEnemyBrain.internalGameBoard.length);
    assertEquals(20, gameEnemyBrain.internalGameBoard[0].length);
    }

    /**
     * Test the targeting and self-updating methods of the GameEnemyBrain instance
     */
    @Test public void testGameEnemyBrainUpdateAndTargeting(){
        gameEnemyBrain.setInternalGameBoard(20);
        //Test update method
        boolean[] shotEffect = {false, false};//{hit, sink}
        int[] target = {3, 7};
        gameEnemyBrain.update(shotEffect, target);
        assertEquals(5, gameEnemyBrain.internalGameBoard[target[0]][target[1]]);
        shotEffect[0] = true;//hit = true
        target[1] = 8;
        gameEnemyBrain.update(shotEffect, target);
        assertEquals(4, gameEnemyBrain.internalGameBoard[target[0]][target[1]]);
        //Since the given target was a hit but not a sinking shot, the next target gameEnemyBrain picks
        // should be close to that target
        int[] lastTgt = target;
        target = gameEnemyBrain.getTarget();
        assertTrue(nextTargetIsCloseToLastHit(lastTgt, target));
    }
    public boolean nextTargetIsCloseToLastHit(int[] lastTarget, int[] currentTarget){
        boolean close = false;
        if((currentTarget[0] >= lastTarget[0]-2 && currentTarget[0] <= lastTarget[0]+2 && currentTarget[1] == lastTarget[1])
                || (currentTarget[1] >= lastTarget[1]-2 && currentTarget[1] <= lastTarget[1]+2 && currentTarget[0] == lastTarget[0])){
            close = true;
        }

        return close;
    }

    /**
     * Test ControllerGameBoard's turn and game condition systems
     */
    @Test public void testControllerGameBoard(){
        assertFalse(controllerGameBoard.isGameOver());
        assertFalse(controllerGameBoard.computerWins());
        assertFalse(controllerGameBoard.playerWins());
        assertEquals(ModelGameBoard.PLAYER_NAME, controllerGameBoard.currentTurn);
        assertEquals(ModelGameBoard.COMPUTER_NAME, controllerGameBoard.getNextTurn(ModelGameBoard.PLAYER_NAME));
        assertEquals(ModelGameBoard.PLAYER_NAME, controllerGameBoard.getNextTurn(ModelGameBoard.COMPUTER_NAME));
        controllerGameBoard.modelGameBoard.playerShipCount = 0;
        assertTrue(controllerGameBoard.computerWins());
        assertFalse(controllerGameBoard.playerWins());
        assertTrue(controllerGameBoard.isGameOver());
        controllerGameBoard.modelGameBoard.computerShipCount = 0;
        controllerGameBoard.modelGameBoard.playerShipCount = 2;
        assertTrue(controllerGameBoard.playerWins());
        assertFalse(controllerGameBoard.computerWins());
        assertTrue(controllerGameBoard.isGameOver());
    }

    /**
     * Test the ControllerGameBoard shoot method
     */
    @Test public void testControllerGameBoardShoot(){
        //Target an empty location to shoot
        int[] target = getTarget(controllerGameBoard.modelGameBoard.computerBoard, false);
        boolean[] effect = controllerGameBoard.shoot(ModelGameBoard.PLAYER_NAME, target[0], target[1]);
        //No Hit
        assertFalse(effect[0]);
        //No Sink
        assertFalse(effect[1]);
        //Target an occupied location to shoot
        target = getTarget(controllerGameBoard.modelGameBoard.computerBoard, true);
        effect = controllerGameBoard.shoot(ModelGameBoard.PLAYER_NAME, target[0], target[1]);
        // Hit
        assertTrue(effect[0]);
    }
}
