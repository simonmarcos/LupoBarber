package com.simonmarcos.lupos.dao;

import com.simonmarcos.lupos.model.Cuts;

public interface DAOCuts extends DAO<Cuts> {

    public double getPriceBarber(String name);

    public double[] getPrizeANDPrice(String cuts);
}
