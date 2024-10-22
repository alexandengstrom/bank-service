package se.liu.ida.tdp024.account.logic.api.facade;

import java.util.List;

public interface ExternalResourceFacade<T> {

    public List<T> list() throws Exception;

    public List<T> findByName(String name) throws Exception;

    public T findById(int id) throws Exception;
}
