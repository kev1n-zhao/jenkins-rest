package io.github.kev1nst.jenkins.common;

/**
 * @author kevinzhao
 * @since 23/07/2019
 */

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nutz.log.Log;
import org.nutz.log.LogAdapter;
import org.nutz.log.impl.AbstractLog;
import org.nutz.plugin.Plugin;

public class Log4j2Adapter implements LogAdapter, Plugin {

    @Override
    public boolean canWork() {
        try {
            org.apache.logging.log4j.Logger.class.getName();
            return true;
        }
        catch (Throwable e) {}
        return false;
    }

    @Override
    public Log getLogger(String className) {
        return new Log4j2Adapter.Log4JLogger(className);
    }

    static class Log4JLogger extends AbstractLog {

        public static final String SUPER_FQCN = AbstractLog.class.getName();
        public static final String SELF_FQCN = Log4j2Adapter.class.getName();

        private Logger logger;

        private static boolean hasTrace;

        static {
            try {
                Level.class.getDeclaredField("TRACE");
                hasTrace = true;
            }
            catch (Throwable e) {}
        }

        Log4JLogger(String className) {
            logger = LogManager.getLogger(className);

            isFatalEnabled = logger.isFatalEnabled();
            isErrorEnabled = logger.isErrorEnabled();
            isWarnEnabled = logger.isWarnEnabled();
            isInfoEnabled = logger.isInfoEnabled();
            isDebugEnabled = logger.isDebugEnabled();
            if (hasTrace){
                isTraceEnabled = logger.isTraceEnabled();
            }

        }

        @Override
        public void debug(Object message, Throwable t) {
            if (isDebugEnabled()) {
                logger.debug(message,t);
            }
        }

        @Override
        public void error(Object message, Throwable t) {
            if (isErrorEnabled()) {
                logger.error(message, t);
            }

        }

        @Override
        public void fatal(Object message, Throwable t) {
            if (isFatalEnabled()) {
                logger.fatal(message, t);
            }
        }

        @Override
        public void info(Object message, Throwable t) {
            if (isInfoEnabled()) {
                logger.info(message, t);
            }
        }

        @Override
        public void trace(Object message, Throwable t) {
            if (isTraceEnabled()) {
                logger.trace( message, t);
            } else if ((!hasTrace) && isDebugEnabled()) {
                logger.debug(message, t);
            }
        }

        @Override
        public void warn(Object message, Throwable t) {
            if (isWarnEnabled()) {
                logger.warn(message, t);
            }
        }

        @Override
        protected void log(int level, Object message, Throwable tx) {
            switch (level) {
                case LEVEL_FATAL:
                    fatal(message,tx);
                    break;
                case LEVEL_ERROR:
                    error(message,tx);
                    break;
                case LEVEL_WARN:
                    warn(message,tx);
                    break;
                case LEVEL_INFO:
                    info(message,tx);
                    break;
                case LEVEL_DEBUG:
                    debug(message,tx);
                    break;
                case LEVEL_TRACE:
                    if (hasTrace) {
                        logger.trace(message, tx);
                    } else {
                        logger.debug(message, tx);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean isDebugEnabled() {
            return logger.isDebugEnabled();
        }

        @Override
        public boolean isErrorEnabled() {
            return logger.isErrorEnabled();
        }

        @Override
        public boolean isFatalEnabled() {
            return logger.isFatalEnabled();
        }

        @Override
        public boolean isInfoEnabled() {
            return logger.isInfoEnabled();
        }

        @Override
        public boolean isTraceEnabled() {
            if (!hasTrace) {
                return logger.isDebugEnabled();
            }
            return logger.isTraceEnabled();
        }

        @Override
        public boolean isWarnEnabled() {
            return logger.isWarnEnabled();
        }
    }
}
