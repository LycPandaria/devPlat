package edu.devplat.test;

import edu.devplat.common.security.Digests;
import edu.devplat.common.utils.Encodes;
import edu.devplat.common.utils.PasswordUtils;

import static edu.devplat.sys.service.SystemService.*;

public class TestPassword {

    public static void main(String[] args) {
        //System.out.println(entryptPassword("admin"));

        String password = "111111111111a@@__";
        //System.out.println(password.matches("\\d+"));
        //System.out.println(password.matches("[A-z]+"));
        System.out.println(PasswordUtils.isSimplePwd(password));
    }

    public static String entryptPassword(String plainPassword) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Digests.generateSalt(PasswordUtils.SALT_SIZE);
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, PasswordUtils.HASH_INTERATIONS);
        return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
    }
}


