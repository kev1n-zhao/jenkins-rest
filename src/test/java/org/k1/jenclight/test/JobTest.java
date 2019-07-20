package org.k1.jenclight.test;

import org.junit.Test;
import org.k1.jenclight.Jenkins;
import org.k1.jenclight.job.po.JobResult;

/**
 * @author kevinzhao
 * @since 20/07/2019
 */

public class JobTest {
    @Test
    public void buildJob(){
        String jenkinsUrl="http://localhost:8081";
        String account="admin";
        String credential="9aebe96f61819ce2c20c01a820719f70"; // token or password
        JobResult result=Jenkins.connect(jenkinsUrl,account,credential).build("pack1/job1").await();
    }
}
