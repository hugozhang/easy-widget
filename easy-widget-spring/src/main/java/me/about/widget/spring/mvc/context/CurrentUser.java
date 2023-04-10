package me.about.widget.spring.mvc.context;

import lombok.Data;

/**
 * 当前登陆用户
 *
 * @author: hugo.zxh
 * @date: 2023/04/03 17:29
 */
@Data
public class CurrentUser {

    private String loginId;

    private String username;

}
