import javafx.application.Application;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by lucasraza on 3/8/17
 *
 *
 */
public class Server {


    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(12345);

            while(true){
                Game game = new Game();
                System.out.println("gamee");
                Socket sX = serverSocket.accept();
                System.out.println("Player connected");
                Game.PlayerHandler playerX = game.new PlayerHandler(sX,'X');
                Socket sO = serverSocket.accept();
                System.out.println("Player connected");
                Game.PlayerHandler playerO = game.new PlayerHandler(sO,'O');
                game.currentPlayer = playerX;

                playerX.start();
                playerO.start();
                System.out.println("Game started");

            }

    }
    }

    class Game {

        private PlayerHandler[] board = {
                null, null, null,
                null, null, null,
                null, null, null
        };
        PlayerHandler currentPlayer;

        public boolean isFull(){
            for(int i = 0; i < board.length; i++) {
                if(board[i] == null){
                    return false;
                }
            }
            return true;
        }

        public boolean hasWinner() {
        	if (checkHorizontalWin() || checkVerticalWin() || checkDiagonalWin()) {
        		return true;
        	} else {
        		return false;
        	}
        	
        }
        
        public boolean checkHorizontalWin() {
        	if (board[0] != null && board[0] == board[1] && board[0] == board[2]) {
        		return true;
        	} else if(board[3] != null && board[3] == board[4] && board[3] == board[5]) {
        		return true;
        	} else if(board[6] != null && board[6] == board[7] && board[6] == board[8]) {
        		return true;
        	} else {
        		return false;
        	}
        }
        
        public boolean checkVerticalWin() {
        	if (board[0] != null && board[0] == board[3] && board[0] == board[6]) {
        		return true;
        	} else if(board[1] != null && board[1] == board[4] && board[1] == board[7]) {
        		return true;
        	} else if(board[2] != null && board[2] == board[5] && board[2] == board[8]) {
        		return true;
        	} else {
        		return false;
        	}
        }
        
        public boolean checkDiagonalWin() {
        	if (board[0] != null && board[0] == board[4] && board[0] == board[8]) {
        		return true;
        	} else if (board[2] != null && board[2] == board[4] && board[2] == board[6]) {
        		return true;
        	} else {
        		return false;
        	}
        }

        public synchronized boolean move(PlayerHandler player, int location) { //A player moves based on their assigned Piece (status)
            if(board[location]==null){
                board[location] = player;
                currentPlayer = currentPlayer.opponent;
                currentPlayer.opponentMoved(location);
                return true;
            }
            return false;
        }
        class PlayerHandler extends Thread{
            char mark;
            private String name;
            private Socket socket;

            PlayerHandler opponent;
            BufferedReader in;
            PrintWriter out;

            public PlayerHandler(Socket socket, char mark) {
                this.socket = socket;
                this.mark = mark;
                try{
                    // initialize input and output streams
                    in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("MESSAGE Finding another player");
                } catch(IOException e){

                }
            }

            public void setOpponent(PlayerHandler opponent){
                this.opponent = opponent;

            }

            public void opponentMoved(int location){
                out.println("OPPONENT_MOVED" + location);
                out.println(
                        hasWinner() ? "DEFEAT" : isFull() ? "TIE" : "");
            }

            public void run() {
                try {
                    if (mark == 'X') {
                        out.println("MESSAGE Your move");
                    }

                    while (true) {
                        String command = in.readLine();
                        System.out.println(command);
                        if (command.startsWith("MOVE")) {
                            int location = Integer.parseInt(command.substring(5));
                            if (move(this,location)) {
                                out.println("VALID_MOVE" + "location");
                                out.println(hasWinner() ? "VICTORY"
                                        : isFull() ? "TIE"
                                        : "");
                            } else {
                                out.println("MESSAGE ?");
                            }
                        }
                    }
                }catch(IOException e) {
                    System.out.println("Player died: " + e);
                } finally {
                    try {socket.close();} catch(IOException e){}
                }
            }
        }
    }








