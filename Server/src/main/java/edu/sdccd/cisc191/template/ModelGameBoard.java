package edu.sdccd.cisc191.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ModelGameBoard {
    public static int DIMENSION = 10;
    public static String PLAYER_NAME = "player";
    public static String COMPUTER_NAME = "computer";
    public static int SHIP_TILE_COUNT = 10; //Total number of tiles that can be occupied by ships for each player.

    public int[][] computerBoard;
    public int computerShipCount;
    public int playerShipsSunk;
    public ArrayList<int[]> computerShipLocations;
    public int[][] playerBoard;
    public int playerShipCount;
    public int computerShipsSunk;
    public ArrayList<int[]> playerShipLocations;
    public String currentMessage;
    public ModelGameBoard() {
        playerShipCount = 0;
        playerShipsSunk = 0;
        computerShipCount = 0;
        computerShipsSunk = 0;
        playerShipLocations = new ArrayList<>();
        computerShipLocations = new ArrayList<>();
        computerBoard = new int[DIMENSION][DIMENSION];
        placeShips(computerBoard, COMPUTER_NAME);
        playerBoard = new int[DIMENSION][DIMENSION];
        placeShips(playerBoard, PLAYER_NAME);
    }

    /**
     * This method populates a given game board with ships of random types and in random locations.
     * @param board the board to populate with ships
     * @param playerType determines which player the ships will belong to.
     */
    public void placeShips(int[][] board, String playerType) {
        int shipTileCount = SHIP_TILE_COUNT;
        Random randomPicker = new Random();
        do {
            int selectedShipType;
            do {
                selectedShipType = randomPicker.nextInt(3)+1;
            } while ((shipTileCount - selectedShipType) < 0);
            shipTileCount = shipTileCount - selectedShipType;
            int x, y;
            int[] ship = new int[4];
           // Ship newShip;
            switch (selectedShipType) {
                case 1:
                    do {
                        x = randomPicker.nextInt(DIMENSION);
                        y = randomPicker.nextInt(DIMENSION);
                    } while (board[x][y] != 0);
                    board[x][y] = 1;
                    ship[0] = x;
                    ship[1] = y;
                    ship[2] = x;
                    ship[3] = y;
                   // newShip = new Ship(x, y, x, y);
                    break;
                case 2:
                case 3:
                    boolean[] freeSpace = new boolean[4];
                    do {
                        x = randomPicker.nextInt(DIMENSION);
                        y = randomPicker.nextInt(DIMENSION);
                        //freeSpace = {left, right, up, down};
                        freeSpace[0] = checkForSpace(board, selectedShipType, x, y, -1, 0);
                        freeSpace[1] = checkForSpace(board, selectedShipType, x, y, 1, 0);
                        freeSpace[2] = checkForSpace(board, selectedShipType, x, y, 0, -1);
                        freeSpace[3] = checkForSpace(board, selectedShipType, x, y, 0, 1);
                    } while (board[x][y] != 0 || (!freeSpace[0] && !freeSpace[1] && !freeSpace[2] && !freeSpace[3]));
                    int shipOrientation;
                    do {
                        shipOrientation = randomPicker.nextInt(freeSpace.length);
                    } while (!freeSpace[shipOrientation]);
                    switch (shipOrientation) {
                        case 0:
                            for (int i = x; i > (x - (selectedShipType)); i--) {
                                board[i][y] = selectedShipType;
                            }
                            ship[0] = x - (selectedShipType - 1);
                            ship[1] = y;
                            ship[2] = x;
                            ship[3] = y;

                            break;
                        case 1:
                            for (int i = x; i < (x + (selectedShipType)); i++) {
                                board[i][y] = selectedShipType;
                            }
                            ship[0] = x;
                            ship[1] = y;
                            ship[2] = x + (selectedShipType - 1);
                            ship[3] = y;
                            break;
                        case 2:
                            for (int i = y; i > (y - (selectedShipType)); i--) {
                                board[x][i] = selectedShipType;
                            }
                            ship[0] = x;
                            ship[1] = y - (selectedShipType - 1);
                            ship[2] = x;
                            ship[3] = y;
                            break;
                        case 3:
                            for (int i = y; i < (y + (selectedShipType)); i++) {
                                board[x][i] = selectedShipType;
                            }
                            ship[0] = x;
                            ship[1] = y;
                            ship[2] = x;
                            ship[3] = y + (selectedShipType - 1);
                            break;
                    }
                    break;
            }
            if (playerType.equals(PLAYER_NAME)) {
                playerShipCount++;
                playerShipLocations.add(ship);
            } else {
                computerShipCount++;
                computerShipLocations.add(ship);
            }
        }while (shipTileCount > 0) ;
    }

    /**
     *
     * @param board the game board to be checked
     * @param type the type of ship, determined by the size of the ship, ships can occupy 3, 2, or 1 tiles.
     * @param x the row location to begin checking
     * @param y the column location to begin checking
     * @param xDir either left or right
     * @param yDir either up or down
     * @return true if the given direction has free space to contain the ship type.
     */
    public boolean checkForSpace(int[][] board, int type, int x, int y, int xDir, int yDir){
        boolean space = true;
        switch(xDir){
            case -1:
                for(int i=x;i>(x-(type));i--){
                    if(i<0 || board[i][y] != 0){
                        space = false;
                        break;
                    }
                }
                break;
            case 1:
                for(int i=x;i<(x+(type));i++){
                    if(i>= DIMENSION || board[i][y] != 0) {
                        space = false;
                        break;
                    }
                }
                break;

            case 0:
                if(yDir == -1){
                    for(int i=y;i>(y-(type));i--){
                        if( i < 0 || board[x][i] != 0){
                            space = false;
                            break;
                        }
                    }
                }
                if(yDir == 1){
                    for(int i=y;i<(y+(type));i++){
                        if(i>=DIMENSION || board[x][i] != 0){
                            space = false;
                            break;
                        }
                    }
                }
                break;
        }
        return space;
    }

    /**
     *
     * @param shooter the String name of the player doing the shooting
     * @param x the row location of the target
     * @param y the column location of the target
     * @return a boolean array where the boolean at index 0 represents whether the shot was a hit and the
     *  boolean at index 1 represents whether the shot sunk a ship.
     */
    public boolean[] shoot(String shooter, int x, int y){
        boolean[] shotEffect = new boolean[2];
        switch(shooter){
            case "player":
                //If location is not empty [0] or already hit [4] acknowledge hit and change location to 4
                if(computerBoard[x][y] != 0 && computerBoard[x][y] != 4) {
                    shotEffect[0] = true;
                    System.out.println("Enemy ship at " + x + "_" + y + " was hit!");
                    System.out.println(computerBoard[x][y]);
                }
                computerBoard[x][y] = 4;
                shotEffect[1] = isShipSunk(x, y, computerBoard, computerShipLocations);
                if(shotEffect[1]){
                    computerShipsSunk++;
                    computerShipCount--;
                }

                break;
            case "computer":
                if(playerBoard[x][y] != 0 && playerBoard[x][y] != 4 && playerBoard[x][y] != 5) {
                    shotEffect[0] = true;
                    playerBoard[x][y] = 4;
                    currentMessage = ("Enemy shot at " + x + "_" + y + " was a hit!");
                   // System.out.println(currentMessage);
                }else{
                    currentMessage = ("Enemy shot at " + x + "_" + y + " was a miss!");
                    playerBoard[x][y] = 5;
                }

                shotEffect[1] = isShipSunk(x, y, playerBoard, playerShipLocations);
                if(shotEffect[1]){
                    playerShipsSunk++;
                    playerShipCount--;
                    System.out.println("Your Ships Sunk: "+playerShipsSunk);
                    System.out.println("Your Ships Remaining: "+playerShipCount);
                }

                break;
        }
        return shotEffect;
    }

    /**
     *
     * @param x the row location of the ship
     * @param y the col location of the ship
     * @param board the game board to check
     * @param locations the ship locations of the board
     * @return true if the ship at location has been hit the required number of times to sink it.
     */
    public boolean isShipSunk(int x, int y, int[][] board, ArrayList<int[]> locations){
        boolean shipSunk = false;
        Iterator<int[]> iter = locations.iterator();
        while(iter.hasNext()){
            int[] location = iter.next();
            if(x>=location[0] && x<=location[2] && y>=location[1] && y<=location[3]) {
                int vSize = location[2] - location[0];
                int hSize = location[3] - location[1];
                int tileCounter = 0;
                if(vSize > 0 || (vSize == 0 && hSize == 0)){
                    for(int i=location[0];i<=location[2];i++){
                        if(board[i][y] == 4){
                            tileCounter++;
                        }
                    }
                    if(tileCounter == (vSize+1)){
                        shipSunk = true;
                    }

                }else if(hSize > 0 ){
                    for(int i=location[1];i<=location[3];i++){
                        if(board[x][i] == 4){
                            tileCounter++;
                        }
                    }
                    if(tileCounter == (hSize+1)){
                        shipSunk = true;
                    }
                }
            }
        }

        return shipSunk;
    }

    /**
     *
     * @param board the game board to check
     * @param x the row location on the board
     * @param y the column location on the board
     * @return true if there is a ship at the location.
     */
    public boolean shipAt(int[][] board, int x, int y){
        boolean shipAtLocation = false;
        if(board[x][y] != 0 && board[x][y] != 4){
            shipAtLocation = true;
        }
        return shipAtLocation;
    }

    /*class Ship{
        public int frontX, frontY, backX, backY, type;

        Ship(int fX, int fY, int bX, int bY){
            frontX = fX;
            frontY = fY;
            backX = bX;
            backY = bY;
            int diffX = frontX - backX;
            int diffY = frontY - backY;
            if(diffX > diffY){
                type = diffX;
            }else if(diffX < diffY) type = diffY;
            else {type = 1;
            }
        }
    }*/
}
