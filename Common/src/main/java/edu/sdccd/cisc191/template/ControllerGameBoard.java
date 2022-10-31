package edu.sdccd.cisc191.template;

import java.util.Random;

/**
 * Adapted from the Week 3 JavaFX Lab
 * This controls the game.
 */
public class ControllerGameBoard {
    public Player[] gamePlayers;
    public boolean gameOver;
    public String currentTurn;
    public int currentPlayerTurnId;
    public String currentMessage;
    public int winningPlayerId;
    /**
     * The only constructor for the ControllerGameBoard.
     */
    protected ControllerGameBoard(){
    }

    public ControllerGameBoard(Player[] players){
        gamePlayers = players;
        Random rand = new Random();
        int randomPlayer = rand.nextInt(gamePlayers.length);
        currentTurn = gamePlayers[randomPlayer].name;
        currentPlayerTurnId = gamePlayers[randomPlayer].id;
        currentMessage = "Current Turn: Player "+(currentPlayerTurnId+1);
    }


    /**
     *
     * @return true if the game is over
     */
    public boolean isGameOver(){
        return gameOver;
    }



    /**
    *Returns the int id of the other player; It alternates between the players
     */

    public int getNextPlayerId(int currentId){
        int nextId = currentId;
        for(int i=0;i<gamePlayers.length;i++){
            if(gamePlayers[i].id != currentId){
                nextId = gamePlayers[i].id;
                break;
            }
        }
        //System.out.println("Next ID: "+nextId);
        return nextId;
    }

  /**
   * Checks to see if the current player won and returns a bool
   */
    private boolean didPlayerWin(int playerId){
        boolean playerWon = false;

        for(int i=0;i<gamePlayers.length;i++){
            if(gamePlayers[i].id != playerId){
                playerWon = (gamePlayers[i].shipCount == 0);
            }
        }
            return playerWon;
    }

    /**
     * Checks for a win condition and sets the appropriate variables to indicate that.
     */
    private void checkForWin(int playerId){
        boolean win = didPlayerWin(playerId);
        if(win){
            winningPlayerId = playerId;
            gameOver = true;
            currentMessage = "Player "+(playerId+1)+" won!";
        }
    }

    /**
     * Checks the target location of playerId's opponent for a hit and sink condition, updates the players,
     * then checks for win condition.
     */
    public boolean[] shoot(int playerId, int[] target){
        int x = target[0];
        int y = target[1];
        boolean[] shotEffect = new boolean[2];
                //If location is not empty [0] or already hit [4] acknowledge hit and change location to 4
        int opponentIndex = getNextPlayerId(playerId);
        shotEffect[0] = gamePlayers[opponentIndex].shipAt(x, y);
        gamePlayers[opponentIndex].update(shotEffect, target);
        shotEffect[1] = gamePlayers[opponentIndex].isShipSunk(x, y);
       if(shotEffect[1]){
         gamePlayers[playerId].enemyShipsSunk++;
         gamePlayers[opponentIndex].shipsSunk++;
         gamePlayers[opponentIndex].shipCount--;
       }

       checkForWin(playerId);
       if(!gameOver) {
         currentPlayerTurnId = opponentIndex;
           currentMessage = "Current Turn: Player "+(currentPlayerTurnId+1);
       }
        return shotEffect;
    }
}