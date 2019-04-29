package com.simonmarcos.lupos.dao.impl;

import com.simonmarcos.lupos.dao.ConnectionDB;
import com.simonmarcos.lupos.dao.DAOTotalCuts;
import com.simonmarcos.lupos.model.Cuts;
import com.simonmarcos.lupos.model.HairCut;
import com.simonmarcos.lupos.model.TotalCuts;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TotalCutsDAOImpl implements DAOTotalCuts {

    private ConnectionDB myConnection;
    private Connection c;

    public TotalCutsDAOImpl() {
        myConnection = ConnectionDB.instanciar();
        c = myConnection.connect();
        System.out.println("TotalCuts DAO");
    }

    @Override
    public int save(TotalCuts o) {
        List<TotalCuts> lista = toList();
        int r = 0;
        if (!lista.contains(o)) {
            c = myConnection.connect();
            if (c != null) {

                String consultaSQL = "INSERT INTO totalcuts1 (idTotalCuts,cutsAdult,cutsBeard,cutsDrawing,cutsEyabrow,cutsWashes,date,earningsTotal,earningsLupos) VALUES (?,?,?,?,?,?,?,?,?)";
                PreparedStatement ps = null;

                try {

                    ps = c.prepareStatement(consultaSQL);
                    ps.setInt(1, o.getIdTotalCuts());
                    ps.setInt(2, o.getCutsAdult());
                    ps.setInt(3, o.getCutsBeard());
                    ps.setInt(4, o.getCutsDrawing());
                    ps.setInt(5, o.getCutsEyabrow());
                    ps.setInt(6, o.getCutsWashes());
                    ps.setDate(7, o.getDate());
                    ps.setDouble(8, o.getEarningsTotal());
                    ps.setDouble(9, o.getEarningsLupos());

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
    public List<TotalCuts> queryFilter(int code, String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int modificar(int code, TotalCuts o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<TotalCuts> toList() {
        List<TotalCuts> list = null;
        if (c != null) {
            try {
                String consultaSQL = "SELECT idTotalCuts,cutsAdult,cutsBeard,cutsDrawing,date,earningsTotal,earningsLupos FROM TotalCuts1";

                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ResultSet rs = ps.executeQuery();

                list = new ArrayList<>();
                while (rs.next()) {
                    TotalCuts tc = new TotalCuts();
                    tc.setIdTotalCuts(rs.getInt("idTotalCuts"));
                    tc.setCutsAdult(rs.getInt("cutsAdult"));
                    tc.setCutsBeard(rs.getInt("cutsBeard"));
                    tc.setCutsDrawing(rs.getInt("cutsDrawing"));
                    tc.setDate(rs.getDate("date"));
                    tc.setEarningsTotal(rs.getDouble("earningsTotal"));
                    tc.setEarningsLupos(rs.getDouble("earningsLupos"));
                    list.add(tc);
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
        return 0;
    }

    @Override
    public int deleteByDate(String date) {
        int r = 3;
        if (c != null) {
            try {
                PreparedStatement ps = c.prepareStatement("DELETE FROM TotalCuts1 WHERE `totalcuts1`.`date` = ?");
                ps.setString(1, date);

                r = ps.executeUpdate();
                ps.close();
                return r;

            } catch (SQLException ex) {
                Logger.getLogger(Cuts.class.getName()).log(Level.SEVERE, null, ex);
                return r;
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
    public List<TotalCuts> queryGetByMonthAndYear(int month, int year) {
        List<TotalCuts> listTotalCuts = new ArrayList<>();
        String consultaSQL = "";
        if (c != null) {
            try {
                //Si quiere las ganancias total del negocio sin descontar lo de los barberos

                consultaSQL += "SELECT `totalcuts1`.`idTotalCuts`,`TotalCuts1`.`CutsAdult`,`TotalCuts1`.`CutsBeard`,`TotalCuts1`.`CutsWashes`,`TotalCuts1`.`CutsEyabrow`,`TotalCuts1`.`CutsDrawing`,totalCuts1.`date`,totalCuts1.`earningsTotal`,totalCuts1.`earningsLupos` FROM TotalCuts1 WHERE MONTH(`totalcuts1`.`date`) = ? AND YEAR(`totalcuts1`.`date`) = ?";

                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ps.setInt(1, month);
                ps.setInt(2, year);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    TotalCuts tc = new TotalCuts();
                    tc.setIdTotalCuts(rs.getInt("idTotalCuts"));
                    tc.setCutsAdult(rs.getInt("cutsAdult"));
                    tc.setCutsBeard(rs.getInt("cutsBeard"));
                    tc.setCutsDrawing(rs.getInt("cutsDrawing"));
                    tc.setCutsEyabrow(rs.getInt("cutsEyabrow"));
                    tc.setCutsWashes(rs.getInt("cutsWashes"));
                    tc.setDate(rs.getDate("date"));
                    tc.setEarningsTotal(rs.getDouble("earningsTotal"));
                    tc.setEarningsLupos(rs.getDouble("earningsLupos"));
                    listTotalCuts.add(tc);
                }

                ps.close();
                rs.close();

                return listTotalCuts;

            } catch (SQLException ex) {
                Logger.getLogger(TotalCuts.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(TotalCuts.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return listTotalCuts;
    }

    @Override
    public List<TotalCuts> queryGetBySinceAndUntil(String since, String until) {
        List<TotalCuts> listTotalCuts = new ArrayList<>();
        String consultaSQL = "";
        if (c != null) {
            try {
                //Si quiere las ganancias total del negocio sin descontar lo de los barberos

                consultaSQL += "SELECT `totalcuts1`.`idTotalCuts`,`TotalCuts1`.`CutsAdult`,`TotalCuts1`.`CutsBeard`,`TotalCuts1`.`CutsDrawing`,`TotalCuts1`.`CutsEyabrow`,`TotalCuts1`.`CutsWashes`,totalCuts1.`date`,totalCuts1.`earningsTotal`,totalCuts1.`earningsLupos` FROM `totalcuts1` WHERE `totalcuts1`.`date` BETWEEN ? AND ?";

                PreparedStatement ps = c.prepareStatement(consultaSQL);
                ps.setString(1, since);
                ps.setString(2, until);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    TotalCuts tc = new TotalCuts();
                    tc.setIdTotalCuts(rs.getInt("idTotalCuts"));
                    tc.setCutsAdult(rs.getInt("cutsAdult"));
                    tc.setCutsBeard(rs.getInt("cutsBeard"));
                    tc.setCutsDrawing(rs.getInt("cutsDrawing"));
                    tc.setCutsEyabrow(rs.getInt("cutsEyabrow"));
                    tc.setCutsWashes(rs.getInt("cutsWashes"));
                    tc.setDate(rs.getDate("date"));
                    tc.setEarningsTotal(rs.getDouble("earningsTotal"));
                    tc.setEarningsLupos(rs.getDouble("earningsLupos"));
                    listTotalCuts.add(tc);
                }

                ps.close();
                rs.close();

                return listTotalCuts;

            } catch (SQLException ex) {
                Logger.getLogger(TotalCuts.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(TotalCuts.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return listTotalCuts;
    }

    @Override
    public double getPriceTotalHairCut(String since, String until) {
        double valueTotal = 0;
        if (c != null) {
            try {
                String consultaSQL = "SELECT SUM(`totalcuts1`.`earningsLupos`) AS valueTotal FROM `totalcuts1` WHERE `totalcuts1`.`date` BETWEEN ? AND ?";
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
                Logger.getLogger(HairCut.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(HairCut.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return valueTotal;
    }
}
