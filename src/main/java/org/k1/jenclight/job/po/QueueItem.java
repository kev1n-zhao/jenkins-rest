package org.k1.jenclight.job.po;

import org.nutz.http.Response;

import java.util.Map;

/**
 * jenkins job queue item
 * @author kevinzhao
 * @since 20/07/2019
 */

public class QueueItem extends Base{
    public static final String EXECUTABLE = "executable";
    private static final String URL = "url";

    public QueueItem(Response response) {
        super(response);
    }
    public Map<String,Object> getExecutable(){
        return (Map<String,Object>)data.get(EXECUTABLE);
    }
    public String getExecutableUrl(){
        return (String)getExecutable().get(URL);
    }
}
