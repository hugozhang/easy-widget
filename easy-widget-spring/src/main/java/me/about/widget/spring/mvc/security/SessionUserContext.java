package me.about.widget.spring.mvc.security;

public class SessionUserContext {

    private static final ThreadLocal<SessionUser> sessionUserHolder = new ThreadLocal<>();

    public static void setSessionUser(SessionUser sessionUser) {
        sessionUserHolder.set(sessionUser);
    }

    public static SessionUser getSessionUser() {
        return sessionUserHolder.get();
    }

    public static void removeSessionUser() {
        sessionUserHolder.remove();
    }

}
