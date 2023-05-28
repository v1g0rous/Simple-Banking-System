package banking.entity;

import java.util.Map;

public class Session {
    String authorizedCardNumber;
    public boolean userIsAuthorized = false;


    public void setAuthorizedCardNumber(String authorizedCardNumber) {
        this.authorizedCardNumber = authorizedCardNumber;
    }

    public Session() {
    }

    public boolean isUserAuthorized() {
        return userIsAuthorized;
    }

    public void setUserIsAuthorized(boolean userIsAuthorized) {
        this.userIsAuthorized = userIsAuthorized;
    }

    private String setCurrentAccount(Map<String, String> userCredentials) {
        Map.Entry<String, String> entry = userCredentials.entrySet().iterator().next();
        return entry.getKey();

    }

    public String getAuthorizedCardNumber() {
        return authorizedCardNumber;
    }

    public void setAuthorizedCardNumber(Map<String, String> userCredentials) {
        Map.Entry<String, String> entry = userCredentials.entrySet().iterator().next();
        this.authorizedCardNumber = entry.getKey();
    }
}
