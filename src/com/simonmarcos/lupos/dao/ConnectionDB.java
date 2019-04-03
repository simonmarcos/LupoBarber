package com.simonmarcos.lupos.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ConnectionDB {

    private static ConnectionDB connection;
    private Connection c;
    public static boolean isConected = false;

    private ConnectionDB() {
    }

    public static ConnectionDB instanciar() {
        if (connection == null) {
            connection = new ConnectionDB();
        }
        return connection;
    }

    public Connection connect() {

        try {
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/luposbarber", "root", "");
            isConected = true;
        } catch (SQLException ex) {
            try {
                Process process = Runtime.getRuntime().exec("C:\\xampp\\xampp-control.exe");
                c = DriverManager.getConnection("jdbc:mysql://localhost:3306/luposbarber", "root", "");
                isConected = true;
            } catch (SQLException ex1) {
                JOptionPane.showMessageDialog(null, "No se pudo conectar a la base de datos.");
            } catch (IOException ex1) {
                JOptionPane.showMessageDialog(null, "No se pudo conectar a la base de datos.");
                Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        return c;
    }

    public void closeConnection() {
        try {
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean isIsConected() {
        return isConected;
    }
}
