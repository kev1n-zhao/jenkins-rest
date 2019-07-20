package org.k1.jenclight.job.po;

import org.nutz.http.Response;

import java.util.Map;

/**
 * jenkins job queue item
 * @author kevinzhao
 * @since 20/07/2019
 */

public class QueueItem extends Base{
    private static final String LOCATION = "Location";
    public QueueItem(Response response) {
        super(response);
    }
    public String getUrl(){
        return data.get(LOCATION);
    }
}
