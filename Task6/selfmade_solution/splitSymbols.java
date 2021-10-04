package functions;

import java.awt.image.ImageProducer;
import java.util.*;

public class splitSymbols {
    private Integer numOfThreads;
    private Integer numOfItems;
    private  List<String> symbols;
    public static Map<Integer, List<String>> splitSymbolsFunc(Integer numOfThreads, Integer numOfItems){
        Map<Integer, List<String>> retmap= new HashMap<>();
        return retmap;
    }

    public splitSymbols(Integer numOfThreads, Integer numOfItems, List<String> symbols) {
        this.numOfThreads = numOfThreads;
        this.numOfItems = numOfItems;
        this.symbols = symbols;
    }
}
