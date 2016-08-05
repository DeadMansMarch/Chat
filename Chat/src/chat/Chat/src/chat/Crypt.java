package chat;
 
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
 
/**
 * @author Liam Pierce
 * @author Gregory Conrad
 */
 
/*
 * Original Author: Liam Pierce
 * Improved By: Gregory Conrad
 */
 
public class Crypt {
    static String IV = "AAAAAAAAAAAAAAAA";
    
    //Encode messages using AES
    private String Encryptor(String Text,int Key) throws Exception{
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
        String newKey = Integer.toString(Key);
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(newKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
        byte[] encrypted = cipher.doFinal(Text.getBytes("UTF-8"));
        return new String(encrypted, "UTF-8");
    }
   
    public String Encrypt(String Text, int Key){
        try{
            return Encryptor(Text,Key);
        }catch(Exception E){
            return E.toString();
        }
    }
   
    //Decrypts using AES
    private String Decryptor(String Encrypted,int Key) throws Exception{
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
        String newKey = Integer.toString(Key);
        byte[] EncryptedBytes = Encrypted.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(newKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
        return new String(cipher.doFinal(EncryptedBytes),"UTF-8");
       
    }
    
    
    public String Decrypt(String Text, int Key){
        try{
            return Decryptor(Text,Key);
        }catch(Exception E){
            return E.toString();
        }
    }
    
    
    public Crypt(){
       
    }
}