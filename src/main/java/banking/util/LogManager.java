package banking.util;

import banking.entity.Log;

import java.util.ArrayList;
import java.util.List;

public class LogManager {
    static List<Log> logs = new ArrayList<>();

    public static List<Log> getLogs() {
        return logs;
    }
}
