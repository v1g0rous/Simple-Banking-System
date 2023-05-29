package banking.entity;

import banking.service.CardService;
import banking.service.SessionService;
import banking.service.TransferService;
import banking.view.View;

import java.util.Map;

// responsibility: manage data between view and service
public class AppController implements Controller {
    public static final String SOMETHING_WENT_WRONG = "Something went wrong, please, try again\n";
    public static final String INCORRECT_USER_COMMAND = "Your input is incorrect, please, try again\n";
    public static final String NEW_CARD_CREATED = "Your card has been created\n";
    public static final String NEW_CARD_NUMBER = "Your card number:";
    public static final String CREATE_NEW_CARD_ERROR = "Something went wrong while creating new card, try again later\n";
    public static final String NEW_CARD_PIN = "Your card PIN:";
    public static final String CARD_HAS_BEEN_CLOSED = "The account has been closed!\n";
    public static final String CLOSE_CARD_ERROR = "Something went wrong while closing account, try again later\n";
    public static final String SUCCESSFULLY_LOGGED_IN = "You have successfully logged in!\n";
    public static final String SUCCESSFULLY_LOGGED_OUT = "You have successfully logged out!\n";
    public static final String CARD_BALANCE_MESSAGE = "Balance: ";
    public static final String SHOW_BALANCE_ERROR = "Can't show balance due to technical issues, please, try again later\n";
    public static final String ENTER_INCOME = "Enter income:";
    public static final String INCOME_WAS_ADDED = "Income was added!\n";
    public static final String INCORRECT_MONEY_INPUT = "Incorrect input\n" +
            "Please enter positive amount for correct operation\n";
    public static final String ENTER_RECIPIENT_CARD = "Transfer\n" + "Enter card number:";
    public static final String ENTER_TRANSFER_MONEY_AMOUNT = "Enter how much money you want to transfer:";
    public static final String TRANSFER_SUCCESS = "Success!\n";
    public static final String BYE = "Bye!";
    public static final String INVALID_CREDENTIALS = "Invalid login or password\n";

    View view;
    App app;
    CardService cardService;
    SessionService sessionService;
    TransferService transferService;

    public AppController(App app, View view, CardService cardService, SessionService sessionService, TransferService transferService) {
        this.app = app;
        this.view = view;
        this.cardService = cardService;
        this.sessionService = sessionService;
        this.transferService = transferService;
    }

    public void request(Command userCommand) {
        try {
            switch (userCommand) {
                case CREATE_ACCOUNT -> createCard();
                case CLOSE_ACCOUNT -> closeCard();
                case LOGIN_TO_ACCOUNT -> loginToAccount();
                case LOGOUT_FROM_ACCOUNT -> logoutFromAccount();
                case SHOW_ACCOUNT_BALANCE -> showBalance();
                case ADD_INCOME -> addIncome();
                case DO_TRANSFER -> doTransfer();
                case EXIT_PROGRAM -> terminate();
                case UNKNOWN_COMMAND -> doInvalidCommandFlow();
            }
        } catch (Exception e) {
            new Log("AppController failed", e);
            view.showNotification(SOMETHING_WENT_WRONG);
        }
    }

    public void doInvalidCommandFlow() {
        view.showNotification(INCORRECT_USER_COMMAND);
    }

    public void terminate() {
        try {
            view.showNotification(BYE);
            this.app.terminate();
        } catch (Exception e) {
            new Log("Failed to terminate app", e);
            view.showNotification(SOMETHING_WENT_WRONG);
        }
    }

