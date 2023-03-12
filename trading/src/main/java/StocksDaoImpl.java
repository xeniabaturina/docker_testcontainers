import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StocksDaoImpl implements StocksDao {
    private final Map<String, Stocks> stocks = new HashMap<>();

    @Override
    public void create(String company, float price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (this.stocks.containsKey(company)) {
            throw new IllegalArgumentException("Company " + company + " exists");
        }

        this.stocks.put(company, new Stocks(price, 0, company));
    }

    @Override
    public List<Stocks> read() {
        return new ArrayList<>(this.stocks.values());
    }

    @Override
    public Stocks read(String company) {
        Stocks stocks = this.stocks.get(company);
        if (stocks == null) {
            throw new IllegalArgumentException("There are no stocks for " + company);
        }
        return stocks;
    }

    @Override
    public void updatePrice(String company, float price) {
        Stocks stocks = this.stocks.get(company);

        float priceNew = stocks.getPrice() + price;
        if (priceNew <= 0) {
            throw new IllegalArgumentException("Stocks price must be positive");
        }

        stocks.setPrice(priceNew);

        this.stocks.put(company, stocks);
    }

    @Override
    public void updateCount(String company, int count) {
        Stocks stocks = this.stocks.get(company);

        int countNew = stocks.getCount() + count;
        if (countNew <= 0) {
            throw new IllegalArgumentException("Stocks count must be positive");
        }

        stocks.setCount(countNew);

        this.stocks.put(company, stocks);
    }

    @Override
    public void delete(String company) {
        stocks.remove(company);
    }
}
