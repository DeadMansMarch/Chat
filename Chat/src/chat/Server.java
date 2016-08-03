/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.util.*;
import java.net.*;

/**
 *
 * @author Liam Pierce
 */
public class Server {
    static boolean IsServer = false;
    
    private int Key = 0;
    private final Socket Connection;
    private final String IP;
    
    //Connects server object to given client.
    
    public void Close(){
        try{
            Connection.close();
        }catch(Exception E){
            
        }
        
        TestConnections();
    }
    
    public void connect(){
        System.out.println("Creating new connection to " + IP);
        Connector.API.Connection(new IP(IP,6789),IP);
    }
    
    //Starts connections.
    public Server(Socket Connection){
        this.Connection = Connection;
        this.IP = Connection.getInetAddress().toString().substring(1);
        
        //Runs connection protocol while allowing connector to continue without wait.
        Thread ServerThread = new Thread(new Runnable(){
            @Override
            public void run(){
                Key = Connector.RandCreator.nextInt(500);
                protocolC();
            }
        },"ServerThread");
        
        
        ServerThread.start();
        
    }
    
    //Connection protocol.
    public boolean protocolC(){
        Connector.API.CreateServerListener(Connection,IP, new FuncStore("MainConnectionProtocol"){
            @Override
            void Run(String Text){
                connectionProtocolAssist(Text);
            }
        });
        return false;
    }
    
    //A protocol to make sure that all connecting clients are meant to connect and
    //have all data that it needs.
    private void connectionProtocolAssist(String K){
        Connector.API.Log(K);
        switch(K){
            case "::Connect?":
                connect();
                Connector.API.Send(IP,"OK");
                break;
            case "::Encrypt?":
                Connector.API.Log(Key);
                Connector.API.Send(IP,Integer.toString(Key));
                break;
            default:
                System.out.println(Connector.EnDe.Decrypt(K.substring(2), Key));
                if (Connector.EnDe.Decrypt(K.substring(2), Key).equals("@Start")){
                    //Tells client to reset actions.
                    enSend(IP,"SessionStart");
                    
                    //Reset protocol action to work for messenger.
                    Connector.API.RemoveListenerAction(IP, "Main_Listener");
                    
                    Connector.API.CreateListenerAction(IP, "Communication", new FuncStore("Connection"){
                        @Override
                        public void Run(String Message){
                            String DeMessage = Connector.EnDe.Decrypt(Message.substring(2),Key);
                            
                            String[] Split = DeMessage.split(":");
                            
                            if (Split[0].equals("@Name")){
                                Connector.API.Log("Name change: " + Split[1]);
                                Connector.setName(IP, Split[1]);
                                Connector.UpdateAll();
                                TestConnections();
                            }else if (Split[0].equals("Connectionset")){
                                Connector.OneConnection(IP,Connector.getIP(Split[1]));
                            }else if (Split[0].equals("DefaultConnectionset")){
                                Connector.createDefaultConnectionset(IP);
                            }else{
                                System.out.println("Message: " + DeMessage);
                                Connector.sendConnectionset(IP, DeMessage);
                            }
                        }
                    });
                    
                    //Connects client to chat relay.
                    Connector.createDefaultConnectionset(IP);
                    if (Connector.getName(IP).equals("")){
                        Connector.setName(IP, IP);
                        Connector.sendAll(IP,"AddName:" + IP);
                    }
                    Connector.UpdateAll();
                }
                break; 
        }
    }
    
    
    
    //Prints all known connections.
    public void TestConnections(){
        for (String K: Connector.getConnections().keySet()){
            Connector.API.Log("Connection :" + Connector.getName(K,true));
        }
    }
    
    public void UpdateConnections(HashMap<String,HashMap<String,Integer>> List){
        Connector.API.Log(Connector.getName(IP));
        TestConnections();
        String StringSet = "ConnectionSet:";
        
        for (String Val: List.keySet()){
            if (!Val.equals(IP)){
                StringSet = StringSet + ":" + Connector.getName(Val,true);
            }
        }
        
         enSend(IP,StringSet);
    }
    
    //Sends encrypted messeges.
    public void enSend(String Connection,String Text){
        Connector.API.Send(Connection,Connector.EnDe.Encrypt(Text, Key));
    }
}
