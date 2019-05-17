import com.google.gson.Gson;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        System.out.println(profitsOrLoss(amountInPln, "usd"));
        System.out.println(profitsOrLoss(amountInPln, "eur"));
        System.out.println(profitsOrLoss(amountInPln, "gbp"));
        System.out.println(profitsOrLoss(amountInPln, "chf"));


    }

    private static BigDecimal currencyExchangeRate(String code) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://api.nbp.pl/api/exchangerates/rates/a/")
                .append(code);

        URL url = new URL(stringBuilder.toString());
        URLConnection request = url.openConnection();
        request.connect();

        Scanner scanner = new Scanner(request.getInputStream());  //odczytuje ze strony JSONa
        String json = scanner.nextLine();
        //System.out.println(json);

        Gson gson = new Gson();
        Currency currency = gson.fromJson(json, Currency.class);

        List<Rates> rates = currency.rates;
        Rates mid = rates.get(0);

        Pattern pattern = Pattern.compile("[0-9]+[.]+[0-9]+");
        Matcher matcher = pattern.matcher(mid.toString());
        matcher.find();
        String valueString = matcher.group();
        BigDecimal valueBigDecimal = new BigDecimal(valueString);
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://api.nbp.pl/api/exchangerates/rates/c/")
                .append(code);

        URL url = new URL(stringBuilder.toString());
        URLConnection request = url.openConnection();
        request.connect();

        Scanner scanner = new Scanner(request.getInputStream());  //odczytuje ze strony JSONa
        String json = scanner.nextLine();
        //System.out.println(json);

        Gson gson = new Gson();
        Currency currency = gson.fromJson(json, Currency.class);

        List<Rates> rates = currency.rates;
        Rates ask = rates.get(0);
        Pattern pattern = Pattern.compile("[0-9]+[.]+[0-9]+");
        Matcher matcher = pattern.matcher(ask.toString());
        matcher.find();
        String valueString = matcher.group();
        BigDecimal valueBigDecimal = new BigDecimal(valueString);

        return valueBigDecimal;
    }

    //ASK One MONTH AGO
    private static BigDecimal currencySellingRateOneMonthAgo (String code) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://api.nbp.pl/api/exchangerates/rates/c/")
                .append(code)
                .append("/"+ LocalDate.now().minusMonths(1)+"/");
        URL url = new URL(stringBuilder.toString());
        URLConnection request = url.openConnection();
        request.connect();

        Scanner scanner = new Scanner(request.getInputStream());  //odczytuje ze strony JSONa
        String json = scanner.nextLine();
        //System.out.println(json);

        Gson gson = new Gson();
        Currency currency = gson.fromJson(json, Currency.class);

        List<Rates> rates = currency.rates;

//        int lastComma = rates.get(0).toString().lastIndexOf(",");
//        String string = rates.get(0).toString().substring(lastComma);
        //System.out.println(string);

        Pattern pattern = Pattern.compile("[0-9]+[.]+[0-9]+");
        Matcher matcher = pattern.matcher(rates.get(0).toString());
        matcher.find();
        String valueString = matcher.group();
        BigDecimal valueBigDecimal = new BigDecimal(valueString);

        return valueBigDecimal;
    }

    //BID NOW
    private static BigDecimal currencyBuyinRate (String code) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://api.nbp.pl/api/exchangerates/rates/c/")
                .append(code);
        URL url = new URL(stringBuilder.toString());
        URLConnection request = url.openConnection();
        request.connect();

        Scanner scanner = new Scanner(request.getInputStream());  //odczytuje ze strony JSONa
        String json = scanner.nextLine();
        //System.out.println(json);

        Gson gson = new Gson();
        Currency currency = gson.fromJson(json, Currency.class);

        List<Rates> rates = currency.rates;

        int lastComma = rates.get(0).toString().lastIndexOf(",");
        String string = rates.get(0).toString().substring(lastComma);
        //System.out.println(string);

        Pattern pattern = Pattern.compile("[0-9]+[.]+[0-9]+");
        Matcher matcher = pattern.matcher(string);
        matcher.find();
        String valueString = matcher.group();
        BigDecimal valueBigDecimal = new BigDecimal(valueString);

        return valueBigDecimal;
    }

    private static BigDecimal amountInAnotherCurrencyOneMonthAgo (BigDecimal amount, String code) throws IOException {
        return amount.divide(currencySellingRateOneMonthAgo(code), 2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal differenceInAmounts (BigDecimal amount, String code) throws IOException {
        return amount.subtract(amountInAnotherCurrencyOneMonthAgo(amount, code).multiply(currencyBuyinRate(code)));
    }

    private static String profitsOrLoss (BigDecimal amount, String code) throws IOException {
        String message = null;
        if (differenceInAmounts(amount,code).compareTo(BigDecimal.ZERO) < 0) {
            message = "Profit";
        }
        else if (differenceInAmounts(amount,code).compareTo(BigDecimal.ZERO) ==0) {
            message = "No loss, no profit";
        }
        else {
            message = "Loss";
        }
        return message;
    }


}
