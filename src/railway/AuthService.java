package railway;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static AuthService instance;
    private Map<String, User> users = new HashMap<>();
    private User currentUser;

    private AuthService() {
        users.put("admin", new User("admin", "admin123", "ADMIN"));
        users.put("passenger", new User("passenger", "pass123", "PASSENGER"));
    }

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.checkPassword(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() { return currentUser; }
    public boolean isAdmin() { return currentUser != null && "ADMIN".equals(currentUser.getRole()); }
    public boolean isPassenger() { return currentUser != null && "PASSENGER".equals(currentUser.getRole()); }
    public boolean isLoggedIn() { return currentUser != null; }
}