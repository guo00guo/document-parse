package com.mooctest;

/**
 * @author guochao
 * @date 2020-06-21 15:16
 */
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 2018年3月19日
 **/
public class MD5_Base64 {

    public static void main(String[] args)  {


        String text ="123456";
        System.out.println("md5:"+MD5_Base64.EncryptionByMD5(text));
        System.out.println("Base64:"+MD5_Base64.EncryptionByBase64(text));
        System.out.println("md5_Base64:"+MD5_Base64.EncryptionByMD5AndBase64(text)+"\n");

        String text2 =MD5_Base64.EncryptionByBase64(text);//MTIzNDU2
        System.out.println(MD5_Base64.decryptBase64(text2)+"\n");

        String text3 =MD5_Base64.EncryptionByMD5AndBase64(text);//4QrcOUm6Wau+VuBX8g+IPg==
        System.out.println(MD5_Base64.decryptBase64To(text3)+"\n");

        // 解密
        String decryptText = "1f2ZfP0LAS0fStGRke8ABg==";
        System.out.println(MD5_Base64.decryptBase64(decryptText)+"\n");

    }

    private final static char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    private static char[] base64EncodeChars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

    private static byte[] base64DecodeChars = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55,
            56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
            21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46,
            47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };
    /**
     * MD5加密
     * @param text
     * @return String
     */
    public static String EncryptionByMD5(String text){
        try {
            byte[] btInput = text.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    /**
     * MD5加密
     * @param text
     * @return byte[]
     */
    public static byte[] EncryptionByMD5T(String text){
        try {
            byte[] btInput = text.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            return md;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    /**
     * Base64加密
     * @param text
     * @return String
     */
    public static String EncryptionByBase64(String text) {
        byte[] data=text.getBytes();
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;

        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 0x3f]);
        }
        return sb.toString();
    }

    /**
     * Base64加密
     * @param data
     * @return String
     */
    public static String EncryptionByBase64(byte[] data) {
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;

        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 0x3f]);
        }
        return sb.toString();
    }


    /**
     * 先MD5加密，再base64加密
     * @param text
     * @return String
     */
    public static String EncryptionByMD5AndBase64(String text){

        return EncryptionByBase64(EncryptionByMD5T(text));
    }

    /**
     * Base64 解密
     * @return String
     */
    public static String decryptBase64(String text) {
        byte[] data = text.getBytes();
        int len = data.length;
        ByteArrayOutputStream buf = new ByteArrayOutputStream(len);
        int i = 0;
        int b1, b2, b3, b4;

        while (i < len)
        {

            /* b1 */
            do
            {
                b1 = base64DecodeChars[data[i++]];
            }
            while (i < len && b1 == -1);
            if (b1 == -1)
            {
                break;
            }

            /* b2 */
            do
            {
                b2 = base64DecodeChars[data[i++]];
            }
            while (i < len && b2 == -1);
            if (b2 == -1)
            {
                break;
            }
            buf.write((int) ((b1 << 2) | ((b2 & 0x30) >>> 4)));

            /* b3 */
            do
            {
                b3 = data[i++];
                if (b3 == 61)
                {
                    return new String(buf.toByteArray());
                }
                b3 = base64DecodeChars[b3];
            }
            while (i < len && b3 == -1);
            if (b3 == -1)
            {
                break;
            }
            buf.write((int) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));

            /* b4 */
            do
            {
                b4 = data[i++];
                if (b4 == 61)
                {
                    return new String(buf.toByteArray());
                }
                b4 = base64DecodeChars[b4];
            }
            while (i < len && b4 == -1);
            if (b4 == -1)
            {
                break;
            }
            buf.write((int) (((b3 & 0x03) << 6) | b4));

        }

        return new String(buf.toByteArray());

    }

    /**
     * Base64 解密到MD5
     * @return String
     */
    public static String decryptBase64To(String text) {
        byte[] data = text.getBytes();
        int len = data.length;
        ByteArrayOutputStream buf = new ByteArrayOutputStream(len);
        int i = 0;
        int b1, b2, b3, b4;

        while (i < len)
        {

            /* b1 */
            do
            {
                b1 = base64DecodeChars[data[i++]];
            }
            while (i < len && b1 == -1);
            if (b1 == -1)
            {
                break;
            }

            /* b2 */
            do
            {
                b2 = base64DecodeChars[data[i++]];
            }
            while (i < len && b2 == -1);
            if (b2 == -1)
            {
                break;
            }
            buf.write((int) ((b1 << 2) | ((b2 & 0x30) >>> 4)));

            /* b3 */
            do
            {
                b3 = data[i++];
                if (b3 == 61)
                {
                    byte[] br = buf.toByteArray();
                    // 把密文转换成十六进制的字符串形式
                    int p = br.length;
                    char str[] = new char[p * 2];
                    int k = 0;
                    for (int r = 0; r < p; r++) {
                        byte byte0 = br[r];
                        str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                        str[k++] = hexDigits[byte0 & 0xf];
                    }
                    return new String(str);
                }
                b3 = base64DecodeChars[b3];
            }
            while (i < len && b3 == -1);
            if (b3 == -1)
            {
                break;
            }
            buf.write((int) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));

            /* b4 */
            do
            {
                b4 = data[i++];
                if (b4 == 61)
                {
                    byte[] br = buf.toByteArray();
                    // 把密文转换成十六进制的字符串形式
                    int p = br.length;
                    char str[] = new char[p * 2];
                    int k = 0;
                    for (int r = 0; r < p; r++) {
                        byte byte0 = br[r];
                        str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                        str[k++] = hexDigits[byte0 & 0xf];
                    }
                    return new String(str);
                }
                b4 = base64DecodeChars[b4];
            }
            while (i < len && b4 == -1);
            if (b4 == -1)
            {
                break;
            }
            buf.write((int) (((b3 & 0x03) << 6) | b4));

        }
        byte[] br = buf.toByteArray();
        // 把密文转换成十六进制的字符串形式
        int p = br.length;
        char str[] = new char[p * 2];
        int k = 0;
        for (int r = 0; r < p; r++) {
            byte byte0 = br[r];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

    /**
     * 网络传输中文UTF-8加密，防止乱码
     * @param text
     * @return String
     */
    public static String netEnCoderUTF8(String text){
        return netEnCoder(text,"UTF-8");
    }

    /***
     * 网络传输中文UTF-8解码
     * @param text
     * @return String
     */
    public static String netEDeCoderUTF8(String text){
        return netDecoder(text,"UTF-8");
    }


    /**
     *
     * @param text
     * @param charsetName 字符集
     * @return String
     */
    public static String netEnCoder(String text,String charsetName){
        String result=null;
        try {
            result= URLEncoder.encode(text,charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param text
     * @param charsetName 字符集
     * @return String
     */
    public static String netDecoder(String text,String charsetName){
        String result=null;
        try {
            result= URLDecoder.decode(text,charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
