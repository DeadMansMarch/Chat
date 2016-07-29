
/* @Error: 1 - HostNotFoundException;
*/
package chat;

/**
 *
 * @author Liam Pierce
 */
public class Client{
    static String Last;
    TCPApi API = new TCPApi();
    Crypt EnDe;
    
    int Key = 0;
    
    IP Server = new IP("136.167.171.151",6789);
    
    public boolean ProtocolC(){
        System.out.println("Attempting connection to server...");
        API.Connection(Server,"Main");
        System.out.println("Connection Init.");
        API.Send("Main","Connect?");
        
        API.CreateListener(6789,"Main", null);
                
        API.CreateListenerAction("Main","ProtocolC",
                new FuncStore("MainConnectionProtocol"){
            @Override
            void Run(String Text){
                Client.Last = Text;
                System.out.println(Text);
                System.out.println("Text:" + Text);
                ConnectionProtocolAssist(Text);
            }
        });
        
        
        System.out.println("Working...");
        
        
        
        
        return false;
    }
    
    private void ConnectionProtocolAssist(String K){
        Client.Last = K;
        System.out.println(K);
        switch(K){
            
            case "::OK":
                API.Send("Main","Encrypt?");
                break;
            
            case "::Connected":
                API.Send("Main", K);
                break;
            default:
                String Key = K.substring(1, K.length());
                System.out.println(Key);
                API.Send("Main","EncryptStart@Connect");
                break;
        }
    }
    
    public void EnSend(String Connection,String Text){
        API.Send(Connection,EnDe.Encrypt(Text, Key));
    }
    /*
    public static void main(String[] args) {
        Client Chat = new Client();
        Chat.EnDe = new Crypt();
        Chat.ProtocolC();
    }
*/
    public Client(){
        
    }
}