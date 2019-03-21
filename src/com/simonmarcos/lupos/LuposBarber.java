package com.simonmarcos.lupos;

import com.simonmarcos.lupos.views.MenuPrincipal;
import javax.swing.JFrame;

public class LuposBarber {

    public static void main(String[] args) {

        MenuPrincipal mp = new MenuPrincipal();
        mp.setVisible(true);
        mp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
