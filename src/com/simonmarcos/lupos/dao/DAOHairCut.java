package com.simonmarcos.lupos.dao;

import com.simonmarcos.lupos.model.HairCut;
import java.util.List;

public interface DAOHairCut extends DAO<HairCut> {
 
    public List<HairCut> queryFilterForDate(String time);
}
