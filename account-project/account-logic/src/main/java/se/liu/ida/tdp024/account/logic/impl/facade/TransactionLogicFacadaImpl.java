package se.liu.ida.tdp024.account.logic.impl.facade;

import java.util.List;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.logic.api.facade.TransactionLogicFacade;
import se.liu.ida.tdp024.account.logic.api.utils.Logger;
import se.liu.ida.tdp024.account.logic.utils.KafkaLogger;

public class TransactionLogicFacadaImpl implements TransactionLogicFacade {

    private Logger logger;

    private final TransactionEntityFacade transactionEntityFacade;

    public TransactionLogicFacadaImpl(TransactionEntityFacade transactionEntityFacade, Logger logger){
        this.transactionEntityFacade = transactionEntityFacade;
        this.logger = logger;
    }
    
    @Override
    public List<Transaction> findTransactionsByAccount(Account account){
        return transactionEntityFacade.list(account);
    }

    @Override
    public Boolean createTransaction(Account account, String type, int amount, String status) {
            int transactionId =  transactionEntityFacade.create(account, type , amount, status);

            String logMessage = String.format(
                "{\"level\": \"%s\", \"transactionId\": \"%d\", \"accountId\": %d, \"type\": \"%s\", \"amount\": %d, \"status\": \"%s\"}", 
                "OK".equals(type) ? "info" : "error",
                transactionId,
                account.getId(), 
                type, 
                amount, 
                status
            );

            this.logger.publish(logMessage);
            return transactionId != -1;
    }
}