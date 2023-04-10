package me.about.widget.spring.mvc.interceptor;

import me.about.widget.spring.mvc.context.CurrentUser;
import me.about.widget.spring.mvc.context.UserContext;
import me.about.widget.spring.mvc.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录拦截
 *
 * @author: hugo.zxh
 * @date: 2023/04/03 17:37
 */
public class LoginInterceptor implements HandlerInterceptor {

    private static final  Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession();
        CurrentUser currentUser = (CurrentUser)session.getAttribute(Constant.SESSION_USER);
        if (currentUser == null) {
            log.error("没有登陆或登录已经过期");
            return false;
        }
        UserContext.setCurrentUser(currentUser);

        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        UserContext.removeCurrentUser();
    }

}
