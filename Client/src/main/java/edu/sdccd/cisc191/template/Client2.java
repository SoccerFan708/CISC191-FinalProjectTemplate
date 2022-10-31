package edu.sdccd.cisc191.template;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;


public class Client2 extends Application implements BattleShipConstants {
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private Scene scene;
    private BorderPane gameCanvasBorderPane;
    private Canvas tileGrid;
    private GameBoardLabel enemyShipsSunk;
    private GameBoardLabel shipsSunk;
    private GameBoardLabel shipCount;
    private GameBoardLabel message;
    PlayerRequest myRequest;
    ServerResponse serverResponse;
    private int[][] board;
    static Client client;
    Stage myStage;
    private int id = -1;
    boolean connectionMade = false;

    public void startConnection(String ip, int port) {
        try {
            client = new Client();
            //client.startConnection("127.0.0.1", 4444);
            socket = new Socket(ip, port);
            // System.out.println("Going in");
            out = new ObjectOutputStream(socket.getOutputStream());
            // System.out.println("After out");
            in = new ObjectInputStream(socket.getInputStream());
            // System.out.println("After in");
            myRequest = new PlayerRequest(id, null);
            connectionMade = true;
             talkToServer();
            //new Thread(new clientThread()).start();
        }catch(Exception e){}
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public void talkToServer(){
        // myRequest = new PlayerRequest();

        try {
            //
            out.reset();
            out.writeObject(myRequest);
            listenToServer();
            //client.stopConnection();
            System.out.println(serverResponse);

        }catch(Exception e){
            System.out.println("Talking to server...Exception");
            e.printStackTrace();
        }
    }
    public void listenToServer(){
        try{
            serverResponse = (ServerResponse) in.readObject();
        }catch (Exception e){}
    }


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        //controller = new ControllerGameBoard();
        //talkToServer();
        tileGrid = new Canvas();
        enemyShipsSunk = new GameBoardLabel(1);
        message = new GameBoardLabel(1);
        shipsSunk = new GameBoardLabel(1);
        shipCount = new GameBoardLabel(1);
        gameCanvasBorderPane = new BorderPane();
        myStage = stage;
        // player = new Player();
        if(!connectionMade){startConnection("localhost",4445);}
        // talkToServer();
        update();
        initial = true;
        display();
    }

    public void stop() throws IOException {
        stopConnection();
    }
    GridPane buttonGrid = new GridPane();
    boolean initial;
    public void display(){
        BorderPane root = new BorderPane();
        if(initial) {
            for (int col = 0; col < DIMENSION; col++)
                for (int row = 0; row < DIMENSION; row++) {
                    GameBoardButton button = new GameBoardButton(row, col);
                    int finalRow = row;
                    int finalCol = col;
                    button.setOnAction(e -> {
                        //myRequest.setTarget(null);
                        //talkToServer();
                        if (!serverResponse.isGameOver && serverResponse.isMyTurn) {
                            int[] target = {finalRow, finalCol};
                            myRequest = new PlayerRequest(id, target);
                            System.out.println(myRequest);
                            talkToServer();
                            button.handleClick(serverResponse.wasLastTargetAHit);
                            update();
                        }

                    });
                    buttonGrid.add(button, row, col);
                }
            initial = false;
        }
        root.setLeft(buttonGrid);
        root.setCenter(gameCanvasBorderPane);

        scene = new Scene(root, 830, 500, Color.WHITE);
        myStage.setTitle("BattleShip!");
        myStage.setScene(scene);
        myStage.show();
    }
    public void update() {
        if(serverResponse != null) {
            enemyShipsSunk.setText("Enemy Ships Sunk: "+serverResponse.enemyShipsSunk);
            shipsSunk.setText("Ships Lost: "+serverResponse.shipsLost);
            shipCount.setText("Ships Remaining: "+serverResponse.shipcount);
            message.setText(serverResponse.message);
            board = serverResponse.board;
            id = serverResponse.playerId;
            System.out.println(serverResponse.message);
            if (serverResponse.isGameOver) {
                message = new GameBoardLabel(2);
                message.setText(serverResponse.message);
                //else message.setText("You Won!");
            }
            if(serverResponse.alertType != 0){
                alert(serverResponse.alertType);
            }
            draw();
            display();
        }

    }
    private void draw(){
        //Create a 10 X 10 grid of tiles to represent the players game board. This grid will display the computer's shots.
        // int TILE_W = 30 = TILE_H;
        displayBoard();
        tileGrid = new BoardDisplay(board, TILE_W, TILE_H, Color.BLUE).getDisplayBoard();
        VBox gameHud = new VBox(enemyShipsSunk, shipsSunk, shipCount, message);
        gameCanvasBorderPane.setTop(gameHud);
        gameCanvasBorderPane.setBottom(tileGrid);
    }

    public void alert(int alertType){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alert");

        if(alertType == 2){
            System.out.println("ALERT");
            alert.setHeaderText("There is a game already in session! You will play against the computer until the other game is over.");
            Optional<ButtonType> option = alert.showAndWait();
            if(ButtonType.OK.equals(option.get())){
                myRequest.setAlertResponse(1);
                talkToServer();
            }
        }
    }
    public void displayBoard(){
        for(int i=0;i<board.length;i++) {
            for (int j=0;j<board[i].length;j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    class clientThread implements Runnable{
        public clientThread(){
        }

        public void run(){
            try {
                while(connectionMade) {
                    listenToServer();
                    //update();
                    Thread.sleep(1000);
                }

            }catch(Exception e){}

        }
    }

} //end class Client

