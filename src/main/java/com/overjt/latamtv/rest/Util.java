package com.overjt.latamtv.rest;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Util {
    static char[] f10625d = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    public static int f10626e = 2147264;
    public static int f10627f = 1152351091;
    public static final String f10628g = new C3907a().toString();
    public static final String f10629h = new C3908b().toString();
    private IvParameterSpec f10630a = new IvParameterSpec(f10628g.getBytes());
    private SecretKeySpec f10631b = new SecretKeySpec(f10629h.getBytes(), "AES");
    private Cipher f10632c;

    static class C3907a {
        int f10633a;

        public String toString() {
            this.f10633a = 1131263053;
            this.f10633a = -922422543;
            this.f10633a = -1631007395;
            this.f10633a = Util.f10626e;
            this.f10633a = -797115685;
            this.f10633a = -206234162;
            this.f10633a = -1569622157;
            this.f10633a = -703419504;
            this.f10633a = -1467434979;
            this.f10633a = -1470088198;
            this.f10633a = 1532560322;
            this.f10633a = 712015029;
            this.f10633a = 975517844;
            this.f10633a = -1313019075;
            this.f10633a = Util.f10626e;
            this.f10633a = -1162234098;
            int i = this.f10633a;
            return new String(new byte[] { (byte) (i >>> 19), (byte) (i >>> 9), (byte) (i >>> 23), (byte) (i >>> 10),
                    (byte) (i >>> 15), (byte) (i >>> 16), (byte) (i >>> 21), (byte) (i >>> 21), (byte) (i >>> 12),
                    (byte) (i >>> 7) });
        }
    }

    static class C3908b {
        int f10634a;

        public String toString() {
            this.f10634a = 1131263053;
            this.f10634a = -922422543;
            this.f10634a = -1631007395;
            this.f10634a = Util.f10627f;
            this.f10634a = -797115685;
            this.f10634a = -206234162;
            this.f10634a = -1569622157;
            this.f10634a = -703419504;
            this.f10634a = -1467434979;
            this.f10634a = -1470088198;
            this.f10634a = 1532560322;
            this.f10634a = 712015029;
            this.f10634a = 975517844;
            this.f10634a = -1313019075;
            this.f10634a = Util.f10627f;
            this.f10634a = -1162234098;
            int i = this.f10634a;
            return new String(new byte[] { (byte) (i >>> 19), (byte) (i >>> 9), (byte) (i >>> 23), (byte) (i >>> 10),
                    (byte) (i >>> 15), (byte) (i >>> 16), (byte) (i >>> 21), (byte) (i >>> 21), (byte) (i >>> 18),
                    (byte) (i >>> 7) });
        }
    }

    public Util() {
        try {
            this.f10632c = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e2) {
            e2.printStackTrace();
        }
    }

    public static String m16372c(byte[] bArr) {
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            int i2 = i * 2;
            char[] cArr2 = f10625d;
            cArr[i2] = cArr2[(bArr[i] & 240) >>> 4];
            cArr[i2 + 1] = cArr2[bArr[i] & 15];
        }
        return new String(cArr);
    }

    public static byte[] m16373f(String str) {
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

    private static String m16374g(String str) {
        int length = 16 - (str.length() % 16);
        for (int i = 0; i < length; i++) {
            str = str + 0;
        }
        return str;
    }

    public byte[] mo14208d(String str) throws Exception {
        int i = 0;
        if (str == null || str.length() == 0) {
            throw new Exception("Empty string");
        }
        try {
            this.f10632c.init(2, this.f10631b, this.f10630a);
            byte[] doFinal = this.f10632c.doFinal(m16373f(str));
            if (doFinal.length > 0) {
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
            throw new Exception("[decrypt] " + e.getMessage());
        }
    }

    public byte[] mo14209e(String str) throws Exception {
        if (str == null || str.length() == 0) {
            throw new Exception("Empty string");
        }
        try {
            this.f10632c.init(1, this.f10631b, this.f10630a);
            return this.f10632c.doFinal(m16374g(str).getBytes());
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }
    }
}