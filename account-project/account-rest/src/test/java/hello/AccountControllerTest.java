package hello;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;

public class AccountControllerTest {
    private AccountController controller = new AccountController();

    private int getAccountId() {
        controller.createAccount("SAVINGS", 5, "SWEDBANK");
        ResponseEntity<List<Account>> result = controller.findAccountbyPerson(5);
        
        List<Account> body = result.getBody();

        if (body != null) {
            return body.get(body.size() - 1).getId();
        } else {
            return -1;
        }
    }

    @Test
    public void testCreateAccount() {
        ResponseEntity<String> result = controller.createAccount("SAVINGS", 5, "SWEDBANK");
        Assert.assertTrue(result.getStatusCode() == HttpStatus.OK);
    }

    @Test
    public void testCreateAccountInvalidBank() {
        ResponseEntity<String> result = controller.createAccount("SAVINGS", 1, "INVALID");
        // Assert.assertTrue(result.getStatusCode() == HttpStatus.OK);
        // Assert.assertEquals("FAILED", result.getBody());

        Assert.assertTrue(result.getStatusCode() == HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testCreateAccountInvalidAccountType() {
        ResponseEntity<String> result = controller.createAccount("INVALID", 1, "SWEDBANK");
        // Assert.assertTrue(result.getStatusCode() == HttpStatus.OK);
        // Assert.assertEquals("FAILED", result.getBody());

        Assert.assertTrue(result.getStatusCode() == HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testCreateAccountInvalidPerson() {
        ResponseEntity<String> result = controller.createAccount("CHECK", 99, "SWEDBANK");
        // Assert.assertTrue(result.getStatusCode() == HttpStatus.OK);
        // Assert.assertEquals("FAILED", result.getBody());

        Assert.assertTrue(result.getStatusCode() == HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testFindPerson() {
        getAccountId();
        ResponseEntity<List<Account>> result = controller.findAccountbyPerson(5);
        Assert.assertTrue(result.getStatusCode() == HttpStatus.OK);
        
        List<Account> body = result.getBody();
        Assert.assertNotNull(body);
        Assert.assertNotEquals(0, body.size());
    }

    @Test
    public void testFindAccountThatDoesNotExist() {
        ResponseEntity<List<Account>> result = controller.findAccountbyPerson(2);
        Assert.assertTrue(result.getStatusCode() == HttpStatus.OK);
        
        List<Account> body = result.getBody();
        Assert.assertNotNull(body);
        Assert.assertEquals(0, body.size());
    }

    @Test
    public void testCorrectCredit() {
        int accountId = getAccountId();
        ResponseEntity<String> response = controller.creditAccount(accountId, 100);
        Assert.assertTrue(response.getStatusCode() == HttpStatus.OK);
        Assert.assertEquals("OK", response.getBody());
    }

    @Test
    public void testCreditInvalidAccount() {
        ResponseEntity<String> response = controller.creditAccount(99, 100);
        //Assert.assertTrue(response.getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
        
        // Assert.assertEquals("FAILED", response.getBody());
    }

    @Test
    public void testDebitAccount() {
        int accountId = getAccountId();
        controller.creditAccount(accountId, 100);
        ResponseEntity<String> response = controller.debitAccount(accountId, 10);
        Assert.assertTrue(response.getStatusCode() == HttpStatus.OK);

        Assert.assertEquals("OK", response.getBody());
    }

    @Test
    public void testDebitToMuch() {
        int accountId = getAccountId();
        controller.creditAccount(accountId, 99);
        ResponseEntity<String> response = controller.debitAccount(accountId, 100);
        // Assert.assertTrue(response.getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
        
        // Assert.assertEquals("FAILED", response.getBody());
    }

    @Test
    public void testDebitInvalidAccount() {
        ResponseEntity<String> response = controller.debitAccount(99, 100);
        // Assert.assertTrue(response.getStatusCode() == HttpStatus.OK);
        Assert.assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
        
        // Assert.assertEquals("FAILED", response.getBody());
    }

    @Test
    public void testGetTransactions() {
        int accountId = getAccountId();
        controller.creditAccount(accountId, 10);
        controller.creditAccount(accountId, 10);
        controller.creditAccount(accountId, 10);
    
        ResponseEntity<?> response = controller.getTransactions(accountId);
    
        Assert.assertTrue(response.getStatusCode() == HttpStatus.OK);
    
        if (response.getBody() instanceof List<?>) {
            List<?> body = (List<?>) response.getBody();
    
            Assert.assertTrue(body.stream().allMatch(item -> item instanceof Transaction));
    
            List<Transaction> transactions = (List<Transaction>) body;
    
            Assert.assertNotNull(transactions);
            Assert.assertEquals(3, transactions.size());
        } else {
            Assert.fail("Expected a List<Transaction> but received: " + response.getBody().getClass());
        }
    }
    
}
