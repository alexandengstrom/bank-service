package se.liu.ida.tdp024.account.data.impl.db.facade;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountDB;
import se.liu.ida.tdp024.account.data.impl.db.util.EMF;

public class AccountEntityFacadeDB implements AccountEntityFacade {
    private TransactionEntityFacade transactionEntityFacade;

    public AccountEntityFacadeDB(TransactionEntityFacadeDB transactionEntityFacade) {
        this.transactionEntityFacade = transactionEntityFacade;
    }

    @Override
    public int create(int personKey, int bankKey, String accountType) {
        EntityManager em = EMF.getEntityManager();
        try {
            em.getTransaction().begin();
            Account account = new AccountDB();
            account.setPersonKey(personKey);
            account.setBankKey(bankKey);
            account.setAccountType(accountType);
            account.setHoldings(0);

            em.persist(account);

            em.getTransaction().commit();
            return account.getId();
        } catch (Exception e) {
            System.out.println("An error occurred while creating the account:");
            e.printStackTrace();
            System.out.println("Error message: " + e.getMessage());
            return -1;
        } finally {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            em.close();

        }

    }

    @Override
    public List<Account> findByPersonKey(int personKey) {
        EntityManager em = EMF.getEntityManager();
        try {
            em.getTransaction().begin();

            String queryStr = "SELECT a FROM AccountDB a WHERE a.personKey = :personKey";
            List<Account> lst = em.createQuery(queryStr, Account.class)
                    .setParameter("personKey", personKey)
                    .getResultList();

            em.getTransaction().commit();
            return lst;

        } catch (Exception e) {
            System.out.println("An error occurred while fetching accounts for person");
            System.out.println("Error message: " + e.getMessage());
            return new ArrayList<>();
        } finally {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();

        }
    }

    @Override
    public Boolean debit(int accountId, int amount) {
        EntityManager em = EMF.getEntityManager();

        try {
            em.getTransaction().begin();

            Account account = em.find(AccountDB.class, accountId, LockModeType.PESSIMISTIC_WRITE);

            if (account.getHoldings() >= amount) {
                account.setHoldings(account.getHoldings() - amount);
                em.merge(account);

                int transactionId = transactionEntityFacade.createTransactionWithEM(account, "DEBIT", amount,  "OK", em);

                em.getTransaction().commit(); 
                return true;
            } else {
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while debiting the account:");
            System.out.println("Error message: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close(); 
        }
    }

    @Override
    public Account findById(int accountId) {
        EntityManager em = EMF.getEntityManager();
        try {
            return em.find(AccountDB.class, accountId);

        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Boolean credit(int accountId, int amount) {
        EntityManager em = EMF.getEntityManager();

        try {
            em.getTransaction().begin();
            Account account = em.find(AccountDB.class, accountId, LockModeType.PESSIMISTIC_WRITE);

            account.setHoldings(account.getHoldings() + amount);
            em.merge(account);
            transactionEntityFacade.createTransactionWithEM(account, "CREDIT", amount,  "OK", em);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.out.println("An error occurred while creating the account:");
            System.out.println("Error message: " + e.getMessage());
            return false;
        } finally {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            em.close();
        }
    }
}
