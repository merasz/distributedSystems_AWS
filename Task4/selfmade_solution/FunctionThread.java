package jFaaS;

import com.google.gson.JsonObject;
//import jFaaS.Gateway;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FunctionThread extends Thread {

    private String functionName;
    private HashMap<String, Object> input;

    public FunctionThread(String functionName, HashMap<String, Object> input) {
        this.functionName = functionName;
        this.input = input;
    }

    @Override
    public void run() {
        super.run();
        Gateway gateway = new Gateway("credentials.properties");
        try {
            JsonObject result = gateway.invokeFunction("arn:aws:lambda:us-east-1:935857016589:function:" + functionName, input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}