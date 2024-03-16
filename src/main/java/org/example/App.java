package org.example;

import org.example.db.DataBase;
import org.example.swing.MathHelperApp;

import javax.swing.*;


public class App 
{
    public static void main( String[] args )
    {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DataBase().main();
                new MathHelperApp().setVisible(true);
            }
        });
    }
}
