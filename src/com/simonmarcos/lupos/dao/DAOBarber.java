package com.simonmarcos.lupos.dao;

import com.simonmarcos.lupos.model.Barber;
import java.util.List;

public interface DAOBarber extends DAO<Barber> {
    
    public List<Barber> toListByNameAndLastName();
}
