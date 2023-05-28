package banking.entity;

public enum Command { // controller fires commands based on MenuItem apiName
    ADD_INCOME("addIncome"),
    CLOSE_ACCOUNT("closeAccount"),
    CREATE_ACCOUNT("createAccount"),
    DO_TRANSFER("doTransfer"),
    LOGIN_TO_ACCOUNT("loginToAccount"),
    LOGOUT_FROM_ACCOUNT("logoutFromAccount"),
    SHOW_ACCOUNT_BALANCE("showAccountBalance"),
    EXIT_PROGRAM("exitProgram"),
    UNKNOWN_COMMAND("unknownCommand");

    private final String apiName;


    Command(String apiName) {
        this.apiName = apiName;
    }

    public static Command getCommandByMenuItem(String apiName) {
        Command resultCommand = UNKNOWN_COMMAND;

        if (apiName == null) return resultCommand;

        for (Command cm : Command.values()) {
            if (cm.apiName.equals(apiName)) {
                resultCommand = cm;
            }
        }

        return resultCommand;
    }


    public static boolean isCommandValid(String userInput) {
        if (Command.getCommandByMenuItem(userInput) != null) {
            return true;
        } else {
            return false;
        }
    }
}
