package banking.service;

import banking.DAO.CardDAO;
import banking.entity.Card;
import banking.entity.Log;
import banking.entity.TransferValidationResult;

public class TransferServiceImpl implements TransferService {
    public static final String TRANSFER_MONEY_TO_SAME_ACCOUNT_ERROR = "You can't transfer money to the same account!\n";
    public static final String INCORRECT_MONEY_INPUT = "Incorrect input\n" +
            "Please enter positive amount for correct operation\n";
    public static final String NOT_ENOUGH_MONEY_FOR_TRANSFER_ERROR = "Not enough money!\n";

    private CardDAO cardDAO;

    public TransferServiceImpl(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
    }

    public void addIncome(String cardNumber, int amount) {
        Card card = cardDAO.getCard(cardNumber);
        card.setBalance(card.getBalance() + amount);

        cardDAO.addIncome(card);
    }

    public void doTransfer(String senderCardNumber, String recipientCardNumber, int amount) {
        cardDAO.doTransfer(senderCardNumber, recipientCardNumber, amount);
    }

    public TransferValidationResult validateRecipient(String senderCardNumber, String recipientCardNumber) {
        boolean recipientAccountIsTheSame = this.isSenderMatchingRecipient(senderCardNumber, recipientCardNumber);

        if (recipientAccountIsTheSame) {
            return new TransferValidationResult(false, TRANSFER_MONEY_TO_SAME_ACCOUNT_ERROR);
        }

        return new TransferValidationResult(true, "");
    }


    public TransferValidationResult validateTransferAmount(int senderBalance, int transferAmount) {

        if (senderBalance >= transferAmount) {
            return new TransferValidationResult(true, "");
        } else {
            return new TransferValidationResult(false, NOT_ENOUGH_MONEY_FOR_TRANSFER_ERROR);
        }
    }


    public TransferValidationResult isTransferAmountFormatValid(String transferAmountStr) {

        try {
            int transferAmount = Integer.parseInt(transferAmountStr);
            if (transferAmount > 0) {
                return new TransferValidationResult(true, "");
            }

        } catch (NumberFormatException e) {
            new Log("Failed to check if transfer amount is valid", e);
        }

        return new TransferValidationResult(false, INCORRECT_MONEY_INPUT);
    }

    public boolean hasEnoughBalanceForTransfer(String senderCardNumber, int transferAmount) {
        boolean balanceIsEnough = false;

        try {
            Card card = cardDAO.getCard(senderCardNumber);

            if (card == null) {
                return false;
            }

            balanceIsEnough = card.getBalance() >= transferAmount;
        } catch (Exception e) {
            new Log("Failed to check if sender has enough balance for transfer", e);
        }

        return balanceIsEnough;
    }


    public boolean isSenderMatchingRecipient(String recipientCardNumber, String senderCardNumber) {
        return recipientCardNumber.equals(senderCardNumber);
    }


}
