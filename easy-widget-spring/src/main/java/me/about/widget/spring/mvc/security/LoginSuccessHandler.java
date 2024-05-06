package me.about.widget.spring.mvc.security;

import me.about.widget.spring.support.SessionUser;
import me.about.widget.spring.support.SessionUserContext;
import org.springframework.session.FindByIndexNameSessionRepository;

import javax.servlet.http.HttpSession;

public class LoginSuccessHandler {

    public static void createSessionUser(HttpSession httpSession, SessionUser sessionUser) {
        if (httpSession == null || sessionUser == null) {
            return;
        }
        httpSession.setAttribute(SessionUserContext.SESSION_USER, sessionUser);
        if (sessionUser.getIndexName() != null) {
            httpSession.setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, sessionUser.getIndexName());
        }
    }
}
