package app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import models.ServidorTCP;

public class Principal {
    private static final int PORTA = 12345;
    
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORTA);
        } catch (IOException ex) {
        }
        
        while (true) {
            try {
                socket = serverSocket.accept();
                ServidorTCP servidor = new ServidorTCP(socket);
                new JogoForca(servidor).start();
            } catch (IOException e) {
            }
           
        }
    }
    
}
