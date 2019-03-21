package com.simonmarcos.lupos.model;

import java.sql.Date;

public class TotalCuts {

    private int idTotalCuts;
    private int cutsAdult;
    private int cutsBoy;
    private int cutsBeard;
    private int cutsDrawing;
    private java.sql.Date date;
    private double earningsTotal;
    private double earningsLupos;

    public TotalCuts(int idTotalCuts, int cutsAdult, int cutsBoy, int cutsBeard, int cutsDrawing, Date date, double earningsTotal, double earningsLupos) {
        this.idTotalCuts = idTotalCuts;
        this.cutsAdult = cutsAdult;
        this.cutsBoy = cutsBoy;
        this.cutsBeard = cutsBeard;
        this.cutsDrawing = cutsDrawing;
        this.date = date;
        this.earningsTotal = earningsTotal;
        this.earningsLupos = earningsLupos;
    }

    public TotalCuts(int cutsAdult, int cutsBoy, int cutsBeard, int cutsDrawing, Date date, double earningsTotal, double earningsLupos) {
        this.cutsAdult = cutsAdult;
        this.cutsBoy = cutsBoy;
        this.cutsBeard = cutsBeard;
        this.cutsDrawing = cutsDrawing;
        this.date = date;
        this.earningsTotal = earningsTotal;
        this.earningsLupos = earningsLupos;
    }

    public TotalCuts() {
    }

    public int getIdTotalCuts() {
        return idTotalCuts;
    }

    public void setIdTotalCuts(int idTotalCuts) {
        this.idTotalCuts = idTotalCuts;
    }

    public int getCutsAdult() {
        return cutsAdult;
    }

    public void setCutsAdult(int cutsAdult) {
        this.cutsAdult = cutsAdult;
    }

    public int getCutsBoy() {
        return cutsBoy;
    }

    public void setCutsBoy(int cutsBoy) {
        this.cutsBoy = cutsBoy;
    }

    public int getCutsBeard() {
        return cutsBeard;
    }

    public void setCutsBeard(int cutsBeard) {
        this.cutsBeard = cutsBeard;
    }

    public int getCutsDrawing() {
        return cutsDrawing;
    }

    public void setCutsDrawing(int cutsDrawing) {
        this.cutsDrawing = cutsDrawing;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getEarningsTotal() {
        return earningsTotal;
    }

    public void setEarningsTotal(double earningsTotal) {
        this.earningsTotal = earningsTotal;
    }

    public double getEarningsLupos() {
        return earningsLupos;
    }

    public void setEarningsLupos(double earningsLupos) {
        this.earningsLupos = earningsLupos;
    }

    @Override
    public String toString() {
        return "TotalCuts{" + "idTotalCuts=" + idTotalCuts + ", cutsAdult=" + cutsAdult + ", cutsBoy=" + cutsBoy + ", cutsBeard=" + cutsBeard + ", cutsDrawing=" + cutsDrawing + ", date=" + date + ", earningsTotal=" + earningsTotal + ", earningsLupos=" + earningsLupos + '}';
    }

}
