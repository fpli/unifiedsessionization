package com.ebay.epic.soj.common.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SOJSampleHash {
    public static final String MD5_ALGORITHM = "MD5";
    private static MessageDigest msgDigest;
    
    static {
        try {
            msgDigest = MessageDigest.getInstance(MD5_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Integer sampleHash(String guid, int mod_value) {
        // checking NULL value for parameter:guid and  parameter:mod_value
        if (guid == null || mod_value <= 0) {
            return null;
        }

        byte[] result = msgDigest.digest(guid.getBytes());
        byte[] preserved = new byte[8];
        for (int i = 0; i < 8; i++) {
            preserved[i] = result[7 - i];
        }

        BigInteger bigValue = new BigInteger(1, preserved);
        //msgDigest.reset();
        return (bigValue.mod(BigInteger.valueOf(mod_value)).intValue());
        
    }
}
