package me.about.widget.mybatis.sensitive.handler;

import me.about.widget.mybatis.sensitive.util.AesUtil;

/**
 * AES 算法
 *
 * @author: hugo.zxh
 * @date: 2023/03/24 17:20
 */
public class AesAlgoHandler implements Handler {
    @Override
    public String encrypt(Object value) {
        return AesUtil.encrypt((String)value);
    }

    @Override
    public String decrypt(Object value) {
        return AesUtil.decrypt((String)value);
    }
}
