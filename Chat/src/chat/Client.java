
/* @Error: 1 - HostNotFoundException;
*/
package chat;

/**
 *
 * @author Liam Pierce
 */
public class Client{
    static String Last;
    static TCPApi API = new TCPApi();
    static Crypt EnDe;
    static int Key = 0;
    static UI UI;
    
    final private IP Server = new IP("136.167.171.151",6789);
    
    public boolean ProtocolC(){
        System.out.println("Attempting connection to server...");
        API.Connection(Server,"Main");
        System.out.println("Connection Init.");
        API.Send("Main","Connect?");
        
        API.CreateListenerAction("Main","ProtocolC",
                new FuncStore("MainConnectionProtocol"){
            @Override
            void Run(String Text){
                Client.Last = Text;
                System.out.println(Text);
                System.out.println("Text:" + Text + "!!!");
                ConnectionProtocolAssist(Text);
            }
        });
        
        API.CreateListener(6789,"Main", null);
                
        
        
        
        System.out.println("Working...");
        
        
        
        
        return false;
    }
    
    private void ConnectionProtocolAssist(String K){
        Client.Last = K;
        System.out.println(K);
        switch(K){
            
            case "::OK":
                System.out.println("OK");
                API.Send("Main","Encrypt?");
                break;
            
            case "::Connected":
                API.Send("Main", K);
                break;
            default:
                System.out.println("WHY");
                if (Key != 0 && EnDe.Decrypt(K.substring(2), Key).equals("SessionStart")){
                    EnSend("Main","SessionStart");
                    API.RemoveListenerAction("Main","ProtocolC");
                    API.CreateListenerAction("Main","Communication",new FuncStore("Connection"){
                        @Override
                        public void Run(String Message){
                            String Demessage = EnDe.Decrypt(Message.substring(2), Key);
                            String[] SplitMessage = Demessage.split(":");
                            Client.UI.appendText(SplitMessage[0],SplitMessage[1]);
                        }
                    });
                }else{
                    Key = Integer.parseInt(K.substring(2));
                    System.out.println(Key);
                    EnSend("Main","@Start");
                }
                break;
        }
    }
    
    static void EnSend(String Connection,String Text){
        System.out.println(Key);
        API.Send(Connection,EnDe.Encrypt(Text, Key));
    }
    
    public static void main(String[] args) {
        Client Chat = new Client();
        
        Thread K = new Thread(new Runnable(){
            @Override
            public void run(){
                Client.UI = new UI();
            }
        },"UI Cancer");
        
        K.start();
        
        
        Client.EnDe = new Crypt();
        Chat.ProtocolC();
    }
    
    public Client(){
        
    }
}