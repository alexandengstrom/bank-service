package hello;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.logic.impl.facade.AccountLogicFacadeImpl;
import se.liu.ida.tdp024.account.logic.impl.facade.ExternalResourceFacadeImpl;
import se.liu.ida.tdp024.account.logic.interfaces.Bank;
import se.liu.ida.tdp024.account.logic.interfaces.Person;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import se.liu.ida.tdp024.account.data.api.exceptions.EntityNotFoundException;
import se.liu.ida.tdp024.account.data.api.exceptions.InputParameterException;

import com.fasterxml.jackson.core.type.TypeReference;

import se.liu.ida.tdp024.account.data.api.exceptions.ServiceConfigurationException;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.logic.impl.facade.TransactionLogicFacadaImpl;
import se.liu.ida.tdp024.account.logic.utils.KafkaLogger;

@RestController
@RequestMapping("/account-rest")
public class AccountController {

    

    private final AccountLogicFacade accountService = new AccountLogicFacadeImpl(
        new AccountEntityFacadeDB(new  TransactionEntityFacadeDB()),
        new ExternalResourceFacadeImpl<>("http://php-api/person", Person.class, new TypeReference<List<Person>>() {}),
        new ExternalResourceFacadeImpl<>("http://rust_api:8000/bank", Bank.class, new TypeReference<List<Bank>>() {}),
        new TransactionLogicFacadaImpl(new  TransactionEntityFacadeDB(), new KafkaLogger("transactions"))
    );
    

    @GetMapping("/account/create")
    public ResponseEntity<String> createAccount(
            @RequestParam String accounttype,
            @RequestParam int person,
            @RequestParam String bank) {

        try {
            if (accounttype.isBlank() || person < 1 || bank.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FAILED");
                // return ResponseEntity.ok().body("FAILED"); OLD VERSION
            }

            if (!("CHECK".equals(accounttype) || "SAVINGS".equals(accounttype))) {
                // return ResponseEntity.ok().body("FAILED"); OLD VERSION
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FAILED");
            }

            Boolean accountCreated = accountService.createAccount(accounttype, person, bank);
            return accountCreated ? ResponseEntity.ok("OK") : ResponseEntity.ok().body("FAILED");

        } catch (InputParameterException e) {
            // return ResponseEntity.ok("FAILED"); OLD VERSION
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ServiceConfigurationException e) {
            // return ResponseEntity.ok("FAILED"); OLD VERSION
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        } catch (Throwable e) {
            // return ResponseEntity.ok("FAILED"); OLD VERSION
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }

    }

    @GetMapping("/account/find/person")
    public ResponseEntity<List<Account>> findAccountbyPerson(@RequestParam int person) {
        List<Account> accountList = accountService.findAccountsByPerson(person);
        return ResponseEntity.ok(accountList);
    }
    

    @GetMapping("/account/debit")
    public ResponseEntity<String> debitAccount(
            @RequestParam int id,
            @RequestParam int amount) {
        
        try {
            Boolean debitedAccount = accountService.debitAccount(id, amount);
            return ResponseEntity.ok(debitedAccount ? "OK" : "FAILED");
        } catch (EntityNotFoundException e) {
            // return ResponseEntity.ok("FAILED"); OLD VERSION
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InputParameterException e) {
            // return ResponseEntity.ok("FAILED"); OLD VERSION
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable e) {
            // return ResponseEntity.ok("FAILED"); OLD VERSION
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/account/credit")
    public ResponseEntity<String> creditAccount(
            @RequestParam int id,
            @RequestParam int amount) {
        
        try {
            Boolean creditedAccount = accountService.creditAccount(id, amount);
            return ResponseEntity.ok(creditedAccount ? "OK" : "FAILED");

        } catch (EntityNotFoundException e) {
            // return ResponseEntity.ok("FAILED"); OLD VERSION
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InputParameterException e) {
            // return ResponseEntity.ok("FAILED"); OLD VERSION
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable e) {
            // return ResponseEntity.ok("FAILED"); OLD VERSION
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("account/transactions")
    public ResponseEntity<?> getTransactions(@RequestParam int id) {
        try {
            List<Transaction> transactionList = accountService.findTransactionsById(id);
            return ResponseEntity.ok(transactionList);
        } catch (EntityNotFoundException e) {
            // return ResponseEntity.ok(Collections.emptyList()); OLD VERSION
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Throwable e) {
            // return ResponseEntity.ok(Collections.emptyList()); OLD VERSION
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }

    }
}
