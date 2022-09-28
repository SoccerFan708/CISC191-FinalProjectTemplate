package edu.sdccd.cisc191.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameEnemyBrain {
    public int[][] internalGameBoard;
    protected boolean sink;
    protected boolean hit;
    protected int[] lastTarget;
    public ArrayList<int[]> possibleTargets;

    /**
     * Construct the enemy brain.
     */
    public GameEnemyBrain(){
        hit = false;
        sink = false;
        lastTarget = new int[2];
        lastTarget[0] = lastTarget[1] = -1;//Initialize last target to a location outside the board.
    }

    /**
     *
     * @param dimension
     * Creates an internal grid for tracking shots and hits.
     */
    public void setInternalGameBoard(int dimension){
        internalGameBoard = new int[dimension][dimension];
        possibleTargets = new ArrayList<>();
    }

    /**
     *
     * @param shotEffect
     * Updates the enemy with the success of its last shot. Updates the internal game board to indicate a successful hit or miss.
     */

    public void update(boolean[] shotEffect, int[] target){
        lastTarget[0] = target[0];
        lastTarget[1] = target[1];
        hit = shotEffect[0];
        sink = shotEffect[1];
        if(hit) {
            internalGameBoard[lastTarget[0]][lastTarget[1]] = 4;
        }else{
            internalGameBoard[lastTarget[0]][lastTarget[1]] = 5;
        }
    }
    /**
     *
     * @return a target that has not been previously shot.
     */
    public int[] getTarget(){
        Random randomPicker = new Random();
        int[] target = new int[2];
        //Pick a random location to shoot if last shot was a miss or if it sunk an enemy ship.
        if ((!hit || sink) && possibleTargets.size() == 0) {
            do {
                //target_x
                target[0] = randomPicker.nextInt(internalGameBoard.length);
                //target_y
                target[1] = randomPicker.nextInt(internalGameBoard[0].length);
            } while (internalGameBoard[target[0]][target[1]] != 0);
            lastTarget = target;
            return target;
        }else if(possibleTargets.size()==0){ //if(possibleTargets.size()==0)
            //If last shot was a hit then search around that area and find more possible targets,
            // assuming the largest ship type (size = 3) is being targeted
            int rowStart = lastTarget[0] - 2;
            int rowEnd = lastTarget[0] + 2;
            int colStart = lastTarget[1] - 2;
            int colEnd = lastTarget[1] + 2;
            for (int row = rowStart; row < rowEnd; row++) {
                for (int col = colStart; col < colEnd; col++) {
                    if (row >= 0 && row < internalGameBoard.length && col >= 0 && col < internalGameBoard[0].length) {
                        target = new int[2];
                        //Only add targets that are on the same row and column as the previous target except the previous target itself.
                        // They also have to have not been previously hit
                        if ((row == lastTarget[0] || col == lastTarget[1]) && (internalGameBoard[row][col] == 0)
                                && !(row == lastTarget[0] && col == lastTarget[1])) {
                            target[0] = row;
                            target[1] = col;
                            possibleTargets.add(target);
                        }
                    }
                }
            }
        }
        Iterator<int[]> iter = possibleTargets.iterator();
        target = iter.next();
        lastTarget = target;
        iter.remove();

        return target;
    }
}