import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by lucasraza on 3/8/17
 *
 *
 */
public class Server {
    private static ServerSocket serverSocket;
    private static ArrayList<Socket> sockets;
    private static Socket socket;

    public static void main(String[] args) throws Exception {
        try {
            serverSocket = new ServerSocket(7777);

            sockets = new ArrayList<>();

            for(int i = 1; i < 3; i++){
                socket = serverSocket.accept();
                System.out.println("Player " + i + " connected");
                sockets.add(socket);

            }
                Game game = new Game();
                System.out.println("Created game, waiting for players to connect");

                Game.PlayerHandler playerX = game.new PlayerHandler(sockets.remove(sockets.size()-1), 'X');
                Game.PlayerHandler playerO = game.new PlayerHandler(sockets.remove(sockets.size()-1), 'O');
                game.currentPlayer = playerX;

                playerX.start();
                playerO.start();
                System.out.println("Game started");

        }catch(IOException e)
        {
            e.printStackTrace();
        }

    }
    }

    class Game{

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

        public synchronized boolean move(PlayerHandler player, int location) { //A player moves based on their assigned Piece
            System.out.println("Player moved at location " + location);
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
                    out.println("WELCOME" + mark);
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
                        if (command != null) {

                            if (command.startsWith("MOVE")) {
                                int location = Integer.parseInt(command.substring(5));
                                System.out.println(location);
                                if (move(this, location)) {
                                    out.println("VALID_MOVE" + "location");
                                    out.println(hasWinner() ? "VICTORY"
                                            : isFull() ? "TIE"
                                            : "");
                                } else {
                                    out.println("MESSAGE ?");
                                }
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








