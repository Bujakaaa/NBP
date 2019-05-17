import java.math.BigDecimal;

public class Rates {

    String mid;
    String ask;
    String bid;

    public String getMid() {
        return mid;
    }

    public String getAsk() {
        return ask;
    }

    public String getBid() {
        return bid;
    }

    @Override
    public String toString() {
        return "Rates{" +
                "mid=" + mid +
                ", ask=" + ask +
                ", bid=" + bid +
                '}';
    }
}

