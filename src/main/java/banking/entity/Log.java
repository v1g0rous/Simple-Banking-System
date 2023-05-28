package banking.entity;

import banking.util.LogManager;

public class Log {
    String error;
    Exception exception;
    String exceptionMessage;
    String exceptionStackTrace;

    public Log(String error, Exception exception) {
        this.error = error;
        this.exception = exception;
        this.exceptionMessage = exception.getMessage();
        this.exceptionStackTrace = exception.getStackTrace().toString();
        LogManager.getLogs().add(this);
    }
}
