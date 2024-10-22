package se.liu.ida.tdp024.account.data.impl.db.facade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.api.util.Logger;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionDB;
import se.liu.ida.tdp024.account.data.impl.db.util.EMF;
import se.liu.ida.tdp024.account.data.impl.db.util.KafkaLogger;

public class TransactionEntityFacadeDB implements TransactionEntityFacade {

    private Logger logger = new KafkaLogger("transactions");

    @Override
    public int create(Account account, String type, int amount, String status) {
        EntityManager em = EMF.getEntityManager();
        try {
            em.getTransaction().begin();
            Transaction transaction = new TransactionDB();
            transaction.setType(type);
            transaction.setAmount(amount);
            transaction.setAccount(account);
            transaction.setStatus(status);
            transaction.setCreated(LocalDateTime.now().toString());

            em.persist(transaction);
            em.getTransaction().commit();
            return transaction.getId();
        }
        catch(Exception e){
            System.out.println("An error occurred while creating the transaction:");
            e.printStackTrace();
            System.out.println("Error message: " + e.getMessage());
            
            String logMessage = String.format(
                "{\"level\": \"%s\", \"reason\": \"%s\", \"accountId\": \"%d\"}", 
                "error",
                e.getMessage(),
                account.getId()
        );
            
            this.logger.publish(logMessage);
            return -1;
        }
        finally {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            em.close();

        }

    }
    @Override
    public int createTransactionWithEM(Account account, String type, int amount, String status, EntityManager em) {
            Transaction transaction = new TransactionDB();
            transaction.setType(type);
            transaction.setAmount(amount);
            transaction.setAccount(account);
            transaction.setStatus(status);
            transaction.setCreated(LocalDateTime.now().toString());

            em.persist(transaction);
            return transaction.getId();
    }

    @Override
    public List<Transaction> list(Account account){
        EntityManager em = EMF.getEntityManager();
        try{
            String queryStr = "SELECT a FROM TransactionDB a WHERE a.account = :account";
            List<Transaction> lst = em.createQuery(queryStr, Transaction.class)
                     .setParameter("account", account)
                     .getResultList();
            return lst;

        } catch (Exception e ){
            System.out.println("An error occurred while fetching accounts for person");
            e.printStackTrace();
            System.out.println("Error message: " + e.getMessage());
            return new ArrayList<>();
        }finally {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }
}
