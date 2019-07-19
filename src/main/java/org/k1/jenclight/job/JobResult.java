package org.k1.jenclight.job;

public class JobResult{
    boolean success;

    public JobResult( boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
