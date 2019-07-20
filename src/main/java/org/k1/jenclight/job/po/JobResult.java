package org.k1.jenclight.job.po;

public class JobResult{
    String buildUrl;
    boolean success;

    public JobResult(boolean success, String buildUrl) {
        this.success = success;
        this.buildUrl=buildUrl;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getBuildUrl() {
        return buildUrl;
    }

    public void setBuildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
    }
}
