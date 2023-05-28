package banking.DAO;

import banking.entity.Card;
import banking.entity.Log;
import banking.service.CardServiceImpl;
import banking.util.SQLiteManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;
import java.util.Map;

public class CardDAO { // responsibility: interaction with DB
    public CardDAO() {
    }

    public boolean isAccountNumberUnique(String accountNumber) {
        boolean accountNumberIsUnique = false;
        String pattern = CardServiceImpl.CARD_BIN + accountNumber + "%";

        String sql = "SELECT number FROM card WHERE number LIKE \"" + pattern + "\"";

        List<Map<String, Object>> records = SQLiteManager.executeQuery(sql);

        if (records.size() == 0) { // if account number doesn't exist in DB, then it's unique
            return true;
        }

        return accountNumberIsUnique;
    }

    public void updateCard(Card card) {
        String sql = "UPDATE card " +
                "SET pin = ?, balance = ? " +
                "WHERE number = ? " +
                ";";

        Connection connection = SQLiteManager.getConnectionInstance();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            try {
                statement.setString(1, card.getPinCode());
                statement.setInt(2, card.getBalance());
                statement.setString(3, card.getCardNumber());

                statement.executeUpdate();
            } catch (Exception e) {
                new Log("Error executing sql statement: " + sql, e);
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    new Log("Error closing connection to DB", e);
                }
            }
        } catch (SQLException e) {
            new Log ("Error establishing connection", e);
        }

    }

    public void insertCard(Card card) {
        String sql = "INSERT INTO card (number, pin, balance) " +
                "VALUES (?, ?, ?);";

        Connection connection = SQLiteManager.getConnectionInstance();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            try {
                statement.setString(1, card.getCardNumber());
                statement.setString(2, card.getPinCode());
                statement.setInt(3, card.getBalance());

                statement.executeUpdate();
            } catch (Exception e) {
                new Log("Error executing sql statement: " + sql, e);
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    new Log("Error closing connection to DB", e);
                }
            }
        } catch (SQLException e) {
            new Log ("Error establishing connection", e);
        }
    }

    private void deleteCard(Card card) {
        String cardNumber = card.getCardNumber();
        String sql = "DELETE FROM card WHERE number = \"" + cardNumber + "\"";
        SQLiteManager.executeUpdate(sql);
    }

    public void closeCardByCardNumber(String cardNumber) {
        Card card = getCard(cardNumber);
        deleteCard(card);
    }


    public Card getCard(String cardNumber) {
        Card card = null;

        String sql = "SELECT * FROM Card WHERE number = " + "\"" + cardNumber + "\"";
        List<Map<String, Object>> cards = SQLiteManager.executeQuery(sql);

        Map<String, Object> cardFields;
        if (cards.size() > 0) {
            cardFields = cards.get(0);
        } else {
            return card;
        }

        String pinCode = null;
        Integer balance = null;

        for (String fieldName : cardFields.keySet()) {
            if (fieldName.equals("pin")) {
                pinCode = (String) cardFields.get(fieldName);
            }

            if (fieldName.equals("balance")) {
                balance = (Integer) cardFields.get(fieldName);
            }
        }

        if (cardNumber != null && pinCode != null && balance != null) {
            card = new Card(cardNumber, pinCode, balance);
        }

        return card;
    }

    public void addIncome(Card card) {
        this.updateCard(card);
    }

    public void doTransfer(String senderCardNumber, String recipientCardNumber, int amount) {

        String sqlDecreaseSenderBalance = "UPDATE card SET balance = balance - ? WHERE number = ?";
        String sqlIncreaseRecipientBalance = "UPDATE card SET balance = balance + ? WHERE number = ?";


        Connection connection = SQLiteManager.getConnectionInstance();

        try {
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();

            try {
                PreparedStatement stDecreaseSender = connection.prepareStatement(sqlDecreaseSenderBalance);
                stDecreaseSender.setInt(1, amount);
                stDecreaseSender.setString(2, senderCardNumber);


                PreparedStatement stIncreaseRecipient = connection.prepareStatement(sqlIncreaseRecipientBalance);
                stIncreaseRecipient.setInt(1, amount);
                stIncreaseRecipient.setString(2, recipientCardNumber);

                stIncreaseRecipient.executeUpdate();
                stDecreaseSender.executeUpdate();

                connection.commit();

            } catch (Exception e) {
                new Log("Failed to execute sql statement" , e);
                try {
                    connection.rollback(savepoint);
                } catch (SQLException ex) {
                    new Log("Failed to rollback" , e);
                }
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    new Log("Error closing connection to DB", e);
                }
            }
        } catch (SQLException e) {
            new Log("Failed to executeUpdateAsTransaction", e);
        }
    }
}
