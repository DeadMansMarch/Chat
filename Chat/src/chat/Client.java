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
    public static ServerLogin NewLogin;
    public static Thread RunUI;
    
    //Disconnects MainUI
    public static void Disconnect(){
        MainUI.dispose();
    }
    
    private IP Server;
    
    //Main connection protocol.
    public boolean protocolC(String Password){
        
        API.Connection(Server,"Main");
        
        API.Send("Main","Password:" + Password);
        
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
            
            case "::BadPass":
                NewLogin.dispose();
            case "::OK":
                RunUI.start();
                API.Send("Main","Encrypt?");
                break;
            case "::Connected":
                
                API.Send("Main", K);
                break;
            default:
                if (Key != 0 && EnDe.Decrypt(K.substring(2), Key).equals("SessionStart")){
                    
                    API.RemoveListenerAction("Main","Main_Listener");
                    API.CreateListenerAction("Main", "Communication", new FuncStore("Connection"){
                        @Override
                        public void Run(String Text){
                            String DeMessage = EnDe.Decrypt(Text.substring(2), Key);
                            String ID[] = DeMessage.split(":");
                            
                            if (ID[0].equals("IsName")){
                                System.out.println("??");
                                MainUI.changeName(ID[1],ID[2]);
                            }else if (ID[0].equals("ConnectionSet")){
                                System.out.println("Set");
                                MainUI.Update(ID);
                            }else if (ID[0].equals("AddName")){
                                //MainUI.ipList(ID[1]);
                            }else if(ID[0].equals("Rem")){
                                 MainUI.nameRemove(ID[1]);   
                            }else if(ID[0].substring(0,3).equals("|||")){
                                MainUI.addMessage(ID[0].substring(3),ID[1]);
                            }
                        }
                    });
                    
                }else{
                    Key = Integer.parseInt(K.substring(2));
                    enSend("Main","@Start");
                }
                break;
        }
    }
    
    //Sets the connectionset to NameOrIP.
    public static void ChangeConnectionset(String NameOrIP){
        enSend("Main","Connectionset:" + NameOrIP);
    }
    
    //Sets the clients connectionset to default.
    public static void DefaultConnectionset(){
        enSend("Main","DefaultConnectionset");
    }
    
    //Sending encrypted messages.
    public static void enSend(String Connection,String Text){
        API.Send(Connection,EnDe.Encrypt(Text, Key));
    }
    
    //Main method.
    
    public static void main(String[] args) {
        API.Log("Attempting connection to server...");

        RunUI = new Thread(new Runnable(){
            @Override
            public void run(){
                MainUI = new UI();
            }
        },"UI Cancer");
        
        Thread W = new Thread(new Runnable(){
            @Override
            public void run(){
                NewLogin = new ServerLogin();
            }
        },"UI Tumor");
        W.start();
        
        //This some trash.
        while (NewLogin == null){
            
        }
        
        NewLogin.login();
        String IP = NewLogin.getIP();
        String Password = NewLogin.getPassword();
        Client NewClient = new Client(IP,Password);
        Client.EnDe = new Crypt();
    }
    
    
    public Client(String IP,String Password){
         Server = new IP(IP,6789);
         protocolC(Password);
    }
}