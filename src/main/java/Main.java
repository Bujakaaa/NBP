import com.google.gson.Gson;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {


        System.out.println("USD: " + currencyExchangeRate("usd"));
        System.out.println("EUR: " + currencyExchangeRate("eur"));
        System.out.println("GBP: " + currencyExchangeRate("gbp"));
        System.out.println("CHF: " + currencyExchangeRate("chf"));

        System.out.println();

        BigDecimal amountInPln = BigDecimal.valueOf(100.00);

        System.out.println("Exchange rate:");
        System.out.println("100 PLN - " + amountInAnotherCurrency(amountInPln, "usd", "exchange") + " USD");
        System.out.println("100 PLN - " + amountInAnotherCurrency(amountInPln, "eur", "exchange") + " EUR");
        System.out.println("100 PLN - " + amountInAnotherCurrency(amountInPln, "gbp", "exchange") + " GBP");
        System.out.println("100 PLN - " + amountInAnotherCurrency(amountInPln, "chf", "exchange") + " CHF");

        System.out.println();

        System.out.println("Selling rate:");
        System.out.println("100 PLN - " + amountInAnotherCurrency(amountInPln, "usd", "selling") + " USD");
        System.out.println("100 PLN - " + amountInAnotherCurrency(amountInPln, "eur", "selling") + " EUR");
        System.out.println("100 PLN - " + amountInAnotherCurrency(amountInPln, "gbp", "selling") + " GBP");
        System.out.println("100 PLN - " + amountInAnotherCurrency(amountInPln, "chf", "selling") + " CHF");

        System.out.println();

        System.out.println("Loss or profit");
        System.out.println(profitsOrLoss(amountInPln, "usd", LocalDate.now().minusMonths(1)));
        System.out.println(profitsOrLoss(amountInPln, "eur", LocalDate.now().minusMonths(1)));
        System.out.println(profitsOrLoss(amountInPln, "gbp", LocalDate.now().minusMonths(1)));
        System.out.println(profitsOrLoss(amountInPln, "chf", LocalDate.now().minusMonths(1)));


    }

    /**
     * Methods is downloading data from http and converting from json.
     * The name of table and the currency code must be specified.
     * @param code is currency
     * @param table table's name
     * @return Currency class from json
     * @throws IOException
     */

    private static Currency getCurrencyFromTable(String code, String table) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://api.nbp.pl/api/exchangerates/rates/")
                .append(table + "/")
                .append(code);

        URL url = new URL(stringBuilder.toString());
        URLConnection request = url.openConnection();
        request.connect();

        Scanner scanner = new Scanner(request.getInputStream());
        String json = scanner.nextLine();
        //System.out.println(json);

        Gson gson = new Gson();
        return gson.fromJson(json, Currency.class);
    }

    /**
     *  Methods is downloading data from http depending on date and converting from json.
     * @param code currency
     * @param table table's name
     * @param date
     * @return Currency class from json
     * @throws IOException
     */

    private static Currency getCurrencyFromTableWithDate (String code, String table, LocalDate date) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://api.nbp.pl/api/exchangerates/rates/")
                .append(table + "/")
                .append(code +"/")
                .append("/"+ date + "/");

        URL url = new URL(stringBuilder.toString());
        URLConnection request = url.openConnection();
        request.connect();

        Scanner scanner = new Scanner(request.getInputStream());
        String json = scanner.nextLine();
        //System.out.println(json);

        Gson gson = new Gson();
        return gson.fromJson(json, Currency.class);
    }


    private static BigDecimal currencyExchangeRate(String code) throws IOException {
        Currency currency = getCurrencyFromTable(code, "a");

        Double currencyValue = Double.parseDouble(currency.getRates().get(0).getMid());
        BigDecimal valueBigDecimal = new BigDecimal(currencyValue).setScale(4, RoundingMode.HALF_EVEN);
        return valueBigDecimal ;
    }


    private static BigDecimal amountInAnotherCurrency(BigDecimal amount, String code, String method) throws IOException {
        BigDecimal amountInAnotherCurrency = BigDecimal.valueOf(1);
        if (method.equals("exchange")) {
            amountInAnotherCurrency = amount.divide(currencyExchangeRate(code), 2, RoundingMode.HALF_EVEN);
        } else if (method.equals("selling")) {
            amountInAnotherCurrency = amount.divide(currencySellingRate(code), 2, RoundingMode.HALF_EVEN);
        }
        return amountInAnotherCurrency;
    }


    //ASK NOW
    private static BigDecimal currencySellingRate(String code) throws IOException {
        Currency currency = getCurrencyFromTable(code, "c");
        Double currencyValue = Double.parseDouble(currency.getRates().get(0).getAsk());
        BigDecimal valueBigDecimal = new BigDecimal(currencyValue).setScale(4, RoundingMode.HALF_EVEN);

        return valueBigDecimal;
    }


    //ASK One MONTH AGO
    private static BigDecimal currencySellingRateBasedOnDate (String code, String table, LocalDate date) throws IOException {
        Currency currency= getCurrencyFromTableWithDate(code, table, date);
        Double currencyValue = Double.parseDouble(currency.getRates().get(0).getAsk());
        BigDecimal valueBigDecimal = new BigDecimal(currencyValue).setScale(4, RoundingMode.HALF_EVEN);

        return valueBigDecimal;
    }

    //BID NOW
    private static BigDecimal currencyBuyinRate (String code) throws IOException {
        Currency currency = getCurrencyFromTable(code, "c");
        Double currencyValue = Double.parseDouble(currency.getRates().get(0).getBid());
        BigDecimal valueBigDecimal = new BigDecimal(currencyValue).setScale(4, RoundingMode.HALF_EVEN);

        return valueBigDecimal;
    }

    private static BigDecimal amountInAnotherBasedOnDate (BigDecimal amount, String code, LocalDate date) throws IOException {
        return amount.divide(currencySellingRateBasedOnDate(code, "c", date), 2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal differenceInAmounts (BigDecimal amount, String code, LocalDate date) throws IOException {
        return amount.subtract(amountInAnotherBasedOnDate( amount, code, date).multiply(currencyBuyinRate(code)));
    }

    private static String profitsOrLoss (BigDecimal amount, String code, LocalDate date) throws IOException {
        String message = null;
        if (differenceInAmounts(amount,code, date).compareTo(BigDecimal.ZERO) < 0) {
            message = "Profit";
        }
        else if (differenceInAmounts(amount,code, date).compareTo(BigDecimal.ZERO) ==0) {
            message = "No loss, no profit";
        }
        else {
            message = "Loss";
        }
        return message;
    }


}
