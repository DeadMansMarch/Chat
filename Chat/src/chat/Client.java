

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
    static UI MainUI;
    
    final private IP Server = new IP("136.167.171.92",6789);
    
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
                    API.RemoveListenerAction("Main","ProtocolC");
                    API.CreateListenerAction("Main", "Communication", new FuncStore("Connection"){
                        @Override
                        public void Run(String Text){
                            String DeMessage = EnDe.Decrypt(Text.substring(2), Key);
                            System.out.println(DeMessage);
                            String ID[] = DeMessage.split(":");
                            MainUI.appendText(ID[0],ID[1]);
                            
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
        Thread K = new Thread(new Runnable(){
            @Override
            public void run(){
                MainUI = new UI();
            }
        },"UI Cancer");
        
        K.start();
        Client Chat = new Client();
        Client.EnDe = new Crypt();
        Chat.ProtocolC();
    }
    
    public Client(){
        
    }
}