package com.simonmarcos.lupos.model;

public class Cuts {

    private int idCuts;
    private String type;
    private double price;
    private double priceBarber;

    public Cuts(int idCuts, String type, double price, double priceBarber) {
        this.idCuts = idCuts;
        this.type = type;
        this.price = price;
        this.priceBarber = priceBarber;
    }

    public Cuts() {
    }

    public int getIdCuts() {
        return idCuts;
    }

    public void setIdCuts(int idCuts) {
        this.idCuts = idCuts;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public double getPriceBarber() {
        return priceBarber;
    }

    public void setPriceBarber(double priceBarber) {
        this.priceBarber = priceBarber;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
