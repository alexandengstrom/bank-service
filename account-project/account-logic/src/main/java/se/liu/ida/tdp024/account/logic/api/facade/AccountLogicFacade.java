package se.liu.ida.tdp024.account.logic.api.facade;
import java.util.List;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.exceptions.EntityNotFoundException;
import se.liu.ida.tdp024.account.data.api.exceptions.InputParameterException;

public interface AccountLogicFacade {
    public Boolean createAccount(String accountType, int personKey, String bankKey) throws EntityNotFoundException, Exception;
    public List<Account> findAccountsByPerson(int personkey);
    public Boolean debitAccount(int accountId ,int ammount) throws EntityNotFoundException, InputParameterException;
    public Boolean creditAccount(int accountId, int ammount) throws EntityNotFoundException;
    public List<Transaction> findTransactionsById(int accountId) throws EntityNotFoundException;
}
