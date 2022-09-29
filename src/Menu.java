import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.io.*;


public class Menu {
    JFrame menu = new JFrame("Menu");
    JPanel StartPanel = new JPanel();
    JButton ConnectButton = new JButton();
    JTextArea StartText = new JTextArea();
    JButton StartButton = new JButton();
    JLabel Your_Color = new JLabel("Your Color is: ");
    JLabel Color = new JLabel();
    String RecData;
    String RecColor = "";
    String StartSign = "";
    java.awt.Color C;
    private Socket MySocket;
    private static PrintWriter out;
    private static BufferedReader in;
    public static String color;

    //To parse the "color" packet
    public static String get_color(String msg)
    {
        if(msg.contains("@"))
        {
            int index=msg.indexOf('@');
            return msg.substring(index+1);
        }
        return "Grey";
    }

    Menu() throws IOException {
        menu.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                // TODO: close connection here
                System.out.println("============================");
                int i=JOptionPane.showConfirmDialog(menu,"Do you want to quit?","Exit Confirmation", JOptionPane.YES_NO_OPTION);

                //Gracefully close the connection
                if(i == JOptionPane.YES_OPTION) {
                    try {
                        if(MySocket != null && in != null && out != null) {
                            MySocket.close();
                            in.close();
                            out.close();
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
                else if (i == JOptionPane.NO_OPTION)
                    menu.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            }
        });

        //setting the component on UI.
        menu.setSize(1000,1000);
        menu.setResizable(false);

        menu.setLayout((null));

        StartPanel.setLayout(null);

        StartPanel.setLocation(100,750);
        StartPanel.setSize(900,150);

        StartPanel.add(ConnectButton);
        StartPanel.add(StartButton);
        ConnectButton.setLocation(450,1);
        ConnectButton.setSize(300,149);
        ConnectButton.setText("Connect");
        ConnectButton.setFont(new Font("", Font.BOLD,30));
        ConnectButton.setContentAreaFilled(false);
        ConnectButton.setFocusPainted(false);

        StartButton.setLocation(1,1);
        StartButton.setSize(300,149);
        StartButton.setText("Start");
        StartButton.setFont(new Font("", Font.BOLD,30));
        StartButton.setContentAreaFilled(false);
        StartButton.setFocusPainted(false);
        StartButton.setEnabled(false);

        StartText.setLocation(600, 100);
        StartText.setSize(300, 500);
        StartText.setFont(new Font("", Font.BOLD,18));

        Your_Color.setLocation(100, 100);
        Your_Color.setSize(300, 75);
        Your_Color.setFont(new Font("", Font.BOLD, 30));

        Color.setLocation(150, 250);
        Color.setSize(250, 250);
        Color.setOpaque(true);

        menu.add(StartPanel);
        menu.add(StartText);
        menu.add(Your_Color);
        menu.add(Color);

        menu.setVisible(true);

        ConnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                RecData = new String("");

                //Creating a Thread to renew the text box

                Thread t1 = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        //When recieve data from server, Button turn to unavailable
                        while (RecData.equals("")) {
                            ConnectButton.setEnabled(false);
                            StartText.setText("Start Connection" + "\r\n");
                            StartText.updateUI();

                            if (!RecData.equals("")) {
                                break;
                            }
                        }
                        //TODO: if received data are success, then outprint "Successfully connect", and show the color.

                        if (RecData.equals("1")) {
                            StartText.setText(StartText.getText() + "Succeed" + "\r\n");
                            ConnectButton.setEnabled(false);
                        } else {
                            StartText.setText(StartText.getText() + "Connection failed" + "\r\n");
                        }
                    }
                });
                Thread t2 = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        System.out.println("T2 start run");
                        try {
                            if((StartSign = in.readLine()) != null)
                            {
                                System.out.println("Catch the readlin()");
                                System.out.println(StartSign);
                                //Go to Game frame.
                                if(StartSign.equals("All_Start"))
                                {
                                    new UI_test(out,in,color);
                                    menu.dispose();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t1.start();
                //Opening the socket, Listening data from server, get stream into Recdata
                try {
                    byte[] ipAddr = new byte[]{127, 0, 0, 1};
                    InetAddress addr = InetAddress.getByAddress(ipAddr);
                    Socket MySocket = new Socket(addr, 10101);
                    OutputStream os = MySocket.getOutputStream();
                    InputStream is = MySocket.getInputStream();
                    out = new PrintWriter(os, true);
                    in = new BufferedReader(new InputStreamReader(is));
                    System.out.println("connection successful");
                    RecData = "1";
                    out.println("require_color");
                    RecColor = in.readLine();

                    color = get_color(RecColor);

                    t1.join();
                    if (color.equals("red")) {
                        C = java.awt.Color.red;
                        Color.setBackground(C);
                        StartButton.setEnabled(true);
                        Color.updateUI();
                    } else if (color.equals("blue")) {
                        C = java.awt.Color.blue;
                        Color.setBackground(C);
                        StartButton.setEnabled(true);
                        Color.updateUI();
                    } else if (color.equals("green")) {
                        C = java.awt.Color.green;
                        Color.setBackground(C);
                        StartButton.setEnabled(true);
                        Color.updateUI();
                    } else if (color.equals("yellow")) {
                        C = java.awt.Color.yellow;
                        Color.setBackground(C);
                        StartButton.setEnabled(true);
                        Color.updateUI();
                    }
                    t2.start();

                } catch (Exception exce) {
                    System.out.println("connection failed");
                    exce.printStackTrace();
                    RecData = "0";
                    ConnectButton.setEnabled(true);
                }
            }
        });

        StartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = "player_1 start";
                out.println(msg);
            }
        });
    }
}
