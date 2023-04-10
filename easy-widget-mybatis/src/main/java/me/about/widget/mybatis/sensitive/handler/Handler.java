package me.about.widget.mybatis.sensitive.handler;

/**
 * 加解密处理
 *
 * @author: hugo.zxh
 * @date: 2023/03/21 11:31
 */
public interface Handler {

    /**
     * 加密
     * @param value
     * @return
     */
    String encrypt(Object value);

    /**
     * 解密
     * @param value
     * @return
     */
    String decrypt(Object value);

}
