package org.k1.jenclight.common;

/**
 * @author kevinzhao
 * @since 20/07/2019
 */

public class JenkinsException extends RuntimeException {
    public JenkinsException(String message) {
        super(message);
    }

    public JenkinsException(String message, Throwable cause) {
        super(message, cause);
    }

    public JenkinsException(Throwable cause) {
        super(cause);
    }
}
