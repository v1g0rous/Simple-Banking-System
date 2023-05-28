package banking.view;

import banking.entity.Command;
import banking.entity.Controller;
import banking.entity.Log;
import banking.entity.Session;

import java.util.Map;
import java.util.Scanner;

public interface View {
    void showMenu(AppMenu menu);
    Command readUserCommand(AppMenu menu);
    Command readUserCommand();
    Boolean isInputValid(String userInput, AppMenu menu);
    void setAppController(Controller appController);

    default String getUserInput() {
        String userInput = "";
        try {
            Scanner scanner = new Scanner(System.in);
            userInput = scanner.nextLine();
        } catch (Exception e) {
            new Log("Invalid user input: userInput=" + userInput, e);
        }

        return userInput;
    }

    void showAccountCreatedNotification(String cardCreatedMessage, String cardNumber,
                                        String cardNumberMessage, String pinCode, String pinCodeMessage);

    Map<String, String> readUserCredentials();

    void showNotification(String notification);
    public Session getUserSession();
}
