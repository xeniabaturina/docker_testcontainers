import static spark.Spark.*;

import com.google.gson.Gson;

public class Main {
    public static void main(String[] args) {
        port(8080);
        Gson gson = new Gson();
        StocksDao stocksDao = new StocksDaoImpl();

        get("/", (req, res) -> {
            String companyName = req.queryParams("company");

            if (companyName == null) {
                return gson.toJson(stocksDao.read());
            } else {
                return gson.toJson(stocksDao.read(companyName));
            }
        });

        get("/admin/register", (req, res) -> {
            String companyName = req.queryParams("company");
            float price = Float.parseFloat(req.queryParams("price"));

            stocksDao.create(companyName, price);
            return "OK";
        });

        get("/sell", (req, res) -> {
            String companyName = req.queryParams("company");
            int count = Integer.parseInt(req.queryParams("count"));

            stocksDao.updateCount(companyName, -count);
            return "OK";
        });

        get("/buy", (req, res) -> {
            String companyName = req.queryParams("company");
            int count = Integer.parseInt(req.queryParams("count"));

            stocksDao.updateCount(companyName, count);
            return "OK";
        });

        get("/admin/change-price", (req, res) -> {
            String companyName = req.queryParams("company");
            float price = Float.parseFloat(req.queryParams("price"));

            stocksDao.updatePrice(companyName, price);
            return "OK";
        });

        get("/admin/change-count", (req, res) -> {
            String companyName = req.queryParams("company");
            int count = Integer.parseInt(req.queryParams("count"));

            stocksDao.updateCount(companyName, count);
            return "OK";
        });
    }
}
