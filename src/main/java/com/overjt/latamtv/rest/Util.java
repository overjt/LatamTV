package com.overjt.latamtv.rest;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Util {
    static char[] f10625d = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private IvParameterSpec ivParam = new IvParameterSpec(getKey1().getBytes());
    private SecretKeySpec secretKey = new SecretKeySpec(getKey2().getBytes(), "AES");
    private Cipher cipher;

    static String getKey1(){
        int i = -1162234098;
        return new String(new byte[] { (byte) (i >>> 19), (byte) (i >>> 9), (byte) (i >>> 23), (byte) (i >>> 10),
                (byte) (i >>> 15), (byte) (i >>> 16), (byte) (i >>> 21), (byte) (i >>> 21), (byte) (i >>> 12),
                (byte) (i >>> 7) });
    }

    static String getKey2(){
        int i = -1162234098;
        return new String(new byte[] { (byte) (i >>> 19), (byte) (i >>> 9), (byte) (i >>> 23), (byte) (i >>> 10),
            (byte) (i >>> 15), (byte) (i >>> 16), (byte) (i >>> 21), (byte) (i >>> 21), (byte) (i >>> 18),
            (byte) (i >>> 7) });
    }

    public Util() {
        try {
            this.cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e2) {
            e2.printStackTrace();
        }
    }

    public static String encodeStr(byte[] bArr) {
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            int i2 = i * 2;
            char[] cArr2 = f10625d;
            cArr[i2] = cArr2[(bArr[i] & 240) >>> 4];
            cArr[i2 + 1] = cArr2[bArr[i] & 15];
        }
        return new String(cArr);
    }

    public static byte[] decodeStr(String str) {
        byte[] bArr = null;
        if (str != null && str.length() >= 2) {
            int length = str.length() / 2;
            bArr = new byte[length];
            for (int i = 0; i < length; i++) {
                int i2 = i * 2;
                bArr[i] = (byte) Integer.parseInt(str.substring(i2, i2 + 2), 16);
            }
        }
        return bArr;
    }

    private static String completeString(String str) {
        int length = 16 - (str.length() % 16);
        for (int i = 0; i < length; i++) {
            str = str + "\00";
        }
        return str;
    }

    public byte[] Decrypt(String str) throws Exception {
        
        if (str == null || str.length() == 0) {
            throw new Exception("Empty string");
        }
        try {
            this.cipher.init(2, this.secretKey, this.ivParam);
            byte[] doFinal = this.cipher.doFinal(decodeStr(str));
            if (doFinal.length > 0) {
                int i = 0;
                for (int length = doFinal.length - 1; length >= 0; length--) {
                    if (doFinal[length] == 0) {
                        i++;
                    }
                }
                if (i > 0) {
                    byte[] bArr = new byte[(doFinal.length - i)];
                    System.arraycopy(doFinal, 0, bArr, 0, doFinal.length - i);
                    return bArr;
                }
            }
            return doFinal;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public byte[] Encrypt(String str) throws Exception {
        if (str == null || str.length() == 0) {
            throw new Exception("Empty string");
        }
        try {
            this.cipher.init(1, this.secretKey, this.ivParam);
            return this.cipher.doFinal(completeString(str).getBytes());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}