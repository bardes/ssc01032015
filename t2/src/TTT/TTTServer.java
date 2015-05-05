package TTT;

import java.net.*;
import java.io.*;


public class TTTServer {
    private ServerSocket clientListener;
    private Socket playerX;
    private Socket playerO;
    private BufferedReader pXIn;
    private BufferedWriter pXOut;
    private BufferedReader pOIn;
    private BufferedWriter pOOut;

    private char turn;
    private char[] board = { 
        '_', '_', '_',
        '_', '_', '_',
        '_', '_', '_'
    };

    public TTTServer(int port) {
        turn = 'X'; // X sempre começa

        try {
            clientListener = new ServerSocket(port);
            
            playerX = clientListener.accept();
            pXIn =  new BufferedReader(new InputStreamReader(
                        playerX.getInputStream()));
            pXOut = new BufferedWriter(new OutputStreamWriter(
                        playerX.getOutputStream()));
            pXOut.write("HELLO X\n");
            pXOut.flush();

            playerO = clientListener.accept();
            pOIn =  new BufferedReader(new InputStreamReader(
                        playerO.getInputStream()));
            pOOut = new BufferedWriter(new OutputStreamWriter(
                        playerO.getOutputStream()));
            pOOut.write("HELLO O\n");
            pOOut.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

    }

    private void parseCommand(String cmd, char player) {
        if(cmd.matches("^MOVE [0-8]$")) {
            String[] args = cmd.split(" ", 2);
            try {
                makeMove(Integer.parseInt(args[1]), player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }

    }

    private int makeMove(int pos, char player) {
        if(pos < 0 || pos > 8)
            return 1;
        else if(board[pos] != '_')
            return 2;
        
        board[pos] = player;
        try {
            pOOut.write(String.format("MOVE %c %d%n", player, pos));
            pXOut.write(String.format("MOVE %c %d%n", player, pos));
            pOOut.flush();
            pXOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
