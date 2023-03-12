package app.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import app.dao.StocksDao;
import app.dao.UserDao;
import app.User;
import app.Stocks;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StocksDaoImpl implements StocksDao {
    private final String baseURL;
    private final UserDao userDao;
    private final OkHttpClient client;

    public StocksDaoImpl(String baseURL, UserDao userDao) {
        this.baseURL = baseURL;
        this.userDao = userDao;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    private Stocks getStocks(String companyName) throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + "?company=" + companyName)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected HTTP response: " + response);
        }

        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            throw new IOException("Empty response body");
        }

        return new ObjectMapper().readValue(responseBody.bytes(), Stocks.class);
    }

    @Override
    public List<Stocks> getWorth(User user) throws IOException {
        List<Stocks> worth = new ArrayList<>();
        for (String company : user.getStocks().keySet()) {
            Stocks stocks = getStocks(company);
            worth.add(stocks);
        }
        return worth;
    }

    @Override
    public void buyStocks(String company, User user, int amount) throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + "/buy?company=" + company + "&amount=" + amount)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected HTTP response: " + response);
        }

        Stocks stocks = getStocks(company);

        userDao.updateBalance(user, -(stocks.getPrice() * amount));
        userDao.addStocks(user, stocks.getCompany(), amount);
    }

    @Override
    public void sellStocks(String company, User user, int amount) throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + "/sell?company=" + company + "&amount=" + amount)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected HTTP response: " + response);
        }

        Stocks stocks = getStocks(company);

        userDao.updateBalance(user, stocks.getPrice() * amount);
        userDao.removeStocks(user, stocks.getCompany(), amount);
    }
}
