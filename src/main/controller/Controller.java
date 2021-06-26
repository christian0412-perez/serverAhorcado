package main.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import main.model.ClientSocket;
import main.model.Nodo;
import main.model.Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Controller implements Observer {
    ServerSocket serverSocket = null;
    private final int PORT = 3001;
    private ArrayList<Nodo> poolSocket = new ArrayList<>();
    private String[] diccionario = {"ABEJA","OSO", "BANANO","PLATANO", "PERRO",  "LOBO","MOSCA","PINGUINO", "ELEFANTE",
            "GATO", "SAPO", "JIRAFA", "COCODRILO","TORTUGA","CABALLO", "LEON", "TIGRE"};
    private char[] palabra_secreta;

    @FXML
    private Button btnOpenServer;

    @FXML
    private Button btnSalir;
    @FXML
    private Circle circleLed;

    @FXML
    private ListView<String> listClient;

    @FXML
    private TextField aquiMandolol;

    @FXML
    private Button sendButtonlol;

    @FXML
    void OpenServerOnMouseClicked(MouseEvent event) {
        byte[] ipBytes = {(byte)192,(byte)168,(byte)0, (byte)19 };
        InetAddress ip = null;

        try {
            ip = InetAddress.getByAddress(ipBytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            serverSocket = new ServerSocket(PORT,100,ip);
            listClient.getItems().add("Server abierto: " + serverSocket.getInetAddress().getHostName());
            circleLed.setFill(Color.GREEN);
           Server server = new Server(serverSocket);
           server.addObserver(this);
           new Thread(server).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
/*        finally {
            try {
                serverSocket.close();
                listClient.getItems().add("Server cerrado");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

    }

    @FXML
    void SalirOnMouseClicked(MouseEvent event) {
        System.exit(1);
    }

    @Override
    public void update(Observable o, Object arg) {

        if (o instanceof Server) {
            Socket socket = (Socket)arg;
            poolSocket.add(new Nodo(socket.hashCode(),"nodo"+poolSocket.size(),socket));
            // Broadcast a todos los sockets conectados para actualizar la lista de conexiones
            //broadCast("hola");
            // Crear un hilo que reciba mensajes entrantes de ese nuevo socket creado
            ClientSocket clientSocket = new ClientSocket(socket);
            clientSocket.addObserver(this);
            new Thread(clientSocket).start();

        }
        if(o instanceof ClientSocket){
            Platform.runLater(() -> listClient.getItems().add((String) arg));
        }

        //Platform.runLater(() -> listClient.getItems().add(socket.getInetAddress().getHostName()));

    }

    private void broadCast(String mensaje){
        DataOutputStream bufferDeSalida = null;
        Nodo ultimaConexion = poolSocket.get(0);
        for (Nodo nodo: poolSocket) {
            try {
                bufferDeSalida = new DataOutputStream(nodo.getSocket().getOutputStream());
                bufferDeSalida.flush();
                bufferDeSalida.writeUTF(mensaje);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void OnSendClicked(MouseEvent event) {
        broadCast(Random());
    }
    private String Random(){
        int num = (int)(Math.random()*(diccionario.length));
        return diccionario[num];
    }

    private void sendMessage(String source, String destino, String mensaje){

    }
}



