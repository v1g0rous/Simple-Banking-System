package banking.view;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppMenu {

    public MainMenu() {
        menuItemsAuthZone = createMenuItemsAuthZone();
        menuItemsUnauthZone = createMenuItemsUnauthZone();
    }


    @Override
    List<MenuItem> createMenuItemsAuthZone() {
        List<MenuItem> itemsAuthZone = new ArrayList<>();

        itemsAuthZone.add(new MenuItem("Balance", "showAccountBalance", 1, 1));
        itemsAuthZone.add(new MenuItem("Add income", "addIncome", 2, 2));
        itemsAuthZone.add(new MenuItem("Do transfer", "doTransfer", 3, 3));
        itemsAuthZone.add(new MenuItem("Close account", "closeAccount", 4, 4));
        itemsAuthZone.add(new MenuItem("Log out", "logoutFromAccount", 5, 5));
        itemsAuthZone.add(new MenuItem("Exit", "exitProgram", 6, 0));
        return itemsAuthZone;
    }

    @Override
    List<MenuItem> createMenuItemsUnauthZone() {
        List<MenuItem> itemsUnauthZone = new ArrayList<>();

        itemsUnauthZone.add(new MenuItem("Create an account", "createAccount", 1, 1));
        itemsUnauthZone.add(new MenuItem("Log into account", "loginToAccount", 2, 2));
        itemsUnauthZone.add(new MenuItem("Exit", "exitProgram", 3, 0));
        return itemsUnauthZone;
    }


}
