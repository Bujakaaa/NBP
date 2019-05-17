import java.util.ArrayList;
import java.util.List;

public class Currency {

String mid;
String code;
String currency;
String effectiveDate;
List<Rates> rates;

    public String getMid() {
        return mid;
    }

    public String getCode() {
        return code;
    }

    public String getCurrency() {
        return currency;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public List<Rates> getRates() {
        return rates;
    }

//String table; //typ tabeli
//String no; // numer tabeli
//String TradingDate; //data notowania (dotyczy tabeli C)
//String EffectiveDate; //data publikacji
//    String rates; // lista kursów poszczególnych walut w tabeli
//    String country; // nazwa kraju
//    String symbol;// symbol waluty (numeryczny, dotyczy kursów archiwalnych)
//    String bid;  // przeliczony kurs kupna waluty (dotyczy tabeli C)
//    String ask;  //– przeliczony kurs sprzedaży waluty (dotyczy tabeli C)

    }