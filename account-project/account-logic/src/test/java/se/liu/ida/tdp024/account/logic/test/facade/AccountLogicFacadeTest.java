package se.liu.ida.tdp024.account.logic.test.facade;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.exceptions.EntityNotFoundException;
import se.liu.ida.tdp024.account.data.api.exceptions.InputParameterException;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountDB;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionDB;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.api.facade.ExternalResourceFacade;
import se.liu.ida.tdp024.account.logic.api.utils.Logger;
import se.liu.ida.tdp024.account.logic.impl.facade.AccountLogicFacadeImpl;
import se.liu.ida.tdp024.account.logic.impl.facade.TransactionLogicFacadaImpl;
import se.liu.ida.tdp024.account.logic.interfaces.Bank;
import se.liu.ida.tdp024.account.logic.interfaces.Person;

public class AccountLogicFacadeTest {

    public Logger mockLogger = new Logger() {
      @Override
      public void publish(String message) {
        return;
      }
    };

    public TransactionEntityFacade transactionEntityFacade = new TransactionEntityFacade() {
        @Override
        public int create(Account account, String type, int amount, String status) {
            return 1;
        }

        @Override
        public List<Transaction> list(Account account) {
            Transaction transaction = new TransactionDB();
            transaction.setAccount(account);

            List<Transaction> transactionList = new ArrayList<>();
            transactionList.add(transaction);

            return transactionList;
        }

        @Override
        public int createTransactionWithEM(Account account, String type, int amount, String status, EntityManager em) {
            return 1;
        }

    };

    public AccountEntityFacade accountEntityFacade = new AccountEntityFacade(){
      @Override
      public int create(int personKey, int bankKey, String accountType){
        return 1;

      }
      @Override
      public List<Account> findByPersonKey(int personKey){
        Account account = new AccountDB();
        account.setId(1);
        List<Account> list = new ArrayList<>();
        list.add(account);

        if(personKey == 1){
          return list;
        }
        return new ArrayList<>();
        

      }
      
      public Boolean debit(int accountId, int ammount){
        return true;

      }
      public Boolean credit(int accountId, int ammount){
        return true;

      }
      public Account findById(int accountId){
        Account account = new AccountDB();
        account.setAccountType("SAVINGS");
        account.setHoldings(100);
        account.setBankKey(1);
        account.setId(1);

        if(account.getId() == accountId){
          return account;
        }
        return null;
      } 
    };
//-----------------------------------------------------------------------------------------

    public ExternalResourceFacade<Person> personService = new ExternalResourceFacade<Person>()  {

    public List<Person> list() throws Exception{
      return null;
    }
    
    @Override
    public List<Person> findByName(String name) throws Exception{
      Person p = new Person();
      p.name = "alexander";

      List<Person> persons = new ArrayList<>();
      persons.add(p);

      if (!name.equals("alexander")){
        return new ArrayList<>();
      }
      
      return persons;
    }

    @Override
    public Person findById(int id) throws Exception{
      Person p = new Person();
      p.name = "alexander";

      if ( id != 1){
        return null;
      }
      
      return p;
    };

  };

//-----------------------------------------------------------------------------------------
    public ExternalResourceFacade<Bank> bankService = new ExternalResourceFacade<Bank>()  {
    @Override
    public List<Bank> list() throws Exception{
      return null;
    }
    @Override
    public List<Bank> findByName(String name) throws Exception{
      Bank swed = new Bank();
      swed.name = "swed";

      List<Bank> banks = new ArrayList<>();
      banks.add(swed);

      if (!name.equals("swed")){
        return new ArrayList<>();
      }
      
      return banks;
    }

    @Override
    public Bank findById(int id) throws Exception{
      return null;
    }

    };
//-----------------------------------------------------------------------------------------

    //--- Unit under test ---//
    public AccountLogicFacade accountLogicFacade = new AccountLogicFacadeImpl(
      accountEntityFacade, 
      personService, 
      bankService, 
      new TransactionLogicFacadaImpl(transactionEntityFacade, mockLogger)); 
//-----------------------------------------------------------------------------------------
    
    public StorageFacade storageFacade  = new StorageFacadeDB();

    @After
    public void tearDown() {
      if (storageFacade != null)
        storageFacade.emptyStorage();
    }

    @Test
    public void testCreateSuccess() throws Exception {
      Boolean result = accountLogicFacade.createAccount("SAVINGS", 1, "swed");
      Assert.assertTrue(result);
    }

    @Test(expected = InputParameterException.class)
    public void testCreateIncorrectBank() throws Exception {
      accountLogicFacade.createAccount("SAVINGS", 4, "nordea");
    }

    @Test(expected = InputParameterException.class)
    public void testCreateIncorrectPerson() throws Exception {
      accountLogicFacade.createAccount("SAVINGS", 2, "swed");
    }

    @Test(expected = InputParameterException.class)
    public void testCreateIncorrectType() throws Exception {
      accountLogicFacade.createAccount("INVALID", 1, "swed");
    }

    @Test
    public void testFindByPerson() {
      List<Account> lst = accountLogicFacade.findAccountsByPerson(1);
      Assert.assertEquals(1, lst.size());
      
      lst = accountLogicFacade.findAccountsByPerson(2);
      Assert.assertEquals(0, lst.size());
    }

    @Test
    public void testDebit(){
      Boolean transaction = accountLogicFacade.debitAccount(1, 100);
      Assert.assertTrue(transaction);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDebitInvalidAccount(){
      accountLogicFacade.debitAccount(2, 100);
    }

    @Test
    public void testCredit(){
      Boolean transaction = accountLogicFacade.creditAccount(1, 100);
      Assert.assertTrue(transaction);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testCreditInvalidAccount(){
      accountLogicFacade.creditAccount(2, 100);
    }

    @Test
    public void testTransactionByID(){
      List<Transaction> lst = accountLogicFacade.findTransactionsById(1);
      Assert.assertEquals(1, lst.size());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testFindTransactionIncorrectId(){
      accountLogicFacade.findTransactionsById(2);
    }
    
} 
