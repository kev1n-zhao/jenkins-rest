package io.github.kev1nst.jenkins;

import io.github.kev1nst.jenkins.job.handler.JobProgressHandler;
import io.github.kev1nst.jenkins.job.po.JobSubmission;
import io.github.kev1nst.jenkins.job.po.QueueItem;
import org.junit.Test;
import io.github.kev1nst.jenkins.job.po.BuildStatus;
import org.nutz.lang.Lang;

/**
 * @author kevinzhao
 * @since 20/07/2019
 */

public class Demo {
    @Test
    public void simpleBuild() {
        String jenkinsUrl = "http://localhost:8081";
        String account = "admin";
        String credential = "9aebe96f61819ce2c20c01a820719f70"; // could be token or password
        Jenkins jenkins = Jenkins.connect(jenkinsUrl, account, credential);
        BuildStatus result = jenkins.build("pack1/job1").await(); // await method is synchronized call
        assert result.isSuccess();
    }

    @Test
    public void buildWithParameter() {
        String jenkinsUrl = "http://localhost:8081";
        String account = "admin";
        String credential = "9aebe96f61819ce2c20c01a820719f70"; // could be token or password
        Jenkins jenkins = Jenkins.connect(jenkinsUrl, account, credential);
        BuildStatus result = jenkins.build("pack1/job2", Lang.map("param1", "param_value")).await();
        assert result.isSuccess();
    }

    @Test
    public void buildWithTimeout() {
        String jenkinsUrl = "http://localhost:8081";
        String account = "admin";
        String credential = "9aebe96f61819ce2c20c01a820719f70"; // could be token or password
        Jenkins jenkins = Jenkins.connect(jenkinsUrl, account, credential);
        BuildStatus result = jenkins.build("pack1/job2", Lang.map("param1", "param_value")).await(100000);
        assert result.isSuccess();
    }

    @Test
    public void buildWithProgressHandler() {
        String jenkinsUrl = "http://localhost:8081";
        String account = "admin";
        String credential = "9aebe96f61819ce2c20c01a820719f70"; // could be token or password
        Jenkins jenkins = Jenkins.connect(jenkinsUrl, account, credential);
        BuildStatus result = jenkins.build("pack1/job2", Lang.map("param1", "param_value")).await(new JobProgressHandler() {
            @Override

            public void log(String logEntry, int index) {
                // handle log line by line
                System.out.println(index + ":" + logEntry);
            }

            @Override
            public void onSubmit(JobSubmission jobSubmission) {
                /**
                 * trigger after job submitted to jenkins
                 *  @param jobSubmission the job queue url in jenkins
                 * */
            }

            @Override
            public void onExecute(QueueItem item) {
                /**
                 * trigger after job start to execute
                 * @param item
                 */
            }

            @Override
            public void onCompleted(BuildStatus status) {
                /**
                 * trigger after job complete
                 * @param status
                 */
            }
        });
        assert result.isSuccess();
    }
}
