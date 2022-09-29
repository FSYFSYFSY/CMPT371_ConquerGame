
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.*;
import java.io.PrintWriter;
import java.net.*;

public class UI_test implements ActionListener {


    private PrintWriter out;
    private BufferedReader in;
    JFrame frame = new JFrame();
    JPanel button_panel = new JPanel();
    JPanel title_panel = new JPanel();
    JLabel textfield = new JLabel();
    JButton[] buttons = new JButton[100];

    UI_test(PrintWriter out, BufferedReader in, String C) {
        this.out = out;
        this.in = in;

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // TODO: close connection here
                System.out.println("=-==========================");
                int i = JOptionPane.showConfirmDialog(frame, "Do you want to quit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
                if (i == JOptionPane.YES_OPTION)
                {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                } else if (i == JOptionPane.NO_OPTION)
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            }
        });

        frame.setSize(1000,1000);
        frame.setResizable(false);

        frame.setLayout(new BorderLayout());

        textfield.setBackground(new Color(25,25,25));
        textfield.setForeground(new Color(25,255,0));
        textfield.setFont(new Font("Ink Free", Font.BOLD, 50));
        textfield.setHorizontalAlignment(JLabel.CENTER);
        textfield.setText("Painting Fight");
        textfield.setOpaque(true);

        title_panel.setLayout(new BorderLayout());
        title_panel.setBounds(0,0,1000,75);

        button_panel.setLayout(new GridLayout(10,10));
        button_panel.setBackground(new Color(150,150,150));

        for(int i = 0; i < 100; i++)
        {
            buttons[i] = new JButton();
            button_panel.add(buttons[i]);
            buttons[i].setFocusable(true);
            buttons[i].setBackground(null);
            buttons[i].addActionListener(this);
        }

        title_panel.add(textfield);
        frame.add(title_panel,BorderLayout.NORTH);
        frame.add(button_panel);
        frame.setVisible(true);

        // Create a thread to keep receiving msg
        Thread RecvHandler;
        clientRecvThreadHandler task = new clientRecvThreadHandler(in, buttons, C);
        RecvHandler = new Thread(task);
        RecvHandler.start();


    }


    public void actionPerformed(ActionEvent e)
    {
        //TODO: 1: when actionperformed send (x,y) to server
        //      2: receive msg from server: server decide whether u can edit the box
        //          3: player who start the game firstly should also send "the layout"
        //          4: at server side : if player join to game, server need to send the current status layout to client
        //      5： when client receive Yes from server, set button's background
        //          6: at server side: contain 10 * 10 array to bookkeeping all boxes status, if box updated , server send back the array to ALL client


        for(int i = 0; i < 100; i++)
        {
            if(e.getSource() == buttons[i])
            {
                int x = buttons[i].getX();
                int y = buttons[i].getY();
                String coordinate = "!"+Integer.toString(x) + ":" + Integer.toString(y);
                // send coordinate to server
                out.println(coordinate);
                // receive command from server to decide whether can be filled
                System.out.println(buttons[i].getX()+"\t"+buttons[i].getY());
                System.out.println("x,y = " + coordinate);

            }
        }
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

}

