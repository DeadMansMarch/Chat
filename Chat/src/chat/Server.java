/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.util.*;
import java.net.Socket;
/**
 *
 * @author Liam Pierce
 */
public class Server {
    static boolean IsServer = false;
    static HashMap<String,Integer> IpToConnection = new HashMap<>();
    private static TCPApi API = new TCPApi();
    private Crypt EnDe = new Crypt();
    private Random R = new Random();
    
    HashMap<String,Integer> Keys = new HashMap<>();
    
    public static boolean CheckIP(String IP){
        if (IpToConnection.containsKey(IP)){
            System.out.println("Truee");
            return true;
        }else{
            AddIPConnection(IP);
        }
        return false;
    }
    
    public static void AddIPConnection(String IPv4){
        System.out.println("Working: Connection.");
        IpToConnection.put(IPv4,1);
        API.Connection(new IP(IPv4,6789),IPv4);
    }
    
    public static void main(String[] args) {
        IsServer = true;
        Server Chat = new Server();
        Chat.EnDe = new Crypt();
        Chat.ProtocolC();
    }
    
    public boolean ProtocolC(){
        API.CreateListener(6789,"Main", null);
        
        API.CreateListenerAction("Main","ProtocolC",
                new FuncStore("MainConnectionProtocol"){
            @Override
            void Run(String Text,String IP){
                Client.Last = Text;
                System.out.println(Text);
                ConnectionProtocolAssist(Text,IP);
            }
        });
        
        return false;
    }
    
    private void ConnectionProtocolAssist(String K,String IP){
        Client.Last = K;
        System.out.println(IP);
        String EncryptedFirst = EnDe.Encrypt("::ES@C", Keys.get(IP));
        switch(K){
            
            
            case "::Connect?":
                API.Send(IP,"OK");
                break;
            
            case "::Encrypt?":
                int NewKey = R.nextInt(256);
                API.Send(IP,Integer.toString(NewKey));
                Keys.put(IP, NewKey);
                break;
            default:
                if (K.equals(EncryptedFirst)){
                    EnSend(IP,"SessionStart",IP);
                }
                break; 
        }
    }
    
    public void EnSend(String Connection,String Text,String IP){
        API.Send(Connection,EnDe.Encrypt(Text, Keys.get(IP)));
    }
}