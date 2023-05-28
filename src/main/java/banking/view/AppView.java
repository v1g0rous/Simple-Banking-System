package banking.view;

import banking.entity.Command;
import banking.entity.Controller;
import banking.entity.Log;
import banking.entity.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// responsibility: get user input and call controllers command
public class AppView implements View {
    public static final String ENTER_CARD_NUMBER = "Enter your card number:";
    public static final String ENTER_PIN = "Enter your PIN:";
    public static final String INCORRECT_USER_COMMAND = "Your input is incorrect, please, try again";

    Session userSession;
    Controller appController;

    public AppView(Session session) {
        this.userSession = session;
    }

    public void setAppController(Controller appController) {
        this.appController = appController;
    }

    public Session getUserSession() {
        return userSession;
    }



    // check if userInput is valid for shown menu
    @Override
    public Boolean isInputValid(String userInput, AppMenu menu) {
        if (userInput == null || userInput.isEmpty()) return false;

        Boolean validInput = false;

        try {
            Boolean isAuthZone = userSession.isUserAuthorized();
            List<MenuItem> items = isAuthZone ? menu.getMenuItemsAuthZone() : menu.getMenuItemsUnauthZone();

            String regex = prepareMenuRegex(items); // check if userInput is within range of menu item numbers

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(userInput);
            if (matcher.find()) {
                validInput = true;
            }
        } catch (Exception e) {
            new Log("Failed to check validity of user input: " + userInput + " for AppMenu", e );
        }

        return validInput;
    }

    @Override
    public Map<String, String> readUserCredentials() {
        System.out.println(ENTER_CARD_NUMBER);
        String cardNumber = this.getUserInput();
        System.out.println(ENTER_PIN);
        String cardPin = this.getUserInput();
        System.out.println();

        return new HashMap<String, String>(Map.of(cardNumber,cardPin));
    }

    private String prepareMenuRegex(List<MenuItem> items) {
        StringBuilder regex = new StringBuilder();
        int lastIndex = items.size() - 1;
        for (int i = 0; i < items.size(); i++) {
            int labelNumber = items.get(i).getLabelNumber();
            regex.append("^" + labelNumber + "$");
            regex.append(i == lastIndex ? "" : "|");
        }

        return regex.toString();
    }

    public Command readUserCommand(AppMenu menu) {

        String labelNumber = this.getUserInput();
        Boolean validInput = this.isInputValid(labelNumber, menu);

        return validInput ? defineUserCommand(menu, labelNumber) : Command.UNKNOWN_COMMAND;
    }

    @Override
    public void showNotification(String notification) {
        System.out.println(notification);
    }

    private Command defineUserCommand(AppMenu menu, String labelNumber) {
        int labelNumberInt = -1;

        try {
            labelNumberInt = Integer.parseInt(labelNumber);
        } catch (NumberFormatException e) {
            new Log("Failed to define Command for userInput: " + labelNumber, e);
        }

        boolean authZone = this.userSession.isUserAuthorized();
        MenuItem item = AppMenu.getMenuItemByLabelNumber(labelNumberInt, menu, authZone);

        if (item != null) {
            return Command.getCommandByMenuItem(item.getApiName());
        } else {
            return Command.UNKNOWN_COMMAND;
        }
    }


    public void showAccountCreatedNotification(String cardCreatedMessage, String cardNumber,
                                               String cardNumberMessage, String pinCode, String pinCodeMessage) {
        System.out.println(cardCreatedMessage);
        showCardNumber(cardNumber, cardNumberMessage);
        showPinCode(pinCode, pinCodeMessage);
        System.out.println();
    }

    private static void showPinCode(String pinCode, String message) {
        System.out.println(message);
        System.out.println(pinCode);
    }

    private static void showCardNumber(String cardNumber, String message) {
        System.out.println(message);
        System.out.println(cardNumber);
    }

    public void showMenu(AppMenu menu) {
        if (userSession.userIsAuthorized) {
            menu.showAuthorized();
        } else {
            menu.showUnauthorized();
        }
    }

    @Override
    public Command readUserCommand() {
        AppMenu menu = new MainMenu();
        this.showMenu(menu);
        Command userCommand = this.readUserCommand(menu);
        return userCommand;
    }
}
