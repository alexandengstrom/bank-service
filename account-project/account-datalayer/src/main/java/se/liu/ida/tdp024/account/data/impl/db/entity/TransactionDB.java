package se.liu.ida.tdp024.account.data.impl.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;

@Entity
public class TransactionDB implements Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="transaction_id")
    private int id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String created;

    @Column(nullable = false)
    private String status;


    @ManyToOne
    @JoinColumn(name = "account", nullable = false, referencedColumnName="account_id")
    private AccountDB account;

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String getCreated() {
        return this.created;
    }

    @Override
    public void setCreated(String time) {
        this.created = time;
    }

    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Account getAccount() {
        return this.account;
    }
    
    @Override
    public void setAccount(Account account) {
        if (account instanceof AccountDB) {
            this.account = (AccountDB) account;
        } else {
            throw new IllegalArgumentException("Invalid account type");
        }
    }
}