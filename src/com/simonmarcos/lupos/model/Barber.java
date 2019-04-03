package com.simonmarcos.lupos.model;

import java.sql.Date;

public class Barber implements Comparable<Barber> {

    private int idBarber;
    private String name;
    private String lastName;
    private int dni;
    private String phone;
    private java.sql.Date dateEntry;
    private String address;
    private java.sql.Date birthday;

    public Barber() {
    }

    public Barber(int idBarber, String name, String lastName, int dni, String phone, Date dateEntry, String address, Date birthday) {
        this.idBarber = idBarber;
        this.name = name;
        this.lastName = lastName;
        this.dni = dni;
        this.phone = phone;
        this.dateEntry = dateEntry;
        this.address = address;
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getIdBarber() {
        return idBarber;
    }

    public void setIdBarber(int idBarber) {
        this.idBarber = idBarber;
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

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public Date getDateEntry() {
        return dateEntry;
    }

    public void setDateEntry(Date dateEntry) {
        this.dateEntry = dateEntry;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.dni;
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
        final Barber other = (Barber) obj;
        if (this.dni != other.dni) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Barber o) {
        int res = this.getLastName().compareToIgnoreCase(o.getLastName());
        if (res != 0) {
            return res;
        }
        return this.getName().compareToIgnoreCase(o.getName());
    }

    @Override
    public String toString() {
        return "Barber{" + "idBarber=" + idBarber + ", name=" + name + ", lastName=" + lastName + ", dni=" + dni + ", phone=" + phone + ", dateEntry=" + dateEntry + ", address=" + address + '}';
    }

}
