package com.simonmarcos.lupos.dao.impl;

import com.simonmarcos.lupos.dao.DAOClient;
import com.simonmarcos.lupos.model.Client;
import com.simonmarcos.lupos.dao.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientDAOImpl implements DAOClient {
    
    private ConnectionDB myConnection;
    private Connection c;
    
    public ClientDAOImpl() {
        myConnection = ConnectionDB.instanciar();
        c = myConnection.connect();
        System.out.println("Client DAO");
    }
    
    @Override
    public int save(Client o) {
        List<Client> lista = toList();
        int r = 0;
        if (!lista.contains(o)) {
            c = myConnection.connect();
            if (c != null) {
                
                String consultaSQL = "INSERT INTO Client (idClient,name,lastName,dni,phone) VALUES (?,?,?,?,?)";
                PreparedStatement ps = null;
                
                try {
                    
                    ps = c.prepareStatement(consultaSQL);
                    ps.setInt(1, o.getIdClient());
                    ps.setString(2, o.getName());
                    ps.setString(3, o.getLastName());
                    ps.setInt(4, o.getDNI());
                    ps.setString(5, o.getPhone());
                    
                    r = ps.executeUpdate();
                    ps.close();
                    return r;
                    
                } catch (SQLException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        c.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        return r;
    }
    
    @Override
    public List<Client> queryFilter(int code, String name) {
        List<Client> list = null;
        if (c != null) {
            try {
                String consultaSQL = "SELECT idClient,name,lastName,dni,phone FROM Client WHERE dni = ?";
                
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ps.setInt(1, code);
                ResultSet rs = ps.executeQuery();
                
                list = new ArrayList<>();
                while (rs.next()) {
                    Client c = new Client();
                    c.setIdClient(rs.getInt("idClient"));
                    c.setName(rs.getString("name"));
                    c.setLastName(rs.getString("lastName"));
                    c.setDNI(rs.getInt("dni"));
                    c.setPhone(rs.getString("phone"));
                    list.add(c);
                }
                
                ps.close();
                rs.close();
                
                return list;
                
            } catch (SQLException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return list;
    }
    
    @Override
    public int modificar(int code, Client o) {
        int r = 0;
        if (c != null) {
            try {
                String consultaSQL = "UPDATE Client SET name = ?, lastName = ?, dni = ?,phone = ? WHERE dni = ?";
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ps.setString(1, o.getName());
                ps.setString(2, o.getLastName());
                ps.setInt(3, o.getDNI());
                ps.setString(4, o.getPhone());
                ps.setInt(5, code);
                
                r = ps.executeUpdate();
                
                ps.close();
                return r;
                
            } catch (SQLException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                    
                } catch (SQLException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return r;
    }
    
    @Override
    public List<Client> toList() {
        List<Client> list = null;
        if (c != null) {
            try {
                String consultaSQL = "SELECT idClient,name,lastName,dni,phone FROM Client";
                
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ResultSet rs = ps.executeQuery();
                
                list = new ArrayList<>();
                while (rs.next()) {
                    Client c = new Client();
                    c.setIdClient(rs.getInt("idClient"));
                    c.setName(rs.getString("name"));
                    c.setLastName(rs.getString("lastName"));
                    c.setDNI(rs.getInt("dni"));
                    c.setPhone(rs.getString("phone"));
                    list.add(c);
                }
                
                ps.close();
                rs.close();
                
                return list;
                
            } catch (SQLException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
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
                PreparedStatement ps = c.prepareStatement("DELETE FROM Client WHERE dni=?");
                ps.setInt(1, code);
                
                r = ps.executeUpdate();
                ps.close();
                return r;
                
            } catch (SQLException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                    
                } catch (SQLException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return r;
    }
}
