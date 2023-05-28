package banking.view;

import java.util.List;

abstract public class AppMenu {

    List<MenuItem> menuItemsAuthZone;
    List<MenuItem> menuItemsUnauthZone;

    public static MenuItem getMenuItemByLabelNumber(int labelNumber, AppMenu menu, Boolean authZone) {
        MenuItem resultItem = null;

        List<MenuItem> items = authZone ? menu.getMenuItemsAuthZone() : menu.getMenuItemsUnauthZone();
        for (MenuItem it : items) {
            if (it.getLabelNumber() == labelNumber) {
                resultItem = it;
            }
        }
        return resultItem;
    }

    abstract List<MenuItem> createMenuItemsAuthZone();
    abstract List<MenuItem> createMenuItemsUnauthZone();

    public List<MenuItem> getMenuItemsAuthZone() {
        return menuItemsAuthZone;
    }

    public List<MenuItem> getMenuItemsUnauthZone() {
        return menuItemsUnauthZone;
    }

    public void showAuthorized() {
        this.show(menuItemsAuthZone);
    }

    public void showUnauthorized() {
        this.show(menuItemsUnauthZone);
    }

    public void show(List<MenuItem> items) {
        items.sort(new MenuItem.DisplayOrderComparator());

        for (MenuItem item : items) {
            System.out.println(item.getLabelNumber() + ". " + item.getLabel());
        }

    }

}
