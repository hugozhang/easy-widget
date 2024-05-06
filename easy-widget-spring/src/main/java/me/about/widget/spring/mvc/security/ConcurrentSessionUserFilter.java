package me.about.widget.spring.mvc.security;

import me.about.widget.spring.support.SessionUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;

public class ConcurrentSessionUserFilter implements Filter {


    @Value("${spring.session.allowed-sessions:1}")
    private int allowedSessions;

    @Resource
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest =(HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse =(HttpServletResponse) servletResponse;

        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        SessionUser sessionUser = (SessionUser)session.getAttribute("SessionUser");

        if (sessionUser == null || sessionUser.getIndexName() == null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Collection<? extends Session> sessions = this.sessionRepository.findByPrincipalName(sessionUser.getIndexName()).values();
        int sessionCount = sessions.size();
        if (sessionCount <= allowedSessions) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        sessions.stream()
                .sorted(Comparator.comparing(Session::getCreationTime))
                .limit(sessions.size() - 1)
                .filter(s -> !s.getId().equals(session.getId()))
                .forEach(this::kickoffSession);

        filterChain.doFilter(servletRequest, servletResponse);

    }

    private void kickoffSession(Session session) {
        sessionRepository.deleteById(session.getId());
    }
}
