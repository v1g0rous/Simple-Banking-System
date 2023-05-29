package banking.DAO;

import banking.util.SQLiteManager;
import banking.util.SqlWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionDAO {

    public static final String SQL_SELECT_CARD_CREDENTIALS = "SELECT number, pin FROM card WHERE number = ? AND pin = ?";

    public boolean hasUserCredentialsInDB(String userCardNumber, String userPin) {

        Map<Integer, Object> sqlArgsByIndex = new HashMap<>();
        sqlArgsByIndex.put(1, userCardNumber);
        sqlArgsByIndex.put(2, userPin);

        SqlWrapper sqlWrapper = new SqlWrapper(SQL_SELECT_CARD_CREDENTIALS, sqlArgsByIndex);

        List<Map<String, Object>> records = SQLiteManager.executeSelectQuery(sqlWrapper);

        if (records.size() == 1) { // if 1 exact record found, then creds matched
            return true;
        }

        return false;
    }
}
