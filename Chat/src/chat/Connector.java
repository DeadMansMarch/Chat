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
public class Connector {
    
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
        Server.API.Connection(new IP(IP,6789),IP);
    }
    
    //Starts connections.
    public Connector(Socket Connection){
        this.Connection = Connection;
        this.IP = Connection.getInetAddress().toString().substring(1);
        
        //Runs connection protocol while allowing connector to continue without wait.
        Thread ServerThread = new Thread(new Runnable(){
            @Override
            public void run(){
                Key = Server.RandCreator.nextInt(500);
                protocolC();
            }
        },"ServerThread");
        
        
        ServerThread.start();
        
    }
    
    //Connection protocol.
    public boolean protocolC(){
        Server.API.CreateServerListener(Connection,IP, new FuncStore("MainConnectionProtocol"){
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
        Server.API.Log(K);
        switch(K){
            case "::Connect?":
                connect();
                Server.API.Send(IP,"OK");
                break;
            case "::Encrypt?":
                Server.API.Log(Key);
                Server.API.Send(IP,Integer.toString(Key));
                break;
            default:
                System.out.println(Server.EnDe.Decrypt(K.substring(2), Key));
                String[] DE = K.split(":");
                if (DE[0].equals("Password")){
                    if (Server.CompareStrings(DE[1])){
                        Server.API.Send(IP,"OK");
                    }else{
                        Server.API.Send(IP,"BadPass");
                    }
                }else if (Server.EnDe.Decrypt(K.substring(2), Key).equals("@Start")){
                    //Tells client to reset actions.
                    enSend(IP,"SessionStart");
                    
                    //Reset protocol action to work for messenger.
                    Server.API.RemoveListenerAction(IP, "Main_Listener");
                    
                    Server.API.CreateListenerAction(IP, "Communication", new FuncStore("Connection"){
                        @Override
                        public void Run(String Message){
                            String DeMessage = Server.EnDe.Decrypt(Message.substring(2),Key);
                            
                            String[] Split = DeMessage.split(":");
                            
                            if (Split[0].equals("@Name")){
                                Server.API.Log("Name change: " + Split[1]);
                                Server.setName(IP, Split[1]);
                                Server.UpdateAll();
                                TestConnections();
                            }else if (Split[0].equals("Connectionset")){
                                Server.OneConnection(IP,Server.getIP(Split[1]));
                            }else if (Split[0].equals("DefaultConnectionset")){
                                Server.createDefaultConnectionset(IP);
                            }else if(DeMessage.substring(0,3).equals("|||")){
                                System.out.println("Message: " + DeMessage);
                                Server.sendConnectionset(IP, "|||" + Server.getName(IP) + ":" + DeMessage.substring(3));
                            }else{
                                System.out.println(DeMessage.substring(0,3));
                            }
                        }
                    });
                    
                    //Connects client to chat relay.
                    Server.createDefaultConnectionset(IP);
                    Server.UpdateConnectionsets(IP);
                    if (Server.getName(IP).equals("")){
                        Server.setName(IP, IP);
                        Server.sendAll(IP,"AddName:" + IP);
                    }
                    
                    Server.UpdateAll();
                }
                break; 
        }
    }
    
    
    
    //Prints all known connections.
    public void TestConnections(){
        for (String K: Server.getConnections().keySet()){
            Server.API.Log("Connection :" + Server.getName(K,true));
        }
    }
    
    public void UpdateConnections(HashMap<String,HashMap<String,Integer>> List){
        Server.API.Log(Server.getName(IP));
        TestConnections();
        String StringSet = "ConnectionSet:";
        
        for (String Val: List.keySet()){
            if (!Val.equals(IP)){
                StringSet = StringSet + ":" + Server.getName(Val,true);
            }
        }
        
         enSend(IP,StringSet);
    }
    
    //Sends encrypted messeges.
    public void enSend(String Connection,String Text){
        Server.API.Send(Connection,Server.EnDe.Encrypt(Text, Key));
    }
}
