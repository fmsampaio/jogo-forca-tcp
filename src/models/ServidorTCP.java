package models;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorTCP {
    private int portaServidor;
    private ServerSocket socketServidor;
    
    private Socket socket;
    private BufferedReader entrada;
    private DataOutputStream saida;
    
    public ServidorTCP(int porta) throws IOException {
        portaServidor = porta;
        socketServidor = new ServerSocket(porta);
    }
    
    public ServidorTCP(Socket socket) throws IOException {
        this.socket = socket;
       
        InputStream streamEntrada = this.socket.getInputStream();
        OutputStream streamSaida = this.socket.getOutputStream();

        this.entrada = new BufferedReader(new InputStreamReader(streamEntrada));
        this.saida = new DataOutputStream(streamSaida);
    }
    
    public void aguardaSolicitacaoConexao() throws IOException {
        System.out.println("Aguardando conexões na porta " + portaServidor + " ...");
        this.socket = socketServidor.accept();
        System.out.println("Conexão estabelecida com o cliente " + this.socket.getInetAddress());
        
        InputStream streamEntrada = this.socket.getInputStream();
        OutputStream streamSaida = this.socket.getOutputStream();

        this.entrada = new BufferedReader(new InputStreamReader(streamEntrada));
        this.saida = new DataOutputStream(streamSaida);
    }
    
    
    
    
    public String recebeMensagem() throws IOException {
        return this.entrada.readLine();
    }
    
    public void enviaMensagem(String msg) throws IOException {
        this.saida.writeBytes(msg + "\n");
    }
    
    public boolean clienteConectado() {
        return !this.socket.isClosed();
    }

    public void encerraConexao() throws IOException {
        this.socket.close();
    }
    
}
