import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


public class game_client1 {
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

        }

}
