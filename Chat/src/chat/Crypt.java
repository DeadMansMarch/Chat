
package chat;

/**
 *
 * @author Liam Pierce
 */

public class Crypt {
    
    public String pureMessage(String Text){
        return Text.substring(3, Text.length() - 1);
    }
    
    public String Encrypt(String Text,int Key){
        String En = "";
        for (Character K: Text.toCharArray()){
            En = En + "-";
            En = En + (((int) K) * Key);
        }
        System.out.println("Encrypted as :"  + En);
        return En;
    }
    
    public String Decrypt(String Encrypted,int Key){
        String De = "";
        for (String k: Encrypted.split("-")){
            if (!k.equals("")){
                De = De + (char) (Integer.parseInt(k) / Key);
            }
           
        }
        return De;
    }
    
    public Crypt(){
        
    }
}
