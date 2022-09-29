import java.io.*;
import java.net.*;
import java.sql.SQLOutput;

// TODO: 3D array bookkeeping buttons status
// B[X,Y][0] = filled or not filled
// B[X,Y][1] = player id
// B[x,y][2] = color
// B[x,y][3] = score

//most allow 4 player to join game

//The server is opening by Socket 10101, all Client will be store for lateron.
public class new_game_server {
    public static Socket [] client_list = new Socket[5];
    public static String[][][] server_inf = new String[10][10][2];
    public static String[][] client_port_list = new String[5][3];
    public static void main(String[] args) throws Exception
    {
        ServerSocket server = new ServerSocket(10101);
        Socket client;
        Thread client_handler;
        int player_count=0;
        while(true) {
            if (player_count >= 5)
                break;

            client_list[player_count] = server.accept();
            player_count++;
            game_thread_handler task = new game_thread_handler(client_list, player_count,server_inf,client_port_list);
            client_handler = new Thread(task);
            client_handler.start(); // These threads is to work on sending message and receiving message
        }


    }


}