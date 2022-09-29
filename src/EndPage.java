import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static java.awt.Color.*;

public class EndPage{

    JFrame ep = new JFrame("Result");
    JLabel wol = new JLabel();
    JLabel Winner = new JLabel("The Winner is");
    JLabel Winner_Color = new JLabel();
    Color winner;

    EndPage(String C, Boolean WOL){
        ep.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                // TODO: close connection here
                System.out.println("============================");
                int i=JOptionPane.showConfirmDialog(ep,"Do you want to quit?","Exit Confirmation", JOptionPane.YES_NO_OPTION);
                if(i == JOptionPane.YES_OPTION) {
                    ep.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
                else if (i == JOptionPane.NO_OPTION)
                    ep.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            }
        });

        if(C.equals("green"))
        {
            winner = green;
        }

        else if(C.equals("red"))
        {
            winner = red;
        }
        else if(C.equals("yellow"))
        {
            winner = yellow;
        }
        else if(C.equals("blue"))
        {
            winner = blue;
        }

        ep.setSize(1000,1000);
        ep.setResizable(false);

        ep.setLayout((null));

        wol.setLocation(45, 100);
        wol.setSize(900, 100);
        wol.setFont(new Font("", Font.BOLD, 72));
        wol.setHorizontalAlignment(JLabel.CENTER);
        if(WOL)
        {
            wol.setText("You Win!");
        }
        else
            wol.setText("You Lost");

        Winner.setLocation(45, 250);
        Winner.setSize(900, 100);
        Winner.setFont(new Font("", Font.BOLD, 28));
        Winner.setHorizontalAlignment(JLabel.CENTER);

        Winner_Color.setLocation(350, 350);
        Winner_Color.setSize(300, 300);
        Winner_Color.setOpaque(true);
        Winner_Color.setBackground(winner);

        ep.add(Winner_Color);
        ep.add(Winner);
        ep.add(wol);

        ep.setVisible(true);
    }

}
