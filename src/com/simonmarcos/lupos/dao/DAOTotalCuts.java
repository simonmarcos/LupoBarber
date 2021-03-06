package com.simonmarcos.lupos.dao;

import com.simonmarcos.lupos.model.TotalCuts;
import java.util.List;

public interface DAOTotalCuts extends DAO<TotalCuts> {

    public int deleteByDate(String date);

    public List<TotalCuts> queryGetByMonthAndYear(int month, int year);

    public List<TotalCuts> queryGetBySinceAndUntil(String since, String until);

    public double getPriceTotalHairCut(String since, String until);
}
