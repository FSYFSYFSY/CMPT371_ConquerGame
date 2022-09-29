import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
public class clientRecvThreadHandler implements Runnable {

    public static String get_color(String msg)
    {
        if(msg.contains("@"))
        {
            int index=msg.indexOf('@');
            return msg.substring(index+1);
        }
        return "Grey";
    }
    public void assign_color(String color,JButton buttons[], int i)
    {
        if(color.equals("red"))
        {
            buttons[i].setBackground(Color.red);
        }
        if(color.equals("yellow"))
        {
            buttons[i].setBackground(Color.yellow);
        }
        if(color.equals("blue"))
        {
            buttons[i].setBackground(Color.blue);
        }
        if(color.equals("green"))
        {
            buttons[i].setBackground(Color.green);
        }
        if(color.equals("black"))
        {
            buttons[i].setBackground(Color.black);
        }
    }
    public int getX(String str)
    {
        int index = str.indexOf(":");
        String tempX=str.substring(0,index);
        return Integer.parseInt(tempX);
    }
    public int getY(String str)
    {
        int index = str.indexOf(":");
        String tempY=str.substring(index+1);
        return Integer.parseInt(tempY);
    }
    String coordinateXY;
    public BufferedReader in;
    String recv_msg;
    JButton[] buttons;
    String C;
    public clientRecvThreadHandler(BufferedReader in, JButton[] buttons, String C) {
        this.in=in;
        this.buttons = buttons;
        this.C = C;
    }

    public void run() {

        do {
            coordinateXY = null;
            recv_msg = null;
            try {
                recv_msg = in.readLine();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("received message: " + recv_msg);

            if(recv_msg.contains("$$$"))//$$$@red
            {
                Boolean WOL;
                String WinC = get_color(recv_msg);
                if(WinC.equals(C))
                {
                    WOL = true;
                }
                else
                    WOL = false;

                new EndPage(WinC,WOL);
                break;
            }

            //if we receive message "Yes", client will go change the square color.
            else if(recv_msg.contains("Yes"))
            {
                int indexStart=recv_msg.indexOf("!");
                int indexEnd = recv_msg.indexOf(" ", indexStart);
                coordinateXY=recv_msg.substring(indexStart+1,indexEnd);
                System.out.println("coordinateXY is " + coordinateXY);
                String currColor=get_color(recv_msg);
                // TODO: UPDATE UI
                System.out.println("Get X and Y !!!");
                int X = getX(coordinateXY);
                int Y = getY(coordinateXY);
                System.out.println(" X is " + X + " Y is "+ Y + " now calculating X and Y");
                int newX = (X - 2) / 98;
                int newY = Y / 90;
                System.out.println("receive coordinate : (" +newX + "," + newY +") "  + "need to filled by color " + currColor);
                assign_color(currColor,buttons,newY*10+newX);

            }
            else if(recv_msg.contains("No"))
            {
                System.out.println("This box is filled by others, please try another one");
            }

        }while(true);
    }
}
