package com.ms.merchant.utils;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AESCipher {
    public static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";

    public static SecureRandom random = new SecureRandom();

    /**
     * 获取随机16位key，key必须要是10的整数倍，否则会出错
     */
    public static String getRandom(int length) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // 输出字母还是数字
            boolean isChar = (random.nextInt(2) % 2 == 0);
            // 字符串
            if (isChar) {
                // 取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                ret.append((char) (choice + random.nextInt(26)));
            } else { // 数字
                ret.append(random.nextInt(10));
            }
        }
        return ret.toString();
    }

    /**
     * 加密方法，使用key充当向量iv，增加加密算法的强度
     * 更加安全
     *
     * @param key 密钥
     * @param raw 需要加密的内容
     * @return
     */
    public static String encrypt(byte[] key, String raw) throws Exception {
        // 第一次加密
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        byte[] enCodeFormat = secretKey.getEncoded();
        // 获取二次加密的key
        SecretKeySpec secondSecretKey = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        // 向量iv，增加加密算法的强度
        IvParameterSpec iv = new IvParameterSpec(key);
        // 初始化加密器
        cipher.init(Cipher.ENCRYPT_MODE, secondSecretKey, iv);
        // 加密
        byte[] result = cipher.doFinal(raw.getBytes());
        // 进行base64编码
        return Base64Utils.encodeToString(result);
    }

    /**
     * 解密方法，使用key充当向量iv，增加加密算法的强度
     *
     * @param key 密钥
     * @param enc 待解密内容
     * @return
     */
    public static String decrypt(byte[] key, String enc) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        byte[] enCodeFormat = secretKey.getEncoded();
        // 二次加密
        SecretKeySpec secondSecretKey = new SecretKeySpec(enCodeFormat, "AES");

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(key);
        // 初始化
        cipher.init(Cipher.DECRYPT_MODE, secondSecretKey, iv);
        // 首先进行base64解码
        byte[] bytes = Base64Utils.decodeFromString(enc);
        // 解密
        byte[] result = cipher.doFinal(bytes);
        return new String(result);
    }

    public static void main(String[] args) throws Exception {

        //客户端代码
        String text = "611f9894e10a81bc5cc9fc4e2288ca00ea8d277416b6f22e941febfc4eb2c130";
        //随机生成16位aes密钥，也可以自己指定16位
        byte[] aesKey = getRandom(16).getBytes();

        String encryptText = AESCipher.encrypt(aesKey, text);
        System.out.println("加密后:\n" + encryptText);
        String decryptText = AESCipher.decrypt(aesKey, encryptText);
        System.out.println("解密后:\n" + decryptText);
    }


}
