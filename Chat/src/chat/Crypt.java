package chat;

/**
 *
 * @author Liam Pierce
 */

public class Crypt {
    
    //Encode messages using ascii characters and a common key.
    public String Encrypt(String Text,int Key){
        String En = "";
        for (Character K: Text.toCharArray()){
            En = En + "-";
            En = En + (((int) K + 1) * Key);
        }
        return En;
    }
    
    //Decrypts using common key.
    public String Decrypt(String Encrypted,int Key){
        String De = "";
        for (String k: Encrypted.split("-")){
            if (!k.equals("")){
                De = De + (char) ((Integer.parseInt(k) - 1) / Key);
            }
           
        }
        return De;
    }
    public Crypt(){
        
    }
}