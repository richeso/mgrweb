package com.mapr.mgrweb.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HashUtils {

    public static final String SHA1 = "SHA-1";
    public static final String SHA256 = "SHA-256";
    public static final String SHA384 = "SHA-384";
    public static final String SHA512 = "SHA-512";

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String passwordToHash = "password";
        byte[] salt = getSalt();

        String securePassword = getPassword(passwordToHash, salt, SHA1);
        System.out.println("SHA-1=" + securePassword);

        securePassword = getPassword(passwordToHash, salt, SHA256);
        System.out.println("SHA-256=" + securePassword);

        securePassword = getPassword(passwordToHash, salt, SHA384);
        System.out.println("SHA-384=" + securePassword);

        securePassword = getPassword(passwordToHash, salt, SHA512);
        System.out.println("SHA-512=" + securePassword);
    }

    private static String getPassword(String passwordToHash, byte[] salt, String algo) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance(algo);
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
}
