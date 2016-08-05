package chat;

/**
 * @author Liam Pierce
 *
 */
 
/*
 * Original Author: Liam Pierce
 *
 */
 
public class Crypt {
    static String IV = "AAAAAAAAAAAAAAAA";
    
    //Encode messages using AES
    private String Encryptor(String Text,String Key) throws Exception{
        //Original Code:
        String En = "";
        for (Character K: Text.toCharArray()){
            En = En + "-";
            En = En + (((int) K + 1) * Integer.parseInt(Key));
        }
        return En;
        
    }
   
    public String Encrypt(String Text, String Key){
        try{
            return Encryptor(Text,Key);
        }catch(Exception E){
            E.printStackTrace();
            return E.toString();
        }
    }
   
    //Decrypts using AES
    private String Decryptor(String Encrypted,String Key) throws Exception{
        //Original Code:

        String De = "";
        for (String k: Encrypted.split("-")){
            if (!k.equals("")){
                De = De + (char) ((Integer.parseInt(k) - 1) / Integer.parseInt(Key));
            }
           
        }
       

       return De;
    }
    
    
    public String Decrypt(String Text, String Key){
        try{
            return Decryptor(Text,Key);
        }catch(Exception E){
            E.printStackTrace();
            return E.toString();
        }
    }
    
    public static String makeKey(){
        return Integer.toString(Server.RandCreator.nextInt(500));
    }
    
    
    public Crypt(){
       
    }
}