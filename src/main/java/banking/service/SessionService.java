package banking.service;

import banking.entity.Session;

import java.util.Map;

public interface SessionService {
    void login(Map<String, String> userCredentials, Session session);
    void logout(Session session);
    Session createUserSession();
    boolean areUserCredentialsValid(Map<String, String> userCredentials);
}
