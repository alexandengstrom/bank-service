package se.liu.ida.tdp024.account.data.test.facade;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;


public class AccountEntityFacadeTest {
    
    //---- Unit under test ----//
    private final   AccountEntityFacade accountEntityFacade = new AccountEntityFacadeDB(new TransactionEntityFacadeDB());

    private StorageFacade storageFacade  = new StorageFacadeDB();
    
    @After
    public void tearDown() {
        storageFacade.emptyStorage();
    }
    
    @Test
    public void testCreate() {
        int accountId = accountEntityFacade.create(1, 1, "SAVINGS");
        int accountIdd = accountEntityFacade.create(1, 1, "CHECK");

        Assert.assertEquals(1, accountId);
        Assert.assertEquals(2, accountIdd);
    }

    @Test
    public void testFind(){
        int accountId = accountEntityFacade.create(1, 1, "SAVINGS");

        List<Account> lst = accountEntityFacade.findByPersonKey(1);
        Assert.assertEquals(1, lst.size());
    }

    @Test
    public void testCredit(){
        int accountId = accountEntityFacade.create(1, 1, "SAVINGS");

        Boolean credit = accountEntityFacade.credit(1, 100);
        Assert.assertTrue(credit);
    }

    @Test
    public void testDebit(){
        int accountId = accountEntityFacade.create(1, 1, "SAVINGS");
        Boolean credit = accountEntityFacade.credit(1, 100);


        Boolean debit = accountEntityFacade.debit(1, 100);
        Boolean failedDbit = accountEntityFacade.debit(2, 100);
        Assert.assertTrue(debit);
        Assert.assertFalse(failedDbit);
    }

    @Test
    public void findTest(){
        int accountId = accountEntityFacade.create(1, 1, "SAVINGS");
        
        Account acc = accountEntityFacade.findById(1);
        Assert.assertEquals(1, acc.getId());
    }
}