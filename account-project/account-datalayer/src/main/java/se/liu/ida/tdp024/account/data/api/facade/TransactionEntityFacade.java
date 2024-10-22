package se.liu.ida.tdp024.account.data.api.facade;
import java.util.List;

import javax.persistence.EntityManager;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;

public interface TransactionEntityFacade {
    public int create(Account account, String type, int amount, String status);
    public int createTransactionWithEM(Account account, String type, int amount, String status, EntityManager em);

    public List<Transaction> list(Account account);
    
}
