package chat;
 
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
 
/**
 * @author Liam Pierce
 * @author Gregory Conrad
 */
 
/*
 * Original Author: Liam Pierce
 * Improved By: Gregory Conrad
 * Improvement Fixed By: Liam Pierce
 */
 
public class Crypt {
    static String IV = "AAAAAAAAAAAAAAAA";
    
    private KeyGenerator keyGen;

    
    //Encode messages using AES
    private String Encryptor(String Text,String Key) throws Exception{
        //Original Code:
        /*
        String En = "";
        for (Character K: Text.toCharArray()){
            En = En + "-";
            En = En + (((int) K + 1) * Key);
        }
        return En;
        */
       
        //New Code:
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        cipher.init(Cipher.ENCRYPT_MODE, DecodeKey(Key),new IvParameterSpec(IV.getBytes("UTF-8")));
        byte[] encrypted = cipher.doFinal(Text.getBytes("UTF-8"));
        return new String(encrypted, "UTF-8");
    }
   
    public String Encrypt(String Text, String Key){
        try{
            return Encryptor(Text,Key);
        }catch(Exception E){
            return E.toString();
        }
    }
   
    //Decrypts using AES
    private String Decryptor(String Encrypted,String Key) throws Exception{
        //Original Code:
        /*
        String De = "";
        for (String k: Encrypted.split("-")){
            if (!k.equals("")){
                De = De + (char) ((Integer.parseInt(k) - 1) / Key);
            }
           
        }
        */
        
       
        //New Code:
        byte[] EncryptedBytes = Encrypted.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        cipher.init(Cipher.DECRYPT_MODE, DecodeKey(Key),new IvParameterSpec(IV.getBytes("UTF-8")));
        return new String(cipher.doFinal(EncryptedBytes),"UTF-8");
       
    }
    
    
    public String Decrypt(String Text, String Key){
        try{
            return Decryptor(Text,Key);
        }catch(Exception E){
            return E.toString();
        }
    }
    
    public String makeKey(){
        return Base64.getEncoder().encodeToString(keyGen.generateKey().getEncoded());
    }
    
    public SecretKey DecodeKey(String Key){
        byte[] Decode = Base64.getDecoder().decode(Key.getBytes());
        return new SecretKeySpec(Decode,0,Decode.length,"AES");
    }
    
    public Crypt(){
        try{
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
        }catch(Exception E){
            
        }
    }
}