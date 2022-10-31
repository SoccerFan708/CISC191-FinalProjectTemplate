package edu.sdccd.cisc191.template;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/**
 * The goal was to create an Application that would display all game sessions. Threads became more challenging to handle.
*/

public class Server extends Application implements BattleShipConstants{
    private ServerSocket serverSocket;
    private ServerSocket[] allServerSockets;
    private int[] listeningPorts = {4444, 4445};
    private ArrayList<Socket> playerSockets;
    private boolean gameInSession = false;
    public void start(Stage stage){
        start();
    }

    public void start() {
        playerSockets = new ArrayList<>();
        allServerSockets = new ServerSocket[2];
        new Thread(() -> {
            try{
               // serverSocket = new ServerSocket(4444);
                for(int a =0;a<allServerSockets.length;a++) {
                    allServerSockets[a] = new ServerSocket(listeningPorts[a]);
                }
                System.out.println("Waiting for client connection...");
                while(true) {
                    Socket socket1 = allServerSockets[playerSockets.size()].accept();
                    System.out.println("Player "+(playerSockets.size()+1)+" connected.");
                        playerSockets.add(socket1);
                    new Thread(new HandleClients(socket1)).start();
                    /*
                    if(playerSockets.size() == 2){
                        new Thread(new HandleBothClients()).start();
                    }
                    */
                }
            }catch(Exception e){
            }

        }).start();

    }
    /**
     * This is the client handling class that runs on a new thread when a socket connection is made
    */
    class HandleClients implements Runnable{
        private Socket sessionSocket;
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        ControllerGameBoard sessionController;
        int PlayerId;
        public HandleClients(Socket passedSocket){
            sessionSocket = passedSocket;
                try {
                    inputStream = new ObjectInputStream(sessionSocket.getInputStream());
                    outputStream = new ObjectOutputStream(sessionSocket.getOutputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
           // }
            Player[] gmPlayers = new Player[2];
            if(!gameInSession) {
                    gmPlayers[0] = new Player(DIMENSION);
                    gmPlayers[0].placeShips(TOTAL_SHIP_TILE_COUNT);
                    gmPlayers[0].id = 0;
                    //gmPlayers[0].setSocket(sessionSocket);
                    PlayerId = gmPlayers[0].id;
            }else{
                gmPlayers = new Player[2];
                gmPlayers[1] = new Player(DIMENSION);
                gmPlayers[1].placeShips(TOTAL_SHIP_TILE_COUNT);
                gmPlayers[1].id = 1;
                PlayerId = gmPlayers[1].id;
            }
            setGameUp(gmPlayers);
        }
        /**
         * Reads in a request from a client specified by index
         */
        public PlayerRequest listenToClient() throws Exception{
            return (PlayerRequest) inputStream.readObject();
        }

        /**
        * Creates a controller for a game session.
         */
        public void setGameUp(Player[] gmPlayers){
            for (int i = 0; i < gmPlayers.length; i++) {
                if (gmPlayers[i] == null) {
                    System.out.println("Playing against the Computer.");
                    gmPlayers[i] = new ComputerPlayer(DIMENSION);
                    gmPlayers[i].placeShips(TOTAL_SHIP_TILE_COUNT);
                    gmPlayers[i].id = i;
                }

            }
            sessionController = new ControllerGameBoard(gmPlayers);
            if(gameInSession){
             talkToClients( 2, false) ;
             }
          // gameInSession = true;
        }
        /**
         * Writes a server response to a client
         */
        public void talkToClients(int alertType, boolean shotEffect){
            try {
                Player player = sessionController.gamePlayers[0];
                String message = sessionController.currentMessage;
                 if(sessionController.isGameOver() && sessionController.winningPlayerId == player.id){
                    message = "You won!";
                }
                ServerResponse response = new ServerResponse(player.id, player.board, sessionController.isGameOver(),
                        (sessionController.currentPlayerTurnId == player.id), shotEffect, message);
                response.setNumbers(player.shipCount, player.shipsSunk, player.enemyShipsSunk);
                response.setAlertType(alertType);
                System.out.println("Response to be sent: " + response);
                outputStream.reset();
                outputStream.writeObject(response);
                //player.sendResponse(response);
            }catch (Exception e)
            {}
        }
        /**
         * Calls on the opponent to make a move.
         */
        public void getOpponentAction(Player player, ControllerGameBoard controller){
            if(controller.currentPlayerTurnId == player.id){
                //Polymorphic call to getTarget() and updateWithShotEffect();
                    int[] target = player.getTarget();
                    player.updateWithShotEffect(controller.shoot(player.id, target), target);
            }else{
                if(!controller.isGameOver()) {
                    controller.currentMessage = "Your Move";
                }
            }
        }
        @Override
        public void run() {
            try {
                    while (!sessionController.isGameOver()) {
                        int index = PlayerId;
                            Player player = sessionController.gamePlayers[index];
                            Player opponent = sessionController.gamePlayers[sessionController.getNextPlayerId(player.id)];
                            PlayerRequest requestFromClient = listenToClient();
                            if (requestFromClient.getHandShake().equals(HANDSHAKE)) {
                                if (requestFromClient.getTarget() == null) {
                                    getOpponentAction(opponent, sessionController);
                                    talkToClients(0, false);
                                } else {
                                    boolean[] shotEffect = sessionController.shoot(player.id, requestFromClient.getTarget());
                                    getOpponentAction(opponent,sessionController);
                                    talkToClients(0, shotEffect[0]);
                                }
                            }
            }
            }catch (Exception e){
            }
        }
    }

    class HandleBothClients implements Runnable{

        private ObjectOutputStream[] out;
        private ObjectInputStream[] in;
        private ControllerGameBoard sessionController;
        private boolean firstMoveMade = false;
        public HandleBothClients(){
            Player[] players = new Player[2];
            out = new ObjectOutputStream[2];
            in = new ObjectInputStream[2];
            for(Socket s: playerSockets){
                try {
                    in[playerSockets.indexOf(s)] = new ObjectInputStream(s.getInputStream());
                    out[playerSockets.indexOf(s)] = new ObjectOutputStream(s.getOutputStream());
                    players[playerSockets.indexOf(s)] = new Player(DIMENSION);
                    players[playerSockets.indexOf(s)].placeShips(TOTAL_SHIP_TILE_COUNT);
                    players[playerSockets.indexOf(s)].id = playerSockets.indexOf(s);
                }catch (Exception e){
                    System.out.println("Exception encountered: "+e.getMessage());
                    //e.printStackTrace();
                    }
            }
            sessionController = new ControllerGameBoard(players);
        }
        @Override
        public void run() {
            try {
                while (!sessionController.isGameOver()) {
                    //for(int i=0;i<sessionController.gamePlayers.length;i++) {
                        PlayerRequest requestFromClient = listenToClient(sessionController.currentPlayerTurnId);
                        if (requestFromClient.getHandShake().equals(HANDSHAKE)) {
                            while (requestFromClient.getTarget() == null){
                                    System.out.println("Things are null");
                                    talkToClients(sessionController.getNextPlayerId(sessionController.currentPlayerTurnId), 0, false);
                                    talkToClients(sessionController.currentPlayerTurnId, 0, false);
                                     requestFromClient = listenToClient(sessionController.currentPlayerTurnId);
                            }
                            getAction(requestFromClient.getTarget());
                        }
                    //}
                }
            }catch (Exception e){
            }
        }

        public PlayerRequest listenToClient(int index) throws Exception{
            return (PlayerRequest) in[index].readObject();
        }

        /**
         * Writes a server response to a client
         */
        public void talkToClients(int index, int alertType, boolean shotEffect){
            try {
                    Player player = sessionController.gamePlayers[index];
                    String message = sessionController.currentMessage;
                    if (sessionController.isGameOver() && sessionController.winningPlayerId == player.id) {
                        message = "You won!";
                    }
                    if(!sessionController.isGameOver() && sessionController.currentPlayerTurnId == player.id){
                        message = "Your move";
                }
                    ServerResponse response = new ServerResponse(player.id, player.board, sessionController.isGameOver(),
                            (sessionController.currentPlayerTurnId == player.id), shotEffect, message);
                    response.setNumbers(player.shipCount, player.shipsSunk, player.enemyShipsSunk);
                    response.setAlertType(alertType);
                    System.out.println("Response to be sent: " + response);
                    out[index].reset();
                    out[index].writeObject(response);
                    //player.sendResponse(response);

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        public void getAction(int[] target) {
            try {
                boolean[] effects = sessionController.shoot(sessionController.currentPlayerTurnId, target);
                System.out.println("Get Action#1");
                talkToClients(sessionController.getNextPlayerId(sessionController.currentPlayerTurnId), 0, effects[0]);
                talkToClients(sessionController.currentPlayerTurnId, 0, false);
            }catch (Exception e){e.printStackTrace();}
        }
    }

    public void stop() throws IOException {
        for(Socket s : playerSockets){
            int index = new ArrayList<>(playerSockets).indexOf(s);
            s.close();
        }
        serverSocket.close();
    }

    /**
     * Debugging purposes
     */
    private void displayBoard(int[][] board){
        for(int i=0;i<board.length;i++) {
            for (int j=0;j<board[i].length;j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }
    public static void main(String[] args) {
        launch();
        Server server = new Server();
        try {
            server.start();
            //server.stop();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
} //end class Serve