package com.simonmarcos.lupos.model;

import java.sql.Timestamp;

public class HairCut implements Comparable<HairCut> {

    private int idHairCut;
    private Client client;
    private Barber barber;
    private String cuts;
    private java.sql.Timestamp date;
    private double price;
    private double priceBarber;

    public HairCut(int idHairCut, Client client, Barber barber, String cuts, Timestamp date, double price, double priceBarber) {
        this.idHairCut = idHairCut;
        this.client = client;
        this.barber = barber;
        this.cuts = cuts;
        this.date = date;
        this.price = price;
        this.priceBarber = priceBarber;
    }

    public HairCut(Client client, Barber barber, String cuts, java.sql.Timestamp date, double price) {
        this.client = client;
        this.barber = barber;
        this.cuts = cuts;
        this.date = date;
        this.price = price;
    }

    public HairCut() {
    }

    public int getIdHairCut() {
        return idHairCut;
    }

    public void setIdHairCut(int idHairCut) {
        this.idHairCut = idHairCut;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Barber getBarber() {
        return barber;
    }

    public void setBarber(Barber barber) {
        this.barber = barber;
    }

    public String getCuts() {
        return cuts;
    }

    public void setCuts(String cuts) {
        this.cuts = cuts;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public java.sql.Timestamp getDate() {
        return date;
    }

    public void setDate(java.sql.Timestamp date) {
        this.date = date;
    }

    public double getPriceBarber() {
        return priceBarber;
    }

    public void setPriceBarber(double priceBarber) {
        this.priceBarber = priceBarber;
    }

    @Override
    public String toString() {
        return "HairCut{" + "idHairCut=" + idHairCut + ", client=" + client + ", barber=" + barber + ", cuts=" + cuts + ", date=" + date + ", price=" + price + '}';
    }

    @Override
    public int compareTo(HairCut o) {
        return this.getDate().compareTo(o.getDate());
    }

}
