package banking.service;

import banking.entity.Card;
import banking.entity.CardValidationResult;

public interface CardService {
    Card createCard() throws Exception;
    void closeCard(String cardNumber);
    Card getCardByCardNumber(String authorizedCardNumber);
    CardValidationResult validateCardByNumber(String userCardNumber);
    boolean isAccountNumberUnique(String accountNumber);
    String generateAccountNumber();
    boolean isCardExistingInDB(String cardNumber);

}
