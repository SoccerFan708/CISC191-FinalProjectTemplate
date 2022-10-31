package edu.sdccd.cisc191.template;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Player {
    public String name;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    public int id;
    public ArrayList<int[]> shipLocations;
    public int shipCount;
    public int shipsSunk;
    public int enemyShipsSunk;
    public int[][] board;

    public Player(int dimension){
     name = "player";
     shipLocations = new ArrayList<>();
     shipCount = 0;
     shipsSunk = 0;
     enemyShipsSunk = 0;
     board = new int[dimension][dimension];
    }

    /**
     * This method finds empty spaces and places ships randomly
    */
 public void placeShips(int totalShipTiles) {
  int shipTileCount = totalShipTiles;
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
      x = randomPicker.nextInt(board[0].length);
      y = randomPicker.nextInt(board.length);
     } while (board[y][x] != 0);
     board[y][x] = 1;
     ship[0] = y;
     ship[1] = x;
     ship[2] = y;
     ship[3] = x;
     break;
    case 2:
    case 3:
     boolean[] freeSpace = new boolean[4];
     do {
      x = randomPicker.nextInt(board[0].length);
      y = randomPicker.nextInt(board.length);
      //freeSpace = {left, right, up, down};
      freeSpace[0] = checkForSpace(selectedShipType, x, y, -1, 0);
      freeSpace[1] = checkForSpace(selectedShipType, x, y, 1, 0);
      freeSpace[2] = checkForSpace(selectedShipType, x, y, 0, -1);
      freeSpace[3] = checkForSpace(selectedShipType, x, y, 0, 1);
     } while (board[y][x] != 0 || (!freeSpace[0] && !freeSpace[1] && !freeSpace[2] && !freeSpace[3]));
     int shipOrientation;
     do {
      shipOrientation = randomPicker.nextInt(freeSpace.length);
     } while (!freeSpace[shipOrientation]);
     switch (shipOrientation) {
      case 0:
       for (int i = x; i > (x - (selectedShipType)); i--) {
        board[y][i] = selectedShipType;
       }
       ship[0] = y;
       ship[1] = x- (selectedShipType - 1);
       ship[2] = y;
       ship[3] = x;

       break;
      case 1:
       for (int i = x; i < (x + (selectedShipType)); i++) {
        board[y][i] = selectedShipType;
       }
       ship[0] = y;
       ship[1] = x;
       ship[2] = y;
       ship[3] = x + (selectedShipType - 1);
       break;
      case 2:
       for (int i = y; i > (y - (selectedShipType)); i--) {
        board[i][x] = selectedShipType;
       }
       ship[0] = y - (selectedShipType - 1);
       ship[1] = x;
       ship[2] = y;
       ship[3] = x;
       break;
      case 3:
       for (int i = y; i < (y + (selectedShipType)); i++) {
        board[i][x] = selectedShipType;
       }
       ship[0] = y;
       ship[1] = x;
       ship[2] = y + (selectedShipType - 1);
       ship[3] = x;
       break;
     }
     break;
   }
   shipLocations.add(ship);
   shipCount++;
  }while (shipTileCount > 0) ;
 }

 /**
  *
  * @param type the type of ship, determined by the size of the ship, ships can occupy 3, 2, or 1 tiles.
  * @param x the row location to begin checking
  * @param y the column location to begin checking
  * @param xDir either left or right
  * @param yDir either up or down
  * @return true if the given direction has free space to contain the ship type.
  */
 public boolean checkForSpace(int type, int x, int y, int xDir, int yDir){
  boolean space = true;
  switch(xDir){
   case -1:
    for(int i=x;i>(x-(type));i--){
     if(i<0 || board[y][i] != 0){
      space = false;
      break;
     }
    }
    break;
   case 1:
    for(int i=x;i<(x+(type));i++){
     if(i>= board[y].length || board[y][i] != 0) {
      space = false;
      break;
     }
    }
    break;

   case 0:
    if(yDir == -1){
     for(int i=y;i>(y-(type));i--){
      if( i < 0 || board[i][x] != 0){
       space = false;
       break;
      }
     }
    }
    if(yDir == 1){
     for(int i=y;i<(y+(type));i++){
      if(i>= board.length || board[i][x] != 0){
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
  * @param x the row location of the ship
  * @param y the col location of the ship
  * @return true if the ship at location has been hit the required number of times to sink it.
  */
 public boolean isShipSunk(int x, int y){
  boolean shipSunk = false;
  Iterator<int[]> iter = shipLocations.iterator();
  //System.out.println("Checking for sunk: Before while loop");
  while(iter.hasNext()){
   //System.out.println("Checking for sunk: Inside while loop");
   int[] location = iter.next();
   //System.out.println("loc_init: "+location[1]+"_"+location[0]+"loc_end: "+location[3]+"_"+location[2]+" target_: "+x+"_"+y);
   if(y>=location[0] && y<=location[2] && x>=location[1] && x<=location[3]) {
    //System.out.println("Checking for sunk: If check #1");
    int vSize = location[2] - location[0];
    int hSize = location[3] - location[1];
    int tileCounter = 0;
    if(vSize > 0 || (vSize == 0 && hSize == 0)){
     //System.out.println("Checking for sunk: vSize>0 || vSize=hsize=0");
     for(int i=location[0];i<=location[2];i++){
      //System.out.println("Checking for sunk: If for...loop");
      if(board[i][x] == 4){
       tileCounter++;
      }
     }
     if(tileCounter == (vSize+1)){
      shipSunk = true;
     // iter.remove();
      //System.out.println("Checking for sunk: found vertical");
     }

    }else if(hSize > 0 ){
     //System.out.println("Checking for sunk: hSize>0");
     for(int i=location[1];i<=location[3];i++){
      if(board[y][i] == 4){
       tileCounter++;
      }
     }
     if(tileCounter == (hSize+1)){
      shipSunk = true;
     // iter.remove();
      //System.out.println("Checking for sunk: found horizontal");
     }
    }
   }
  }
  return shipSunk;
 }

 /**
  *
  * @param x the row location on the board
  * @param y the column location on the board
  * @return true if there is a ship at the location.
  */
 public boolean shipAt(int x, int y){
  boolean shipAtLocation = false;
  if(board[y][x] != 0 && board[y][x] != 4 && board[y][x] != 5){
   shipAtLocation = true;
  }
  return shipAtLocation;
 }

/**
 * Update the board with opponents shotEffect
 */
 public void update(boolean[] effect, int[] target){
  if(effect[0]) {
   board[target[1]][target[0]] = 4;
  }else{
   board[target[1]][target[0]] = 5;
  }
 }

 public int[] getTarget(){
  int[] target = null;
  try{
   target = getRequest().getTarget();
  }catch(Exception e){}
  return target;
 }

 public PlayerRequest getRequest(){
  PlayerRequest request = new PlayerRequest();
  try {
   request = (PlayerRequest) in.readObject();
  }catch (Exception e){
   e.printStackTrace();;
  }
  return request;
 }

 public void sendResponse(ServerResponse response){
  try{
   out.reset();
   out.writeObject(response);
  }catch(Exception e){}
 }

 public void updateWithShotEffect(boolean[] shotEffect, int[] target){
 }

 public void setSocket(Socket s) {
  this.socket = s;
   try {
     this.in = new ObjectInputStream(s.getInputStream());
     this.out = new ObjectOutputStream(s.getOutputStream());
   } catch (Exception e) {
    e.printStackTrace();
   }
  }

}
