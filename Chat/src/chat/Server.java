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
    private Socket Connection;
    private String IP;
    
    public void Connect(){
        System.out.println("Working: Connection.");
        Connector.IpToConnection.put(IP,1);
        Connector.API.Connection(new IP(IP,6789),IP);
    }
    
    public Server(Socket Connection){
        this.Connection = Connection;
        this.IP = Connection.getInetAddress().toString();
        Key = Connector.RandCreator.nextInt(500);
        Connect();
    }
    
    public boolean ProtocolC(){
        Connector.API.CreateServerListener(Connection,IP, new FuncStore("MainConnectionProtocol"){
            @Override
            void Run(String Text){
                Client.Last = Text;
                System.out.println("Text : " + Text);
                ConnectionProtocolAssist(Text);
            });

        
        return false;
    }
    
    private void ConnectionProtocolAssist(String K){
       
        switch(K){
            
            
            case "::Connect?":
                Connector.API.Send(IP,"OK");
                break;
            
            case "::Encrypt?":
                Connector.API.Send(IP,Integer.toString(Key));
                break;
            default:
                if (Connector.EnDe.Decrypt(K.substring(2), Key).equals("@Start")){
                    EnSend(IP,"SessionStart",IP);
                }else if (Connector.EnDe.Decrypt(K.substring(2),Key).equals("SessionStart")){
                    Connector.API.RemoveListenerAction("Main", "ProtocolC");
                    Connector.API.CreateListenerAction("Main", "Communication", new FuncStore("Connection"){
                        @Override
                        public void Run(String message,String IP){
                            String DeMessage = Connector.EnDe.Decrypt(message.substring(2),Key);
                            System.out.println(DeMessage);
                        }
                    });
                }
                break; 
        }
    }
    
    public void EnSend(String Connection,String Text,String IP){
        Connector.API.Send(Connection,Connector.EnDe.Encrypt(Text, Key));
    }
}