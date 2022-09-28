package edu.sdccd.cisc191.template;

/**
 * Adapted from the Week 3 JavaFX Lab
 * This controls the game.
 */
public class ControllerGameBoard {
    public ModelGameBoard modelGameBoard;
    public GameEnemyBrain enemyBrain;
    public boolean gameOver;
    public String currentTurn;

    /**
     * The only constructor for the ControllerGameBoard.
     */
    public ControllerGameBoard(){
        modelGameBoard = new ModelGameBoard();
        enemyBrain = new GameEnemyBrain();
        enemyBrain.setInternalGameBoard(ModelGameBoard.DIMENSION);
        currentTurn = ModelGameBoard.PLAYER_NAME;
    }

    /**
     * Checks to see if player ship count is 0, then it ends the game.
     * @return true for a computer win if player ship count is 0.
     */
    public boolean computerWins(){
        boolean computerWins = modelGameBoard.playerShipCount == 0;
        if(computerWins){
            gameOver = true;
        }
        return computerWins;
    }
    /**
     * Checks to see if computer ship count is 0, then it ends the game.
     * @return true for a player win if computer ship count is 0.
     */
    public boolean playerWins(){
        boolean playerWins = modelGameBoard.computerShipCount == 0;
        if(playerWins){
            gameOver = true;
        }
        return playerWins;
    }

    /**
     *
     * @return true if the game is over
     */
    public boolean isGameOver(){
        return gameOver;
    }

    /**
     *
     * @param shooter is a String representation of player
     * @param row is an int value representing the row
     * @param col is an int value representing the column
     * It tells the game instance of the model game board to take a shot on the location row_col.
     * It receives a boolean array of size 2 that shows whether the shot was a hit and whether an enemy ship was sunk.
     * It also gets the player whose turn it is.
     * @return the received boolean array.
     */
    public boolean[] shoot(String shooter, int row, int col){
        boolean[] effect = modelGameBoard.shoot(shooter, row, col);
        currentTurn = getNextTurn(shooter);
        return effect;
    }

    /**
     *
     * @param playerName
     * It takes the name of the current player whose turn just passed.
     * @return the name of the player whose turn it is.
     * It also checks for a win situation.
     */
    public String getNextTurn(String playerName){
        String nextTurn;
        if(playerName.equals(ModelGameBoard.PLAYER_NAME)){
            nextTurn = ModelGameBoard.COMPUTER_NAME;
        }else{
            nextTurn = ModelGameBoard.PLAYER_NAME;
        }
        computerWins();
        playerWins();
        return nextTurn;
    }

    /**
     * This controls the game enemy brain. It gets the enemy's next target, it shoots, and updates the enemy with the
     * effects of the shot.
     */
    public void enemyThink(){
        if(currentTurn.equals(ModelGameBoard.COMPUTER_NAME)) {
            int[] target = enemyBrain.getTarget();
            enemyBrain.update(shoot(ModelGameBoard.COMPUTER_NAME, target[0], target[1]), target);
        }
    }
}