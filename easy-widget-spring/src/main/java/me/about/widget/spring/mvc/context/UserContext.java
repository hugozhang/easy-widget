package me.about.widget.spring.mvc.context;

/**
 * Current User 上下文管理
 *
 * @author: hugo.zxh
 * @date: 2023/04/03 17:31
 */
public class UserContext {

    private static final ThreadLocal<CurrentUser> USER_CONTAINER = new ThreadLocal<>();

    public static CurrentUser getCurrentUser() {
        return USER_CONTAINER.get();
    }

    public static void removeCurrentUser() {
        USER_CONTAINER.remove();
    }

    public static void setCurrentUser(CurrentUser currentUser) {
        USER_CONTAINER.set(currentUser);
    }
}
