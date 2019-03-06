package com.simonmarcos.lupos.dao;

import java.util.List;

public interface DAO<Object> {

    public int save(Object o);

    public List<Object> queryFilter(int code, String name);

    public int modificar(int code, Object o);

    public List<Object> toList();

    public int delete(int code);
}
