package edu.sdccd.cisc191.template;

import java.io.Serializable;

public class PlayerRequest implements Serializable {
    private final String handShake = "BattleShipv1";
    private int[] target;
    private int id;
    private int alertResponse;

    protected PlayerRequest(){}

    public PlayerRequest(int id, int[] target){
        this.id = id;
        this.target = target;
        this.alertResponse = 0;
    }

    public int[] getTarget() {
        return target;
    }

    public void setTarget(int[] target) {
        this.target = target;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public int getAlertResponse() {
        return alertResponse;
    }
    public void setAlertResponse(int alertResponse) {
        this.alertResponse = alertResponse;
    }

    public String getHandShake() {
        return handShake;
    }

    public String toString(){
        if(target != null) {
            return "Player request[id: " + id + ", target_x: " + target[0] + "_y: " + target[1]+", alertResponse: "+alertResponse;
        }else{
            return "Player request[id: " + id + ", target: null, alertResponse: "+alertResponse;
        }
    }
}
