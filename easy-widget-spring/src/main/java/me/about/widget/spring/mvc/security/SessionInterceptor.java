package me.about.widget.spring.mvc.security;

import com.alibaba.fastjson.JSON;
import me.about.widget.spring.mvc.result.Result;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class SessionInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        SessionUser sessionUser = (SessionUser) request.getSession().getAttribute("SessionUser");
        if (sessionUser != null) {
            SessionUserContext.setSessionUser(sessionUser);
            return true;
        } else {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = response.getWriter();
            out.append(JSON.toJSONString(Result.failed(401, "未登录，请登录后再操作")));
            out.flush();
            out.close();
            return false;
        }
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }
}
