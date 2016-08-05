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
    
    private String Key = "";
    private final Socket Connection;
    private final String IP;
    
    //Connects server object to given client.
    
    public void Close(){
        try{
            Connection.close();
        }catch(Exception E){
        }
    }
    
    public void connect(){
        System.out.println("Creating new connection to " + IP);
        Server.API.connection(new IP(IP,6789),IP);
    }
    
    //Starts connections.
    public Connector(Socket Connection){
        this.Connection = Connection;
        this.IP = Connection.getInetAddress().toString().substring(1);
        
        //Runs connection protocol while allowing connector to continue without wait.
        Thread ServerThread = new Thread(new Runnable(){
            @Override
            public void run(){
                Key = Server.EnDe.makeKey();
                protocolC();
            }
        },"ServerThread");
        
        
        ServerThread.start();
        
    }
    
    //Connection protocol.
    public boolean protocolC(){
        Server.API.createServerListener(Connection,IP, new FuncStore("MainConnectionProtocol"){
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
        Server.API.log(K);
        switch(K){
            case "::Connect?":
                connect();
                
                Server.API.send(IP,"OK");
                break;
            case "::Encrypt?":
                Server.API.send(IP,Key);
                break;
            default:
                String[] DE = K.substring(2).split(":");
                if (DE[0].equals("Password")){
                    
                    
                    String Comp = "";
                    if (DE.length > 1){
                        Comp = DE[1];
                    }
                    
                    if (Server.compareStrings(Comp)){
                        enSend(IP,"GoodPass");
                    }else{
                        enSend(IP,"BadPass");
                    }
                }else if (Server.EnDe.Decrypt(K.substring(2), Key).equals("@Start")){
                    //Tells client to reset actions.
                    enSend(IP,"SessionStart");
                    
                    //Reset protocol action to work for messenger.
                    Server.API.removeListenerAction(IP, "Main_Listener");
                    
                    Server.API.createListenerAction(IP, "Communication", new FuncStore("Connection"){
                        @Override
                        public void Run(String Message){
                            String DeMessage = Server.EnDe.Decrypt(Message.substring(2),Key);
                            
                            String[] Split = DeMessage.split(":");
                            
                            if (Split[0].equals("@Name")){
                                Server.API.log("Name change: " + Split[1]);
                                Server.setName(IP, Split[1]);
                                Server.updateAll();
                            }else if (Split[0].equals("Connectionset")){
                                Server.oneConnection(IP,Server.getIP(Split[1]));
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
                    Server.updateConnectionsets(IP);
                    if (Server.getName(IP).equals("")){
                        Server.setName(IP, IP);
                        Server.sendAll(IP,"AddName:" + IP);
                    }
                    
                    Server.updateAll();
                }
                break; 
        }
    }
    
    
    //Sends the connections to the associated client.
    public void updateConnections(HashMap<String,HashMap<String,Integer>> List){
        Server.API.log(Server.getName(IP));
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
        Server.API.send(Connection,Server.EnDe.Encrypt(Text, Key));
    }
}
