import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


public class game_client2 {
        public static String color;
        public static String get_color(String msg)
        {
                if(msg.contains("@"))
                {
                        int index=msg.indexOf('@');
                        return msg.substring(index+1);
                }
                return "Black";
        }
        public static void main(String[] args) throws Exception
        {
                byte[] ipAddr = new byte[]{127, 0, 0, 1};
                InetAddress addr = InetAddress.getByAddress(ipAddr);

                Menu menu = new Menu();
                PrintWriter out;
                BufferedReader in;

//                System.out.println("sending player_X start from player_X");
//                Scanner msg_input = new Scanner(System.in);
//                System.out.println("Enter :player_X start");
//                String msg = msg_input.nextLine();  // Read user input
//                System.out.println("Username is: " + msg);
                // msg: "player_1 start"


                //TODO: Add argument to UI_test constructor
                // UI_test(PrintWriter , BufferedReader)
                // client need a while loop to keep receiving msg from server.

                //color = get_color(recv_msg);

                //UI_test ttt = new UI_test(out,in,color);

                //thread to receive command from server
        }

}
