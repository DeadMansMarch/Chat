package chat;

/**
 *
 * @author Liam Pierce
 */
public class Client{
    private String Serverpass;
    static TCPApi API = new TCPApi(false);
    static Crypt EnDe;
    static String Key = "";
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
        this.Serverpass = Password;
        API.connection(Server,"Main");
        
        API.send("Main","Connect?");
        
        API.createListener(6789,"Main", new FuncStore("MainConnectionProtocol"){
            @Override
            void Run(String Text){
                connectionProtocolAssist(Text);
            }
        });
        return false;
    }
    
    //Intercept for connection protocol.
    private void connectionProtocolAssist(String K){
        System.out.println(K);
        switch(K){
            case "::OK":
                RunUI.start();
                API.send("Main","Encrypt?");
                break;
            case "::Connected":
                
                API.send("Main", K);
                break;
            default:
                String[] DeMessage = EnDe.Decrypt(K.substring(2), Key).split(":");
                if (DeMessage[1].equals("::Key")){
                    Key = DeMessage[1];
                    enSend("Main","Password:" + Serverpass);
                    System.out.println("Sent");
                }else if (DeMessage[0].equals("GoodPass")){
                    enSend("Main","@Start");
                }else if (DeMessage[1].equals("BadPass")){
                    NewLogin.dispose();
                }else if (!Key.equals("") && EnDe.Decrypt(K.substring(2), Key).equals("SessionStart")){
                    
                    API.removeListenerAction("Main","Main_Listener");
                    API.createListenerAction("Main", "Communication", new FuncStore("Connection"){
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
        API.send(Connection,EnDe.Encrypt(Text, Key));
    }
    
    //Main method.
    
    public static void main(String[] args) {
        API.log("Attempting connection to server...");
        
        //UI to be run after login.
        RunUI = new Thread(new Runnable(){
            @Override
            public void run(){
                MainUI = new UI();
            }
        },"UI Cancer");
        
        //UI for login to be run right away.
        Thread W = new Thread(new Runnable(){
            @Override
            public void run(){
                NewLogin = new ServerLogin();
            }
        },"UI Tumor");
        W.start();
        
        //This some trash.
        while (NewLogin == null){
            try{
                Thread.sleep(10); // And this is the line that fixed an hour of bug testing.
            }catch(Exception E){
                
            }
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