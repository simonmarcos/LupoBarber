package com.simonmarcos.lupos.dao;

import com.simonmarcos.lupos.model.HairCut;
import java.util.List;

public interface DAOHairCut extends DAO<HairCut> {

    public List<HairCut> queryFilterForDate(String time);

    public List<HairCut> queryFilterForDateBetwen(int idBarber, String since, String until);

    public double queryGetEarningsTotal(String since, String until);
}
