package banking.view;

import java.util.Comparator;

public class MenuItem {
    String label;
    String apiName;
    int displayOrderNumber;
    int labelNumber;

    public MenuItem(String label, String apiName, int displayOrder, int labelNumber) {
        this.label = label;
        this.labelNumber = labelNumber;
        this.apiName = apiName;
        this.displayOrderNumber = displayOrder;
    }

    public String getLabel() {
        return label;
    }

    public String getApiName() {
        return apiName;
    }

    public int getDisplayOrderNumber() {
        return displayOrderNumber;
    }

    public int getLabelNumber() {
        return labelNumber;
    }

    public static class DisplayOrderComparator implements Comparator<MenuItem> {
        @Override
        public int compare(MenuItem it1, MenuItem it2) {

            if (it1 == null && it2 == null) {
                return 0; // both are null, considered equal
            } else if (it1 == null) {
                return 1; // the first element should go AFTER the second one
            } else if (it2 == null) {
                return -1; // the first element should go BEFORE the second one
            }

            int orderNumberIt1 = it1.getDisplayOrderNumber();
            int orderNumberIt2 = it2.getDisplayOrderNumber();

            return Integer.compare(orderNumberIt1, orderNumberIt2);
        }
    }
}
