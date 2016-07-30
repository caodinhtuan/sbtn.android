package onworld.sbtn.security;

import android.util.Base64;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
	
    static byte salt[] = {54, (byte) 247, 33, 49, (byte) 153, 76, 17, (byte) 181};
    static byte pass[] = {83, 110, 52, 57, 98, 118, 99, 107, 33, 64};

    public static String encrypt(String json) {
        // SHA pass
    	String result = "";
    	try{
	        MessageDigest dig = MessageDigest.getInstance("SHA-256");
	        dig.update(pass);
	        byte[] shaPass = dig.digest();
	        
	        // Derive key
	        Rfc2898DeriveBytes rfc2898 = new Rfc2898DeriveBytes(shaPass, salt, 1002);
			byte[] key = rfc2898.getBytes(32);
	        byte[] iv = rfc2898.getBytes(16);
	
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
	        
	        byte[] c = cipher.doFinal(json.getBytes("UTF-8"));
	        result = new String(Base64.encode(c, 0));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
        return result;
    }
    
    public static String encryptUTF8(String json) {
        // SHA pass
    	String result = "";
    	try{
	        MessageDigest dig = MessageDigest.getInstance("SHA-256");
	        dig.update(pass);
	        byte[] shaPass = dig.digest();
	        
	        // Derive key
	        Rfc2898DeriveBytes rfc2898 = new Rfc2898DeriveBytes(shaPass, salt, 1002);
			byte[] key = rfc2898.getBytes(32);
	        byte[] iv = rfc2898.getBytes(16);
	
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
	        
	        byte[] c = cipher.doFinal(json.getBytes("UTF-8"));
	        result = new String(c, "UTF-8");
//	        result = new String(Base64.encode(c, 0));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
        return result;
    }

	public static String getEncryptedHMAC(String secret, String message){
		if(secret == null || secret.equals(""))
			return "";

        String hash = "";
        Mac sha256_HMAC = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(Charset.forName("UTF8")), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            hash = Base64.encodeToString(sha256_HMAC.doFinal(message.getBytes(Charset.forName("UTF8"))), Base64.NO_WRAP);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
		catch (Exception ex) {
			ex.printStackTrace();
		}

        return hash;
    }
}

