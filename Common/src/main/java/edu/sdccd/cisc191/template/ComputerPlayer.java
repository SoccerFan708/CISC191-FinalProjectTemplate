package edu.sdccd.cisc191.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ComputerPlayer extends Player{

    public int[][] internalGameBoard;
    protected boolean sink;
    protected boolean hit;
    protected int[] lastTarget;
    public ArrayList<TargetClass> possibleTargets;

    public ComputerPlayer(int dimension){
        super(dimension);
        this.name = "computer";
        setInternalGameBoard(dimension);
    }
    /**
    * Creates an internal board to track shots and their effects.
    */
    private void setInternalGameBoard(int dimension){
        internalGameBoard = new int[dimension][dimension];
        possibleTargets = new ArrayList<>();
        lastTarget = new int[2];

    }
    /**
    * Lets the Computer player know the effects of its last shot.
    */
    public void updateWithShotEffect(boolean[] shotEffect, int[] target){
        //super.update(shotEffect, target);
        lastTarget[0] = target[0];
        lastTarget[1] = target[1];
        hit = shotEffect[0];
        sink = shotEffect[1];
        if(shotEffect[0]) internalGameBoard[target[0]][target[1]] = 4;
        else internalGameBoard[target[0]][target[1]] = 5;
    }

    /**
     *
     * @return a target
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
                            //I create a TargetClass object and add it to the list of possible targets.
                            possibleTargets.add(new TargetClass(target, lastTarget));
                        }
                    }
                }
            }
        }
        //Using Collections.sort(Comparator) // I implemented Comparable therefore I did not need a Comparator.
        possibleTargets.sort(null);
        Iterator<TargetClass> iter = possibleTargets.iterator();
        target = iter.next().getTarget();
        lastTarget = target;
        iter.remove();

        return target;
    }


    /**
    * The TargetClass exists so I can easily sort the targets by distance from last target. I implemented Comparable so
     * I had to override compareTo to use the distance from last successful target.
     */
    class TargetClass implements Comparable{
        int[] target;
        int distanceFromLast;

        TargetClass(int[] target, int[] last){
            this.target = target;
            calculateDistanceFrom(last);
        }

        private void calculateDistanceFrom(int[] last){
            if(last[0] == target[0]){
                distanceFromLast = Math.abs(target[1] - last[1]);
            }else{
                distanceFromLast = Math.abs(target[0] - last[0]);
            }
        }
        public int[] getTarget(){
            return target;
        }
        @Override
        public int compareTo(Object o) {
            int rt = 0;
             if (distanceFromLast < ((TargetClass)o).distanceFromLast) {
                rt = -1;
            } else if (distanceFromLast > ((TargetClass)o).distanceFromLast) {
                rt = 1;
            }
            return rt;
        }
    }
}
