package app.dao;

import app.User;

import java.util.Map;

public interface UserDao {
    void create(String userName, int id);
    Map<Integer, User> read();
    User read(int id);
    void updateBalance(User user, float amount);
    void addStocks(User user, String company, int count);
    void removeStocks(User user, String company, int count);
    void delete(int id);
}
