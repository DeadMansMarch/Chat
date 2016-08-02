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
            En = En + (((int) K + Math.ceil(Key / (Key % 40))) * Key);
        }
        System.out.println("Encrypted as :"  + En);
        return En;
    }
    
    //Decrypts using common key.
    public String Decrypt(String Encrypted,int Key){
        String De = "";
        for (String k: Encrypted.split("-")){
            if (!k.equals("")){
                De = De + (char) ((Integer.parseInt(k) - Math.ceil(Key / (Key % 40))) / Key);
            }
           
        }
        return De;
    }
    
    public Crypt(){
        
    }
}