package app;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.ServidorTCP;

public class JogoForca extends Thread {
    private static final String ARQUIVO_PALAVRAS = "/home/felipe/Projetos/jogo-forca-tcp/palavras.txt";
    private static final int NUM_ERROS_POSSIVEIS = 5;
    
    private String palavra;
    private Boolean[] acertos; 
    private int erros;
    
    private final ServidorTCP servidor;
        
    public JogoForca(ServidorTCP servidor) {
        try {
            FileReader fr = new FileReader(ARQUIVO_PALAVRAS);Scanner scan = new Scanner(fr);
        
            ArrayList<String> listaPalavras = new ArrayList<>();

            while(scan.hasNext()) {
                listaPalavras.add(scan.nextLine());
            }

            Random rand = new Random();
            this.palavra = listaPalavras.get(rand.nextInt(listaPalavras.size()));
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            this.palavra = "BRASIL";
        }
                
        System.out.println("Palavra sorteada: " + this.palavra);
        
        this.acertos = new Boolean[this.palavra.length()];
        for (int i = 0; i < this.palavra.length(); i++) {
            this.acertos[i] = false;
        } 
        
        this.servidor = servidor;        
        this.erros = NUM_ERROS_POSSIVEIS;
    }
    
    public void run() {
        try {
            this.servidor.enviaMensagem(this.geraMensagemInicial());
        
            while(true) {
                
                this.servidor.enviaMensagem("Erros possiveis: " + this.erros);
                this.servidor.enviaMensagem(this.geraStatusJogo());
                this.servidor.enviaMensagem("Envia sua tentativa: ");
                
                String tentativaStr = this.servidor.recebeMensagem();
                
                if (tentativaStr.length() != 1) {
                    this.servidor.enviaMensagem("Entrada invalida, tente novamente!");
                    continue;
                }
                
                char tentativaChar = Character.toUpperCase(tentativaStr.charAt(0));
                
                this.servidor.enviaMensagem("Sua tentativa foi: " + tentativaChar);
                    
                this.tentativa(Character.toUpperCase(tentativaChar));
                
                if(this.ganhouJogo()) {
                    this.servidor.enviaMensagem("VOCE GANHOU!");
                    this.servidor.enviaMensagem("A palavra era: " + this.palavra);
                    break;
                }
                if(this.perdeuJogo()) {
                    this.servidor.enviaMensagem("VOCE PERDEU!");
                    this.servidor.enviaMensagem("A palavra era: " + this.palavra);
                    break;
                }
            }
            try {
                Thread.sleep(5000);
                this.servidor.encerraConexao();
            } catch (InterruptedException ex) {
            }

        } catch (IOException ex) {
        }
    }
    
    public String geraStatusJogo() {
        String retorno = "";
        
        for (int i = 0; i < this.palavra.length(); i++) {
            if (this.acertos[i]) { 
                retorno += this.palavra.charAt(i) + " ";
            }
            else {
                retorno += "_ ";
            }
        }
        
        return retorno + "\n";
    }
    
    public Boolean tentativa(char c) {
        Boolean acertou = false;
        
        for (int i = 0; i < this.palavra.length(); i++) {
            if (this.palavra.charAt(i) == c) {
                this.acertos[i] = true;
                acertou = true;
            }
        }
        
        if (!acertou) {
            this.erros --;
        }
        
        return acertou;
    }
    
    public Boolean ganhouJogo() {
        for (int i = 0; i < this.palavra.length(); i++) {
            if (!this.acertos[i]) {
                return false;
            }
        }
        return true;
    }
    
    public Boolean perdeuJogo() {
        return this.erros == 0;
    }
    
    public String geraMensagemInicial() {
        String retorno;
        retorno =  "Jogo da Forca TCP - Nomes de Países\n";
        retorno += "Numero maximo de erros possiveis: " + NUM_ERROS_POSSIVEIS + "\n";
        return retorno;
    }
    
    
}
