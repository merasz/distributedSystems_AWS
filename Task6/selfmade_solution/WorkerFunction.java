import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.*;

public class WorkerFunction implements RequestHandler<Map<String, Object>, Map<String, Object>>{

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        Map<String, Object> returnMap = new HashMap<>();
        return returnMap;
    }
}