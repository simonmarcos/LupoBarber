package com.simonmarcos.lupos.dao.impl;

import com.simonmarcos.lupos.dao.ConnectionDB;
import com.simonmarcos.lupos.dao.DAOExpenses;
import com.simonmarcos.lupos.model.Expenses;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExpensesDAOImpl implements DAOExpenses {

    private ConnectionDB myConnection;
    private Connection c;

    public ExpensesDAOImpl() {
        myConnection = ConnectionDB.instanciar();
        c = myConnection.connect();
        System.out.println("Expenses DAO");
    }

    @Override
    public int save(Expenses o) {
        List<Expenses> lista = toList();
        int r = 0;
        if (!lista.contains(o)) {
            c = myConnection.connect();
            if (c != null) {

                String consultaSQL = "INSERT INTO Expenses (idExpenses,category,type,date,description,value) VALUES (?,?,?,?,?,?)";
                PreparedStatement ps = null;

                try {

                    ps = c.prepareStatement(consultaSQL);
                    ps.setInt(1, o.getIdExpenses());
                    ps.setString(2, o.getCategory());
                    ps.setString(3, o.getType());
                    ps.setDate(4, o.getDate());
                    ps.setString(5, o.getDescription());
                    ps.setDouble(6, o.getValue());

                    r = ps.executeUpdate();
                    ps.close();
                    return r;

                } catch (SQLException ex) {
                    Logger.getLogger(Expenses.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        c.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(Expenses.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        return r;
    }

    @Override
    public List<Expenses> queryFilter(int code, String name) {
        List<Expenses> list = null;
        String consultaSQL = "";
        if (c != null) {
            try {

                consultaSQL += "SELECT idExpenses,category,type,date,description,value FROM Expenses WHERE type = ?";

                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();

                list = new ArrayList<>();
                while (rs.next()) {
                    Expenses e = new Expenses();
                    e.setIdExpenses(rs.getInt("idClient"));
                    e.setCategory(rs.getString("category"));
                    e.setType(rs.getString("type"));
                    e.setDate(rs.getDate("date"));
                    e.setDescription(rs.getString("description"));
                    e.setValue(rs.getDouble("value"));
                    list.add(e);
                }

                ps.close();
                rs.close();

                return list;

            } catch (SQLException ex) {
                Logger.getLogger(Expenses.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Expenses.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return list;
    }

    @Override
    public int modificar(int code, Expenses o) {
        int r = 0;
        if (c != null) {
            try {
                String consultaSQL = "UPDATE Expenses SET category = ?, type = ?, date = ? , description = ?, value = ? WHERE idExpenses = ?";
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ps.setString(1, o.getCategory());
                ps.setString(2, o.getType());
                ps.setDate(3, o.getDate());
                ps.setString(4, o.getDescription());
                ps.setDouble(5, o.getValue());
                ps.setInt(6, code);

                r = ps.executeUpdate();

                ps.close();
                return r;

            } catch (SQLException ex) {
                Logger.getLogger(Expenses.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();

                } catch (SQLException ex) {
                    Logger.getLogger(Expenses.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return r;
    }

    @Override
    public List<Expenses> toList() {
        List<Expenses> list = null;
        if (c != null) {
            try {
                String consultaSQL = "SELECT idExpenses,category,type,date,description,value FROM Expenses";

                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ResultSet rs = ps.executeQuery();

                list = new ArrayList<>();
                while (rs.next()) {
                    Expenses e = new Expenses();
                    e.setIdExpenses(rs.getInt("idExpenses"));
                    e.setCategory(rs.getString("category"));
                    e.setType(rs.getString("type"));
                    e.setDate(rs.getDate("date"));
                    e.setDescription(rs.getString("description"));
                    e.setValue(rs.getInt("value"));
                    list.add(e);
                }

                ps.close();
                rs.close();

                return list;

            } catch (SQLException ex) {
                Logger.getLogger(Expenses.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Expenses.class.getName()).log(Level.SEVERE, null, ex);
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
                PreparedStatement ps = c.prepareStatement("DELETE FROM Expenses WHERE dni=?");
                ps.setInt(1, code);

                r = ps.executeUpdate();
                ps.close();
                return r;

            } catch (SQLException ex) {
                Logger.getLogger(Expenses.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();

                } catch (SQLException ex) {
                    Logger.getLogger(Expenses.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return r;
    }

    @Override
    public List<Expenses> queryFilterForDateBetwen(String since, String until) {
        List<Expenses> list = null;
        if (c != null) {
            try {
                String consultaSQL = "SELECT `expenses`.`IdExpenses`,`expenses`.`category`,`expenses`.`type`,`Expenses`.`date`,`expenses`.`description`,`Expenses`.`value` FROM `Expenses` WHERE `Expenses`.`date` BETWEEN ? AND ?";
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ps.setString(1, since);
                ps.setString(2, until);

                ResultSet rs = ps.executeQuery();

                list = new ArrayList<>();
                while (rs.next()) {
                    Expenses e = new Expenses();
                    e.setIdExpenses(rs.getInt("idExpenses"));
                    e.setCategory(rs.getString("category"));
                    e.setType(rs.getString("type"));
                    e.setDate(rs.getDate("date"));
                    e.setDescription(rs.getString("description"));
                    e.setValue(rs.getDouble("value"));
                    list.add(e);
                }

                ps.close();
                rs.close();

                return list;

            } catch (SQLException ex) {
                Logger.getLogger(Expenses.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Expenses.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return list;
    }

    @Override
    public double getPriceTotalExpenses(String since, String until) {
        double valueTotal = 0;
        if (c != null) {
            try {
                String consultaSQL = "SELECT SUM(`expenses`.`value`) AS valueTotal FROM `expenses` WHERE `expenses`.`date` BETWEEN ? AND ?";
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ps.setString(1, since);
                ps.setString(2, until);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    valueTotal = rs.getDouble("valueTotal");
                }

                ps.close();
                rs.close();

                return valueTotal;

            } catch (SQLException ex) {
                Logger.getLogger(Expenses.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Expenses.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return valueTotal;
    }
}
