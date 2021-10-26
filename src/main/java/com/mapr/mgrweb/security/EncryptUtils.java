package com.mapr.mgrweb.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * Java String Encryption Decryption Example
 * @author Ramesh Fadatare
 *
 */
public class EncryptUtils {

    private SecretKeySpec secretKey;
    private byte[] key;
    private final String ALGORITHM = "AES";

    public void prepareSecreteKey(String myKey) {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String strToEncrypt, String secret) {
        try {
            prepareSecreteKey(secret);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public String decrypt(String strToDecrypt, String secret) {
        try {
            prepareSecreteKey(secret);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    public static String generateSecret() {
        Key key;
        SecureRandom rand = new SecureRandom();
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256, rand);
            key = generator.generateKey();
            return new String(key.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /*
    public static void main(String[] args) {
        byte[] secret = generateSecret();
        String secretKey = new String(secret);
        String originalString = "javaguides";

        EncryptUtils enutil = new EncryptUtils();
        String encryptedString = enutil.encrypt(originalString, secretKey);
        String decryptedString = enutil.decrypt(encryptedString, secretKey);
        System.out.println("secret="+secretKey+"  SecretLength="+secretKey.length());
        System.out.println("original="+originalString);
        System.out.println("encrypted="+encryptedString);
        System.out.println("decrypted="+decryptedString);
    }

 */

}
