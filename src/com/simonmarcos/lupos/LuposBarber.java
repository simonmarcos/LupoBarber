package com.simonmarcos.lupos;

import com.simonmarcos.lupos.views.MenuPrincipal;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class LuposBarber {

    public static void main(String[] args) {

        try {
            new ProcessBuilder("C:/xampp/xampp_start.exe").start();

            MenuPrincipal mp = new MenuPrincipal();
            mp.setVisible(true);
            mp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (IOException ex) {
            Logger.getLogger(LuposBarber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
