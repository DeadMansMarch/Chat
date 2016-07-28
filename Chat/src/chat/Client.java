
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
        
        System.out.println("Working?");
        API.CreateListener(6789,"Main_Listener", null);
        System.out.println("Working...");
        
        API.CreateListenerAction("Main","ProtocolC",
                new FuncStore("MainConnectionProtocol"){
            @Override
            void Run(String Text){
                Client.Last = Text;
                System.out.println(Text);
                ConnectionProtocolAssist(Text);
            }
        });
        
        
        return false;
    }
    
    private void ConnectionProtocolAssist(String K){
        Client.Last = K;
        System.out.println("Connecting...");
        switch(K){
            
            case "::OK\n":
                API.Send("Main","Encrypt?");
                break;
            
            case "::Connected\n":
                API.Send("Main", K);
                break;
            default:
                int Key = Integer.parseInt(K.substring(3, K.length() - 1));
                API.Send("Main","EncryptStart@Connect");
                break; 
        }
    }
    
    public void EnSend(String Connection,String Text){
        API.Send(Connection,EnDe.Encrypt(Text, Key));
    }
    
    public static void main(String[] args) {
        Client Chat = new Client();
        Chat.EnDe = new Crypt();
        Chat.ProtocolC();
    }
}

