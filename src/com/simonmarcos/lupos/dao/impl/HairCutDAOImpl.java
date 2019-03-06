package com.simonmarcos.lupos.dao.impl;

import com.simonmarcos.lupos.dao.ConnectionDB;
import com.simonmarcos.lupos.dao.DAOHairCut;
import com.simonmarcos.lupos.model.Barber;
import com.simonmarcos.lupos.model.HairCut;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HairCutDAOImpl implements DAOHairCut {
    
    private ConnectionDB myConnection;
    private Connection c;
    
    public HairCutDAOImpl() {
        myConnection = ConnectionDB.instanciar();
        c = myConnection.connect();
        System.out.println("HairCut DAO");
    }
    
    @Override
    public int save(HairCut o) {
        List<HairCut> lista = toList();
        int r = 0;
        if (!lista.contains(o)) {
            c = myConnection.connect();
            if (c != null) {
                
                String consultaSQL = "INSERT INTO HairCut (idHairCut,idClient,idBarber,cuts,date,price,priceBarber) VALUES (?,?,?,?,?,?,?)";
                PreparedStatement ps = null;
                
                try {
                    ps = c.prepareStatement(consultaSQL);
                    ps.setInt(1, o.getIdHairCut());
                    ps.setInt(2, o.getClient().getIdClient());
                    ps.setInt(3, o.getBarber().getIdBarber());
                    ps.setString(4, o.getCuts());
                    ps.setTimestamp(5, o.getDate());
                    ps.setDouble(6, o.getPrice());
                    ps.setDouble(7, o.getPriceBarber());
                    
                    r = ps.executeUpdate();
                    ps.close();
                    return r;
                    
                } catch (SQLException ex) {
                    Logger.getLogger(HairCut.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        c.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(HairCut.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        return r;
    }
    
    @Override
    public List<HairCut> queryFilter(int code, String name) {
        List<HairCut> list = null;
        String consultaSQL = "";
        if (c != null) {
            try {
                if (code != 0 && code > 0) {
                    consultaSQL += "SELECT `haircut`.`date`, `barber`.`name`,`barber`.`lastName`, `haircut`.`cuts`,`haircut`.`price` FROM HairCut INNER JOIN barber ON `haircut`.`idBarber`= barber.idBarber WHERE idClient = ?";
                } else if (code == 0) {
                    consultaSQL += "SELECT `haircut`.`date`, `barber`.`name`,`barber`.`lastName`, `haircut`.`cuts`,`haircut`.`priceBarber` FROM HairCut INNER JOIN barber ON `haircut`.`idBarber`= barber.idBarber";
                }
                
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                if (code != 0 && code > 0) {
                    ps.setInt(1, code);
                }
                ResultSet rs = ps.executeQuery();
                
                list = new ArrayList<>();
                while (rs.next()) {
                    HairCut hairCut = new HairCut();
                    hairCut.setDate(rs.getTimestamp("date"));
                    
                    Barber b = new Barber();
                    b.setName(rs.getString("name"));
                    b.setLastName(rs.getString("lastName"));
                    hairCut.setBarber(b);
                    
                    hairCut.setCuts(rs.getString("cuts"));
                    hairCut.setPrice(rs.getDouble("price"));
                    
                    list.add(hairCut);
                }
                
                ps.close();
                rs.close();
                
                return list;
                
            } catch (SQLException ex) {
                Logger.getLogger(HairCut.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(HairCut.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return list;
    }
    
    @Override
    public int modificar(int code, HairCut o) {
        int r = 0;
        if (c != null) {
            try {
                String consultaSQL = "UPDATE HairCut SET idClient = ?,idBarber = ?,cuts = ?, date = ?, price = ?,priceBarber = ? WHERE idCuts = ?";
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ps.setInt(1, o.getClient().getIdClient());
                ps.setInt(2, o.getBarber().getIdBarber());
                ps.setString(3, o.getCuts());
                ps.setTimestamp(4, o.getDate());
                ps.setInt(5, code);
                ps.setDouble(6, o.getPrice());
                ps.setDouble(7, o.getPriceBarber());
                r = ps.executeUpdate();
                
                ps.close();
                return r;
                
            } catch (SQLException ex) {
                Logger.getLogger(HairCut.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                    
                } catch (SQLException ex) {
                    Logger.getLogger(HairCut.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return r;
    }
    
    @Override
    public List<HairCut> toList() {
        List<HairCut> list = null;
        if (c != null) {
            try {
                String consultaSQL = "SELECT idHairCut,idClient,idBarber,cuts,date,price,priceBarber FROM HairCut";
                
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ResultSet rs = ps.executeQuery();
                
                list = new ArrayList<>();
                while (rs.next()) {
                    HairCut hairCut = new HairCut();
                    hairCut.setDate(rs.getTimestamp("date"));
                    
                    Barber b = new Barber();
                    b.setIdBarber(rs.getInt("idBarber"));
                    hairCut.setBarber(b);
                    
                    hairCut.setCuts(rs.getString("cuts"));
                    hairCut.setPrice(rs.getDouble("price"));
                    hairCut.setPriceBarber(rs.getDouble("priceBarber"));
                    list.add(hairCut);
                }
                
                ps.close();
                rs.close();
                
                return list;
                
            } catch (SQLException ex) {
                Logger.getLogger(HairCut.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(HairCut.class.getName()).log(Level.SEVERE, null, ex);
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
                PreparedStatement ps = c.prepareStatement("DELETE FROM HairCut WHERE idHairCut=?");
                ps.setInt(1, code);
                
                r = ps.executeUpdate();
                ps.close();
                return r;
                
            } catch (SQLException ex) {
                Logger.getLogger(HairCut.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                    
                } catch (SQLException ex) {
                    Logger.getLogger(HairCut.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return r;
    }
    
    @Override
    public List<HairCut> queryFilterForDate(String time) {
        List<HairCut> list = null;
        if (c != null) {
            try {
                String consultaSQL = "SELECT `haircut`.`date`, `barber`.`name`,`barber`.`lastName`, `haircut`.`cuts`,`haircut`.`price`,`haircut`.`priceBarber` FROM `haircut` INNER JOIN `barber` ON `haircut`.`idBarber`= barber.idBarber WHERE DATE(`hairCut`.`date`)= ?";
                
                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ps.setString(1, time);
                
                ResultSet rs = ps.executeQuery();
                
                list = new ArrayList<>();
                while (rs.next()) {
                    HairCut hairCut = new HairCut();
                    hairCut.setDate(rs.getTimestamp("date"));
                    
                    Barber b = new Barber();
                    b.setName(rs.getString("name"));
                    b.setLastName(rs.getString("lastName"));
                    hairCut.setBarber(b);
                    
                    hairCut.setCuts(rs.getString("cuts"));
                    hairCut.setPrice(rs.getDouble("price"));
                    hairCut.setPriceBarber(rs.getDouble("priceBarber"));
                    
                    list.add(hairCut);
                }
                
                ps.close();
                rs.close();
                
                return list;
                
            } catch (SQLException ex) {
                Logger.getLogger(HairCut.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(HairCut.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return list;
    }
}
