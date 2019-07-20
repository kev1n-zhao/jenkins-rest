package io.github.kev1nst.jenkins.common;

/**
 * @author kevinzhao
 * @since 20/07/2019
 */

public class Lang {

    /**
     * Parses the provided job name for folders to get the full path for the job.
     *
     * @param jobName the fullName of the job.
     * @return the path of the job including folders if present.
     */
    public static String toFullJobPath(final String jobName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("job/");
        final String[] parts = jobName.split("/");
        if (parts.length == 1) {
            sb.append(parts[0]);
            return sb.toString();
        }
        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i]);
            if (i != parts.length - 1){
                sb.append("/job/");
            }
        }
        return sb.toString();
    }
}
