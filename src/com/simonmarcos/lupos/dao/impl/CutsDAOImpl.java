package com.simonmarcos.lupos.dao.impl;

import com.simonmarcos.lupos.dao.ConnectionDB;
import com.simonmarcos.lupos.dao.DAOCuts;
import com.simonmarcos.lupos.model.Cuts;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CutsDAOImpl implements DAOCuts {

    private ConnectionDB myConnection;
    private Connection c;

    public CutsDAOImpl() {
        myConnection = ConnectionDB.instanciar();
        c = myConnection.connect();
        System.out.println("Cuts DAO");
    }

    @Override
    public int save(Cuts o) {
        List<Cuts> lista = toList();
        int r = 0;
        if (!lista.contains(o)) {
            c = myConnection.connect();
            if (c != null) {

                String consultaSQL = "INSERT INTO Cuts (idCuts,type,price,priceBarber,prize) VALUES (?,?,?,?,?)";
                PreparedStatement ps = null;

                try {

                    ps = c.prepareStatement(consultaSQL);
                    ps.setInt(1, o.getIdCuts());
                    ps.setString(2, o.getType());
                    ps.setDouble(3, o.getPrice());
                    ps.setDouble(4, o.getPriceBarber());
                    ps.setDouble(5, o.getPrize());

                    r = ps.executeUpdate();
                    ps.close();
                    return r;

                } catch (SQLException ex) {
                    Logger.getLogger(Cuts.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        c.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(Cuts.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        return r;
    }

    @Override
    public List<Cuts> queryFilter(int code, String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int modificar(int code, Cuts o) {
        int r = 0;
        if (c != null) {
            try {
                String consultaSQL = "UPDATE Cuts SET type = ?, price = ?,priceBarber = ?, prize = ? WHERE idCuts = ?";
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ps.setString(1, o.getType());
                ps.setDouble(2, o.getPrice());
                ps.setDouble(3, o.getPriceBarber());
                ps.setDouble(3, o.getPrize());
                ps.setInt(5, code);

                r = ps.executeUpdate();

                ps.close();
                return r;

            } catch (SQLException ex) {
                Logger.getLogger(Cuts.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();

                } catch (SQLException ex) {
                    Logger.getLogger(Cuts.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return r;
    }

    @Override
    public List<Cuts> toList() {
        List<Cuts> list = null;
        if (c != null) {
            try {
                String consultaSQL = "SELECT idCuts,type,price,priceBarber,prize FROM Cuts";

                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ResultSet rs = ps.executeQuery();

                list = new ArrayList<>();
                while (rs.next()) {
                    Cuts c = new Cuts();
                    c.setIdCuts(rs.getInt("idCuts"));
                    c.setType(rs.getString("type"));
                    c.setPrice(rs.getDouble("price"));
                    c.setPriceBarber(rs.getDouble("priceBarber"));
                    c.setPrize(rs.getDouble("prize"));
                    list.add(c);
                }

                ps.close();
                rs.close();

                return list;

            } catch (SQLException ex) {
                Logger.getLogger(Cuts.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Cuts.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return list;
    }

    @Override
    public int delete(int code) {
        int r = 0;
        if (c != null) {
            try {
                PreparedStatement ps = c.prepareStatement("DELETE FROM Cuts WHERE idCuts=?");
                ps.setInt(1, code);

                r = ps.executeUpdate();
                ps.close();
                return r;

            } catch (SQLException ex) {
                Logger.getLogger(Cuts.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();

                } catch (SQLException ex) {
                    Logger.getLogger(Cuts.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return r;
    }

    @Override
    public double getPriceBarber(String name) {
        double priceBarber = 0;
        if (c != null) {
            try {
                PreparedStatement ps = c.prepareStatement("SELECT priceBarber FROM Cuts WHERE cuts.`type`= ?");
                ps.setString(1, name);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    priceBarber += rs.getDouble("priceBarber");
                }
                ps.close();
                return priceBarber;

            } catch (SQLException ex) {
                Logger.getLogger(Cuts.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();

                } catch (SQLException ex) {
                    Logger.getLogger(Cuts.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return priceBarber;
    }

    @Override
    public double getPrize() {
        double priceBarber = 0;
        if (c != null) {
            try {
                PreparedStatement ps = c.prepareStatement("SELECT prize FROM Cuts WHERE type='Corte'");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    priceBarber += rs.getDouble("prize");
                }
                ps.close();
                return priceBarber;

            } catch (SQLException ex) {
                Logger.getLogger(Cuts.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();

                } catch (SQLException ex) {
                    Logger.getLogger(Cuts.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return priceBarber;
    }
}
