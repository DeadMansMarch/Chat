
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
        API.CreateListener(6789,"", null);
        API.Connection(Server,"Main");
        API.CreateListenerAction("Main","ProtocolC",
                new FuncStore("MainConnectionProtocol"){
            @Override
            void Run(String Text){
                Client.Last = Text;
                ConnectionProtocolAssist(Text);
            }
        });
        API.Send("Main","Connect?");
        
        return false;
    }
    
    private void ConnectionProtocolAssist(String K){
        Client.Last = K;
        
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

