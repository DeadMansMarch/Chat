
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
    
    //Main connection protocol.
    public boolean protocolC(){
        
        API.Connection(Server,"Main");
        
        API.Send("Main","Connect?");
        
        API.CreateListener(6789,"Main", new FuncStore("MainConnectionProtocol"){
            @Override
            void Run(String Text){
                connectionProtocolAssist(Text);
            }
        });
        return false;
    }
    
    //Intercept for connection protocol.
    private void connectionProtocolAssist(String K){
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
                if (Key != 0 && EnDe.Decrypt(K.substring(2), Key).equals("SessionStart")){
                    //
                    API.RemoveListenerAction("Main","Main_Listener");
                    API.CreateListenerAction("Main", "Communication", new FuncStore("Connection"){
                        @Override
                        public void Run(String Text){
                            String DeMessage = EnDe.Decrypt(Text.substring(2), Key);
                            String ID[] = DeMessage.split(":");
                            
                            if (ID[0].equals("IsName")){
                                MainUI.changeName(ID[1],ID[2]);
                            }else if (ID[0].equals("ConnectionSet")){
                                for (String IP : ID){
                                    if (!IP.equals("ConnectionSet")){
                                        MainUI.ipList(IP);
                                    }
                                }
                            }else if (ID[0].equals("AddName")){
                                MainUI.ipList(ID[1]);
                            }
                            MainUI.addMessage(ID[0],ID[1]);
                        }
                    });
                    
                }else{
                    Key = Integer.parseInt(K.substring(2));
                    enSend("Main","@Start");
                }
                break;
        }
    }
    
    public static void ChangeConnectionset(String NameOrIP){
        enSend("Main","Connectionset:" + NameOrIP);
    }
    
    public static void DefaultConnectionset(){
        enSend("Main","DefaultConnectionset");
    }
    
    //Sending encrypted messages.
    public static void enSend(String Connection,String Text){
        API.Send(Connection,EnDe.Encrypt(Text, Key));
    }
    
    //Main method.
    /*
    public static void main(String[] args) {
        Thread K = new Thread(new Runnable(){
            @Override
            public void run(){
                MainUI = new UI();
            }
        },"UI Cancer");
        
        K.start();
        API.Log("Attempting connection to server...");
        Client Chat = new Client();
        Client.EnDe = new Crypt();
        Chat.protocolC();
    }
    */
    
    public Client(){
        
    }
}