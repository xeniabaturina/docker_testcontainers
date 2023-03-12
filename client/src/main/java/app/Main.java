package app;

import static spark.Spark.*;

import app.dao.UserDao;
import app.dao.StocksDao;
import app.impl.StocksDaoImpl;
import app.impl.UserDaoImpl;
import com.google.gson.Gson;

public class Main {
    public static void main(String[] args) {
        port(8080);
        Gson gson = new Gson();
        UserDao userDao = new UserDaoImpl();
        StocksDao stocksDao = new StocksDaoImpl("http://localhost:27017", userDao);

        get("/", (req, res) -> {
            String id = req.queryParams("user-id");

            if (id == null) {
                return gson.toJson(userDao.read());
            } else {
                return gson.toJson(userDao.read(Integer.parseInt(id)));
            }
        });

        get("/register", (req, res) -> {
            int id = Integer.parseInt(req.queryParams("user-id"));
            String name = req.queryParams("name");

            userDao.create(name, id);
            return "OK";
        });

        get("/sell", (req, res) -> {
            int id = Integer.parseInt(req.queryParams("user-id"));
            String company = req.queryParams("company");
            int count = Integer.parseInt(req.queryParams("count"));

            stocksDao.sellStocks(company, userDao.read(id), count);
            return "OK";
        });

        get("/buy", (req, res) -> {
            int id = Integer.parseInt(req.queryParams("user-id"));
            String company = req.queryParams("company");
            int count = Integer.parseInt(req.queryParams("count"));

            stocksDao.buyStocks(company, userDao.read(id), count);
            return "OK";
        });

        get("/admin/change-balance", (req, res) -> {
            int id = Integer.parseInt(req.queryParams("user-id"));
            float value = Float.parseFloat(req.queryParams("value"));

            userDao.updateBalance(userDao.read(id), value);
            return "OK";
        });
    }
}
