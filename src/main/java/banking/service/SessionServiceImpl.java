package banking.service;

import banking.DAO.SessionDAO;
import banking.entity.Session;

import java.util.Map;

public class SessionServiceImpl implements SessionService {
    private SessionDAO sessionDAO;

    public SessionServiceImpl(SessionDAO sessionDAO) {
        this.sessionDAO = sessionDAO;
    }

    public Session createUserSession() {
        Session session = new Session();
        return session;
    }

    public void login(Map<String, String> userCredentials, Session session) {

        if (userCredentials != null) {
            session.setUserIsAuthorized(true);
            session.setAuthorizedCardNumber(userCredentials);
        }
    }

    public void logout(Session session) {
        session.setUserIsAuthorized(false);
        session.setAuthorizedCardNumber("");
    }

    public boolean areUserCredentialsValid(Map<String, String> userCredentials) {
        if (userCredentials == null || userCredentials.size() != 1) {
            return false;
        }

        Map.Entry<String, String> entry = userCredentials.entrySet().iterator().next();
        String userCardNumber = entry.getKey();
        String userPin = entry.getValue();

        if (userCardNumber == null || userPin == null) {
            return false;
        }

        if (sessionDAO.hasUserCredentialsInDB(userCardNumber, userPin)) {
            return true;
        }

        return false;
    }
}
