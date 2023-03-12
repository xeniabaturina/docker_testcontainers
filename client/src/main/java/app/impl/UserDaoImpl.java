package app.impl;

import app.dao.UserDao;
import app.User;

import java.util.HashMap;
import java.util.Map;

public class UserDaoImpl implements UserDao {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public void create(String userName, int id) {
        if (users.containsKey(id)) {
            throw new IllegalArgumentException("app.User with id " + id + " already exists.");
        }
        users.put(id, new User(id, userName));
    }

    @Override
    public Map<Integer, User> read() {
        return users;
    }

    @Override
    public User read(int id) {
        return users.get(id);
    }

    @Override
    public void updateBalance(User user, float amount) {
        float balance = user.getBalance();
        if (balance + amount < 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }
        user.setBalance(balance + amount);
    }

    @Override
    public void addStocks(User user, String company, int count) {
        user.add(company, count);
    }

    @Override
    public void removeStocks(User user, String company, int count) {
        if (user.getStocksByCompany(company) < count) {
            throw new IllegalArgumentException("Insufficient stocks.");
        }
        user.remove(company, count);
    }

    @Override
    public void delete(int id) {
        users.remove(id);
    }
}