package edu.sdccd.cisc191.template;

import java.io.Serializable;

public class ServerResponse implements Serializable {
    public boolean isMyTurn = false;
    public boolean isGameOver = false;
    public int[][] board;
    public boolean wasLastTargetAHit = false;
    public String message;
    public int alertType;
    public int playerId;
    public int messageId = 0;
    //private String handShake = "";
    int shipsLost, enemyShipsSunk, shipcount;

    protected ServerResponse(){}

    public ServerResponse(int id, int[][] bd, boolean gameOver, boolean turn, boolean tgtHit, String msg){
        this.playerId = id;
        this.board = bd;
        this.isGameOver = gameOver;
        this.isMyTurn = turn;
        this.wasLastTargetAHit = tgtHit;
        this.message = msg;
        this.alertType = 0;
        messageId++;
    }

    public void setNumbers(int shipCount, int shipsLost, int enemyShipsSunk){
        this.shipcount = shipCount;
        this.shipsLost = shipsLost;
        this.enemyShipsSunk = enemyShipsSunk;
    }

    public void setAlertType(int type){
        this.alertType = type;
    }

    public String toString(){
        return "Player id: "+playerId+", Message id: "+messageId+", Game Over: "+isGameOver
                        +", My turn: "+isMyTurn+", Message: "+message+", Alert: "+alertType+", Ship Count: "+shipcount
                            +", Enemy Ships Sunk: "+enemyShipsSunk+", Ships Lost: "+shipsLost;
    }
}
