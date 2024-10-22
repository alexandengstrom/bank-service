package se.liu.ida.tdp024.account.logic.api.facade;
import java.util.List;

import javax.persistence.EntityManager;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;


public interface TransactionLogicFacade {
    public List<Transaction> findTransactionsByAccount(Account account);
    public Boolean createTransaction(Account account, String type, int amount, String status);
}
