package com.simonmarcos.lupos.dao;

import com.simonmarcos.lupos.model.Expenses;
import java.util.List;

public interface DAOExpenses extends DAO<Expenses> {

    public List<Expenses> queryFilterForDateBetwen(String since, String until);

    public double getPriceTotalExpenses(String since, String until);
}
