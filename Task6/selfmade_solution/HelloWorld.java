import java.math.BigDecimal;
import java.util.*;
import com.amazonaws.regions.Regions;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistQuotesRequest;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.fx.FxQuote;
import yahoofinance.quotes.csv.FxQuotesRequest;
import yahoofinance.quotes.csv.StockQuotesData;
import yahoofinance.quotes.csv.StockQuotesRequest;
import yahoofinance.quotes.query1v7.FxQuotesQuery1V7Request;
import yahoofinance.quotes.query1v7.StockQuotesQuery1V7Request;


public class HelloWorld {
    public static void main(String[] args) throws Exception {
        
        Stock stock = YahooFinance.get("INTC");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -2); // from 5 years ago

        String[] symbols = new String[] {"INTC", "BABA", "TSLA", "AIR.PA"};
        Stock intel = YahooFinance.get("INTC", from, to, Interval.DAILY);
        Stock baba = YahooFinance.get("BABA", from, to, Interval.DAILY);
        Stock tesla = YahooFinance.get("TSLA", from, to, Interval.DAILY);
        Stock airbus = YahooFinance.get("AIR.PA", from, to, Interval.DAILY);

        intel.print();
        baba.print();
        tesla.print();
        airbus.print();

    }
}
