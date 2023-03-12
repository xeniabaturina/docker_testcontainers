import java.util.List;

public interface StocksDao {
    void create(String company, float price);
    List<Stocks> read();
    Stocks read(String company);
    void updatePrice(String company, float price);
    void updateCount(String company, int amount);
    void delete(String company);
}
