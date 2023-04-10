package me.about.widget.trace.view;

/**
 * 视图接口
 *
 * @Author: hugo.zxh
 * @Date: 2022/03/09 14:40
 * @Description:
 */
public interface View {

    /**
     * 输出树形结构日志
     * @return
     */
    String draw();

    /**
     * 进入方法
     * @param message
     * @return
     */
    View begin(String message);

    /**
     * 退出方法
     * @return
     */
    View end();

    /**
     * 退出方法
     * @param mark
     * @return
     */
    View end(String mark);

}
