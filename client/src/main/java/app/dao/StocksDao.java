package app.dao;

import app.User;
import app.Stocks;

import java.io.IOException;
import java.util.List;

public interface StocksDao {
    List<Stocks> getWorth(User user) throws IOException;
    void buyStocks(String company, User user, int amount) throws IOException;
    void sellStocks(String company, User user, int amount) throws IOException;
}
