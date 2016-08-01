
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
    String User = "Guest";
    String Pass = null;
    int Key = 1;
    
    private boolean ProtocolC(){
        API.Connection(new IP("136.167.171.151",6789),"Main");
        API.CreateListener(6789,"", null);
        
        API.CreateListenerAction("Main","ProtocolC",
                new FuncStore("MainConnectionProtocol"){
            @Override
            void Run(String Text){
                Client.Last = Text;
                ConnectionProtocolAssist(Text);
            }
        });
        return false;
    }
    
    private void EnSend(String Socket,String Message){
        API.Send(Socket,EnDe.Encrypt(Message,Key));
    }
    
    private void ConnectionProtocolAssist(String K){
        Client.Last = K;
        
        switch(K){
            
            case "::OK\n":
                API.Send("Main","Encrypt?");
                break;
            
            case "::Connected\n":
                EnSend("Main", "Handled.");
                break;
            case "Information?":
                EnSend("Main",User);
            default:
                Key = Integer.parseInt(K.substring(3, K.length() - 1));
                break;
        }
    }
    
    public static void main(String[] args) {
        Client Chat = new Client();
        Chat.EnDe = new Crypt();
        String K = Chat.EnDe.Decrypt(Chat.EnDe.Encrypt("StraightUpCancer", 200), 200);
        System.out.println("@Done" + K + "-");
    }
}
