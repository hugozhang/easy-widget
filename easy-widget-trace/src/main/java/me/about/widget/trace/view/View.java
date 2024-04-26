package me.about.widget.trace.view;

/**
 * 视图接口
 *
 * @author: hugo.zxh
 * @date: 2022/03/09 14:40
 * @description:
 */
public interface View {

    /**
     * 输出树形结构日志
     */
    String draw();

    /**
     * 进入方法
     */
    void begin(String message);

    /**
     * 退出方法
     */
    void end();

    /**
     * 退出方法
     */
    void end(String mark);

}
