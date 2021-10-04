package jFaaS.invokers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFibonacci implements RequestHandler <Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        ArrayList<Integer> inputArray = (ArrayList<Integer>) input.get("input");
        ArrayList<BigInteger> resultArray = new ArrayList<>();
        for(int i : inputArray){
            resultArray.add(fib(i));
        }
        Map<String, Object> output = new HashMap<>();
        output.put("output", resultArray);
        return output;
    }

    public static BigInteger fib(int n) {
        if (n == 0){
            return BigInteger.ZERO;
        } else if (n == 1){
            return BigInteger.ONE;
        }
        return fib(n - 2).add(fib(n - 1));
    }

}