package banking.DAO;

import banking.entity.Card;
import banking.service.CardServiceImpl;
import banking.util.SQLiteManager;
import banking.util.SqlWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardDAO {
    public static final String SQL_DELETE_CARD_BY_NUMBER = "DELETE FROM card WHERE number = ?";
    public static final String SQL_SELECT_CARD_BY_CARD_NUMBER = "SELECT * FROM Card WHERE number = ?";
    public static final String SQL_SELECT_CARD_NUMBER_BY_ACCOUNT_NUMBER = "SELECT number FROM card WHERE number LIKE ?";
    public static final String SQL_INSERT_NEW_CARD = "INSERT INTO card (number, pin, balance) VALUES (?, ?, ?);";
    public static final String SQL_SET_CARD_FIELDS = "UPDATE card SET pin = ?, balance = ? WHERE number = ? ;";
    public static final String SQL_DECREASE_SENDER_BALANCE = "UPDATE card SET balance = balance - ? WHERE number = ?";
    public static final String SQL_INCREASE_RECIPIENT_BALANCE = "UPDATE card SET balance = balance + ? WHERE number = ?";

    public CardDAO() {
    }

    public boolean isAccountNumberUnique(String accountNumber) {
        boolean accountNumberIsUnique = false;
        String pattern = CardServiceImpl.CARD_BIN + accountNumber + "%";

        Map<Integer, Object> sqlArgsByIndex = new HashMap<>();
        sqlArgsByIndex.put(1, pattern);

        SqlWrapper sqlWrapper = new SqlWrapper(SQL_SELECT_CARD_NUMBER_BY_ACCOUNT_NUMBER, sqlArgsByIndex);

        List<Map<String, Object>> records = SQLiteManager.executeSelectQuery(sqlWrapper);


        if (records.size() == 0) { // if account number doesn't exist in DB, then it's unique
            return true;
        }

        return accountNumberIsUnique;
    }

    public void updateCard(Card card) {
        Map<Integer, Object> sqlArgsByIndex = new HashMap<>();
        sqlArgsByIndex.put(1, card.getPinCode());
        sqlArgsByIndex.put(2, card.getBalance());
        sqlArgsByIndex.put(3, card.getCardNumber());

        SqlWrapper sqlWrapper = new SqlWrapper(SQL_SET_CARD_FIELDS, sqlArgsByIndex);

        SQLiteManager.executeUpdate(sqlWrapper);
    }

    public void insertCard(Card card) {
        Map<Integer, Object> sqlArgsByIndex = new HashMap<>();

        sqlArgsByIndex.put(1, card.getCardNumber());
        sqlArgsByIndex.put(2, card.getPinCode());
        sqlArgsByIndex.put(3, card.getBalance());

        SqlWrapper sqlWrapper = new SqlWrapper(SQL_INSERT_NEW_CARD, sqlArgsByIndex);

        SQLiteManager.executeUpdate(sqlWrapper);

    }

    public void deleteCard(Card card) {
        Map<Integer, Object> sqlArgsByIndex = new HashMap<>();

        sqlArgsByIndex.put(1, card.getCardNumber());

        SqlWrapper sqlWrapper = new SqlWrapper(SQL_DELETE_CARD_BY_NUMBER, sqlArgsByIndex);

        SQLiteManager.executeUpdate(sqlWrapper);
    }

    public void closeCardByCardNumber(String cardNumber) {
        Card card = getCard(cardNumber);
        deleteCard(card);
    }


    public Card getCard(String cardNumber) {

        Map<Integer, Object> sqlArgsByIndex = new HashMap<>();
        sqlArgsByIndex.put(1, cardNumber);

        SqlWrapper sqlWrapper = new SqlWrapper(SQL_SELECT_CARD_BY_CARD_NUMBER, sqlArgsByIndex);

        List<Map<String, Object>> records = SQLiteManager.executeSelectQuery(sqlWrapper);

        if (records.isEmpty()) {
            return null;
        }

        Map<String, Object> cardFields = records.get(0);

        String pinCode = (String) cardFields.get("pin");
        Integer balance = (Integer) cardFields.get("balance");

        if (cardNumber != null && pinCode != null && balance != null) {
            return new Card(cardNumber, pinCode, balance);
        }

        return null;
    }

    public void addIncome(Card card) {
        this.updateCard(card);
    }

    public void doTransfer(String senderCardNumber, String recipientCardNumber, int amount) {

        Map<Integer, Object> sqlArgsByIndexDecreaseSender = new HashMap<>();
        sqlArgsByIndexDecreaseSender.put(1, amount);
        sqlArgsByIndexDecreaseSender.put(2, senderCardNumber);

        SqlWrapper sqlWrapperDecreaseSender = new SqlWrapper(SQL_DECREASE_SENDER_BALANCE, sqlArgsByIndexDecreaseSender);


        Map<Integer, Object> sqlArgsByIndexIncreaseRecipient = new HashMap<>();
        sqlArgsByIndexIncreaseRecipient.put(1, amount);
        sqlArgsByIndexIncreaseRecipient.put(2, recipientCardNumber);

        SqlWrapper sqlWrapperIncreaseRecipient = new SqlWrapper(SQL_INCREASE_RECIPIENT_BALANCE, sqlArgsByIndexIncreaseRecipient);


        SQLiteManager.executeUpdateAsTransaction(List.of(sqlWrapperIncreaseRecipient, sqlWrapperDecreaseSender));
    }
}
