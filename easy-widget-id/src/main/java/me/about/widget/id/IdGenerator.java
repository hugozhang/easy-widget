package me.about.widget.id;

/**
 * 分布式id生成接口
 *
 * @author: hugo.zxh
 * @date: 2020/11/18 18:04
 * @description:
 */
public interface IdGenerator {

    /**
     * 获取下一个id
     * @param tag
     * @return
     */
    long nextId(String tag);

}
