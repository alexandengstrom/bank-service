package se.liu.ida.tdp024.account.data.api.facade;
import java.util.List;

import se.liu.ida.tdp024.account.data.api.entity.Account;

public interface AccountEntityFacade {
    public int create(int personKey, int bankKey, String accountType);
    public List<Account> findByPersonKey(int personKey);
    public Boolean debit(int accountId, int ammount);
    public Boolean credit(int accountId, int ammount);
    public Account findById(int accountId);
    
}
