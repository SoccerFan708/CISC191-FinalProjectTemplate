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
    private ServerResponse serverResponse;
    private PlayerRequest playerRequest;
    private int[][] board;
    ControllerGameBoard controllerGameBoard;
    Player player;
    ComputerPlayer computerPlayer;
    final static int DIMENSION = 10;
    final static int SHIP_TILE_COUNT = 10;

    /**
     * Sets up for the tests
     */
    @BeforeAll public void setUp(){
        board = new int[5][5];
        serverResponse = new ServerResponse(1, board, false, false, false, "Hello client!" );
        playerRequest = new PlayerRequest(1, null);
        player = new Player(DIMENSION);
        player.id = 0;
        player.placeShips(SHIP_TILE_COUNT);
        computerPlayer = new ComputerPlayer(DIMENSION);
        computerPlayer.id = 1;
        computerPlayer.placeShips(SHIP_TILE_COUNT);
        Player[] players = {player, computerPlayer};
        controllerGameBoard = new ControllerGameBoard(players);
        //testBoard = new int[DIMENSION][DIMENSION];
       // playerShipLocations = modelGameBoard.playerShipLocations;
       // computerShipLocations = modelGameBoard.computerShipLocations;
    }

    /**
     * Test that both player and computerPlayer initialize, create game boards, and ships
     */
    @Test public void testPlayerConstructions(){
        assertEquals(DIMENSION, player.board.length);
        assertEquals(DIMENSION, computerPlayer.board[0].length);
        //The total tiles occupied by each player's ship must add to 10, the static SHIP_TILE_COUNT;
        assertEquals(SHIP_TILE_COUNT, getShipCounts(player.shipLocations));
        assertEquals(SHIP_TILE_COUNT, getShipCounts(computerPlayer.shipLocations));
    }

    /**
     * Tests ControllerGameBoard's shoot method
     */
    @Test public void testControllerGameBoardShoot(){
        //Target an empty location to shoot
       int[] target = getTarget(computerPlayer, false);
        boolean[] effect = controllerGameBoard.shoot(player.id, target);
        //No Hit
        assertFalse(effect[0]);
        //No Sink
        assertFalse(effect[1]);
        //Target an occupied location to shoot
         target = getTarget(player, true);
         effect = controllerGameBoard.shoot(computerPlayer.id, target);
        // Hit
        assertTrue(effect[0]);
    }
    public int[] getTarget(Player player, boolean occupiedLocation){
        int[] target = new int[2];
        Random random = new Random();
        boolean shipAtLocation;
        do{
            target[0] = random.nextInt(player.board[0].length);
            target[1] = random.nextInt(player.board.length);
            shipAtLocation = player.shipAt(target[0], target[1]);
        }while(shipAtLocation != occupiedLocation);
        return target;
    }
    public int getShipCounts(ArrayList<int[]> shipLocations){
        int sum = 0;
        Iterator<int[]> iter = shipLocations.iterator();
        for(int[] location : shipLocations){
            int typeX = location[3] - location[1];
            int typeY = location[2] - location[0];
            if(typeX > typeY) sum += (typeX+1);
            else sum += (typeY+1);
        }
        return sum;
    }

    /**
     * Test that the computer player instance is creating an internal board to track its shots.
     */
    @Test public void testComputerPlayer(){
        //Test internal tracking board
    assertEquals(DIMENSION, computerPlayer.internalGameBoard.length);
    assertEquals(DIMENSION, computerPlayer.internalGameBoard[0].length);
    }

    /**
    *Test ServerResponse
     */
     @Test void  getServerResponse() {
        assertEquals(serverResponse.toString(), "Player id: 1, Message id: 1, Game Over: false, " +
                "My turn: false, Message: Hello client!, Alert: 0, " +
                "Ship Count: 0, Enemy Ships Sunk: 0, Ships Lost: 0");
    }

    @Test    void setNumbers() {
        serverResponse.setNumbers(5, 1, 3);
        assertEquals(serverResponse.toString(), "Player id: 1, Message id: 1, Game Over: false, " +
                "My turn: false, Message: Hello client!, Alert: 0, " +
                "Ship Count: 5, Enemy Ships Sunk: 3, Ships Lost: 1");
    }

    /**
     * Test PlayerRequest
     */

    @Test void testPlayerRequest(){
        assertEquals(playerRequest.toString(), "Player request[id: 1, target: null, alertResponse: 0");
        int[] target = {1, 3};
        playerRequest.setTarget(target);
        assertEquals(playerRequest.toString(), "Player request[id: 1, target_x: 1_y: 3, alertResponse: 0");
    }

    /**
     * Test the targeting and self-updating methods of the computerPlayer instance
     */
    @Test public void testComputerPlayerUpdateAndTargeting(){
        //computerPlayer.setInternalGameBoard(20);
        //Test update method
        boolean[] shotEffect = {false, false};//{hit, sink}
        int[] target = {3, 7};
        computerPlayer.updateWithShotEffect(shotEffect, target);
        assertEquals(5, computerPlayer.internalGameBoard[target[0]][target[1]]);
        shotEffect[0] = true;//hit = true
        target[1] = 8;
        computerPlayer.updateWithShotEffect(shotEffect, target);
        assertEquals(4, computerPlayer.internalGameBoard[target[0]][target[1]]);
        //Since the given target was a hit but not a sinking shot, the next target gameEnemyBrain picks
        // should be close to that target
        int[] lastTgt = target;
        target = computerPlayer.getTarget();
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



}
