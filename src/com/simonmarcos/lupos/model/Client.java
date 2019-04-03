package com.simonmarcos.lupos.model;

import java.sql.Date;

public class Client implements Comparable<Client> {

    private int idClient;
    private int DNI;
    private String name;
    private String lastName;
    private String phone;
    private java.sql.Date birthday;

    public Client(int idClient, int DNI, String name, String lastName, String phone, Date birthday) {
        this.idClient = idClient;
        this.DNI = DNI;
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.birthday = birthday;
    }

    public Client() {
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getDNI() {
        return DNI;
    }

    public void setDNI(int DNI) {
        this.DNI = DNI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + this.DNI;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Client other = (Client) obj;
        if (this.DNI != other.DNI) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Client o) {
        int res = this.getLastName().compareToIgnoreCase(o.getLastName());
        if (res != 0) {
            return res;
        }
        return this.getName().compareToIgnoreCase(o.getName());
    }

    @Override
    public String toString() {
        return "Client{" + "idClient=" + idClient + ", DNI=" + DNI + ", name=" + name + ", lastName=" + lastName + ", phone=" + phone + '}';
    }

}