    public void doTransfer() {
        try {
            String senderCardNumber = view.getUserSession().getAuthorizedCardNumber();

            // getting and validating recipient and his card
            view.showNotification(ENTER_RECIPIENT_CARD);
            String recipientCardNumber = view.getUserInput();

            TransferValidationResult transferRecipientCheck = transferService.validateRecipient(senderCardNumber, recipientCardNumber);
            if (!transferRecipientCheck.isValid()) {
                view.showNotification(transferRecipientCheck.getMessage());
                return;
            }

            CardValidationResult recipientCardCheck = cardService.validateCardByNumber(recipientCardNumber);
            if (!recipientCardCheck.isValid()) {
                view.showNotification(recipientCardCheck.getMessage());
                return;
            }

            // getting and validating transfer amount
            view.showNotification(ENTER_TRANSFER_MONEY_AMOUNT);
            String transferAmountStr = view.getUserInput();

            TransferValidationResult transferAmountFormatCheck = transferService.isTransferAmountFormatValid(transferAmountStr);
            if (!transferAmountFormatCheck.isValid()) {
                view.showNotification(transferAmountFormatCheck.getMessage());
                return;
            }

            int senderBalance = cardService.getCardByCardNumber(senderCardNumber).getBalance();
            int transferAmount = Integer.parseInt(transferAmountStr);

            TransferValidationResult amountValidationResult = transferService.validateTransferAmount(senderBalance, transferAmount);
            if (!amountValidationResult.isValid()) {
                view.showNotification(amountValidationResult.getMessage());
                return;
            }

            // conducting transfer
            transferService.doTransfer(senderCardNumber, recipientCardNumber, transferAmount);
            view.showNotification(TRANSFER_SUCCESS);

        } catch (Exception e) {
            new Log("Failed to transfer money", e);
            view.showNotification(SOMETHING_WENT_WRONG);
        }
    }

    public void addIncome() {
        try {
            String cardNumber = view.getUserSession().getAuthorizedCardNumber();

            view.showNotification(ENTER_INCOME);
            String amountStr = view.getUserInput();
            TransferValidationResult amountCheck = transferService.isTransferAmountFormatValid(amountStr);

            if (!amountCheck.isValid()) {
                view.showNotification(INCORRECT_MONEY_INPUT);
                return;
            }

            int amount = Integer.parseInt(amountStr);

            transferService.addIncome(cardNumber, amount);
            view.showNotification(INCOME_WAS_ADDED);
        } catch (Exception e) {
            new Log("Failed to add income", e);
            view.showNotification(INCORRECT_MONEY_INPUT);
        }
    }

    public void showBalance() {
        try {
            Session session = view.getUserSession();
            Card card = cardService.getCardByCardNumber(session.getAuthorizedCardNumber());
            int balance = card.getBalance();

            view.showNotification(CARD_BALANCE_MESSAGE + balance + "\n");
        } catch (Exception e) {
            new Log("Couldn't show user balance", e);
            view.showNotification(SHOW_BALANCE_ERROR);
        }
    }

    public void logoutFromAccount() {
        try {
            Session userSession = view.getUserSession();

            sessionService.logout(userSession);
            view.showNotification(SUCCESSFULLY_LOGGED_OUT);
        } catch (Exception e) {
            new Log("Failed to logout user from system", e);
            view.showNotification(SOMETHING_WENT_WRONG);
        }
    }

    public void loginToAccount() {
        try {
            Map<String, String> userCredentials = view.readUserCredentials();
            Session userSession = view.getUserSession();

            if (!sessionService.areUserCredentialsValid(userCredentials)) {
                view.showNotification(INVALID_CREDENTIALS);
                return;
            }

            sessionService.login(userCredentials, userSession);
            view.showNotification(SUCCESSFULLY_LOGGED_IN);
        } catch (Exception e) {
            new Log("Failed to login user to system", e);
            view.showNotification(SOMETHING_WENT_WRONG);
        }
    }

    public void createCard() {
        try {
            Card card = cardService.createCard();
            view.showAccountCreatedNotification(NEW_CARD_CREATED, card.getCardNumber(), NEW_CARD_NUMBER, card.getPinCode(), NEW_CARD_PIN);
        } catch (Exception e) {
            view.showNotification(CREATE_NEW_CARD_ERROR);
            new Log("Failed to create new card", e);
        }
    }

    public void closeCard() {
        try {
            Session userSession = view.getUserSession();
            String cardNumber = userSession.getAuthorizedCardNumber();

            sessionService.logout(userSession);
            cardService.closeCard(cardNumber);
            view.showNotification(CARD_HAS_BEEN_CLOSED);
        } catch (Exception e) {
            new Log("Failed to close the card", e);
            view.showNotification(CLOSE_CARD_ERROR);
        }
    }

}

