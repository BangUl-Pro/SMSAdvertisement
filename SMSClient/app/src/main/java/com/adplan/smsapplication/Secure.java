package com.adplan.smsapplication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ironFactory on 2015-08-03.
 */
public class Secure {

    private static final String TAG = "Secure";
    private static String c = "!\"#$%&(){}@`*:+;-.<>,^~|'[]";
    private static int cond = 0;

    public static final int NO_SPECIAL_LETTER = 0;
    public static final int NOT_ENOUGH_LETTER = 1;
    public static final int SUCCESS = 2;



    /**
     * TODO: 특수문자 검사
     * */
    public static int checkPasswordSecureLevel(String pw) {
        cond = 0;
        if (pw.length() < 7)
            return NOT_ENOUGH_LETTER;

        for (int i = 0; i < pw.length(); i++) {
            char letter = pw.charAt(i);
            for (int j = 0; j < c.length(); j++) {
                char specialLetter = c.charAt(j);
                if (letter == specialLetter) {
                    cond++;
                }
            }
        }

        if (cond == 0)
            return NO_SPECIAL_LETTER;
        return SUCCESS;
    }


    public static String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }



    public static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }


    /**
     * TODO: SHA-256 암호화
     * */
    public static String Sha256Encrypt(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());
            byte byteData[] = md.digest();


            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1)
                    hexString.append("0");
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {

        }
        return null;
    }
}
