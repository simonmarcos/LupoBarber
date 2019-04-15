package com.simonmarcos.lupos.dao.impl;

import com.simonmarcos.lupos.dao.ConnectionDB;
import com.simonmarcos.lupos.dao.DAOBarber;
import com.simonmarcos.lupos.model.Barber;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BarberDAOImpl implements DAOBarber {
    
    private ConnectionDB myConnection;
    private Connection c;
    
    public BarberDAOImpl() {
        myConnection = ConnectionDB.instanciar();
        c = myConnection.connect();
        System.out.println("Barber DAO");
    }
    
    @Override
    public int save(Barber o) {
        List<Barber> lista = toList();
        int r = 0;
        if (!lista.contains(o)) {
            c = myConnection.connect();
            if (c != null) {
                
                String consultaSQL = "INSERT INTO Barber (idBarber,name,lastName,dni,phone,dateEntry,address,birthday) VALUES (?,?,?,?,?,?,?,?)";
                PreparedStatement ps = null;
                
                try {
                    
                    ps = c.prepareStatement(consultaSQL);
                    ps.setInt(1, o.getIdBarber());
                    ps.setString(2, o.getName());
                    ps.setString(3, o.getLastName());
                    ps.setInt(4, o.getDni());
                    ps.setString(5, o.getPhone());
                    ps.setDate(6, o.getDateEntry());
                    ps.setString(7, o.getAddress());
                    ps.setDate(8, o.getBirthday());
                    
                    r = ps.executeUpdate();
                    ps.close();
                    return r;
                    
                } catch (SQLException ex) {
                    Logger.getLogger(Barber.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        c.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(Barber.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        return r;
    }
    
    @Override
    public List<Barber> queryFilter(int code, String name) {
        List<Barber> list = null;
        if (c != null) {
            try {
                String consultaSQL = "SELECT idBarber,name,lastName,dni,phone,dateEntry,address,birthday FROM Barber WHERE dni = ?";
                
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ps.setInt(1, code);
                ResultSet rs = ps.executeQuery();
                
                list = new ArrayList<>();
                while (rs.next()) {
                    Barber b = new Barber();
                    b.setIdBarber(rs.getInt("idBarber"));
                    b.setName(rs.getString("name"));
                    b.setLastName(rs.getString("lastName"));
                    b.setDni(rs.getInt("dni"));
                    b.setPhone(rs.getString("phone"));
                    b.setDateEntry(rs.getDate("dateEntry"));
                    b.setAddress(rs.getString("address"));
                    b.setBirthday(rs.getDate("birthday"));
                    list.add(b);
                }
                
                ps.close();
                rs.close();
                
                return list;
                
            } catch (SQLException ex) {
                Logger.getLogger(Barber.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Barber.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return list;
    }
    
    @Override
    public int modificar(int code, Barber o) {
        int r = 0;
        if (c != null) {
            try {
                String consultaSQL = "UPDATE Barber SET name = ?, lastName = ?, dni = ?,phone = ?,dateEntry = ?, address = ?, birthday = ? WHERE dni = ?";
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ps.setString(1, o.getName());
                ps.setString(2, o.getLastName());
                ps.setInt(3, o.getDni());
                ps.setString(4, o.getPhone());
                ps.setDate(5, o.getDateEntry());
                ps.setString(6, o.getAddress());
                ps.setDate(7, o.getBirthday());
                ps.setInt(8, code);
                
                r = ps.executeUpdate();
                
                ps.close();
                return r;
                
            } catch (SQLException ex) {
                Logger.getLogger(Barber.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                    
                } catch (SQLException ex) {
                    Logger.getLogger(Barber.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return r;
    }
    
    @Override
    public List<Barber> toList() {
        List<Barber> list = null;
        if (c != null) {
            try {
                String consultaSQL = "SELECT idBarber,name,lastName,dni,phone,dateEntry,address,birthday FROM Barber";
                
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ResultSet rs = ps.executeQuery();
                
                list = new ArrayList<>();
                while (rs.next()) {
                    Barber b = new Barber();
                    b.setIdBarber(rs.getInt("idBarber"));
                    b.setName(rs.getString("name"));
                    b.setLastName(rs.getString("lastName"));
                    b.setDni(rs.getInt("dni"));
                    b.setPhone(rs.getString("phone"));
                    b.setDateEntry(rs.getDate("dateEntry"));
                    b.setAddress(rs.getString("address"));
                    b.setBirthday(rs.getDate("birthday"));
                    
                    list.add(b);
                }
                
                ps.close();
                rs.close();
                
                return list;
                
            } catch (SQLException ex) {
                Logger.getLogger(Barber.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Barber.class.getName()).log(Level.SEVERE, null, ex);
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
                PreparedStatement ps = c.prepareStatement("DELETE FROM Barber WHERE dni=?");
                ps.setInt(1, code);
                
                r = ps.executeUpdate();
                ps.close();
                return r;
                
            } catch (SQLException ex) {
                Logger.getLogger(Barber.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                    
                } catch (SQLException ex) {
                    Logger.getLogger(Barber.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return r;
    }
    
    @Override
    public List<Barber> toListByNameAndLastName() {
        List<Barber> list = null;
        if (c != null) {
            try {
                String consultaSQL = "SELECT idBarber,name,lastName,birthday FROM Barber";
                
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ResultSet rs = ps.executeQuery();
                
                list = new ArrayList<>();
                while (rs.next()) {
                    Barber b = new Barber();
                    b.setIdBarber(rs.getInt("idBarber"));
                    b.setName(rs.getString("name"));
                    b.setLastName(rs.getString("lastName"));
                    b.setBirthday(rs.getDate("birthday"));
                    list.add(b);
                }
                
                ps.close();
                rs.close();
                
                return list;
                
            } catch (SQLException ex) {
                Logger.getLogger(Barber.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Barber.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return list;
    }
}
