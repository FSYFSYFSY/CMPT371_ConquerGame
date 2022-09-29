import java.io.*;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.Semaphore;
// coordinate: !12:123
//color: @red

public class game_thread_handler implements Runnable {
    public static boolean game_start = false;
    //private static String color;
    public static int color_count = 0;
    public static int fill_number = 0;
    public static int port_index = 0;
    Socket client;
    public static String[] color = {"red","yellow","blue","green","black"};
    public static Socket [] client_list;
    public static String[][][] server_inf;
    public static String[][] client_port_list;
    static Semaphore semaphore = new Semaphore(1);
    public static int player_count;
    public static boolean isGameOver= false;

    //Initialize the thread for recieveing and sending.
    public game_thread_handler(Socket[] clientlist, int player_count, String[][][] server_inf, String[][] client_port_list) {
        client_list=clientlist;
        game_thread_handler.player_count =player_count;
        this.client = clientlist[game_thread_handler.player_count -1];
        game_thread_handler.server_inf = server_inf;
        game_thread_handler.client_port_list = client_port_list;
    }
    public void run(){

        OutputStream os = null;
        InputStream is = null;
        try {
            os = client.getOutputStream();
            is = client.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PrintWriter out = new PrintWriter(os, true);
        BufferedReader in = new BufferedReader( new InputStreamReader(is));
        client_port_list[port_index][0] = client.getRemoteSocketAddress().toString();
        client_port_list[port_index][1]= "0";

        //gracefully close the port.
        while(true) {

            if(isGameOver)
            {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for(int i = 0; i < player_count ; i++) {
                    OutputStream os_temp = null;
                    PrintWriter out_temp = null;
                    try {
                        os_temp = client_list[i].getOutputStream();
                        out_temp = new PrintWriter(os_temp, true);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    out_temp.println("game over, gracefully close socket and client");
                }
                semaphore.release();
                break;
            }

            String client_ip_port = client.getRemoteSocketAddress().toString();

            String recv_msg = null;
            try {
                recv_msg = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("received msg from ip:port:  " + client_ip_port + "\t" + recv_msg );
            // if msg is start, then game start
            if(recv_msg.equals("require_color"))
            {
                client_port_list[port_index][2]=color[color_count];
                out.println("@" + color[color_count]);
                color_count++;
                port_index ++;
            }
            //when it recieved "Start", server will do boradcast to make all cilent start the game at the same time.
            if(recv_msg.contains("start")) {
                String reply_msg = "";

                reply_msg = "All_Start";
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for(int i = 0; i < player_count ; i++) {
                    OutputStream os_temp = null;
                    InputStream is_temp = null;
                    PrintWriter out_temp = null;
                    try {
                        os_temp = client_list[i].getOutputStream();
                        out_temp = new PrintWriter(os_temp, true);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    String tmp_client_ip_port = client_list[i].getRemoteSocketAddress().toString();
                    System.out.println(reply_msg);

                    out_temp.println(reply_msg);
                }
                semaphore.release();
                String player_id = recv_msg.substring(0, 8);
                String op_msg = recv_msg.substring(9);
                System.out.println("player_id: " + player_id + " op_msg: " + op_msg);
                if (game_start) {
                    System.out.println("new player joined!!");
                    reply_msg = "game already start, assign you to thread " + Thread.currentThread().getId() + " assign you color @" + color[color_count];
                    color_count++;
                    out.println(reply_msg);
                } else {
                    System.out.println("game starting by you!!!");
                    game_start = true;
                    reply_msg = "game starting by you, assign you to thread " + Thread.currentThread().getId() + " assign you color @" + color[color_count];
                    color_count++;
                    out.println(reply_msg);
                }
            }

            //The server fills the square within a 3D array with a semaphore
            if (recv_msg.charAt(0) == '!') {

                System.out.println("receive coordinate :" + recv_msg.substring(1));
                // check for 3D array in [x,y], whether it is filled.
                // if [x,y] is empty, send "Yes" to allow the player_X to fill box and bookkeeping the 3D array
                // if [x,y] is filled already, send "No" to disagree the player_X to fill box

                String coordinateXY=recv_msg.substring(1);
                System.out.println(coordinateXY);

                int index = coordinateXY.indexOf(":");
                String tempX=coordinateXY.substring(0,index);

                int X = Integer.parseInt(tempX);

                String tempY = coordinateXY.substring(index+1);
                int Y = Integer.parseInt(tempY);

                int newX = (X - 2) / 98;
                int newY = Y / 90;
                boolean isFill = false;

                if (server_inf[newX][newY][0] != "fill"){ //able to fill the grid

                    try {
                        semaphore.acquire();
                        fill_number ++;
                        server_inf[newX][newY][0] = "fill";
                        //server_inf[newX][newY][1] = color[color_count];
                        server_inf[newX][newY][1] = client_ip_port;
                        System.out.println( "new coordinate : " + newX + " " + newY);
                        System.out.println("able to fill !!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        isFill = true;
                        semaphore.release();

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }


                }
                else{ //the grid is  filled

                    System.out.println("fail to fill !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }


                if(isFill)
                {// sending message if it is able to fill
                    String currColor = null;
                    for(int i = 0; i < player_count ; i++)
                    {
                        if(client_list[i].equals(client))
                        {
                            currColor = color[i];
                        }
                    }
                    System.out.println("player_count: " + player_count);
                    String reply_msg = "Yes !"+recv_msg.substring(1)+" is filled by the client" + client_ip_port + " which color is @"+currColor;
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    for(int i = 0; i < player_count ; i++) {
                        OutputStream os_temp = null;
                        InputStream is_temp = null;
                        PrintWriter out_temp = null;
                        try {
                            os_temp = client_list[i].getOutputStream();
                            out_temp = new PrintWriter(os_temp, true);
                            //is_temp = client_list[i].getInputStream();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        String tmp_client_ip_port = client_list[i].getRemoteSocketAddress().toString();
                        System.out.println("i=" + i + " sending: " + reply_msg + " to " + tmp_client_ip_port);

                        out_temp.println(reply_msg);
                    }
                    semaphore.release();

//
                }

                if(fill_number == 100){ // all grids are filled
                    System.out.println(" the number is : " + port_index);
                    for(int i = 0; i < 10; i++){
                        for(int j = 0; j < 10; j ++){
                            if(server_inf[i][j][1] == null) {

                                continue;
                            }
                            if(client_port_list[0][0] != null && server_inf[i][j][1].equals(client_port_list[0][0])) {
                                int temp = Integer.parseInt(client_port_list[0][1]);
                                temp++;

                                client_port_list[0][1] = Integer.toString(temp);
                            }

                            else if(client_port_list[1][0] != null && server_inf[i][j][1].equals(client_port_list[1][0])){
                                int temp = Integer.parseInt(client_port_list[1][1]);
                                temp++;

                                client_port_list[1][1] = Integer.toString(temp);
                            }

                            else if(client_port_list[2][0] != null && server_inf[i][j][1].equals(client_port_list[2][0])){
                                int temp = Integer.parseInt(client_port_list[2][1]);
                                temp++;
                                client_port_list[2][1] = Integer.toString(temp);
                            }

                            else if(client_port_list[3][0] != null && server_inf[i][j][1].equals(client_port_list[3][0])){
                                int temp = Integer.parseInt(client_port_list[3][1]);
                                temp++;
                                client_port_list[3][1] = Integer.toString(temp);
                            }

                            else if(client_port_list[4][0] != null && server_inf[i][j][1].equals(client_port_list[4][0])){
                                int temp = Integer.parseInt(client_port_list[4][1]);
                                temp++;
                                client_port_list[4][1] = Integer.toString(temp);
                            }

                        }
                    }

                    //sort the list from highest to lowest score
                    for(int i = 0; i < port_index; i ++){
                        for(int j = 0; j < port_index - 1;j++){
                            if(Integer.parseInt(client_port_list[j][1]) < Integer.parseInt(client_port_list[j+1][1])){
                                String temp0 = client_port_list[j][0];
                                String temp1 = client_port_list[j][1];
                                String temp2 = client_port_list[j][2];
                                client_port_list[j][0] = client_port_list[j+1][0];
                                client_port_list[j][1] = client_port_list[j+1][1];
                                client_port_list[j][2] = client_port_list[j+1][2];
                                client_port_list[j+1][0] = temp0;
                                client_port_list[j+1][1] = temp1;
                                client_port_list[j+1][2] = temp2;
                            }
                        }
                    }


                    System.out.println("game is over !!!");


                    String result = ""; // contain final score of all players

//
                    result = result + "$$$@" + client_port_list[0][2];
                    System.out.println("output the result string :\n" + result);
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    for(int i = 0; i < player_count ; i++) {
                        OutputStream os_temp = null;
                        PrintWriter out_temp = null;
                        try {
                            os_temp = client_list[i].getOutputStream();
                            out_temp = new PrintWriter(os_temp, true);
                            //is_temp = client_list[i].getInputStream();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        out_temp.println(result);
                    }
                    semaphore.release();
                    isGameOver = true;
                }

            }

//            String reply_msg = "reply from thread " + Thread.currentThread().getId() + "\t";
//            out.println(reply_msg);
        }

        try {
            System.out.println("game over, gracefully close socket and client and already send last ,msg to all client.");
            in.close();
            os.close();
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
//

}

