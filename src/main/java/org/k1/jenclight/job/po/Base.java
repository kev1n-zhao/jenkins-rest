package org.k1.jenclight.job.po;

import org.nutz.http.Response;
import org.nutz.json.Json;
import org.nutz.json.JsonException;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.HashMap;
import java.util.Map;

/**
 * base po
 * @author kevinzhao
 * @since 20/07/2019
 */

public class Base {
    Log LOG= Logs.get();
    Map<String,String> data;

    public Base(Response response) {
        data=new HashMap<>();
        for(Map.Entry<String,String> entry:response.getHeader().getAll()){
            data.put(entry.getKey(),entry.getValue());
        }
        //get response text
        try {
            Map responseContent=Json.fromJson(HashMap.class,response.getContent());
            data.putAll(responseContent);
        } catch (Exception e) {
            LOG.debug("fail to parse the response content to json",e);
        }
    }
}
