package se.liu.ida.tdp024.account.data.api.entity;

import java.io.Serializable;

public interface Transaction extends Serializable{
    public int getId();
    public void setId(int id);
    public String getType();
    public void setType(String type);
    public int getAmount();
    public void setAmount(int amount);
    public String getCreated();
    public void setCreated(String time);
    public String getStatus();
    public void setStatus(String status);
    public Account getAccount();
    public void setAccount(Account account);   
}