package TTT;

import java.net.*;
import java.io.*;
import java.util.*;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.scene.image.Image;

public class TTTClient extends Application {
    private final Canvas screen;
    private final Image imgBoard;
    private final Image imgO;
    private final Image imgX;
    
    private Socket conn;
    private BufferedReader serverIn;
    private BufferedWriter serverOut;

    public TTTClient() throws UnknownHostException, IOException {
        this("127.0.0.1", 1234);
    }

    public TTTClient(String host, int port) throws UnknownHostException, IOException {
        // Tenta abrir uma conexão com o servidor
        conn = new Socket(host, port);

        // Embrulha o socket para facilitar manipulação de texto
        serverIn = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
        serverOut = new BufferedWriter(new OutputStreamWriter(
                    conn.getOutputStream()));

        // Tenta entrar no jogo
        serverOut.write("HELLO\n");
        serverOut.flush();

        // Criando a tela
        screen = new Canvas(384, 384);

        // Criando o callback do mouse
        screen.setOnMousePressed((evt)-> {
            this.sendMove(3 * ((int)evt.getY()/128) + ((int)evt.getX()/128));
        });

        // Carregando as imagens do .jar
        imgBoard = new Image(getClass().getResourceAsStream("/board.png"));
        imgO = new Image(getClass().getResourceAsStream("/O.png"));
        imgX = new Image(getClass().getResourceAsStream("/X.png"));
    }

    private void sendMove(int pos) {
        try {
            serverOut.write(String.format("MOVE %d%n", pos));
            serverOut.flush();
        } catch (Exception e) {
        }
    }

    private void drawMove(char player, int pos) {
        Image i;
        if(player == 'X')
            i = imgX;
        else if (player == 'O')
            i = imgO;
        else
            return;

        screen.getGraphicsContext2D().drawImage(i, (pos%3) * 128,
                                                   (pos/3) * 128);
    }

    private void endGame(String state) {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //TODO desenhar "placar"
    }

    /**
     * Fica ouvindo a conexão com o servidor e chama as funçoes necessárias.
     */
    private void parseCommand(String cmd) {

        // Recebeu DISCONNECT
        if(cmd.matches("^DISCONNECT( \\w.*)?$")) {
            String[] args = cmd.split(" ", 2);
            
            System.err.print("Desconectado pelo servidor!");
            if(args.length == 2) {
                System.err.format(" (%s)", args[1]);
            }
            System.err.println();

            endGame(args[0]);
            Platform.exit();
        } 
        
        // Recebeu GAMEOVER
        else if(cmd.matches("^GAMEOVER (WIN|LOSE|DRAW)$")) {
            String[] args = cmd.split(" ", 2);
            endGame(args[1]);
        }
     
        // Recebeu INVALID
        else if(cmd.matches("^INVALID [_A-Z]+( \\w.*)?$")) {
            String[] args = cmd.split(" ", 3);
            
            System.err.print("Erro: " + args[1]);
            if(args.length == 3) {
                System.err.format(" (%s)", args[2]);
            }
            System.err.println();
        }

        // Recebeu MOVE
        else if(cmd.matches("^MOVE [XO] [0-8]$")) {
            String[] args = cmd.split(" ", 3);
            System.err.println("Recebeu jogada: " + cmd);
            drawMove(args[1].charAt(0), Integer.parseInt(args[2]));
        }

        // Recebeu HELLO
        else if(cmd.matches("^HELLO [XO]$")) {
            String[] args = cmd.split(" ", 2);
            System.err.println("Conseguiu entrar no jogo como jogador: "
                                + args[1]);
        }

        // Recebeu um comando malformado
        else {
            System.err.println("Erro: Comando malformado!");
        }
    }

    @Override
    public void start(Stage s) {
        s.setScene(new Scene(new Group(screen), 384, 384));
        
        s.setOnCloseRequest((_evt)->{
            try {
                serverOut.write("BYE Client closed the window.\n");
                serverOut.flush();
            } catch (Exception e) {
            }
            Platform.exit();
        });

        new Thread(()-> {
            String c;
            try {
                while(!conn.isClosed() && (c = serverIn.readLine()) != null) {
                    parseCommand(c);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        screen.getGraphicsContext2D().drawImage(imgBoard, 0, 0);
        s.show();
    }

    // Autoteste
    public static void main(String args[]) {
        launch(args);
    }
}
