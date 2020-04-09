package edu.devplat.test;

import edu.devplat.common.security.Digests;
import edu.devplat.common.utils.Encodes;
import edu.devplat.common.utils.PasswordUtils;

import static edu.devplat.sys.service.SystemService.*;

public class TestPassword {

    public static void main(String[] args) {
        System.out.println(entryptPassword("admin"));
    }

    public static String entryptPassword(String plainPassword) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Digests.generateSalt(PasswordUtils.SALT_SIZE);
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, PasswordUtils.HASH_INTERATIONS);
        return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
    }
}


