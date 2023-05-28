package banking.entity;

import banking.DAO.CardDAO;
import banking.DAO.SessionDAO;
import banking.service.*;
import banking.util.SQLiteManager;
import banking.view.AppView;
import banking.view.View;

import java.util.ArrayList;
import java.util.List;

public class AppImpl implements App {

    public static final String COMMAND_LINE_ARGS_ARE_EMPTY = "Command line args are empty";
    public static final String MISSING_COMMAND_LINE_PARAMETER_VALUE = "Missing command line parameter value";

    List<String> validCommandLineArgs = new ArrayList<>(List.of("-fileName"));
    Controller controller;
    View view;
    SQLiteManager databaseManager;

    public AppImpl(String[] args) throws Exception {
        validateCommandLineArgs(args); // throw exception if starting args not valid
        this.databaseManager = new SQLiteManager(args);

        CardDAO cardDAO = new CardDAO();
        CardService cardService = new CardServiceImpl(cardDAO);
        TransferService transferService = new TransferServiceImpl(cardDAO);

        SessionDAO sessionDAO = new SessionDAO();
        SessionService sessionService = new SessionServiceImpl(sessionDAO);
        Session userSession = sessionService.createUserSession();

        this.view = new AppView(userSession);
        this.controller = new AppController(this, view, cardService, sessionService, transferService);
        this.view.setAppController(controller);
    }

    public void run() {
        Command userCommand = view.readUserCommand();
        controller.request(userCommand);
        run();
    }


    private void validateCommandLineArgs(String[] args) throws Exception {
        String errorMessage;
        // check args not empty
        if (args.length == 0) {
            errorMessage = COMMAND_LINE_ARGS_ARE_EMPTY;
            Exception exception = new RuntimeException(errorMessage);
            new Log(errorMessage, exception);
            throw exception;
        }

        // check params are valid
        for (int i = 0; i < args.length; i += 2) {
            if (!validCommandLineArgs.contains(args[i])) {
                errorMessage = "Command line arg: " + args[i] + " not recognized";
                Exception exception = new RuntimeException();
                new Log(errorMessage, exception);
                throw exception;
            }
        }

        // check each param has a value
        if (args.length % 2 != 0) {
            errorMessage = MISSING_COMMAND_LINE_PARAMETER_VALUE;
            Exception exception = new RuntimeException(errorMessage);
            new Log(errorMessage, exception);
            throw exception;
        }
    }

    public void terminate() {
        try {
            System.exit(0);
        } catch (Exception e) {
            new Log("Failed to terminate app", e);
        }
    }
}
