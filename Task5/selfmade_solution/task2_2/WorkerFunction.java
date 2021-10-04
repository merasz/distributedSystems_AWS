package jFaaS.invokers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.*;

public class WorkerFunction implements RequestHandler<Map<String, Object>, Map<String, Object>>{

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        double n = (Double) input.get("N");
        double f = (Double) input.get("F");

        List<List<Number>> threads = new ArrayList<>();
        for(int i =1;i<=f;i++){
            List<Number> inputValues = new ArrayList<>();
            inputValues.add((double)i);
            threads.add(inputValues);
        }

        for(int i = (int)f+1; i <=n;i++){
            int element = (int)(i%f);
            threads.get(element).add((double)i);
        }

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("output", threads);
        return returnMap;
    }
}