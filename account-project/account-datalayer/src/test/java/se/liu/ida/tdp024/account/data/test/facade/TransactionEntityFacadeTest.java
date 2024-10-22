package se.liu.ida.tdp024.account.data.test.facade;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;

public class TransactionEntityFacadeTest {
    
    //---- Unit under test ----//
    private final TransactionEntityFacade transactionEntityFacade = new TransactionEntityFacadeDB();
   // private StorageFacade storageFacade = new StorageFacadeDB();
    private final AccountEntityFacade accountEntityFacade = new AccountEntityFacadeDB( new TransactionEntityFacadeDB());
    // @After
    // public void tearDown() {
    //     storageFacade.emptyStorage();
    // }
    
    @Test
    public void testCreate() {
        int accountId = accountEntityFacade.create(1, 1, "SAVINGS");
        Account acc = accountEntityFacade.findById(accountId);

        int transId = transactionEntityFacade.create(acc, "CREDIT", 100, "OK");

        Assert.assertNotEquals(-1, transId); // Adjust the expected result as needed
    }
    @Test
    public void testList(){
         int accountId = accountEntityFacade.create(2, 1, "SAVINGS");
        Account acc = accountEntityFacade.findById(accountId);

        int transId = transactionEntityFacade.create(acc, "CREDIT", 100, "OK");
        int transI2 = transactionEntityFacade.create(acc, "CREDIT", 100, "OK");
        int transI3 = transactionEntityFacade.create(acc, "CREDIT", 100, "OK");
        int transI4 = transactionEntityFacade.create(acc, "CREDIT", 100, "OK");
        int transI5 = transactionEntityFacade.create(acc, "CREDIT", 100, "OK");

        List<Transaction> lst = transactionEntityFacade.list(acc);
        Assert.assertEquals(5, lst.size());
    }
}
