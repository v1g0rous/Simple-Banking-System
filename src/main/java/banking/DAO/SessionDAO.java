package banking.DAO;

import banking.util.SQLiteManager;

import java.util.List;
import java.util.Map;

public class SessionDAO {
    public boolean hasUserCredentialsInDB(String userCardNumber, String userPin) {
        String sql = "SELECT number, pin FROM card WHERE number = \"" + userCardNumber +
                "\" AND " + "pin = \"" + userPin + "\"";

        List<Map<String, Object>> records = SQLiteManager.executeQuery(sql);

        if (records.size() == 1) { // if 1 exact record found, then creds matched
            return true;
        }

        return false;
    }
}
