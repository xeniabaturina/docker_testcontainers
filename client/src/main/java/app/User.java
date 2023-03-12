package app;

import java.util.HashMap;

public class User {
    private int id;
    private String userName;
    private float balance;
    private HashMap<String, Integer> stocks;

    public User(int id, String userName) {
        this.id = id;
        this.userName = userName;
        this.balance = 0;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public HashMap<String, Integer> getStocks() {
        return stocks;
    }

    public float getBalance() {
        return balance;
    }

    public int getStocksByCompany(String company) {
        return stocks.getOrDefault(company, 0);
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public void add(String company, int count) {
        int old = getStocksByCompany(company);
        stocks.put(company, old + count);
    }

    public void remove(String company, int count) {
        if (stocks.get(company) == count) {
            stocks.remove(company);
        } else {
            stocks.put(company, stocks.get(company) - count);
        }
    }
}
