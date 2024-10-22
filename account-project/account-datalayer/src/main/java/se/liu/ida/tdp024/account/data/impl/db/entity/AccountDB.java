package se.liu.ida.tdp024.account.data.impl.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import se.liu.ida.tdp024.account.data.api.entity.Account;

@Entity
public class AccountDB implements Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "account_id")
    private int id;

    @Column(nullable = false)
    private int personKey;

    @Column(nullable = false)
    private String accountType;

    @Column(nullable = false)
    private int bankKey;

    @Column(nullable = false)
    private int holdings;

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getPersonKey() {
        return this.personKey;
    }

    @Override
    public void setPersonKey(int personKey) {
        this.personKey = personKey;
    }

    @Override
    public String getAccountType() {
        return this.accountType;
    }

    @Override
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    public int getBankKey() {
        return this.bankKey;
    }

    @Override
    public void setBankKey(int bankKey) {
        this.bankKey = bankKey;
    }

    @Override
    public int getHoldings() {
        return this.holdings;
    }

    @Override
    public void setHoldings(int holdings) {
        this.holdings = holdings;
    }
}
