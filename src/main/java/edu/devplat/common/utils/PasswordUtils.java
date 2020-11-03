package edu.devplat.common.utils;

import edu.devplat.common.security.Digests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 密码相关工具类
 * @author liyc
 * @date 2020-04-09
 */
public class PasswordUtils {

    private static Logger logger = LoggerFactory.getLogger(PasswordUtils.class);

    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_INTERATIONS = 1024;
    public static final int SALT_SIZE = 8;

    /**
     * 验证密码
     * @param plainPassword 明文密码
     * @param password 密文密码
     * @return 验证成功返回true
     */
    public static boolean validatePassword(String plainPassword, String password) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Encodes.decodeHex(password.substring(0,16));
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
        return password.equals(Encodes.encodeHex(salt)+ Encodes.encodeHex(hashPassword));
    }

    /**
     * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
     */
    public static String entryptPassword(String plainPassword) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
        return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
    }

    /**
     * 判断密码是否为简单密码
     * 如果密码小于8位并且全为数字或全为字母则会被判定为简单密码
     * @param password
     * @return
     */
    public static boolean isSimplePwd(String password){
        if(password.length() < 8)
            return true;
        // 密码不能仅为数字或字母
        return password.matches("\\d+") || password.matches("[a-zA-Z]+");

    }
}
