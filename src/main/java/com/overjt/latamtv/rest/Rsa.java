package com.overjt.latamtv.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
// import java.util.HashMap;
// import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Rsa {

    private static Cipher f10698d;

    // private static final Map store = new HashMap();

    public Rsa() {
    }

    public static String m16415a(String str) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : digest) {
                stringBuffer.append(Integer.toHexString((b & 255) | 256).substring(1, 3));
            }
            return stringBuffer.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String EncryptStr(String str) {
        StringBuilder sb = new StringBuilder();
        try {
            PublicKey g = getPublicKey("data.dat");
            Cipher instance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            f10698d = instance;
            instance.init(1, g);
            sb.append(Base64.getEncoder().encodeToString(f10698d.doFinal(str.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException encrypt");
        } catch (NoSuchPaddingException e2) {
            System.out.println("NoSuchPaddingException encrypt");
        } catch (BadPaddingException e3) {
            System.out.println("BadPaddingException encrypt");
        } catch (IllegalBlockSizeException e4) {
            System.out.println("IllegalBlockSizeException encrypt");
        } catch (InvalidKeyException e5) {
            System.out.println("InvalidKeyException encrypt");
        } catch (Exception e6) {
            System.out.println("Exception encrypt");
        }
        return sb.toString();
    }

    private static PublicKey getPublicKey(String str)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        // PublicKey publicKey = (PublicKey) store.get("public_key");
        // if (publicKey != null) {
        //     return publicKey;
        // }
        File initialFile = new File(str);
        InputStream open = new FileInputStream(initialFile);
        byte[] bArr = new byte[open.available()];
        open.read(bArr);
        open.close();
        PublicKey generatePublic = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bArr));
        // store.put("public_key", generatePublic);
        return generatePublic;
    }


}