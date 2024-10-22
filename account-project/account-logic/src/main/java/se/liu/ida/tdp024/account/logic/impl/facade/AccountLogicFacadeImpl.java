package se.liu.ida.tdp024.account.logic.impl.facade;

import java.util.List;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.exceptions.EntityNotFoundException;
import se.liu.ida.tdp024.account.data.api.exceptions.InputParameterException;
import se.liu.ida.tdp024.account.data.api.exceptions.ServiceConfigurationException;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.api.facade.ExternalResourceFacade;
import se.liu.ida.tdp024.account.logic.api.facade.TransactionLogicFacade;
import se.liu.ida.tdp024.account.logic.interfaces.Bank;
import se.liu.ida.tdp024.account.logic.interfaces.Person;

public class AccountLogicFacadeImpl implements AccountLogicFacade {
    
    private final AccountEntityFacade accountEntityFacade;
    private final TransactionLogicFacade transactionLogicFacade;
    private final ExternalResourceFacade<Person> personService;
    private final ExternalResourceFacade<Bank> bankService;

    
    public AccountLogicFacadeImpl(AccountEntityFacade accountEntityFacade, ExternalResourceFacade<Person> personServiceImpl, ExternalResourceFacade<Bank> bankServiceImpl, TransactionLogicFacade tef) {
        this.personService = personServiceImpl;
        this.bankService = bankServiceImpl;
        this.accountEntityFacade = accountEntityFacade;
        this.transactionLogicFacade = tef;
    }

    @Override
    public Boolean createAccount(String accountType, int personKey, String bankName) throws EntityNotFoundException, ServiceConfigurationException, Exception {
        final List<Bank> banks = this.bankService.findByName(bankName);

        if (accountType.isBlank() || !("SAVINGS".equals(accountType) || "CHECK".equals(accountType))) {
            throw new InputParameterException("Account type must be SAVINGS or CHECK");
        }

        if (banks.size() < 1) {
            // return false;
            throw new InputParameterException("Bank " + bankName + " does not exist");
        }

        final Bank bank = banks.get(0);
        
        try {
            final Person person = this.personService.findById(personKey);

            final int result = this.accountEntityFacade.create(person.key, bank.key, accountType);
            
            return result != -1;
        } catch (ServiceConfigurationException e) {
            throw e;
        } catch (Throwable e) {
            throw new InputParameterException("Person with key " + personKey + " does not exist");
        }

    }
    @Override  
    public List<Account> findAccountsByPerson(int personkey){
        return accountEntityFacade.findByPersonKey(personkey);
    }
    @Override
        public Boolean debitAccount(int accountId ,int amount) throws EntityNotFoundException, InputParameterException{
            
            Account account = accountEntityFacade.findById(accountId);

            if (account == null) { 
                throw new EntityNotFoundException("Account with id " + accountId + " does not exist"); 
            }

            Boolean debitedAccount = accountEntityFacade.debit(accountId, amount);

            if(!debitedAccount){
                transactionLogicFacade.createTransaction(account, "DEBIT" ,amount, "FAILED");
            }
            return debitedAccount;   
    }

    @Override
    public Boolean creditAccount(int accountId, int ammount) throws EntityNotFoundException {
            Account account = accountEntityFacade.findById(accountId);

            if (account == null) { 
                throw new EntityNotFoundException("Account with id " + accountId + " does not exist"); 
            }

            Boolean creditedAccount = accountEntityFacade.credit(accountId, ammount);

            if (!creditedAccount){
                transactionLogicFacade.createTransaction(account, "CREDIT", ammount, "FAILED");
            }

            return creditedAccount;
    }
    @Override
    public List<Transaction> findTransactionsById(int accountId) throws EntityNotFoundException {
        Account account = accountEntityFacade.findById(accountId);

        if (account == null) { 
                throw new EntityNotFoundException("Account with id " + accountId + " does not exist"); 
            }

        return transactionLogicFacade.findTransactionsByAccount(account);
    }
}
