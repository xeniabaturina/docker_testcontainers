package app;

public class Stocks {
    private float price;
    private int count;
    private String company;

    public Stocks(float price, int count, String company) {
        this.price = price;
        this.count = count;
        this.company = company;
    }

    public float getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public String getCompany() {
        return company;
    }
}
