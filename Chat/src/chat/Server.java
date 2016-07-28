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
        if (!IpToConnection.containsKey(IP)){
            return true;
        }else{
            return false;
        }
    }
    
    public static void AddIPConnection(String IPv4){
        IpToConnection.put(IPv4,0);
        API.Connection(new IP(IPv4,6789),IPv4);
    }
    
    public static void main(String[] args) {
        IsServer = true;
        Server Chat = new Server();
        Chat.EnDe = new Crypt();
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
        
        switch(K){
            
            case "::Connect?\n":
                API.Send(IP,"OK");
                break;
            
            case "::Encrypt?\n":
                int NewKey = R.nextInt(256);
                API.Send(IP,Integer.toString(NewKey));
                Keys.put(IP, NewKey);
                break;
            case "::EncryptStart@Connect\n":
                EnSend(IP,"Connection_Started",IP);
                break;
            default:
                break; 
        }
    }
    
    public void EnSend(String Connection,String Text,String IP){
        API.Send(Connection,EnDe.Encrypt(Text, Keys.get(IP)));
    }
}