package com.simonmarcos.lupos.dao;

import com.simonmarcos.lupos.model.TotalCuts;

public interface DAOTotalCuts extends DAO<TotalCuts> {

    public int deleteByDate(String date);
}
