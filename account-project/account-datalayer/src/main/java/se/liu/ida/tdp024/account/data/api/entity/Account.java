package se.liu.ida.tdp024.account.data.api.entity;

import java.io.Serializable;

public interface Account extends Serializable {
    public int getId();
    public void setId(int id);
    public int getPersonKey();
    public void setPersonKey(int personKey);
    public String getAccountType();
    public void setAccountType(String accountType);
    public int getBankKey();
    public void setBankKey(int bankKey);
    public int getHoldings();
    public void setHoldings(int holdings);
   // public List<Transaction> getTransactions();
    //public void addTransaction(Transaction transaction);
}
