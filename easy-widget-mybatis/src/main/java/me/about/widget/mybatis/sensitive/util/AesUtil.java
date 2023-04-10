package me.about.widget.mybatis.sensitive.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES   aes-cbc-256 工具
 *
 * @author: hugo.zxh
 * @date: 2023/03/21 13:12
 */

public class AesUtil {

    private static Logger logger = LoggerFactory.getLogger(AesUtil.class);

    /**
     * 密钥
     */
    private static String key = "19FEAC5904F52D3D1E6C9141F08BDA0C";

    private static String charset = "utf-8";
    /**
     * 偏移量
     */
    private static int offset = 16;

    private static String transformation = "AES/CBC/PKCS5Padding";

    private static String algorithm = "AES";

    /**
     * 加密
     *
     * @param content
     * @return
     */
    public static String encrypt(String content) {
        return encrypt(content, key);
    }
    /**
     * 解密
     *
     * @param content
     * @return
     */
    public static String decrypt(String content) {
        return decrypt(content, key);
    }
    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @param key     加密密码
     * @return
     */
    public static String encrypt(String content, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), algorithm);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, offset);
            Cipher cipher = Cipher.getInstance(transformation);
            byte[] byteContent = content.getBytes(charset);
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] result = cipher.doFinal(byteContent);
            // 加密
            return  Base64.byteArrayToBase64(result);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }
    /**
     * AES（256）解密
     *
     * @param content 待解密内容
     * @param key     解密密钥
     * @return 解密之后
     * @throws Exception
     */
    public static String decrypt(String content, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), algorithm);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, offset);
            Cipher cipher = Cipher.getInstance(transformation);
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] result = cipher.doFinal(Base64.base64ToByteArray(content));
            // 解密
            return new String(result,"utf-8");
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }
    public static void main(String[] args)  {
        String s = "hello world";
        // 加密
        System.out.println("加密前：" + s);
        String encryptResultStr = encrypt(s);
        System.out.println("加密后：" + encryptResultStr);
        // 解密
        System.out.println("解密后：" + decrypt(encryptResultStr));
    }
}
