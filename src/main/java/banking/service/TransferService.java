package banking.service;

import banking.entity.TransferValidationResult;

public interface TransferService {
    void addIncome(String cardNumber, int amount);
    void doTransfer(String senderCardNumber, String recipientCardNumber, int amount);
    boolean isSenderMatchingRecipient(String recipientCardNumber, String senderCardNumber);
    TransferValidationResult isTransferAmountFormatValid(String transferAmountStr);
    boolean hasEnoughBalanceForTransfer(String senderCardNumber, int transferAmount);
    TransferValidationResult validateRecipient(String senderCardNumber, String recipientCardNumber);
    TransferValidationResult validateTransferAmount(int senderBalance, int transferAmount);



}
