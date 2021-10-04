package jFaaS;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainHomework {

    public static void main(String[] args) throws Exception {
        if(args.length != 4) {
            String info = "Parameters must be 4: use 'gradle run --args='FunctionName inputFilePathWithFileName" +
                    " Iterations true' last option enables multithread";
            System.err.println(info);
            System.exit(01);
        }


        String fxName = args[0];
        String filePath = args[1];
        int iter = Integer.parseInt(args[2]);
        boolean multi = Boolean.parseBoolean(args[3]);

        ArrayList<Integer> inputFileJson = getInputJson(filePath);
        System.out.println("Input: " + inputFileJson);
        int inputLen = inputFileJson.size();

        long absStart, absEnd, start, end;
        ArrayList<Long> times = new ArrayList<>();
        for (int i=0; i<iter; i++)
            times.add(Long.parseLong("16"));

        absStart = System.currentTimeMillis();
        if (multi){
            Thread[] threads = new Thread[inputLen];
            for(int curr = 1; curr <= iter; curr++) {
                start = System.currentTimeMillis();
                for(int i = 1; i <= inputLen; i++) {
                    ArrayList<Integer> list = new ArrayList<>();
                    list.add(i);
                    HashMap<String, Object> input = new HashMap<>();
                    input.put("input", list);
                    threads[i-1] = new LambdaThread(fxName, input);
                    threads[i-1].start();
                }
                for(int i = 0; i < inputLen; i++)
                    threads[i].join();
                end = System.currentTimeMillis();
                times.set(curr-1, end-start);
            }
        } else {
            for(int curr = 1; curr <= iter; curr++) {
                start = System.currentTimeMillis();
                HashMap<String, Object> input = new HashMap<>();
                input.put("input", inputFileJson);
                Thread thread = new LambdaThread(fxName, input);
                thread.start();
                thread.join();
                end = System.currentTimeMillis();
                times.set(curr-1, end-start);
            }
        }
        absEnd = System.currentTimeMillis();

        int k = 1;
        float avgTime = 0;
        for (long val: times) {
            System.out.println(val*1.0/1000);
            avgTime += val;
        }

        System.out.println("avg: " + avgTime/iter/1000);
        System.out.println("Absolute Time:" + ((absEnd*1.0-absStart)/1000) + " seconds!");

    }

    //https://crunchify.com/how-to-read-json-object-from-file-in-java/
    public static ArrayList<Integer> getInputJson(String path) {
        JsonParser parser = new JsonParser();
        ArrayList<Integer> input = new ArrayList<>();
        try {
            Object obj = parser.parse(new FileReader(path));
            JsonObject jsonObject = (JsonObject) obj;
            JsonArray in = (JsonArray) jsonObject.get("input");
            Iterator<JsonElement> iterator = in.iterator();
            while (iterator.hasNext()) {
                input.add(iterator.next().getAsInt());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return input;
    }

}